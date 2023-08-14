package com.hindu.joltt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Model.MyAppModel
import com.hindu.joltt.Model.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApplicantAdapter(private val mContext:Context,
                       private val mApplicant:List<MyAppModel>):RecyclerView.Adapter<ApplicantAdapter.ViewHolder>() {

                           inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                               private val profileImage:CircleImageView = itemView.findViewById(R.id.userProfileImage_pr) as CircleImageView
                               private val userName:TextView = itemView.findViewById(R.id.userName_pr) as TextView
                               private val reject:CircleImageView = itemView.findViewById(R.id.reject_request) as CircleImageView
                               private val approve:CircleImageView = itemView.findViewById(R.id.approve_request) as CircleImageView

                               fun bind(list:MyAppModel){
                                   CoroutineScope(Dispatchers.IO).launch {
                                       userInfo(profileImage,userName,list.applicantId!!)
                                   }
                                   reject.setOnClickListener {
                                       rejectRequest(list.projectID!!,list.applicationId!!)
                                   }
                                   approve.setOnClickListener {
                                       approveRequest(list.projectID!!,list.applicationId!!)
                                   }
                               }

                           }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.join_request_project_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mApplicant.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mApplicant[position])
    }

    private fun userInfo(profile:CircleImageView,name:TextView,userId:String){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profile)
                    name.text = data.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })

    }

    private fun rejectRequest(projectId:String,applicationId: String){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Projects")
            .child(projectId)
            .child("JoiningRequests")
            .child(applicationId)
        val hashMap = HashMap<String,Any>()
        hashMap["appStatus"] = 1
        ref.updateChildren(hashMap)
        Toast.makeText(mContext,"Requested rejected",Toast.LENGTH_SHORT).show()
    }

    private fun approveRequest(projectId:String,applicationId:String){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Projects")
            .child(projectId)
            .child("JoiningRequests")
            .child(applicationId)
        val hashMap = HashMap<String,Any>()
        hashMap["appStatus"] = 2
        ref.updateChildren(hashMap)
        Toast.makeText(mContext,"Requested rejected",Toast.LENGTH_SHORT).show()
    }

}