package com.hindu.cunow.Fragments.Internship

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.Activity.WebViewActivity
import com.hindu.cunow.Model.InternshipModel
import com.hindu.cunow.R

class InternshipAdapter(private val mContext: Context,
                        private val mInternship:List<InternshipModel>): RecyclerView.Adapter<InternshipAdapter.ViewHolder>(){

                            inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                                val intern_name: TextView = itemView.findViewById(R.id.intern_title) as TextView
                                val offered: TextView = itemView.findViewById(R.id.tv_offeredBy) as TextView
                                val location: TextView = itemView.findViewById(R.id.tv_iLocation) as TextView
                                val startDate:TextView = itemView.findViewById(R.id.tv_startDate) as TextView
                                val duration:TextView = itemView.findViewById(R.id.tv_duration) as TextView
                                val stipend:TextView = itemView.findViewById(R.id.tv_startStipend) as TextView
                                val type:TextView = itemView.findViewById(R.id.tv_iType) as TextView
                                val posted:TextView = itemView.findViewById(R.id.tv_iPosted) as TextView




                                fun bind(list:InternshipModel){
                                    intern_name.text = list.internTitle
                                    offered.text = list.iOffered
                                    location.text  = list.iLocation
                                    startDate.text = list.iStart
                                    duration.text = list.iDuration
                                    stipend.text = list.iStipend
                                    type.text = list.iType
                                    posted.text = "posted "+list.iPosted

                                    itemView.setOnClickListener {
                                        openLink(list.iLink!!,list.internTitle!!)
                                    }
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

    private fun openLink(link:String,title:String){
        val intent = Intent(mContext, WebViewActivity::class.java)
        intent.putExtra("url", link)
        intent.putExtra("title", title)
        mContext.startActivity(intent)


//        intent.data = Uri.parse(link)
//        mContext.startActivity(intent)

    }
}