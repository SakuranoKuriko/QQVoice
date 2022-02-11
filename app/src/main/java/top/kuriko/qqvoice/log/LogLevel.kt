@file:Suppress("unused")
package top.kuriko.qqvoice.log

enum class LogLevel(val value: Int){
    Quiet(-8),
    Panic(0),
    Fatal(8),
    Error(16),
    Warning(24),
    Info(32),
    Verbose(40),
    Debug(48),
    Trace(56),
    MaxOffset(Trace.value - Quiet.value);

    fun toAlignedString(): String {
        val ret = CharArray(7) { ' ' }
        val str = toString()
        val off = 7 - str.length
        for (i in str.indices)
            ret[off + i] = str[i]
        return ret.joinToString("")
    }
}