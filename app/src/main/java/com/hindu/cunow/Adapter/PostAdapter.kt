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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext: Context,
                  private val mPost:List<PostModel>):RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
                      private var firebaseUser:FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post = mPost[position]

        holder.bind(mPost[position],mContext)
        publisher(holder.publisherImage,holder.publisherName,post.publisher!!,false,holder.verification)


    }

    override fun getItemCount(): Int {
        return mPost.size
    }


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.postImage)
        val caption: TextView = itemView.findViewById(R.id.caption)
        val publisherImage:CircleImageView = itemView.findViewById(R.id.publisher_profile_image)
        val publisherName: TextView = itemView.findViewById(R.id.publisher_Name)
        val verification:CircleImageView = itemView.findViewById(R.id.verification_image)

        fun bind(list:PostModel,context: Context){
            caption.text = list.caption
            if (list!!.image == ""){
                image.visibility = View.GONE
            }else{
                Glide.with(context).load(list.image).into(image)
            }
        }
    }

    private fun publisher(profileImage:CircleImageView, name:TextView,publisherId:String,verification:Boolean,verifImage:CircleImageView){

        val userDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profileImage)
                    name.text = data.fullName
                    if (verification){
                        Glide.with(mContext).load(data.verifImage).into(verifImage)
                    }else{
                        verifImage.visibility =View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}