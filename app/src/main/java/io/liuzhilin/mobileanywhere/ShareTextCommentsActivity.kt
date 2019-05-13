package io.liuzhilin.mobileanywhere

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import io.liuzhilin.mobileanywhere.bean.BlogComment
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

class ShareTextCommentsActivity : AppCompatActivity(){

    lateinit var blogId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        blogId = intent.getStringExtra("blogId")

        val localLayoutParams = window.attributes
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags
        setContentView(R.layout.fragment_send_text_message)

        setListener()
        text.text="评论文字"
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
            val bogComment = BlogComment()

            bogComment.blogCommentsTextData = data
            bogComment.blogCommentsCreateTime = Date(System.currentTimeMillis())
            bogComment.blogCommentsType = 1
            bogComment.blogCommentsOwner = UserCacheManager.getCurrentUser().userAccount
            bogComment.blogId = blogId
            bogComment.blogCommentsToType = 1

            val jsonString = GsonUtils.gson.toJson(bogComment)
            val map = HashMap<String,String>()
            map.put("data",jsonString)
            RequestCenter.requestPost(RequestCenter.ADD_COMMENT,map, CallBackParser(object : RequestCallback {
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