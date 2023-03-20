package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.Model.ClubModel
import com.hindu.cunow.Model.CourseModel
import com.hindu.cunow.R

class ClubsAdapter(private val mContext: Context,
                   private val mList:List<ClubModel>):RecyclerView.Adapter<ClubsAdapter.ViewHolder>() {

                       inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                           private val clubName:TextView = itemView.findViewById(R.id.clubName_item) as TextView
                           private val clubImage: ImageView = itemView.findViewById(R.id.clubIcon) as ImageView

                           fun bind(list:ClubModel){
                               Glide.with(mContext).load(list.clubImage).into(clubImage)
                               clubName.text = list.clubName
                           }
                       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.club_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }
}