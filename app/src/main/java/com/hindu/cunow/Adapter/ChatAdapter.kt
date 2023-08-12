package com.hindu.cunow.Adapter

import android.content.Context
import android.media.AudioTimestamp
import android.media.Image
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hindu.cunow.Model.ChatModel
import com.hindu.cunow.R
import org.w3c.dom.Text
import java.util.logging.Handler

class ChatAdapter(private val mContext: Context,
                  private val mChat:List<ChatModel>):RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

                      inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
                          val chatImage_right: ImageView? = itemView.findViewById(R.id.chatImage_right) as? ImageView
                          val chatText:TextView = itemView.findViewById(R.id.chatText) as TextView
                          val chatImage_left:ImageView? = itemView.findViewById(R.id.chatImage_left) as? ImageView
                          val imageRL:RelativeLayout = itemView.findViewById(R.id.sendingImage_RL) as RelativeLayout
                          val imageLL:LinearLayout = itemView.findViewById(R.id.sendingImage_ll) as LinearLayout
                          val timeStamp:TextView = itemView.findViewById(R.id.timeStamp) as TextView

                          fun bind(list:ChatModel){
                              if (list.containImage){
                                  chatText.visibility = View.GONE
                                  if (list.chatImage == ""){
                                      imageRL.visibility = View.VISIBLE
                                      imageLL.visibility = View.VISIBLE
                                  }else{
                                      imageRL.visibility = View.VISIBLE
                                      imageLL.visibility = View.GONE
                                      if (list.sender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                          chatImage_right!!.visibility = View.VISIBLE
                                          Glide.with(mContext).load(list.chatImage).into(chatImage_right)

                                      }else if (!list.sender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                          chatImage_left!!.visibility = View.VISIBLE
                                          Glide.with(mContext).load(list.chatImage).into(chatImage_left)
                                      }
                                  }
                              }else{
                                  chatText.text = list.chatText
                                  imageRL.visibility = View.GONE
                                  imageLL.visibility = View.GONE
                                  if (list.sender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                      chatImage_right!!.visibility = View.GONE
                                  }else if (!list.sender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                      chatImage_left!!.visibility = View.GONE
                                  }
                              }

                              timeStamp.text = formatTimeStamp(list.timeStamp!!.toLong())
                          }

                      }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1){
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
            ViewHolder(view)
        }else{
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mChat[position])

    }

    override fun getItemCount(): Int {
       return mChat.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(mChat[position].sender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
            0
        }else{
            1
        }
    }

    private fun formatTimeStamp(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - timestamp

        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minutes ago"
            hours < 24 -> "$hours hours ago"
            days == 1L -> "Yesterday"
            else -> "$days days ago"
        }
    }



}