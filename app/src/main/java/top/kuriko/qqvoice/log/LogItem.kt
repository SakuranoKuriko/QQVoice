@file:Suppress("unused")
package top.kuriko.qqvoice.log

data class LogItem(val level: LogLevel, val message: String) {
    constructor(level: Int, msg: String) : this(Log.levels[level]!!, msg)
    override fun toString(): String = "[${level.toAlignedString()}]$message"
}