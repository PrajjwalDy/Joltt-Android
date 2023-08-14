package com.hindu.joltt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Model.MyAppModel
import com.hindu.joltt.Model.ProjectModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAppAdapter(private val mContext:Context,
                   private val mApp:List<MyAppModel>):RecyclerView.Adapter<MyAppAdapter.ViewHolder>() {

                       inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                           private val projectImage:ImageView= itemView.findViewById(R.id.projectImage_myapp) as ImageView
                           private val projectName: TextView = itemView.findViewById(R.id.projectName_myapp) as TextView
                           private val statusButton: AppCompatButton = itemView.findViewById(R.id.application_status_btn) as AppCompatButton

                           fun bind(list:MyAppModel){
                               CoroutineScope(Dispatchers.IO).launch {
                                   launch { getProjectDetails(projectName,projectImage,list.projectID!!) }
                                   launch { applicationStatus(list.projectID!!,list.applicationId!!,statusButton) }
                               }
                           }
                       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.myapplication_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mApp.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mApp[position])
    }

    private fun getProjectDetails(projectName:TextView,projectImage:ImageView,projectId:String){
        val ref = FirebaseDatabase.getInstance().reference.child("Projects")
            .child(projectId)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(ProjectModel::class.java)
                    projectName.text = data!!.projectName
                    Glide.with(mContext).load(data.projectImage).into(projectImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })
    }

    private fun applicationStatus(projectId:String,applicationId:String,button: AppCompatButton){
        val ref = FirebaseDatabase.getInstance().reference
            .child("ProjectApplications")
            .child(applicationId)
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(MyAppModel::class.java)
                    if (data!!.appStatus == 0){
                        button.setBackgroundResource(R.drawable.status_button_bg_pending)
                        button.setTextColor(ContextCompat.getColor(mContext,R.color.yellow))
                    }else if(data!!.appStatus == 1){
                        button.setBackgroundResource(R.drawable.status_button_bg_rejected)
                        button.setTextColor(ContextCompat.getColor(mContext,R.color.reddish))
                    }else if (data!!.appStatus == 2){
                        button.setBackgroundResource(R.drawable.status_button_bg_approved)
                        button.setTextColor(ContextCompat.getColor(mContext,R.color.red))
                    }else{
                        button.setBackgroundResource(R.drawable.status_button_bg_pending)
                        button.setTextColor(ContextCompat.getColor(mContext,R.color.yellow))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })
    }
}