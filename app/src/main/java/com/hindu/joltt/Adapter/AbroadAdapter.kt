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
import com.hindu.joltt.Model.AbroadModel

class AbroadAdapter(
    private val mContext: Context,
    private val mList: List<AbroadModel>
) : RecyclerView.Adapter<AbroadAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val programName: TextView = itemView.findViewById(R.id.abroad_title) as TextView
        val description: TextView = itemView.findViewById(R.id.abroad_description) as TextView
        val instituteName: TextView = itemView.findViewById(R.id.instituteName) as TextView
        val country: TextView = itemView.findViewById(R.id.country) as TextView
        val classMode: TextView = itemView.findViewById(R.id.classMode) as TextView
        val degree: TextView = itemView.findViewById(R.id.programFor) as TextView

        fun bind(list: AbroadModel) {
            programName.text = list.programName
            description.text = list.prgramDes
            instituteName.text = list.instituteName
            country.text = list.country
            classMode.text = list.classMode
            degree.text = list.degree

            itemView.setOnClickListener{
                openLink(list.programLink!!)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.abroad_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    private fun openLink(link:String){

        /* val intent = Intent(mContext, WebView2::class.java)
         intent.putExtra("url", link)
         intent.putExtra("title", title)
         mContext.startActivity(intent)*/

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(mContext, Uri.parse(link))
    }
}