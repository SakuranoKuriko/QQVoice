package top.kuriko.qqvoice.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.OpenableColumns
import android.view.View
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import top.kuriko.qqvoice.BuildConfig
import top.kuriko.qqvoice.R
import top.kuriko.qqvoice.codec.ConvertException
import top.kuriko.qqvoice.codec.FFmpegCmd
import top.kuriko.qqvoice.codec.audio.AudioUtils.slkBitRate
import top.kuriko.qqvoice.codec.audio.AudioUtils.slkSampleRate
import top.kuriko.qqvoice.codec.audio.SilkCmd
import top.kuriko.qqvoice.data.*
import top.kuriko.qqvoice.res.XR
import top.kuriko.qqvoice.utils.ChatUtils.sendVoice
import top.kuriko.qqvoice.utils.XLog
import top.kuriko.qqvoice.utils.Utils
import top.kuriko.qqvoice.utils.Utils.deleteFile
import top.kuriko.qqvoice.utils.Utils.getFieldEx
import top.kuriko.qqvoice.utils.Utils.getMethodEx
import top.kuriko.qqvoice.utils.Utils.loadClassNotNull
import java.io.File
import java.io.InputStream
import java.lang.reflect.Modifier

object ShareHook {
    private val dealIntentDataMethod by lazy { jumpActivityClass.getMethodEx("dealIntentData", Intent::class.java) }
    private val forwardBaseOptionClass by lazy { loadClassNotNull("$currentPackageName.forward.ForwardBaseOption") }
    private val forwardBundleField by lazy {
        for (f in forwardBaseOptionClass.declaredFields) {
            f.isAccessible = true
            if (Modifier.isStatic(f.modifiers) || f.type != Bundle::class.java)
                continue
            return@lazy f!!
        }
        throw NoSuchFieldException("field ForwardBaseOption.Bundle not found!")
    }
    private val forwardActivityField by lazy {
        for (f in forwardBaseOptionClass.declaredFields) {
            f.isAccessible = true
            if (Modifier.isStatic(f.modifiers) || f.type != Activity::class.java)
                continue
            return@lazy f!!
        }
        throw NoSuchFieldException("field ForwardBaseOption.Activity not found!")
    }
    private val shareMethod by lazy { forwardBaseOptionClass.getMethodEx("b") }
    internal class SharingInfo(ext: String, openInputStream: () -> InputStream) {
        var shareAsVoice = false
        val voicePath: String by lazy {
            val srcExt = ext.lowercase()
            val cacheDir = Utils.mkdir(File(privateCachePath))
            val src = File(cacheDir, convertSrcFileName)
            val tmp = File(cacheDir, convertTempFileName)
            val cache = File(cacheDir, "$voiceCacheFileName${TargetFormat.SLK.ext}") // 总是转换为SLK发送
            deleteFile(src)
            deleteFile(tmp)
            deleteFile(cache)
            try {
                src.run {
                    createNewFile()
                    outputStream().use { os ->
                        openInputStream().use {
                            it.copyTo(os)
                        }
                    }
                }
                if (srcExt == "slk" || srcExt == "amr") // 现在QQ用的AMR实际上也是SLK
                    src.copyTo(cache, true)
                else {
                    FFmpegCmd.exec("-y", "-f", srcExt, "-i", "$src", "-ar", "$slkSampleRate", "-ab", "$slkBitRate", "-ac", "1", "-f", "s16le", "$tmp").run {
                        process.waitFor()
                        stderrReader.use {
                            val lines = it.readLines()
                            if (lines.find { l -> l.lowercase().contains("error") } != null)
                                throw ConvertException(lines.joinToString("\r\n"))
                        }
                    }
                    SilkCmd.exec("$tmp", "$cache", "-Fs_API", "$slkSampleRate", "-rate", "$slkBitRate", "-tencent").run {
                        if (process.waitFor() != 0)
                            throw ConvertException("silk encode failed")
                    }
                }
            } catch (e: Throwable) {
                Utils.toast(XR.string[R.string.msg_transcodingFailed]!!)
//                deleteFile(cache)
                throw e
            } finally {
                if (!BuildConfig.DEBUG) {
                    deleteFile(src)
                    deleteFile(tmp)
                }
            }
            return@lazy cache.canonicalPath
        }
    }
    private var sharingInfo: SharingInfo? = null

    @SuppressLint("Range")
    fun start(resolver: ContentResolver, intent: Intent): Boolean {
        if (intent.action != Intent.ACTION_SEND) // 不支持多个文件：无法解决顺序
            return false
        val uri = intent.extras!!.getParcelable<Uri>(Intent.EXTRA_STREAM) ?: return false
        val (fileName, fileSize) = resolver.query(uri, null, null, null, null)?.use {
            it.moveToFirst()
            val name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            val size = it.getLong(it.getColumnIndex(OpenableColumns.SIZE))
            return@use Pair(name, size)
        } ?: return false
        if (fileSize < 1)
            return false
        val i = fileName.lastIndexOf(".")
        val ext = if (i >= 0) fileName.substring(i+1) else fileName
        if (".$ext" == TargetFormat.SLK.ext || (SupportFormats.containsKey(ext) && SupportFormats[ext]!!.canDecode)){
            try {
                resolver.openInputStream(uri)?.use {} ?: return false
            } catch (_: Throwable) {
                XLog.d("cannot open sharing file")
                return false
            }
            XLog.d("share start")
            sharingInfo = SharingInfo(ext) { resolver.openInputStream(uri)!! }
            Utils.toast("Initialized")
            return true
        } else return false
    }

    private fun reset() {
        if (sharingInfo != null) {
            XLog.d("share end")
            sharingInfo = null
        }
    }

    fun init() {
        XposedBridge.hookMethod(dealIntentDataMethod, object: XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    if (!start((param.thisObject as Context).contentResolver, param.args[0] as Intent))
                        reset()
                } catch (e: Throwable) {
                    XLog.d("start share error: ${e.stackTraceToString()}")
                    Utils.toast("Initialization failed")
                }
            }
        })

        XposedBridge.hookMethod(setOnClickListenerMethod, object: XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (sharingInfo == null || param.args[0] == null || param.thisObject !is TextView)
                    return

                try {
                    val tv = param.thisObject as TextView
                    if (!tv.text.equals("发送"))
                        return
                    for (f in param.args[0]::class.java.declaredFields) {
                        f.isAccessible = true
                        if (qqCustomDialogClass.isAssignableFrom(f.type)) { // == forwardNewDialogClass
                            // 此处应该是"是否发送"提示框
                            val onClickOrigin = param.args[0] as View.OnClickListener
                            param.args[0] = View.OnClickListener {
                                if (sharingInfo != null) {
                                    try {
                                        AlertDialog.Builder(tv.context)
                                            .setTitle(XR.string[R.string.msg_confirmConvert2QQVoice]!!)
                                            .setNegativeButton(XR.string[R.string.msg_no]!!) { _, _ ->
                                                run {
                                                    onClickOrigin.onClick(it)
                                                    reset()
                                                }
                                            }
                                            .setPositiveButton(XR.string[R.string.msg_yes]!!) { _, _ ->
                                                run {
                                                    sharingInfo!!.shareAsVoice = true
                                                    onClickOrigin.onClick(it)
                                                    reset()
                                                }
                                            }
                                            .create()
                                            .show()
                                    } catch (e: Throwable) {
                                        XLog.d("show dialog failed: ${e.stackTraceToString()}")
                                        Utils.toast(XR.string[R.string.msg_showDialogFailed]!!)
                                        reset()
                                    }
                                    return@OnClickListener
                                }
                                onClickOrigin.onClick(it)
                            }
                            break
                        }
                    }
                } catch (e: Throwable) {
                    XLog.d("hook send button failed: ${e.stackTraceToString()}")
                    Utils.toast(XR.string[R.string.msg_hookSendButtonFailed]!!)
                    reset()
                }
            }
        })

        XposedBridge.hookMethod(shareMethod, object: XC_MethodHook() {

            override fun beforeHookedMethod(param: MethodHookParam) {
                if (sharingInfo == null || !sharingInfo!!.shareAsVoice)
                    return

                XLog.d("sharing as voice...")

                val forwardBaseOption = param.thisObject!!
                try {
                    val bundle = forwardBundleField.get(forwardBaseOption) as Bundle
                    val path = sharingInfo!!.voicePath
                    if (bundle.containsKey(FORWARD_MULTI_TARGET)) {
                        val list = bundle.getParcelableArrayList<Parcelable>(FORWARD_MULTI_TARGET)!!
                        val resultRecordClass = list[0].javaClass
                        val uinField = resultRecordClass.getFieldEx("uin")
                        val uinTypeField = try {
                            resultRecordClass.getFieldEx("uinType")
                        } catch (e: NoSuchFieldException) {
                            resultRecordClass.getFieldEx("uintype")
                        }
                        for (target in list) {
                            val uin = uinField.get(target) as String
                            val type = uinTypeField.get(target) as Int
                            sendVoice(uin, type, path)
                        }
                    } else {
                        val uin = bundle.getString("uin")!!
                        var type = bundle.getInt("uintype")
                        if (type == 0)
                            type = bundle.getInt("uinType")
                        sendVoice(uin, type, path)
                    }
                } catch (e: ConvertException) {
                    Utils.toast(XR.string[R.string.msg_transcodingFailed]!!)
                } catch (e: Throwable) {
                    XLog.d(e)
                    Utils.toast(XR.string[R.string.msg_sendFailed]!!)
                }
                param.result = false
                (forwardActivityField.get(forwardBaseOption) as Activity).finish()
            }
        })
    }
}