package com.hindu.joltt.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Activity.HelpActivity
import com.hindu.joltt.Model.PlayerState
import com.hindu.joltt.Model.UserModel
import com.hindu.joltt.Model.VibesModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.vibes_item_layout.view.*

class VibesAdapter(arrVideo:ArrayList<VibesModel>, private val mContext:Context):RecyclerView.Adapter<VibesAdapter.ViewHolder>() {

    var arrVideoModel:ArrayList<VibesModel> = arrVideo
    var playerState = PlayerState(true,0,0L)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vibes_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val zoom = AnimationUtils.loadAnimation(mContext, R.anim.zoom)

        //playVideo(holder.playerView,arrVideoModel[position].vibe!!)

        holder.setVibeData(arrVideoModel[position])

        isLike(holder.likeButton,arrVideoModel[position].vibeId!!)

        viber(holder.profileImage,holder.viberName,arrVideoModel[position].viberId!!)

        holder.likeButton.setOnClickListener {
            likeVibe(holder.likeButton,arrVideoModel[position].vibeId!!,zoom)
        }
        totalLike(holder.totalLike,arrVideoModel[position].vibeId!!)

        holder.moreOption.setOnClickListener {
            val intent = Intent(mContext, HelpActivity::class.java)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return arrVideoModel.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val viberName:TextView = itemView.findViewById(R.id.fullName_viber) as TextView
        val profileImage:CircleImageView = itemView.findViewById(R.id.vibe_profileImage) as CircleImageView
        val likeButton:ImageView = itemView.findViewById(R.id.likeButton_vibes) as ImageView
        val totalLike:TextView = itemView.findViewById(R.id.vibes_totalLike) as TextView
        val moreOption:ImageView = itemView.findViewById(R.id.moreOption_vibe) as ImageView

        //fun

        fun setVibeData(videoModel:VibesModel){
            itemView.vibeCaption.text = videoModel.vibeDescription
            itemView.vibes_viedoView.setVideoPath(videoModel.vibe)
            itemView.vibes_viedoView.setOnPreparedListener { p0 ->
                itemView.progressBar_Vibes.visibility = View.GONE
                p0.start()
                val videoRatio = p0.videoWidth / p0.videoHeight.toFloat()
                /*val screenRatio = itemView.vibes_viedoView.width / itemView.vibes_viedoView as Float

                val scale = videoRatio/screenRatio
                            if (scale > 1f){
                                itemView.vibes_viedoView.scaleX = scale
                            }else{
                                itemView.vibes_viedoView.scaleY = (1f / scale)
                            }*/
            }
            itemView.vibes_viedoView.setOnCompletionListener { MediaPlayer.OnCompletionListener { p0 -> p0!!.start() } }
        }
    }

    private fun viber(profileImageView: CircleImageView, fullName:TextView,viberId:String){

        val userDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(viberId)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profileImageView)
                    fullName.text = data.fullName
                    /*if (data.verification){
                        verifImage.visibility =View.VISIBLE
                    }else{
                        verifImage.visibility =View.GONE
                    }*/
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun likeVibe(likeButton:ImageView, vibeId:String, zoom: Animation){
        likeButton.startAnimation(zoom)
        if (likeButton.tag == "Like"){

            FirebaseDatabase.getInstance().reference
                .child("VibeLikes")
                .child(vibeId)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)
        }else{
            FirebaseDatabase.getInstance().reference
                .child("VibeLikes")
                .child(vibeId)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()
            likeButton.tag = "Like"
        }
        isLike(likeButton,vibeId)
    }

    private fun isLike(likeButton: ImageView,vibeId: String){

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val likeRef = FirebaseDatabase.getInstance().reference
            .child("VibeLikes")
            .child(vibeId)
        likeRef.addValueEventListener(object : ValueEventListener{
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
    private fun totalLike(totalLike:TextView,vibeId: String){
        val data = FirebaseDatabase.getInstance().reference.child("VibeLikes")
            .child(vibeId)
        data.addValueEventListener(object :ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val count = snapshot.childrenCount.toInt()
                    if (count <=1){
                        totalLike.text = snapshot.childrenCount.toString() + "Like"
                    }else{
                        totalLike.text = snapshot.childrenCount.toString() + "Likes"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    /*private fun totalLikes(vi: String, totalLikes:TextView){
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
    }*/


    /*private fun playVideo(videoView: PlayerView, videoUrl: String){
        initPlayer(videoView,videoUrl)
    }

    private fun initPlayer(videoView: PlayerView, videoUrl: String) {
        lateinit var simpleExoPlayer: SimpleExoPlayer
        lateinit var mediaSource: MediaSource


        simpleExoPlayer = SimpleExoPlayer.Builder(mContext).build()

        videoView.player = simpleExoPlayer
        val urlType = URLType.MP4
        urlType.url = videoUrl

        simpleExoPlayer.seekTo(0)
        when (urlType){
            URLType.MP4 ->{
                val datasourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                    mContext,
                    Util.getUserAgent(mContext,"")
                )
                mediaSource = ProgressiveMediaSource.Factory(datasourceFactory).createMediaSource(
                    MediaItem.fromUri(Uri.parse(urlType.url))
                )
            }
        }

        simpleExoPlayer.setMediaSource(mediaSource)
        simpleExoPlayer.prepare()
        val playerListener = object : Player.Listener{

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
                videoView.useController = true
                (videoView.player as SimpleExoPlayer).playWhenReady
                videoView.requestFocus()
                videoView.setShowNextButton(false)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(mContext,"$error", Toast.LENGTH_SHORT).show()
            }

        }
        simpleExoPlayer.addListener(playerListener)

    }


    enum class URLType(var url:String){
        MP4(""), HLS("")
    }*/

}