package io.liuzhilin.mobileanywhere.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.liuzhilin.mobileanywhere.R
import kotlinx.android.synthetic.main.view_comments.view.*

class PYQAdapter(val context : Context) : RecyclerView.Adapter<PYQAdapter.PYQCommentsViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PYQCommentsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_comments,p0,false)
        val holder = PYQCommentsViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return  10
    }

    override fun onBindViewHolder(p0: PYQCommentsViewHolder, p1: Int) {
        p0.onBind(p1)
    }


    inner class PYQCommentsViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView){

        fun onBind(position : Int){

        }

    }
}