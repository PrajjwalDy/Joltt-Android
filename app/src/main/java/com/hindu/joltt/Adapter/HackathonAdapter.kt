package com.hindu.joltt.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.joltt.Model.HackathonModel

class HackathonAdapter(private val mContext:Context,
                       private val mHack:List<HackathonModel>): RecyclerView.Adapter<HackathonAdapter.ViewHolder>(){

                           inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                               private val hackName: TextView = itemView.findViewById(R.id.hackathon_name) as TextView
                               private val hackTheme:TextView = itemView.findViewById(R.id.hackthonTheme) as TextView
                               private val hDate:TextView = itemView.findViewById(R.id.hackDate) as TextView
                               private val hackType:TextView = itemView.findViewById(R.id.hackathonType) as TextView

                               fun bind(list: HackathonModel){
                                   hackName.text = list.hackName
                                   hackTheme.text = list.hTheme
                                   hDate.text = list.hDate
                                   hackType.text = list.hType
                                   itemView.setOnClickListener{
                                       openLink(list.hLink!!,list.hackName!!)
                                   }
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
    private fun openLink(link:String,title:String){
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(mContext, Uri.parse(link))
    }
}