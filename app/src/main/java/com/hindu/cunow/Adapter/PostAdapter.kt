package com.hindu.cunow.Adapter

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.auth.FirebaseAuth
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

        val zoom = AnimationUtils.loadAnimation(mContext, R.anim.zoom)
        val post = mPost[position]
        islike(post.postId!!, holder.like)

        holder.bind(mPost[position],mContext,holder.image)
        publisher(holder.publisherImage,holder.publisherName,post.publisher!!,holder.verification)

        holder.like.setOnClickListener {
            like(holder.like, post.postId!!,zoom,post.publisher)

        }



        //Animations
        //Animations




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
        val like: ImageView = itemView.findViewById(R.id.like)
        val playerView: PlayerView = itemView.findViewById(R.id.videoPlayer)

        fun bind(list:PostModel,context: Context,imageView: ImageView){
            caption.text = list.caption
            if (list.image != ""){
                imageView.visibility = View.VISIBLE
                Glide.with(context).load(list.image).into(imageView)
            }else{
                imageView.visibility = View.GONE
            }
        }
    }

    private fun publisher(profileImage:CircleImageView, name:TextView,publisherId:String,verifImage:CircleImageView){

        val userDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profileImage)
                    name.text = data.fullName
                    if (data.verification){
                        verifImage.visibility =View.VISIBLE
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
    //Like Mechanism
    private fun like(likeButton: ImageView,postId: String,zoom:Animation,publisherId: String){
        likeButton.startAnimation(zoom)
        if (likeButton.tag == "Like"){
            FirebaseDatabase.getInstance().reference
                .child("Likes")
                .child(postId)
                .child(publisherId)
                .setValue(true)
        }else{
            FirebaseDatabase.getInstance().reference
                .child("Likes")
                .child(postId)
                .child(publisherId)
                .removeValue()
            likeButton.tag = "Like"
        }
        islike(postId,likeButton)

    }


    private fun islike(postId:String,
                     likeButton:ImageView,
                     ){
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val likeRef = FirebaseDatabase.getInstance().reference
            .child("Likes")
            .child(postId)
        likeRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()){
                    likeButton.setImageResource(R.drawable.filled_heart)
                    likeButton.tag = "Liked"
                }else{
                    likeButton.tag = "Like"
                    if (likeButton.tag == "Like"){
                        likeButton.setImageResource(R.drawable.blank_heart)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


}