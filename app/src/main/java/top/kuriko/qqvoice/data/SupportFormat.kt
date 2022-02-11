@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package top.kuriko.qqvoice.data

data class SupportFormat(val formats: Set<String>, val description: String, var canEncode: Boolean, var canDecode: Boolean) {
    override fun toString() = "{[${if (canDecode) 'D' else ' '}${if (canEncode) 'E' else ' '}]\t${formats.joinToString(",")}\t$description}"
}