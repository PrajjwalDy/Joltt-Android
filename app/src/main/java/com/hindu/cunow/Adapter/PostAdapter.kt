package com.hindu.cunow.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.CommentActivity
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.more_option_dialogbox.view.*

class PostAdapter (private val mContext: Context,
                   private val mPost:List<PostModel>,
                   ):RecyclerView.Adapter<PostAdapter.ViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val zoom = AnimationUtils.loadAnimation(mContext, R.anim.zoom)
        val post = mPost[position]
        islike(post.postId!!, holder.like)
        totalLikes(post.postId,holder.totalLikes)

        holder.bind(mPost[position],mContext,holder.image,holder.playerView)
        publisher(holder.publisherImage,holder.publisherName,post.publisher!!,holder.verification)

        holder.like.setOnClickListener {
            like(holder.like,
                post.postId,zoom,
                post.publisher,
                holder.animation,
                holder.caption,
                holder.totalLikes)
        }
        holder.comment.setOnClickListener {
            val commentIntent = Intent(mContext,CommentActivity::class.java)
            commentIntent.putExtra("postId",post.postId)
            commentIntent.putExtra("publisher",post.publisher)
            mContext.startActivity(commentIntent)
        }

        holder.moreOption.setOnClickListener {
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.more_option_dialogbox, null)

            val dialogBuilder = AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setTitle("Options")

            val alertDialog = dialogBuilder.show()


//            if (firebaseUser!!.uid != post.postId ){
//                dialogView.deletePost.visibility = View.GONE
//                dialogView.editPost.visibility = View.GONE
//            }
            dialogView.savePost.setOnClickListener {
                Toast.makeText(mContext,"Click Received",Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
            dialogView.download.setOnClickListener {
                Toast.makeText(mContext,"Click Received",Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
            dialogView.editPost.setOnClickListener {
                Toast.makeText(mContext,"Click Received",Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
            dialogView.deletePost.setOnClickListener {
                Toast.makeText(mContext,"Click Received",Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
            dialogView.reportPost.setOnClickListener {
                Toast.makeText(mContext,"Click Received",Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        }
    }

    override fun getItemCount(): Int {
        return mPost.size
    }


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.postImage) as ImageView
        val caption: TextView = itemView.findViewById(R.id.caption) as TextView
        val publisherImage:CircleImageView = itemView.findViewById(R.id.publisher_profile_image) as CircleImageView
        val publisherName: TextView = itemView.findViewById(R.id.publisher_Name) as TextView
        val verification:CircleImageView = itemView.findViewById(R.id.verification_image) as CircleImageView
        val like: ImageView = itemView.findViewById(R.id.like) as ImageView
        val playerView: PlayerView = itemView.findViewById(R.id.videoPlayer) as PlayerView
        val comment:ImageView = itemView.findViewById(R.id.comment) as ImageView
        val animation:LottieAnimationView = itemView.findViewById(R.id.animation) as LottieAnimationView
        val moreOption:ImageView = itemView.findViewById(R.id.moreOptionPost) as ImageView
        val totalLikes: TextView = itemView.findViewById(R.id.totalLikes) as TextView


        fun bind(list:PostModel,context: Context,imageView: ImageView,playerView: PlayerView){
            caption.text = list.caption
            if (list.iImage){
                imageView.visibility = View.VISIBLE
                playerView.visibility = View.GONE
                Glide.with(context).load(list.image).into(image)
            }else if(list.video){
                imageView.visibility = View.GONE
                playerView.visibility = View.VISIBLE
                playVideo(playerView,list.videoId!!)
            }else{
                playerView.visibility = View.GONE
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
    private fun like(likeButton: ImageView,
                     postId: String,
                     zoom:Animation,
                     publisherId: String,
                     likeAnimationView: LottieAnimationView,
                     caption: TextView,
                     likeTextView: TextView){
        likeButton.startAnimation(zoom)
        if (likeButton.tag == "Like"){
            likeAnimationView.visibility = View.VISIBLE
            likeAnimationView.playAnimation()
            FirebaseDatabase.getInstance().reference
                .child("Likes")
                .child(postId)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)
            addNotification(publisherId,postId,caption)
        }else{
            FirebaseDatabase.getInstance().reference
                .child("Likes")
                .child(postId)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()
            likeButton.tag = "Like"
            likeAnimationView.visibility = View.GONE
        }
        islike(postId,likeButton)
        totalLikes(postId,likeTextView)

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

    private fun playVideo(videoView:PlayerView,videoUrl: String){
        initPlayer(videoView,videoUrl)
    }

    private fun initPlayer(videoView:PlayerView,videoUrl: String) {
        lateinit var simpleExoPlayer:SimpleExoPlayer
        lateinit var mediaSource: MediaSource


        simpleExoPlayer = SimpleExoPlayer.Builder(mContext).build()

        videoView.player = simpleExoPlayer
        val urlType = URLType.MP4
        urlType.url = videoUrl

        simpleExoPlayer.seekTo(0)
        when (urlType){
            URLType.MP4 ->{
                val datasourceFactory:DataSource.Factory = DefaultDataSourceFactory(
                    mContext,
                    com.google.android.exoplayer2.util.Util.getUserAgent(mContext,"")
                )
                mediaSource = ProgressiveMediaSource.Factory(datasourceFactory).createMediaSource(
                    MediaItem.fromUri(Uri.parse(urlType.url))
                )
            }
        }

        simpleExoPlayer.setMediaSource(mediaSource)
        simpleExoPlayer.prepare()
        val playerListener = object :Player.Listener{

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
                videoView.useController = true
                (videoView.player as SimpleExoPlayer).playWhenReady = false
                videoView.requestFocus()
                videoView.setShowNextButton(false)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(mContext,"Some Error Occurred",Toast.LENGTH_SHORT).show()
            }

        }
        simpleExoPlayer.addListener(playerListener)

    }


    enum class URLType(var url:String){
        MP4(""), HLS("")
    }

    private fun totalLikes(postId: String, totalLikes:TextView){
        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val likes = snapshot.childrenCount.toInt()
                    if (likes <=1){
                        totalLikes.text = snapshot.childrenCount.toString() + "Like"
                    }else{
                        totalLikes.text = snapshot.childrenCount.toString() + "Likes"
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addNotification(publisherId: String,postId: String,caption:TextView){
        if (publisherId != FirebaseAuth.getInstance().currentUser!!.uid){
            val dataRef = FirebaseDatabase.getInstance()
                .reference.child("Notification")
                .child(publisherId)

            val dataMap = HashMap<String,Any>()
            dataMap["notificationId"] = dataRef.push().key!!
            dataMap["notificationText"] = "Liked your post "+caption.text.toString()
            dataMap["postID"] = postId
            dataMap["isPost"] = true
            dataMap["notifierId"] = FirebaseAuth.getInstance().currentUser!!.uid

            dataRef.push().setValue(dataMap)
        }
    }

}