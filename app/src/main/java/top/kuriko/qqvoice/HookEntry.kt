package top.kuriko.qqvoice

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.kuriko.qqvoice.data.currentPackageName
import top.kuriko.qqvoice.data.Packages
import top.kuriko.qqvoice.hook.MainHook
import top.kuriko.qqvoice.res.XR
import top.kuriko.qqvoice.utils.Utils
import top.kuriko.qqvoice.utils.XLog


class HookEntry : IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        try {
            XR.init(startupParam)
        } catch (e: Throwable) {
            XLog.d("initZygote error: ${e.stackTraceToString()}")
            Utils.toast("InitZygote error")
        }
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        try {
            for (p in Packages.values()) {
                if (p.pName == resparam.packageName) {
                    XR.initRes(resparam)
                    break
                }
            }
        } catch (e: Throwable) {
            XLog.d("initResources error: ${e.stackTraceToString()}")
            Utils.toast("Error loading resources")
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        currentPackageName = lpparam.packageName
        try {
            Utils.init(lpparam)
            if (lpparam.packageName != lpparam.processName) return
            for (p in Packages.values()) {
                if (p.pName == lpparam.packageName) {
                    MainHook.init(lpparam)
                    break
                }
            }
        } catch (e: Throwable) {
            XLog.d("load package error: ${e.stackTraceToString()}")
            Utils.toast("Error loading program")
        }
    }
}