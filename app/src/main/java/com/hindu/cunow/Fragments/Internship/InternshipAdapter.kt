package com.hindu.cunow.Fragments.Internship

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.Model.InternshipModel
import com.hindu.cunow.R

class InternshipAdapter(private val mContext: Context,
                        private val mInternship:List<InternshipModel>): RecyclerView.Adapter<InternshipAdapter.ViewHolder>(){

                            inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                                val interImg: ImageView = itemView.findViewById(R.id.intern_img) as ImageView
                                val intern_name: TextView = itemView.findViewById(R.id.intern_title) as TextView
                                val internDesc: TextView = itemView.findViewById(R.id.intern_desc) as TextView

                                fun bind(list:InternshipModel){
                                    Glide.with(mContext).load(list.internImg).into(interImg)
                                    intern_name.text = list.internTitle
                                    internDesc.text = list.internDesc
                                }
                            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.internship_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mInternship.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mInternship[position])
    }
}