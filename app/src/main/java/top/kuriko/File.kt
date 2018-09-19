package top.kuriko

import android.util.Log
import io.dcloud.common.util.Md5
import java.io.File
import top.kuriko.jni.FFmpeg
import top.kuriko.jni.Silk
//import sonic.Sonic
//import com.tencent.mobileqq.utils.SilkCodecWrapper
//import com.tencent.mobileqq.voicechange.VoiceChange
import java.io.FileInputStream
import java.net.URLEncoder

//TODO APP图标
//TODO 首页图
//TODO 添加SCHEMA、分享调用支持
//TODO 文件重命名、删除
//TODO 历史记录
//TODO 收藏单个文件
//TODO 筛选后缀，不支持的调用外部程序尝试打开
class File {
    fun cpfile(source: String, target: String) = File(source).copyTo(File(target), true)
    fun dirchk(path: String): Boolean{
        val d = File(splitFilePath(path).path)
        if (!d.exists())
            return d.mkdir()
        return true
    }
    fun preplaydec(source: String, target: String){
        if (!dirchk(target))
            File(target).delete()
        if (Regex("\\.wav$", RegexOption.IGNORE_CASE).containsMatchIn(source)){
            cpfile(source, target)
        }else if(Regex("\\.slk$", RegexOption.IGNORE_CASE).containsMatchIn(source)){
            decSilk(source, target, splitFilePath(target).path)
        }
        else {
            val ff = FFmpeg()
            ff.init()
            val args: Array<String> = arrayOf("ffmpeg", "-i", source, target)
            ff.exec(args)
        }
    }
    fun decSilk(source: String, target: String, cachedir: String) : String{
        Log.d("d", "$source\r\n$target\r\n$cachedir")
        val targetf = splitFilePath(target)
        val rn = "decSilktmp_${URLEncoder.encode(targetf.name, "UTF-8").substring(0..8)}"
        val rncache = "$cachedir/$rn.rename.bit"
        cpfile(source, rncache)
        val cache = "$cachedir/$rn.pcm"
        File(cache).delete()
        val s = Silk()
        s.init()
        try {
            Log.d("d", "1")
            s.decodefile(rncache, cache)
        } catch(e: Exception){
            return "Decode silk failed: ${e.message}"
        }
        File(rncache).delete()
        Log.d("d", "2")
        val ff = FFmpeg()
        ff.init()
        val args: Array<String> = arrayOf("ffmpeg", "-f", "s16le", "-ar", "24k", "-ac", "1", "-i", cache, target) //ffmpeg -f s16le -ar 24k -ac 1 -i pcm wav
        var msg = "Success"
        try {
            ff.exec(args)
        }catch(e: Exception){
            msg = "Convert pcm to wav failed: ${e.message}"
        }
        Log.d("d", "3")
        File(cache).delete()
        Log.d("d", "4")
        return msg
    }
    fun encSilk(source: String, target: String, cachedir: String) : String{
        val sourcef = splitFilePath(source)
        var cache : String
        if (cachedir.last()=='/')
            cache = "$cachedir${sourcef.name}${sourcef.ext}.wav"
        else cache = "$cachedir/${sourcef.name}${sourcef.ext}.wav"
        dirchk(cache)
        File(cache).delete()
        val ff = FFmpeg()
        ff.init()
        var args : Array<String>
        try {
            args = arrayOf("ffmpeg", "-i", source, "-ar", "48000", "-ac", "1", cache)
            //args = arrayOf("ffmpeg", "-i", source, "-af", "aresample=48000,aresample=async=48000,pan=mono,atempo=2.0", cache)
            ff.exec(args)
        } catch (e: Exception){
            return "Convert source to wav failed: ${e.message}"
        }
        val meta = ByteArray(32)
        val fi = FileInputStream(cache)
        fi.read(meta)
        fi.close()
        //val hz=byte2intR(meta.sliceArray(24..27))
        //val bps=byte2intR(meta.sliceArray(28..31))*8
        val hz=48000
        val bps=hz*16
        val cache2 = "$cache.1.wav"
        args = arrayOf("ffmpeg", "-i", cache, "-ar", "24000", cache2)
        /*if (hz==44100||hz==48000)
            args = arrayOf("ffmpeg", "-f", "s16le", "-ar", "$hz", "-acodec", "pcm_s16le", "-ac", "1", "-i", cache, "$cache.1.wav")
        else if (hz==24000||hz==16000||hz==8000)
            args = arrayOf("ffmpeg", "-f", "s16le", "-ar", "$hz", "-acodec", "pcm_s16le", "-ac", "1", "-i", cache, "$cache.1.wav")
        else return "Unsupported SampleRate"
        */
        ff.exec(args)
        File(cache).delete()
        cache = cache2

        try{
            val s = Silk()
            s.init()
            //FIXME Silk编码器有问题，采样率传入无效
            s.encodefile(cache, target, hz, bps)
        } catch (e: Exception){
            return "Encode silk failed: ${e.message}"
        }
        File(cache).delete()
        return "Success"
    }
    /*/                                                                            24000
    fun silkenc(source : String, target : String, newarg1 : Int, newarg2 : Int, encarg1 : Int, encarg2 : Int){
        val s = SilkCodecWrapper()
        s.init()
        Log.d("enc", "1")
        try {
            s.SilkEncoderNew(newarg1, newarg2)
            Log.d("enc", "2")
            val f = File(source).readBytes()
            Log.d("enc", "3")
            var ed = ByteArray(f.size)
            s.encode(encarg1, f, ed, f.size)
            Log.d("enc", "4")
            //s.deleteCodec(delarg)
            Log.d("enc", "5")
            File(target).delete()
            val fi = File(target)
            fi.delete()
            fi.createNewFile()
            fi.writeBytes("\u0002#!SILK_V3".toByteArray())
            fi.writeBytes(ed)
            Log.d("enc", "6")
        }catch(e: Exception){
            throw(e)
        }
    }*/
    //return (int)(chr[0]&chr[1]<<8&chr[2]<<16&chr[3]<<24)
    fun byte2intR(bytes : ByteArray): Int{
        val n = Array(4, {i -> bytes[3-i].toInt().and(0xFF).shl(8*(3-i))})
        return (n[0] or n[1] or n[2] or n[3])
    }
    data class FileInfo(var path: String, var name:String, var ext: String){
        override fun toString(): String{
            return "{path: \"$path\", name: \"$name\", ext: \"$ext\"}"
        }
    }
    fun splitFilePath(fullPath: String): FileInfo{
        val r=Regex("^(.*)/(.*?)(\\..*?)/?$").replace(fullPath, "$1,$2,$3").split(',')
        return FileInfo(path = r[0], name = r[1], ext = r[2])
    }
}

