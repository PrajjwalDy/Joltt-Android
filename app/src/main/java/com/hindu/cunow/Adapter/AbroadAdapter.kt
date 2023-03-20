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
import com.hindu.cunow.Model.AbroadModel
import com.hindu.cunow.R

class AbroadAdapter(private val mContext: Context,
                    private val mList:List<AbroadModel>):RecyclerView.Adapter<AbroadAdapter.ViewHolder>() {

                        inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                            val abrImage:ImageView = itemView.findViewById(R.id.abr_image)
                            val title:TextView = itemView.findViewById(R.id.abroad_title) as TextView
                            val description:TextView = itemView.findViewById(R.id.abroad_description) as TextView

                            fun bind(list:AbroadModel){
                                title.text = list.abroadTitle
                                description.text = list.abroadDetails
                                Glide.with(mContext).load(list.abroadImage).into(abrImage)
                            }
                        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.abroad_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }
}