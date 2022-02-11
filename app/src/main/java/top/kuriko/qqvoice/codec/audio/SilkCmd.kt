@file:Suppress("unused")
package top.kuriko.qqvoice.codec.audio

import top.kuriko.qqvoice.res.XR
import top.kuriko.qqvoice.utils.Shell
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object SilkCmd {
    private const val encName = "silkenc"
    private const val decName = "silkdec"

    private fun initVersion(f: File): String
            = Shell.exec(f).run {
        if (process.waitFor() != 0)
            throw Shell.ShellExecuteException("silk error")
        return@run BufferedReader(InputStreamReader(stdout)).use { it.readLine() }
    }

    lateinit var encoderVersion: String
        private set

    lateinit var decoderVersion: String
        private set

    private val enc: File by lazy {
        return@lazy XR.extractRawExecutableFile("silkenc").also {
            encoderVersion = initVersion(it)
        }
    }

    private val dec: File by lazy {
        return@lazy XR.extractRawExecutableFile("silkdec").also {
            decoderVersion = initVersion(it)
        }
    }

    fun exec(vararg args: String, encode: Boolean = true, quiet: Boolean = true): Shell.Session {
        val f = if (encode) enc else dec
        return if (quiet)
            Shell.exec(f, *args, "-quiet")
        else Shell.exec(f, *args)
    }

}