package io.liuzhilin.mobileanywhere.util

import android.util.Base64
import io.liuzhilin.mobileanywhere.callback.RequestCallback
import sendToGitHub
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    fun getFileBytes(file:File):ByteArray{
        return file.readBytes()
    }

    fun sendFileToGitHub(file: File,endName:String,requestCallback: RequestCallback){
        val time = System.currentTimeMillis()
        //val file = File(imageUri!!.path)
        val bytes = file.readBytes()
        val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        var name = DIgestUtils.sha1(base64)
        val dateformat = SimpleDateFormat("yyyy-MM-dd")
        name = "mobileanywhere/" + dateformat.format(Date(System.currentTimeMillis())) +"/"+ name + "@" + System.currentTimeMillis() + endName
        sendToGitHub(name,base64,requestCallback)
    }
}