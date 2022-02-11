@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package top.kuriko.qqvoice.codec.audio

import top.kuriko.qqvoice.data.currentPackageName
import top.kuriko.qqvoice.data.Packages
import top.kuriko.qqvoice.utils.Utils.loadClassNotNull
import java.lang.reflect.Modifier

object AudioUtils {
    val qqAudioUtilsClass by lazy { loadClassNotNull("$currentPackageName.qqaudio.QQAudioUtils") }
    val supportedSampleRates by lazy {
        when (currentPackageName) {
            Packages.QQ.pName -> {
                for (f in qqAudioUtilsClass.declaredFields) {
                    if (Modifier.isStatic(f.modifiers) && f.type == IntArray::class.java)
                        return@lazy f.get(null) as IntArray // 应该是 { 8000, 12000, 16000, 24000, 36000, 44100, 48000 }, 索引对应文件第一个字节？
                }
            }
            else -> TODO()
        }
        throw NoSuchFieldException("field SampleRates not found!")
    }
    val slkSampleRate: Int
    val slkBitRate: Int
    val amrSampleRate: Int
    val amrBitRate: Int
    val slkEncodeBufferSize: Int
    val slkDecodeBufferSize: Int
    val slkDecodeBufferSize_VoiceChange: Int

    init {
        when (currentPackageName) {
            Packages.QQ.pName -> {
                slkSampleRate = 16000
                slkBitRate = 16000
                amrSampleRate = 8000
                amrBitRate = 8000
                slkEncodeBufferSize = 800
                slkDecodeBufferSize = 640
                slkDecodeBufferSize_VoiceChange = 960
            }
            else -> TODO()
        }
    }
}