@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package top.kuriko.qqvoice.data

enum class TargetFormat(val ext: String, val audioType: Int, val headerSize: Int) {
    // QQ语音现在使用的AMR后缀文件实际上也是SLK，而只会有AMR和SLK两种后缀，因此等于只使用SLK
    AMR(".amr", 0, 6), // 文件头无用，在解码时直接忽略
    SLK(".slk", 1, 10), // 文件头无用，在解码时直接忽略
    UNKNOWN("", 2, 64); // 文件头需要复制到输出开头

    override fun toString() = "{ext=$ext, audioType=$audioType, headerSize=$headerSize}"
}