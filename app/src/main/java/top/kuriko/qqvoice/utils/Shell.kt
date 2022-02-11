@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package top.kuriko.qqvoice.utils

import java.io.*
import java.lang.RuntimeException

object Shell {
    class ShellExecuteException(msg: String = "Shell execute failed"): RuntimeException(msg)
    class Session(val process: Process) {
        val stdin: OutputStream
            get() = process.outputStream
        val stdout: InputStream
            get() = process.inputStream
        val stderr: InputStream
            get() = process.errorStream

        val stdinWriter: BufferedWriter
            get() = BufferedWriter(OutputStreamWriter(stdin))
        val stdoutReader: BufferedReader
            get() = BufferedReader(InputStreamReader(stdout))
        val stderrReader: BufferedReader
            get() = BufferedReader(InputStreamReader(stderr))
    }

    fun exec(file: File, vararg args: String, env: Map<String, String>? = null, dir: File? = null) = exec(file.canonicalPath, *args, env = env, dir = dir)

    fun exec(vararg commandLine: String, env: Map<String, String>? = null, dir: File? = null): Session
        = Session(ProcessBuilder(*commandLine).apply {
            if (dir != null)
                directory(dir)
            if (env != null)
                environment().putAll(env)
        }.start())

    fun mkdir(path: String, createParent: Boolean = true, env: Map<String, String>? = null, dir: File? = null)
        = exec("mkdir", if (createParent) "-p" else "", path, env = env, dir = dir)

    fun mv(src: String, dst: String, force: Boolean = true, env: Map<String, String>? = null, dir: File? = null)
        = exec("mv", if (force) "-f" else "", src, dst, env = env, dir = dir)

    fun rm(path: String, force: Boolean = true, env: Map<String, String>? = null, dir: File? = null)
        = exec("rm", "-r", if (force) "-f" else "", path, env = env, dir = dir)
}