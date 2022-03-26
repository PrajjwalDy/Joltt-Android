package com.hindu.cunow.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
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
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.R

class PostGridAdapter(private val mContext: Context,
                      private val mPost:List<PostModel>,
): RecyclerView.Adapter<PostGridAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.postImage_grid) as ImageView
        val playerView: PlayerView = itemView.findViewById(R.id.videoPlayer_grid) as PlayerView

        fun bind(list:PostModel){
            if (list.iImage){
                imageView.visibility = View.VISIBLE
                playerView.visibility = View.GONE
                Glide.with(mContext).load(list.image).into(imageView)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.grid_post_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mPost[position])
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    private fun playVideo(videoView:PlayerView,videoUrl: String){
        initPlayer(videoView,videoUrl)
    }

    private fun initPlayer(videoView:PlayerView,videoUrl: String) {
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
                (videoView.player as SimpleExoPlayer).playWhenReady = false
                videoView.requestFocus()
                videoView.setShowNextButton(false)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(mContext,"Some Error Occurred", Toast.LENGTH_SHORT).show()
            }

        }
        simpleExoPlayer.addListener(playerListener)

    }


    enum class URLType(var url:String){
        MP4(""), HLS("")
    }

}