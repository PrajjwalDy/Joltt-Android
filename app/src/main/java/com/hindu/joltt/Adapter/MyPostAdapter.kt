package com.hindu.joltt.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
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
import com.hindu.cunow.R
import com.hindu.joltt.Model.PostModel

class MyPostAdapter(
    private val mContext: Context,
    private val mPost: List<PostModel>,
    private val from:String
) : RecyclerView.Adapter<MyPostAdapter.ViewHolder>() {


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imagePost) as ImageView
        val caption: TextView = itemView.findViewById(R.id.postCaption) as TextView
        val caption2: TextView = itemView.findViewById(R.id.postCaption2) as TextView
        val playerView: PlayerView = itemView.findViewById(R.id.videoView) as PlayerView

        fun bind(list: PostModel) {
            if (list.caption == "") {
                caption.visibility = View.GONE
            } else {
                caption2.visibility = View.VISIBLE
                caption2.text = list.caption
            }

            if (list.iImage) {
                image.visibility = View.VISIBLE
                playerView.visibility = View.GONE
                caption2.visibility = View.GONE
                Glide.with(mContext).load(list.image).into(image)
            } else if (list.video) {
                image.visibility = View.GONE
                playerView.visibility = View.VISIBLE
                caption2.visibility = View.GONE
                playVideo(playerView, list.videoId!!)
            } else {
                playerView.visibility = View.GONE
                image.visibility = View.GONE
                caption2.visibility = View.VISIBLE
                caption.visibility = View.GONE
            }

                itemView.setOnClickListener {

                    if (from == "UserProfile"){
                        val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                        pref.putString("postId",list.postId)
                        pref.putString("from","ProfileFrag")
                        pref.apply()

                        Navigation.findNavController(itemView)
                            .navigate(R.id.action_userProfile_to_fullPostView)
                    }else{
                        val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                        pref.putString("postId",list.postId)
                        pref.putString("from","ProfileFrag")
                        pref.apply()

                        Navigation.findNavController(itemView)
                            .navigate(R.id.action_navigation_profile_to_fullPostView)
                    }

                }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.mypost_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mPost[position])
    }

    private fun playVideo(videoView: PlayerView, videoUrl: String) {
        initPlayer(videoView, videoUrl)
    }

    private fun initPlayer(videoView: PlayerView, videoUrl: String) {
        lateinit var simpleExoPlayer: SimpleExoPlayer
        lateinit var mediaSource: MediaSource


        simpleExoPlayer = SimpleExoPlayer.Builder(mContext).build()

        videoView.player = simpleExoPlayer
        val urlType = URLType.MP4
        urlType.url = videoUrl

        simpleExoPlayer.seekTo(0)
        when (urlType) {
            URLType.MP4 -> {
                val datasourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                    mContext,
                    Util.getUserAgent(mContext, "")
                )
                mediaSource = ProgressiveMediaSource.Factory(datasourceFactory).createMediaSource(
                    MediaItem.fromUri(Uri.parse(urlType.url))
                )
            }

            else -> {}
        }

        simpleExoPlayer.setMediaSource(mediaSource)
        simpleExoPlayer.prepare()
        val playerListener = object : Player.Listener {

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
                videoView.useController = true
                (videoView.player as SimpleExoPlayer).playWhenReady = false
                videoView.requestFocus()
                videoView.setShowNextButton(false)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                //Toast.makeText(mContext,"Some Error Occurred",Toast.LENGTH_SHORT).show()
            }

        }
        simpleExoPlayer.addListener(playerListener)

    }

    enum class URLType(var url: String) {
        MP4(""), HLS("")
    }
}