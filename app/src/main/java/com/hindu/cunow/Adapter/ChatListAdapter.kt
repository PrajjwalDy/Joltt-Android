package com.hindu.cunow.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.ChatActivity
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class ChatListAdapter(private val mContext:Context,
                      private val mUser:List<UserModel>): RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val profileImage:CircleImageView = itemView.findViewById(R.id.chatProfileImage) as CircleImageView
        val name:TextView = itemView.findViewById(R.id.chatFullName) as TextView
        val textCount:TextView = itemView.findViewById(R.id.textCount_TV) as TextView

        fun bind(list:UserModel){
            Glide.with(mContext).load(list.profileImage).into(profileImage)
            name.text = list.fullName

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.chatlist_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mUser[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext,ChatActivity::class.java)
            intent.putExtra("uid", mUser[position].uid)
            mContext.startActivity(intent)
            FirebaseDatabase.getInstance().reference.child("ChatMessageCount")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(mUser[position].uid!!).removeValue()
        }

        countUnreadMessage(holder.textCount, mUser[position].uid!!)

    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    private fun countUnreadMessage(count:TextView,profileId:String){
        val data = FirebaseDatabase.getInstance().reference.child("ChatMessageCount")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(profileId)
        data.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    count.visibility = View.VISIBLE
                    count.text = snapshot.childrenCount.toString()
                }else{
                    count.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}