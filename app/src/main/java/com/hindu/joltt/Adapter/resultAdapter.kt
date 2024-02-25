package com.hindu.joltt.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.core.view.View
import com.hindu.cunow.R
import com.hindu.joltt.Model.ResultModel

class resultAdapter (private val mContext: Context,
   private val mResult:List<ResultModel>):RecyclerView.Adapter<resultAdapter.ViewHolder>() {

        inner class ViewHolder (@NonNull itemView:android.view.View): RecyclerView.ViewHolder(itemView){
            private val resultName: TextView = itemView.findViewById(R.id.resultName) as TextView
            private val resultImage: ImageView = itemView.findViewById(R.id.result_image) as ImageView

            fun bind(list:ResultModel){
                Glide.with(mContext).load(list.resultImage).into(resultImage)
                resultName.text = list.resultName

                itemView.setOnClickListener {
                    openLink(list.resultLink!!)}
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.result_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mResult.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mResult[position])
    }
    private fun openLink(link:String){
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(mContext, Uri.parse(link))
    }

}