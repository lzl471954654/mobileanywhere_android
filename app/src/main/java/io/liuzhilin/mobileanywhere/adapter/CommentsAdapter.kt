package io.liuzhilin.mobileanywhere.adapter

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import io.liuzhilin.mobileanywhere.R
import io.liuzhilin.mobileanywhere.bean.Blog
import io.liuzhilin.mobileanywhere.callback.OnBlogClickListener
import io.liuzhilin.mobileanywhere.manager.UserCacheManager
import kotlinx.android.synthetic.main.view_comments.view.*
import kotlinx.android.synthetic.main.view_comments.view.main_time
import kotlinx.android.synthetic.main.view_comments.view.people_name
import kotlinx.android.synthetic.main.view_image_comments.view.*
import kotlinx.android.synthetic.main.view_text_comments.view.*
import kotlinx.android.synthetic.main.view_text_image_comments.view.*

class PYQAdapter(val context : Context,var dataList : List<Blog>,val blogClickListener: OnBlogClickListener) : RecyclerView.Adapter<PYQAdapter.PYQCommentsViewHolder>() {



    override fun onCreateViewHolder(p0: ViewGroup,viewType : Int): PYQCommentsViewHolder {
        Log.e("onCreateViewHolder@type:",viewType.toString())
        return when(viewType){
            TEXT_BLOG->{
                val view = LayoutInflater.from(context).inflate(R.layout.view_text_comments,p0,false)
                PYQTextCommentsViewHolder(view)
            }
            IMAGE_BLOG->{
                Log.e("PYQAdapter","ImageBlog")
                val view = LayoutInflater.from(context).inflate(R.layout.view_image_comments,p0,false)
                PYQImageCommentsViewHolder(view)
            }
            TEXT_IMAGE_BLOG->{
                val view = LayoutInflater.from(context).inflate(R.layout.view_text_image_comments,p0,false)
                PYQTextImageCommentsViewHolder(view)
            }
            else->{
                val view = LayoutInflater.from(context).inflate(R.layout.view_comments,p0,false)
                PYQCommentsViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return  dataList.size
    }

    override fun onBindViewHolder(p0: PYQCommentsViewHolder, p1: Int) {
        p0.onBind(p1)
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].blogType.toInt()
    }


    open inner class PYQCommentsViewHolder(private val rootView: View) : RecyclerView.ViewHolder(rootView){


        open fun onBind(position : Int){
            rootView.setOnClickListener {
                blogClickListener.onBlogClick(dataList[position])
            }
            rootView.people_name.text = dataList[position].blogOwner
            rootView.main_time.text = dataformat.format(dataList[position].blogCreateTime)
            val user = UserCacheManager.getUser(dataList[position].blogOwner)
            if(user != null){
                Glide.with(context)
                        .load(user.userIconUrl)
                        .into(rootView.findViewById<CircleImageView>(R.id.head_image))
                rootView.people_name.text = user.userNickname
            }
        }

    }

    inner class PYQTextCommentsViewHolder(private val rootView: View) : PYQCommentsViewHolder(rootView){
        override fun onBind(position: Int) {
            super.onBind(position)
            rootView.comments_text_data.text = dataList[position].blogTextData
        }
    }

    inner class PYQImageCommentsViewHolder(private val rootView: View) : PYQCommentsViewHolder(rootView){
        override fun onBind(position: Int) {
            super.onBind(position)
            Glide.with(context)
                    .load(dataList[position].blogMediaUrl)
                    .into(rootView.comments_image_data)
        }
    }

    inner class PYQTextImageCommentsViewHolder(private val rootView: View) : PYQCommentsViewHolder(rootView){
        override fun onBind(position: Int) {
            super.onBind(position)
            rootView.comments_text_1_data.text = dataList[position].blogTextData
            Glide.with(context)
                    .load(dataList[position].blogMediaUrl)
                    .into(rootView.comments_image_2_data)
        }
    }


    companion object{

        const val TEXT_BLOG = 1
        const val IMAGE_BLOG = 2
        const val VIDEO_BLOG = 3
        const val VOICE_BLOG = 4
        const val TEXT_IMAGE_BLOG = 5

        val dataformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    }
}