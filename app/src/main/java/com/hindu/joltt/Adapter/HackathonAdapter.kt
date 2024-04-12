package com.hindu.joltt.Adapter

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.R
import com.hindu.joltt.Model.HackathonModel

class HackathonAdapter(private val mContext:Context): RecyclerView.Adapter<HackathonAdapter.ViewHolder>(){


    private var hackathons: List<HackathonModel> = mutableListOf()

    fun setItems(items: List<HackathonModel>) {
        hackathons = items
        notifyDataSetChanged()
    }

                           inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                               private val hackName: TextView = itemView.findViewById(R.id.hackathon_name)
                               private val hackTheme:TextView = itemView.findViewById(R.id.hackthonTheme)
                               private val hDate:TextView = itemView.findViewById(R.id.hackDate)
                               private val hackType:TextView = itemView.findViewById(R.id.hackathonType)
                               private val hImage:ImageView = itemView.findViewById(R.id.hImage)
                               private val hPrize:TextView = itemView.findViewById(R.id.hPrize)
                               private val hCard: CardView = itemView.findViewById(R.id.hCard)

                               fun bind(list: HackathonModel){
                                   hackName.text = list.hackName
                                   hackTheme.text = list.hTheme
                                   hDate.text = list.hDate
                                   hackType.text = list.hType
                                   hPrize.text = list.hPrize
                                   Glide.with(mContext).load(list.hImage).into(hImage)
                                   itemView.setOnClickListener{
                                       openLink(list.hLink!!,list.hackName!!)
                                   }

                                   if (list.hStatus == "C"){
                                       hCard.setCardBackgroundColor(Color.parseColor("#ffbca8"))
                                   }
                               }

                           }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.hackathon_item_layout, parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return hackathons.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(hackathons[position])
    }
    private fun openLink(link:String,title:String){
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(mContext, Uri.parse(link))
    }
}