@file:Suppress("unused")
package top.kuriko.qqvoice.codec

import top.kuriko.qqvoice.res.XR.extractRawExecutableFile
import top.kuriko.qqvoice.utils.Shell
import top.kuriko.qqvoice.utils.XLog
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object FFmpegCmd {
    private const val name = "ffmpeg"

    lateinit var version: String
        private set

    private val versionRegex = Regex("version ([0-9a-zA-Z.-_]+?) Copyright", RegexOption.IGNORE_CASE)
    private val ffmpeg: File by lazy {
        return@lazy extractRawExecutableFile(name).also { f ->
            version = Shell.exec(f, "-version").run {
                if (process.waitFor() != 0)
                    throw Shell.ShellExecuteException("$name error")
                return@run stdoutReader.use { r ->
                    val line = r.readLine()
                    return@use versionRegex.find(line)?.groups?.get(1)?.value ?: line
                }
            }
            XLog.d("ffmpeg $version")
        }
    }

    fun exec(vararg args: String, hideBanner: Boolean = true): Shell.Session
        = if (hideBanner)
            Shell.exec(ffmpeg, "-hide_banner", *args)
        else Shell.exec(ffmpeg, *args)

}