package top.kuriko.jni

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.FileWriter


class Silk {
    //private val DEFAULT_COMPLEXITY = 0
    //fun Silk() = System.loadLibrary("silkcodec")
    fun init() = System.loadLibrary("silkcodec")
    external fun encodefile(source: String, target: String, samplerate: Int, ratebps: Int)
    external fun decodefile(source: String, target: String, samplerate: Int)
    fun decodefile(source: String, target: String) = decodefile(source, target, 24000)
    external fun open(compression: Int, samplerate: Int, ratebps: Int): Int
    external fun decode(encoded: ByteArray, lin: ShortArray, size: Int): Int
    external fun encode(lin: ShortArray, offset: Int, encoded: ByteArray, size: Int): Int
    external fun close()
    /*fun decodefile2(source: String, target: String): Int{
        this.open(2, 24000, 20000)
        val fi = FileInputStream(source)
        val fc = fi.channel
        val fsize = fc.size().toInt()
        fc.close()
        fi.close()
        var ft = CharArray(fsize)
        FileReader(source).read(ft)
        var fs = ShortArray(ft.size)
        var i=0
        while (i<fsize)
            fs[i] = ft[i++].toShort()
        if (ft[0]==2.toChar())
            fs = fs.copyOfRange(1, fs.size)
        if (fs.copyOfRange(0,8).joinToString("")=="3383737675958651") // "!SILK_V3"
            fs = fs.copyOfRange(8, fs.size)
        if (fs.copyOfRange(0,9).joinToString("")=="353383737675958651") // "#!SILK_V3"
            fs = fs.copyOfRange(9, fs.size)
        var ed = ByteArray(fs.size)
        //             ||
        //            \||/
        //             ∀ JNI DETECTED ERROR IN APPLICATION: JNI SetShortArrayRegion called with pending exception java.lang.ArrayIndexOutOfBoundsException
        var r = this.decode(ed, fs, fs.size)
        this.close()
        if (r==0){
            File(target).delete()
            File(target).writeBytes(ed)
        }
        return r
    }*/
    fun encodefile2(compression: Int, source: String, target: String, samplerate: Int, ratebps: Int): Int{
        this.open(compression, samplerate, ratebps)
        val f = File(source).readBytes()
        var ed = ByteArray(f.size)
        var fs = ShortArray(f.size)
        for (i in f.indices){
            fs[i] = f[i].toShort()
        }
        val r = this.encode(fs, 0, ed, f.size)
        this.close()
        if (r==0){
            File(target).delete()
            val fi = File(target)
            fi.delete()
            fi.createNewFile()
            fi.writeBytes("\u0002#!SILK_V3".toByteArray())
            fi.writeBytes(ed)
        }
        return r
    }
    fun encodefile2(source: String, target: String, samplerate: Int, ratebps: Int): Int = this.encodefile2(2, source, target, samplerate, ratebps)
}