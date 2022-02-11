@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package top.kuriko.qqvoice.res

import android.annotation.SuppressLint
import android.content.res.Resources
import android.content.res.XModuleResources
import android.content.res.XResources
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import top.kuriko.qqvoice.R
import top.kuriko.qqvoice.data.*
import top.kuriko.qqvoice.utils.XLog
import top.kuriko.qqvoice.utils.Utils
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.util.*

object XR {
    lateinit var modulePath : String
        private set
    lateinit var string: Map<Int, Int>
        private set
    lateinit var array: Map<Int, Int>
        private set
    lateinit var raw: Map<Int, Int>
        private set

    private lateinit var res: XResources
    lateinit var modRes: XModuleResources
        private set

    private val loader by lazy { XR::class.java.classLoader!! }
    private val xpLoader by lazy { Class.forName("de.robv.android.xposed.XposedBridge")?.classLoader }
    private val libNameRegex by lazy { Regex("(?<=(^|/))lib([^\r\n/\\\\:*?\"<>|]+?)\\.so$") }
    private val exLibDir by lazy { Utils.mkdir(privateFilesPath).canonicalPath }

    fun init(param: IXposedHookZygoteInit.StartupParam) {
        modulePath = param.modulePath
    }

    fun initRes(param: XC_InitPackageResources.InitPackageResourcesParam) {
        res = param.res
        modRes = XModuleResources.createInstance(modulePath, res)
        array = mapXRes(R.array::class.java)
        string = mapXRes(R.string::class.java)
        raw = mapXRes(R.raw::class.java)
    }

    fun initNativeLib() {
        // 加载原生库
        for (lib in app.resources.getStringArray(array[R.array.xposedPreloadLibraries]!!))
            loadLibrary("lib/$currentAbi/lib$lib.so", lib)
    }

    fun mapXRes(clazz: Class<*>): Map<Int, Int> {
        val map = HashMap<Int, Int>()
        val instance = clazz.declaredConstructors[0].run {
            isAccessible = true
            return@run newInstance()
        }
        for (f in clazz.declaredFields) {
            val id = f.getInt(instance)
            map[id] = res.addResource(modRes, id)
        }
        return map
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private fun loadLibrary(path: String, name: String = "") {
        val lib = if (name == "") {
            libNameRegex.find(path)?.groups?.get(1)?.value ?: throw IllegalArgumentException("The library file name must be lib<name>.so ")
        }
        else name
        try {
            System.load("$modulePath!/$path")
        } catch (e: UnsatisfiedLinkError) {
            File(exLibDir, "lib$lib.so").run {
                loader.getResourceAsStream(path)?.use {
                    outputStream().use { os ->
                        it.copyTo(os)
                    }
                } ?: throw e
                try {
                    xpLoader?.loadClass("org.lsposed.lspd.nativebridge.NativeAPI")
                        ?.getMethod("recordNativeEntrypoint", String::class.java)!!
                        .invoke(null, lib)
                } catch (_: ClassNotFoundException) {
                } catch (e: NoSuchMethodException) {
                    XLog.d(e)
                } catch (e: IllegalArgumentException) {
                    XLog.d(e)
                } catch (e: InvocationTargetException) {
                    XLog.d(e)
                } catch (e: IllegalAccessException) {
                    XLog.d(e)
                }
                System.load(canonicalPath)
            }
        }
    }

    fun extractRawAsFile(name: String, targetFileName: String, force: Boolean = true) = extractRawAsFile(name, File(privateFilesPath, targetFileName), force)

    fun extractRawAsFileToCache(name: String, targetFileName: String, force: Boolean = true) = extractRawAsFile(name, File(privateCachePath, targetFileName), force)

    fun extractRawExecutableFile(name: String, targetFileName: String? = null, force: Boolean = true): File {
        val target = targetFileName ?: name
        return try { extractRawAsFile("${name}_${currentAbi.replace('-', '_')}", target, force) }
        catch (_: Resources.NotFoundException) { extractRawAsFile("${name}_armeabi", target, force) }
            .apply { setExecutable(true) }
    }

    private fun extractRawAsFile(name: String, targetFile: File, force: Boolean = true): File {
        for (f in R.raw::class.java.declaredFields) {
            f.isAccessible = true
            val mod = f.modifiers
            if (f.type != Int::class.java || !Modifier.isStatic(mod) || !Modifier.isFinal(mod))
                continue
            if (f.name == name) {
                modRes.openRawResource(f.getInt(null)).use {
                    var size = 0L
                    while (it.read() != -1)
                        size++
                    it.reset()
                    if (targetFile.isFile && targetFile.length() == size && !force)
                        return targetFile
                    Utils.mkdir(targetFile.parentFile!!)
                    targetFile.outputStream().use { os -> it.copyTo(os) }
                    return targetFile
                }
            }
        }
        throw Resources.NotFoundException("resource raw/$name not found")
    }

}