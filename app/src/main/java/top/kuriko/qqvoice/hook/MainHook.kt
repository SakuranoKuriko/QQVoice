package top.kuriko.qqvoice.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.kuriko.qqvoice.data.*
import top.kuriko.qqvoice.utils.XLog

object MainHook {

    @Suppress("unused")
    val MethodHook = object: XC_MethodHook(){
        override fun beforeHookedMethod(param: MethodHookParam) {
            XLog.d("before " + param.thisObject::class.java.name + "." + param.method.name)
            XLog.dMethodArgs(param)
        }
        override fun afterHookedMethod(param: MethodHookParam) {
            XLog.d("${param.method.name}() = ${param.result}")
        }
    }

    @Suppress("unused")
    val ReplaceMethodHook = object: XC_MethodReplacement(){
        override fun replaceHookedMethod(param: MethodHookParam): Any? {
            XLog.d("replace "+param.method.name)
            XLog.dMethodArgs(param)
            return null
        }
    }

    fun init(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
//            Packages.QQ_INTERNATIONAL.pName,
//            Packages.QQ_LITE.pName,
//            Packages.TIM.pName,
            Packages.QQ.pName -> {
                ShareHook.init()
            }
            Packages.WECHAT.pName -> {
//                EzXHelperInit.initHandleLoadPackage(lpparam)
//                EzXHelperInit.setLogTag("QQVoice")
//                WeChatHookLoader()
            }
        }
    }
}