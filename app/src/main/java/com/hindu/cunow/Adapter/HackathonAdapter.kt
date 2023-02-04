package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.Model.HackathonModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class HackathonAdapter(private val mContext:Context,
                       private val mHack:List<HackathonModel>): RecyclerView.Adapter<HackathonAdapter.ViewHolder>(){

                           inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                               private val hackImage:CircleImageView = itemView.findViewById(R.id.hackImage) as CircleImageView
                               private val hackName: TextView = itemView.findViewById(R.id.hackathon_name) as TextView
                               private val hackDes: TextView = itemView.findViewById(R.id.hackathon_description) as TextView

                               fun bind(list: HackathonModel){
                                   Glide.with(mContext).load(list.hackImage).into(hackImage)
                                   hackName.text = list.hackName
                                   hackDes.text = list.hackDescription
                               }

                           }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.hackathon_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mHack.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mHack[position])
    }
}