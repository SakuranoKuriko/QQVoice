package top.kuriko.qqvoice.utils

import android.os.Parcel
import android.os.Parcelable
import top.kuriko.qqvoice.data.*
import top.kuriko.qqvoice.utils.Utils.getConstructorEx
import top.kuriko.qqvoice.utils.Utils.loadClassNotNull
import java.lang.reflect.Modifier


object ChatUtils {
    private val chatFacadeClass by lazy { loadClassNotNull("$currentPackageName.activity.ChatActivityFacade") }
    private val sendVoiceMethod by lazy {
        for (m in chatFacadeClass.methods) {
            if (m.returnType == Long::class.java && Modifier.isStatic(m.modifiers)) {
                val args = m.parameterTypes
                if (args.size != 3)
                    continue
                if (args[0] == qqAppInterfaceClass && args[1] == sessionInfoClass && args[2] == String::class.java)
                    return@lazy m!!
            }
        }
        throw NoSuchMethodException("method sendPttMessage not found!")
    }

    private fun createSessionInfo(uin: String, type: Int) : Parcelable
        = Parcel.obtain().run {
            writeInt(type)
            writeString(uin)
            writeString(null) // troopUin_b
            writeString(null) // uin_name_d
            writeString(null) // phoneNum_e
            writeInt(3999)    // add_friend_source_id_d
            writeBundle(null)
            setDataPosition(0)
            val info = sessionInfoClass.getConstructorEx(Parcel::class.java).newInstance(this) as Parcelable
            recycle()
            return@run info
        }

    fun sendVoice(uin: String, type: Int, pttPath: String): Long = sendVoiceMethod.invoke(null, appRuntimeField.get(app), createSessionInfo(uin, type), pttPath) as Long
}