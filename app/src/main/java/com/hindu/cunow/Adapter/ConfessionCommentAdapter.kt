package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Model.ConfessionCommentModel
import com.hindu.cunow.R

class ConfessionCommentAdapter(private val mContext:Context,
                               private val mComment:List<ConfessionCommentModel>,
                               private val cPublisher:String,
                               private val confessionId:String):RecyclerView.Adapter<ConfessionCommentAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val commentText:TextView = itemView.findViewById(R.id.commentText_c) as TextView
        val moreOption: ImageView = itemView.findViewById(R.id.moreOption_c) as ImageView

        fun bind(list: ConfessionCommentModel){
            commentText.text = list.cComment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.confession_comment_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mComment[position])
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

}