package io.liuzhilin.mobileanywhere

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.Snackbar
import android.view.View
import android.view.WindowManager
import com.google.gson.reflect.TypeToken
import io.liuzhilin.mobileanywhere.adapter.PYQAdapter
import io.liuzhilin.mobileanywhere.bean.Blog
import io.liuzhilin.mobileanywhere.callback.RequestCallback
import io.liuzhilin.mobileanywhere.manager.UserCacheManager
import io.liuzhilin.mobileanywhere.requests.RequestCenter
import io.liuzhilin.mobileanywhere.util.CallBackParser
import io.liuzhilin.mobileanywhere.util.GsonUtils
import kotlinx.android.synthetic.main.fragment_send_text_message.*
import kotlinx.android.synthetic.main.view_layout_title.*
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class ShareTExtActivity : AppCompatActivity() {

    lateinit var pointId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pointId = intent.getStringExtra("pointId")

        val localLayoutParams = window.attributes
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags
        setContentView(R.layout.fragment_send_text_message)

        setListener()
        text.text="分享"
        back_tools.setOnClickListener(View.OnClickListener {
            finish()
        })
        supportActionBar?.hide()
    }

    private fun setListener(){
        feedback_ac_submit.setOnClickListener {
            if(feedback_ac_advice.text.toString()==""||feedback_ac_advice.text==null)
                return@setOnClickListener
            val data = feedback_ac_advice.text.toString()
            val blog = Blog()
            blog.blogTextData = data
            val user = UserCacheManager.getCurrentUser()
            blog.blogOwner = user.userAccount
            blog.blogType = 1
            blog.blogCreateTime = Date(System.currentTimeMillis())
            blog.blogPointId = pointId
            val jsonString = GsonUtils.gson.toJson(blog)
            val map = HashMap<String,String>()
            map.put("blogData",jsonString)
            RequestCenter.requestPost(RequestCenter.SEND_BLOG,map, CallBackParser(object : RequestCallback {
                override fun success(json: String?) {
                    handler.post {
                        showSnackBar(feedback_ac_submit,json!!)
                    }

                }

                override fun failed(e: Exception?) {
                    handler.post {
                        showSnackBar(feedback_ac_submit,e!!.message!!)
                    }
                }
            }))
        }
    }


    @SuppressLint("HandlerLeak")
    private val handler = object : Handler(){
        override fun handleMessage(msg: Message?) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

fun Activity.showSnackBar(view:View,msg:String){
    Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show()
}
