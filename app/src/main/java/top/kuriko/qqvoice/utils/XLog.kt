@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package top.kuriko.qqvoice.utils

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

// Xposed Log
@Suppress("unused")
object XLog {
    private const val LogID = "QQVoice"
    var showLog = true

    fun d(msg: String) {
        if (!showLog)
            return
        XposedBridge.log("$LogID: $msg")
    }
    fun d(e: Throwable) = d(e.stackTraceToString())
    fun d(r: BufferedReader) {
        r.reset()
        while (true)
            d(r.readLine() ?: break)
        r.reset()
    }
    fun d(s: InputStream) = d(BufferedReader(InputStreamReader(s)))
    fun d(r: Reader) = d(BufferedReader(r))

    fun dFieldSig(f: Field, className: String = "") = d("${Modifier.toString(f.modifiers)} ${f.type.name} ${className}.${f.name}")
    fun dMethodSig(m: Method) = d("${Modifier.toString(m.modifiers)} ${m.returnType} ${m.name}(${m.parameterTypes.joinToString()})")

    fun dObjFields(obj: Any?, objName: String){
        if (obj != null) {
            val clazz = obj::class.java
            d("[${clazz.name}]$objName = $obj")
            for (f in clazz.declaredFields) {
                f.isAccessible = true
                if (Modifier.isStatic(f.modifiers))
                    continue
//                    d("${clazz.name}.${f.name} = ${f.get(null)}")
                else d("$objName.${f.name} = [${f.type.name}]${f.get(obj)}")
            }
        }
        else d("$objName = null")
    }
    fun dObjFields(clazz: Class<*>){
        for (f in clazz.declaredFields){
            f.isAccessible = true
            if (Modifier.isStatic(f.modifiers))
                continue
            dFieldSig(f, clazz.name)
        }
    }
    fun dObjMethods(clazz: Class<*>){
        for (m in clazz.declaredMethods) {
            m.isAccessible = true
            d("${Modifier.toString(m.modifiers)} ${m.returnType.name} ${clazz.simpleName}.${m.name}(${m.parameterTypes.joinToString()})")
        }
    }
    fun dMethodArgs(param: XC_MethodHook.MethodHookParam){
        val m = param.method as Method
        dMethodSig(m)
        if (param.thisObject != null)
            d("this: ${param.thisObject::class.java.name}")
        dObjFields(param.thisObject, "this")
        for (i in param.args.indices)
            dObjFields(param.args[i], "arg$i")
    }
}