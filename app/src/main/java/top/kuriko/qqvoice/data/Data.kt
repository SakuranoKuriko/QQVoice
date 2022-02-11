@file: Suppress("unused", "MemberVisibilityCanBePrivate")
package top.kuriko.qqvoice.data

import android.app.AndroidAppHelper
import android.app.Application
import android.content.Context
import android.os.Build
import android.view.View
import top.kuriko.qqvoice.R
import top.kuriko.qqvoice.codec.FFmpegCmd
import top.kuriko.qqvoice.res.XR
import top.kuriko.qqvoice.utils.Utils
import top.kuriko.qqvoice.utils.XLog
import java.lang.reflect.Method
import java.lang.reflect.Modifier

const val myPackageName = "top.kuriko.qqvoice"
val myName: String
    get() = XR.modRes.getString(XR.string[R.string.app_name]!!)

const val FORWARD_MULTI_TARGET = "forward_multi_target"
const val privateDirName = "send_as_voice"
const val voiceCacheFileName = "cache"
const val convertSrcFileName = "voice.src"
const val convertTempFileName = "voice.tmp"
lateinit var currentPackageName: String
const val deleteFileUseShell = false
val privateFilesPath by lazy { "${app.filesDir.canonicalPath}/$privateDirName/" }
val privateDir: String by lazy { app.externalCacheDir!!.canonicalPath }
val privateCachePath by lazy { "${app.externalCacheDir!!.canonicalPath}/$privateDirName/" }

val loader: ClassLoader
    get() = Utils._loader
val app: Application
    get() = Utils._app.invoke()
val currentContext: Context
    get() = AndroidAppHelper.currentApplication()

val currentAbi: String by lazy {
    @Suppress("deprecation")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        Build.SUPPORTED_ABIS[0]
    else Build.CPU_ABI
}

val loadDexClass by lazy { Utils.loadClassNotNull("$currentPackageName.startup.step.LoadDex") }
val baseApplicationImplClass by lazy { Utils.loadClassNotNull("com.tencent.common.app.BaseApplicationImpl") }
val qqAppInterfaceClass by lazy { Utils.loadClassNotNull("$currentPackageName.app.QQAppInterface") }
val appRuntimeClass by lazy { Utils.loadClassNotNull("mqq.app.AppRuntime") }
val mobileQQClass by lazy { Utils.loadClassNotNull("mqq.app.MobileQQ") }
val sessionInfoClass by lazy { Utils.loadClassNotNull("$currentPackageName.activity.aio.SessionInfo") }
val setOnClickListenerMethod: Method by lazy { View::class.java.getMethod("setOnClickListener", View.OnClickListener::class.java) }
val jumpActivityClass by lazy { Utils.loadClassNotNull("$currentPackageName.activity.JumpActivity") }
val qqCustomDialogClass by lazy { Utils.loadClassNotNull("$currentPackageName.utils.QQCustomDialog") }

val qqApplicationField by lazy {
    for (f in baseApplicationImplClass.declaredFields) {
        f.isAccessible = true
        if (Modifier.isStatic(f.modifiers) && f.type === baseApplicationImplClass)
            return@lazy f!!
    }
    throw NoSuchFieldException("field QQApplication not found")
}
val appRuntimeField by lazy {
    for (f in mobileQQClass.declaredFields) {
        f.isAccessible = true
        if (!Modifier.isStatic(f.modifiers) && f.type == appRuntimeClass)
            return@lazy f!!
    }
    throw NoSuchFieldException("field AppRuntime not found")
}

val SupportFormats: Map<String, SupportFormat> by lazy {
    val list = HashMap<String, SupportFormat>()
    val regex = Regex("(?<=(?:^|[\\r\\n]))\\s+([D ])([E ])\\s+([^\\s]+)\\s+(.*?)(?=(?:[\\r\\n]|\$))") // 不要删除这个“不必要的非捕获组”，它实际上是必要的
    FFmpegCmd.exec("-formats").run {
        process.waitFor()
        stdoutReader.use {
            while (true) {
                val line = it.readLine() ?: break
                for (match in regex.findAll(line)) {
                    val decode = match.groups[1]!!.value == "D"
                    val encode = match.groups[2]!!.value == "E"
                    val formats = match.groups[3]!!.value.split(',').toSet()
                    val description = match.groups[4]!!.value
                    val format = SupportFormat(formats, description, encode, decode)
                    for (f in formats)
                        list[f] = format
                }
            }
        }
    }
    val slk = SupportFormat(setOf("slk"), "Skype Silk", true, true)
    list["slk"] = slk
    list["silk"] = slk
    XLog.d("SupportFormats count = ${list.size}")
    return@lazy list
}