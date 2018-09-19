package top.kuriko.jni

class FFmpeg {
    fun init() = System.loadLibrary("ffmpegcmd")
    external fun exec(argv: Array<String>): Int
}