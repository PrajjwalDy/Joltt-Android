package com.hindu.cunow.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.CommentModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comments_more_option.view.*

class CommentAdapter(private val mContext: Context,
                     private val mComment:List<CommentModel>,
                     private val publisher:String,
                     private val postId:String):RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
       holder.bind(mComment[position])

        commentPublisher(holder.profileImage,holder.publisherName,mComment[position].commenter!!)


        holder.moreOption.setOnClickListener {
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.comments_more_option, null)

            val dialogBuilder = AlertDialog.Builder(mContext)
                .setView(dialogView)


            val alertDialog = dialogBuilder.show()

            if (mComment[position].commenter == FirebaseAuth.getInstance().currentUser!!.uid || mComment[position].commenter == publisher){
                dialogView.deleteComment.visibility = View.VISIBLE
            }else{
                dialogView.deleteComment.visibility = View.GONE
            }

            dialogView.deleteComment.setOnClickListener {
                FirebaseDatabase.getInstance().reference
                    .child("Comments")
                    .child(postId)
                    .child(mComment[position].commentId!!)
                    .removeValue()
                Toast.makeText(mContext,"comment removed",Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }

            dialogView.setOnClickListener {
                Toast.makeText(mContext,"Comment Reported as abuse",Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int {
       return mComment.size
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){

        val profileImage:CircleImageView = itemView.findViewById(R.id.commentProfileImage) as CircleImageView
        val publisherName:TextView = itemView.findViewById(R.id.commenterName) as TextView
        val commentText:TextView = itemView.findViewById(R.id.commentedText) as TextView
        val moreOption: ImageView = itemView.findViewById(R.id.moreOptionComment) as ImageView

        fun bind(list: CommentModel){
            commentText.text = list.commentText
        }

    }

    private fun commentPublisher(profileImage:CircleImageView, name:TextView,Id:String,){
        val userDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(Id)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profileImage)
                    name.text = data.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



}