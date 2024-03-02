package com.hindu.joltt.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.R
import com.hindu.joltt.Model.AdmissionModel

class admissionAdapter (private val mContext: Context,
    private val mAdmission:List<AdmissionModel>):RecyclerView.Adapter<admissionAdapter.ViewHolder>(){

        inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
            private val admissionImage: ImageView = itemView.findViewById(R.id.admission_image) as ImageView
            private val admissionName: TextView = itemView.findViewById(R.id.admissionName) as TextView

            fun bind(list:AdmissionModel){
                Glide.with(mContext).load(list.admissionImage).into(admissionImage)
                    admissionName.text = list.admissionName

                itemView.setOnClickListener {
                    openLink(list.admissionLink!!)

            }
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout
            .admission_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
   return  mAdmission.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     holder.bind(mAdmission[position])
    }
    private fun openLink(link:String){
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(mContext, Uri.parse(link))}
}