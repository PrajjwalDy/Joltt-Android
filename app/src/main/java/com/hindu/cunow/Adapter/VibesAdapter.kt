package com.hindu.cunow.Adapter

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Model.VibesModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.vibes_item_layout.view.*

class VibesAdapter(arrVideo:ArrayList<VibesModel>):RecyclerView.Adapter<VibesAdapter.ViewHolder>() {

    var arrVideoModel:ArrayList<VibesModel> = arrVideo

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vibes_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setVibeData(arrVideoModel[position])
    }

    override fun getItemCount(): Int {
        return arrVideoModel.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun setVibeData(videoModel:VibesModel){
            itemView.vibeCaption.text = videoModel.vibeDescription
            itemView.vibes_viedoView.setVideoPath(videoModel.vibe)
            itemView.vibes_viedoView.setOnPreparedListener(object :MediaPlayer.OnPreparedListener{

                override fun onPrepared(p0: MediaPlayer) {
                    itemView.progressBar_Vibes.visibility = View.GONE
                    p0.start()
                    val videoRatio = p0.videoWidth /p0.videoHeight as Float
                    val screenRatio = itemView.vibes_viedoView.width / itemView.vibes_viedoView as Float

                    val scale = videoRatio/screenRatio
                    if (scale > 1f){
                        itemView.vibes_viedoView.scaleX = scale
                    }else{
                        itemView.vibes_viedoView.scaleY = (1f / scale)
                    }

                }

            })
            itemView.vibes_viedoView.setOnCompletionListener { object :MediaPlayer.OnCompletionListener{
                override fun onCompletion(p0: MediaPlayer?) {
                    p0!!.start()
                }

            } }
        }
    }
}