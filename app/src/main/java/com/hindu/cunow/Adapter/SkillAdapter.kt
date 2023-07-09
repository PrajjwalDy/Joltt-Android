package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Model.ESModel
import com.hindu.cunow.R

class SkillAdapter(private val mContext: Context,
                   private val mSkill:List<ESModel>):RecyclerView.Adapter<SkillAdapter.ViewHolder>() {

                       inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                           val skillName:TextView = itemView.findViewById(R.id.skillName)

                           fun bind(list:ESModel){
                               skillName.text = list.skillName
                           }
                       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.skill_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mSkill.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(mSkill[position])
    }
}