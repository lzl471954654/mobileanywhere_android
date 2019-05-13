package io.liuzhilin.mobileanywhere.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import io.liuzhilin.mobileanywhere.R
import io.liuzhilin.mobileanywhere.bean.BlogComment
import io.liuzhilin.mobileanywhere.bean.User
import io.liuzhilin.mobileanywhere.manager.UserCacheManager
import kotlinx.android.synthetic.main.blog_comments.view.*
import kotlinx.android.synthetic.main.blog_comments.view.head_image
import kotlinx.android.synthetic.main.blog_comments.view.main_time
import kotlinx.android.synthetic.main.blog_comments.view.people_name
import kotlinx.android.synthetic.main.blog_image_comments.view.*
import kotlinx.android.synthetic.main.view_image_comments.view.*
import kotlinx.android.synthetic.main.view_image_comments.view.comments_image_data

class BlogCommentsAdapter(private val context : Context,private val dataList : List<BlogComment>) : RecyclerView.Adapter<BlogCommentsAdapter.BaseBlogCommentsViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, itemViewType: Int): BaseBlogCommentsViewHolder {
        return when(itemViewType){
            1->{
                val view = LayoutInflater.from(context).inflate(R.layout.blog_comments,p0,false)
                TextBlogCommentsViewHolder(view)
            }
            2->{
                val view = LayoutInflater.from(context).inflate(R.layout.blog_image_comments,p0,false)
                ImageBlogCommentsViewHolder(view)
            }
            else->{
                val view = LayoutInflater.from(context).inflate(R.layout.blog_comments,p0,false)
                BaseBlogCommentsViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].blogCommentsType.toInt()
    }

    override fun onBindViewHolder(viewHolder: BaseBlogCommentsViewHolder, position: Int) {
        viewHolder.onBind(position)
    }


    open inner class BaseBlogCommentsViewHolder(private val rootView : View) : RecyclerView.ViewHolder(rootView){

        open fun onBind(position : Int){
            rootView.people_name.text = dataList[position].blogCommentsOwner
            rootView.main_time.text = PYQAdapter.dataformat.format(dataList[position].blogCommentsCreateTime)
            val user = UserCacheManager.getUser(dataList[position].blogCommentsOwner)
            if (user != null){
                rootView.people_name.text = user.userNickname
                Glide.with(context)
                        .load(user.userIconUrl)
                        .into(rootView.head_image)
            }
        }
    }

    inner class TextBlogCommentsViewHolder(private val rootView: View) : BaseBlogCommentsViewHolder(rootView){

        override fun onBind(position: Int) {
            super.onBind(position)
            rootView.comments_data.text = dataList[position].blogCommentsTextData
        }

    }

    inner class ImageBlogCommentsViewHolder(private val rootView: View) : BaseBlogCommentsViewHolder(rootView){

        override fun onBind(position: Int) {
            super.onBind(position)
            Glide.with(context)
                    .load(dataList[position].blogCommentsMediaUrl)
                    .into(rootView.comments_image_data)
        }

    }


}