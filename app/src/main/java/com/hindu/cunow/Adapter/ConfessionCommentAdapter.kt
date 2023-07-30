package com.hindu.cunow.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.Activity.ReportPostActivity
import com.hindu.cunow.Model.ConfessionCommentModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.more_option_confession.view.deleteConfession
import kotlinx.android.synthetic.main.more_option_confession.view.reportConfession

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
        holder.moreOption.setOnClickListener {
            val cList = mComment[position]
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.more_option_confession, null)

            val dialogBuilder = AlertDialog.Builder(mContext)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.reportConfession.setOnClickListener {
                val intent = Intent(mContext, ReportPostActivity::class.java)
                intent.putExtra("postId",mComment[position].cCommentId+"+Confessioncomment")
                mContext.startActivity(intent)
                alertDialog.dismiss()
            }

            if (cList.cPublisher != FirebaseAuth.getInstance().currentUser!!.uid){
                dialogView.deleteConfession.visibility = View.GONE
            }
            dialogView.deleteConfession.setOnClickListener {
                FirebaseDatabase.getInstance().reference.child("ConfessionComment")
                    .child(cList.confessionId!!)
                    .child(cList.cCommentId!!)
                    .removeValue()
                alertDialog.dismiss()
            }
        }
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

}