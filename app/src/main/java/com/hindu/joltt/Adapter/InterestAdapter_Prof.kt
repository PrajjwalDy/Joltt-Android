package com.hindu.joltt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.joltt.Model.InterestModel

class InterestAdapter_Prof(private val mContext: Context,
                           private val mInterest:List<InterestModel>):RecyclerView.Adapter<InterestAdapter_Prof.ViewHolder>() {

                          inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
                               val interest: TextView = itemView.findViewById(R.id.interestName_profile) as TextView
                              fun bind(list:InterestModel){
                                  interest.text = list.interestTV
                              }
                          }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.interest_layout_profile, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mInterest.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mInterest[position])
    }
}