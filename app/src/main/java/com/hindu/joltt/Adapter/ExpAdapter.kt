package com.hindu.joltt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.joltt.Model.ESModel

class ExpAdapter(private val mContext: Context,
                 private val mSkill:List<ESModel>):RecyclerView.Adapter<ExpAdapter.ViewHolder>() {

                       inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                           val expName:TextView = itemView.findViewById(R.id.expName)

                           fun bind(list: ESModel){
                               expName.text = list.expName
                           }
                       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.exp_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mSkill.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(mSkill[position])
    }
}