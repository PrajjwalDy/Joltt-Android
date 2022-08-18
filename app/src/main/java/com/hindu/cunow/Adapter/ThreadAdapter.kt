package com.hindu.cunow.Adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.disklrucache.DiskLruCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.CircleFlowModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class ThreadAdapter(private val mContext: Context,
                    private val mThread:List<CircleFlowModel>,
                    ):RecyclerView.Adapter<ThreadAdapter.ViewHolder>() {

                        inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                            val image:ImageView = itemView.findViewById(R.id.thread_image) as ImageView
                            val text:TextView = itemView.findViewById(R.id.thread_text) as TextView
                            val profileImage:CircleImageView = itemView.findViewById(R.id.thread_profile_image) as CircleImageView
                            val name:TextView = itemView.findViewById(R.id.name_thread) as TextView
                            val react:ImageView = itemView.findViewById(R.id.react_thread) as ImageView
                            val reply:ImageView = itemView.findViewById(R.id.reply_thread) as ImageView
                            val imageCV:CardView = itemView.findViewById(R.id.image_cv) as CardView
                            val totalReact:TextView = itemView.findViewById(R.id.total_react) as TextView

                            fun bind(list:CircleFlowModel){
                                senderInfo(profileImage,name,list.circleFlowSender!!)
                                text.text = list.circleFlowText
                                if (list.messageImage){
                                    image.visibility = View.VISIBLE
                                    imageCV.visibility = View.VISIBLE
                                    Glide.with(mContext).load(list.circleFlowImg).into(image)
                                }else{
                                    image.visibility = View.GONE
                                    imageCV.visibility = View.GONE
                                }

                            }
                        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.thread_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        isReacted(mThread[position].circleFlowId!!,holder.react)
        totalReact(mThread[position].circleFlowId!!,holder.totalReact)
        holder.bind(mThread[position])
        holder.react.setOnClickListener{
            react(holder.react,mThread[position].circleFlowId!!,holder.totalReact)
        }
    }

    override fun getItemCount(): Int {
        return mThread.size
    }

    private fun senderInfo(profileImage:ImageView,name:TextView,uid:String){
        val data = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                Glide.with(mContext).load(user!!.profileImage).into(profileImage)
                name.text = user.fullName
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun react(react:ImageView,uid: String,totalReact:TextView){
        if (react.tag == "react"){
            FirebaseDatabase.getInstance().reference.child("ThreadReact")
                .child(uid)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)
        }else{
            FirebaseDatabase.getInstance().reference.child("ThreadReact")
                .child(uid)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()
            react.tag = "react"
        }
        isReacted(uid,react)
        totalReact(uid,totalReact)
    }

    private fun totalReact(uid: String, totalReact: TextView) {
        val dataRef = FirebaseDatabase.getInstance().reference
            .child("ThreadReact")
            .child(uid)
        dataRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.childrenCount.toInt()
                    if (data <=0){
                        totalReact.text = "react"
                    }else if (data >=1){
                        totalReact.text = snapshot.childrenCount.toString() + "reacted"
                    }else{
                        totalReact.text = "react"
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun isReacted(uid: String, react: ImageView) {
        val fireabaseUser = FirebaseAuth.getInstance().currentUser

        val ref = FirebaseDatabase.getInstance().reference
            .child("ThreadReact")
            .child(uid)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(fireabaseUser!!.uid).exists()){
                    react.setImageResource(R.drawable.filled_heart)
                    react.tag = "reacted"
                }else{
                    react.tag = "react"
                    if (react.tag == "react"){
                        react.setImageResource(R.drawable.heart)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}