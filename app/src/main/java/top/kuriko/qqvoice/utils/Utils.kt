@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package top.kuriko.qqvoice.utils

import android.app.Application
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.kuriko.qqvoice.R
import top.kuriko.qqvoice.data.*
import top.kuriko.qqvoice.res.XR
import java.io.File
import java.lang.reflect.*

object Utils {
    @Suppress("ObjectPropertyName")
    lateinit var _loader: ClassLoader
        private set
    @Suppress("ObjectPropertyName")
    lateinit var _app: () -> Application
        private set

    fun init(param: XC_LoadPackage.LoadPackageParam) {
        _loader = param.classLoader
        when (currentPackageName) {
            Packages.QQ.pName -> {
                for (m in loadDexClass.declaredMethods) {
                    m.isAccessible = true
                    if (m.returnType == Boolean::class.java && m.parameterTypes.isEmpty()) {
                        XposedBridge.hookMethod(m, object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam) {
                                try {
                                    _app = { qqApplicationField.get(null) as Application }
                                    XR.initNativeLib()
                                } catch (e: Throwable) {
                                    XLog.d("load package error: ${e.stackTraceToString()}")
                                    toast("Error loading program")
                                }
                            }
                        })
                        break
                    }
                }
            }
        }
    }

    fun loadClass(name: String): Class<*>? = _loader.loadClass(name)
    fun loadClassNotNull(name: String): Class<*> = loadClass(name) ?: throw ClassNotFoundException("class $name not found!")
    fun Class<*>.getMethodEx(name: String, vararg args: Class<*>): Method {
        val m = getDeclaredMethod(name, *args)
        m.isAccessible = true
        return m
    }
    fun <T> Class<T>.getConstructorEx(vararg args: Class<*>): Constructor<T> {
        val c = getDeclaredConstructor(*args)
        c.isAccessible = true
        return c
    }
    fun Class<*>.getFieldEx(name: String): Field {
        val c = getDeclaredField(name)
        c.isAccessible = true
        return c
    }

    fun mkdir(path: String, deleteUseShell: Boolean = deleteFileUseShell) = mkdir(File(path), deleteUseShell)
    fun mkdir(file: File, deleteUseShell: Boolean = deleteFileUseShell): File
        = file.apply {
        if (!isDirectory) {
            if (isFile)
                deleteFile(this, deleteUseShell)
            mkdir()
        }
    }

    fun deleteFile(path: String, useShell: Boolean = deleteFileUseShell) = deleteFile(File(path), useShell)
    fun deleteFile(file: File, useShell: Boolean = deleteFileUseShell) {
        if (useShell)
            Shell.rm(file.canonicalPath)
        else file.delete()
    }

    private var prevToast: Toast? = null
    fun toast(msg: String, tag: String = "QQVoice", duration: Int = Toast.LENGTH_LONG) {
        prevToast?.cancel()
        prevToast = Toast.makeText(currentContext, "$tag: $msg", duration)
        prevToast!!.show()
    }
    fun toast(resID: Int, duration: Int = Toast.LENGTH_LONG) = toast(XR.modRes.getString(XR.string[resID]!!), myName, duration)
}