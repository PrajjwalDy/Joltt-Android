package com.hindu.joltt.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Activity.ChatActivity
import com.hindu.joltt.Model.ChatModel
import com.hindu.joltt.Model.UserModel
import de.hdodenhof.circleimageview.CircleImageView

class ChatListAdapter(private val mContext:Context,
                      private val mUser:List<UserModel>): RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val profileImage:CircleImageView = itemView.findViewById(R.id.chatProfileImage) as CircleImageView
        val name:TextView = itemView.findViewById(R.id.chatFullName) as TextView
        val textCount:TextView = itemView.findViewById(R.id.textCount_TV) as TextView
        val layout:RelativeLayout = itemView.findViewById(R.id.RL_chat_count) as RelativeLayout
        val lastMessage:TextView = itemView.findViewById(R.id.chatLast_message) as TextView
        val card:CardView =itemView.findViewById(R.id.chatCardView) as CardView

        fun bind(list:UserModel){
            Glide.with(mContext).load(list.profileImage).into(profileImage)
            name.text = list.fullName
            getLastMessage(lastMessage,list.uid!!)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.chatlist_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mUser[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ChatActivity::class.java)
            intent.putExtra("uid", mUser[position].uid)
            mContext.startActivity(intent)
            FirebaseDatabase.getInstance().reference.child("ChatMessageCount")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(mUser[position].uid!!).removeValue()
        }

        countUnreadMessage(holder.textCount, mUser[position].uid!!,holder.layout,holder.card)

    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    private fun countUnreadMessage(count:TextView,profileId:String,layout:RelativeLayout,card:CardView){
        val data = FirebaseDatabase.getInstance().reference.child("ChatMessageCount")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(profileId)
        data.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    layout.visibility = View.VISIBLE
                    count.visibility = View.VISIBLE
                    count.text = snapshot.childrenCount.toString()
                    card.setCardBackgroundColor(Color.parseColor("#ffc2cf"))
                }else{
                    count.visibility = View.GONE
                    layout.visibility = View.GONE
                    card.setCardBackgroundColor(Color.parseColor("#ffffff"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getLastMessage(textView:TextView, receiver:String){
        val chatData = FirebaseDatabase.getInstance().reference.child("ChatData")

        chatData.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val chatList = mutableListOf<ChatModel>()
                    for (snapshot in snapshot.children){
                        val chat = snapshot.getValue(ChatModel::class.java)

                        if (chat!!.receiver.equals(FirebaseAuth.getInstance().currentUser!!.uid) && chat.sender.equals(receiver)
                            ||chat.receiver.equals(receiver) && chat.sender.equals(FirebaseAuth.getInstance().currentUser!!.uid) ){
                            chatList.add(chat)
                        }
                        if (chatList.isNotEmpty()) {
                            val lastItem = chatList.last()

                            if (lastItem.containImage){
                                textView.text ="Photo"
                            }else{
                                textView.text = lastItem.chatText
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}