package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.Model.CourseModel
import com.hindu.cunow.R

class CourseAdapter(private val mContext:Context,
                    private val mCourse:List<CourseModel>):RecyclerView.Adapter<CourseAdapter.ViewHolder>() {


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        private val courseImage: ImageView = itemView.findViewById(R.id.courseImage) as ImageView
        private val courseName: TextView = itemView.findViewById(R.id.courseName) as TextView
        private val coursePlatform: TextView = itemView.findViewById(R.id.coursePlatform) as TextView

        fun bind(list:CourseModel){
            Glide.with(mContext).load(list.courseImage).into(courseImage)
            courseName.text = list.courseName
            coursePlatform.text = list.coursePlatform
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
}