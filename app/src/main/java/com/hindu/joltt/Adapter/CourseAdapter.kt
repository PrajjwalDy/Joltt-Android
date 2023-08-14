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
import com.hindu.joltt.Model.CourseModel

class CourseAdapter(private val mContext:Context,
                    private val mCourse:List<CourseModel>):RecyclerView.Adapter<CourseAdapter.ViewHolder>() {


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        private val courseImage: ImageView = itemView.findViewById(R.id.courseImage) as ImageView
        private val courseName: TextView = itemView.findViewById(R.id.courseName) as TextView
        private val coursePlatform: TextView = itemView.findViewById(R.id.coursePlatform) as TextView
        private val courseDuration: TextView = itemView.findViewById(R.id.courseDuration) as TextView
        private val courseDescription: TextView = itemView.findViewById(R.id.courseDescription) as TextView

        fun bind(list: CourseModel){
            Glide.with(mContext).load(list.courseImage).into(courseImage)
            courseName.text = list.courseName
            coursePlatform.text = list.coursePlatform
            courseDescription.text = list.courseDescription
            courseDuration.text = list.courseDuration

            itemView.setOnClickListener {

                openLink(list.courseLink!!)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.courses_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mCourse.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mCourse[position])

    }

    private fun openLink(link:String){
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(mContext, Uri.parse(link))
    }
}