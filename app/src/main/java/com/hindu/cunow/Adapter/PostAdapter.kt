package com.hindu.cunow.Adapter

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext: Context,
                  private val mPost:List<PostModel>):RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
                      private var firebaseUser:FirebaseUser? = null
    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.postImage)
        val caption: TextView = itemView.findViewById(R.id.caption)
        val publisherImage:CircleImageView = itemView.findViewById(R.id.publisher_profile_image)
        val publisherName: TextView = itemView.findViewById(R.id.publisher_Name)
        val varification:CircleImageView = itemView.findViewById(R.id.verification_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = mPost[position]


    }

    override fun getItemCount(): Int {
        return mPost.size
    }
}