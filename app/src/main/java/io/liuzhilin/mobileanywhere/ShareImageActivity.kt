package io.liuzhilin.mobileanywhere

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.linchaolong.android.imagepicker.ImagePicker
import io.liuzhilin.mobileanywhere.bean.Blog
import io.liuzhilin.mobileanywhere.callback.RequestCallback
import io.liuzhilin.mobileanywhere.manager.UserCacheManager
import io.liuzhilin.mobileanywhere.requests.RequestCenter
import io.liuzhilin.mobileanywhere.util.CallBackParser
import io.liuzhilin.mobileanywhere.util.DIgestUtils
import io.liuzhilin.mobileanywhere.util.GsonUtils
import kotlinx.android.synthetic.main.fragment_send_image_message.*
import kotlinx.android.synthetic.main.fragment_send_image_message.feedback_ac_advice
import kotlinx.android.synthetic.main.fragment_send_image_message.feedback_ac_submit
import kotlinx.android.synthetic.main.fragment_send_text_message.*
import org.jetbrains.anko.doAsync
import sendToGitHub
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ShareImageActivity : AppCompatActivity() {

    lateinit var imagePicker: ImagePicker

    private var imageUri: Uri? = null

    lateinit var pointId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_send_image_message)
        supportActionBar?.hide()
        pointId = intent.getStringExtra("pointId")
        initLisener()
    }

    fun initLisener() {
        imagePicker = ImagePicker()
        imagePicker.setCropImage(false)

        pick_image.setOnClickListener {
            imagePicker.startChooser(this, object : ImagePicker.Callback() {
                override fun onPickImage(imageUri: Uri?) {
                    this@ShareImageActivity.imageUri = imageUri
                    Glide.with(this@ShareImageActivity)
                            .load(imageUri)
                            .into(image_data)
                }
            })
        }

        shot_image.setOnClickListener {
            imagePicker.startCamera(this, object : ImagePicker.Callback() {
                override fun onPickImage(imageUri: Uri?) {
                    this@ShareImageActivity.imageUri = imageUri
                    Glide.with(this@ShareImageActivity)
                            .load(imageUri)
                            .into(image_data)
                }
            })
        }

        feedback_ac_submit.setOnClickListener {
            if (imageUri != null){
                showSnackBar(feedback_ac_submit,"开始上传图片")
                startCompressImage(imageUri!!)
            }
        }
    }

    fun startCompressImage(uri: Uri){
        Luban.with(this)
                .load(uri)
                .ignoreBy(100)
                .setTargetDir(this.cacheDir.absolutePath)
                .setCompressListener(object : OnCompressListener {
                    override fun onSuccess(file: File?) {
                        runOnUiThread {
                            Toast.makeText(this@ShareImageActivity,"开始压缩图片",Toast.LENGTH_SHORT).show()
                            upload(file!!)
                        }
                    }

                    override fun onError(e: Throwable?) {
                        runOnUiThread {
                            Toast.makeText(this@ShareImageActivity,"压缩出错",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onStart() {
                        runOnUiThread {
                            Toast.makeText(this@ShareImageActivity,"开始压缩图片",Toast.LENGTH_SHORT).show()
                        }
                    }
                }).launch()
    }

    fun upload(file: File) {
        doAsync {
            val time = System.currentTimeMillis()
            //val file = File(imageUri!!.path)
            val bytes = file.readBytes()
            val base64 = Base64.encodeToString(bytes,Base64.DEFAULT)
            Log.e("ImagePicker",base64)
            Log.e("ImagePicker time :",(System.currentTimeMillis() - time).toString())
            var name = DIgestUtils.sha1(base64)
            val dateformat = SimpleDateFormat("yyyy-MM-dd")
            name = "mobileanywhere/" + dateformat.format(Date(System.currentTimeMillis())) +"/"+ name + ".jpg"
            sendToGitHub(name,base64, object : RequestCallback {
                override fun failed(e: Exception?) {
                    runOnUiThread {
                        showSnackBar(feedback_ac_submit,e?.message!!)
                        Log.e("UploadImage:",e.message)
                    }
                }

                override fun success(json: String?) {
                    runOnUiThread {
                        showSnackBar(feedback_ac_submit,json!!)
                        Log.e("UploadImage:",json)
                    }

                    //val data = feedback_ac_advice.text.toString()
                    val blog = Blog()
                    val user = UserCacheManager.getCurrentUser()
                    blog.blogOwner = user.userAccount
                    blog.blogType = 2
                    blog.blogCreateTime = Date(System.currentTimeMillis())
                    blog.blogPointId = pointId
                    blog.blogMediaUrl = json
                    val jsonString = GsonUtils.gson.toJson(blog)
                    val map = HashMap<String,String>()
                    map.put("blogData",jsonString)
                    RequestCenter.requestPost(RequestCenter.SEND_BLOG,map, CallBackParser(object : RequestCallback {
                        override fun success(json: String?) {
                            runOnUiThread {
                                showSnackBar(feedback_ac_submit,json!!)
                                Toast.makeText(this@ShareImageActivity,"发送成功",Toast.LENGTH_LONG).show()
                            }

                        }

                        override fun failed(e: Exception?) {
                            runOnUiThread {
                                showSnackBar(feedback_ac_submit,e!!.message!!)
                            }
                        }
                    }))
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagePicker.onActivityResult(this,requestCode,resultCode,data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        imagePicker.onRequestPermissionsResult(this,requestCode,permissions,grantResults)
    }

}