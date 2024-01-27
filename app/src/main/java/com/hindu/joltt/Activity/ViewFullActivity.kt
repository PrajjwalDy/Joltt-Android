package com.hindu.joltt.Activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Model.PostModel

class ViewFullActivity : AppCompatActivity() {
    private var postId = ""
    private var publisher = ""

    private lateinit var postImage_full:ImageView
    private lateinit var videoPlayer_full:PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full)

        postId = intent.getStringExtra("postId").toString()
        publisher = intent.getStringExtra("publisher").toString()

        postImage_full = findViewById(R.id.postImage_full)
        videoPlayer_full = findViewById(R.id.videoPlayer_full)


        /*val videoView = R.id.videoPlayer_full
        val videoUrl = */

        loadPost()
    }


    private fun loadPost(){

        val userRef = FirebaseDatabase.getInstance().reference.child("Post").child(postId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val userData = snapshot.getValue(PostModel::class.java)
                    if (userData!!.iImage){
                        postImage_full.visibility = View.VISIBLE
                        videoPlayer_full.visibility = View.GONE
                        Glide.with(this@ViewFullActivity).load(userData.image).into(postImage_full)

                    }else{
                        playVideo(userData.videoId!!)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("some error occurred")
            }
        })
    }
    private fun playVideo(videoUrl: String){
        initPlayer(videoUrl)
    }

    private fun initPlayer(videoUrl: String) {
        lateinit var simpleExoPlayer: SimpleExoPlayer
        lateinit var mediaSource: MediaSource


        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()

        videoPlayer_full.player = simpleExoPlayer
        val urlType = URLType.MP4
        urlType.url = videoUrl

        simpleExoPlayer.seekTo(0)
        when (urlType){
            URLType.MP4 ->{
                val datasourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                    this@ViewFullActivity,
                    Util.getUserAgent(this@ViewFullActivity,"")
                )
                mediaSource = ProgressiveMediaSource.Factory(datasourceFactory).createMediaSource(
                    MediaItem.fromUri(Uri.parse(urlType.url))
                )
            }else->{}
        }

        simpleExoPlayer.setMediaSource(mediaSource)
        simpleExoPlayer.prepare()
        val playerListener = object : Player.Listener{

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
                videoPlayer_full.useController = true
                (videoPlayer_full.player as SimpleExoPlayer).playWhenReady = false
                videoPlayer_full.requestFocus()
                videoPlayer_full.setShowNextButton(false)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(this@ViewFullActivity,"Some Error Occurred", Toast.LENGTH_SHORT).show()
            }

        }
        simpleExoPlayer.addListener(playerListener)

    }
    enum class URLType(var url:String){
        MP4(""), HLS("")
    }

}