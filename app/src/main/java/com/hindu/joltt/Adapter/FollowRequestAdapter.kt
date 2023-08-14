package com.hindu.joltt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Model.RequestModel
import com.hindu.joltt.Model.UserModel
import de.hdodenhof.circleimageview.CircleImageView

class FollowRequestAdapter(private val mContext:Context,
                           private val mUser:List<RequestModel>):RecyclerView.Adapter<FollowRequestAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowRequestAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.follow_request_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowRequestAdapter.ViewHolder, position: Int) {

        requesterDetails(mUser[position].requesterId!!,holder.profileImage,holder.name)
        holder.acceptButton.setOnClickListener {
            acceptRequest(mUser[position].requesterId!!,holder.acceptButton)
        }
        holder.rejectButton.setOnClickListener {
            rejectRequest(mUser[position].requesterId!!)
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
        val profileImage: CircleImageView = itemView.findViewById(R.id.profileImage_requester) as CircleImageView
        val name:TextView = itemView.findViewById(R.id.fullName_Requester) as TextView
        val acceptButton:Button = itemView.findViewById(R.id.accept_request) as Button
        val rejectButton:Button = itemView.findViewById(R.id.reject_request) as Button
    }

    private fun requesterDetails(requesterId:String,profileImage:ImageView,name:TextView){
        val dataRef = FirebaseDatabase.getInstance().reference.child("Users").child(requesterId)
        dataRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val rData = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(rData!!.profileImage).into(profileImage)
                    name.text = rData.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun acceptRequest(requesterId: String,acceptButton: Button){

        requesterId.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(it1.toString())
                .child("Following")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)
        }

        FirebaseAuth.getInstance().currentUser!!.uid.let { it1->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Followers").child(requesterId)
                .setValue(true)
        }

        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Requesters")
            .child(requesterId)
            .removeValue()

        acceptButton.text = "Accepted"

    }
    private fun rejectRequest(requesterId: String){
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Requesters")
            .child(requesterId)
            .removeValue()
    }

}