package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
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
import com.hindu.cunow.Model.RequestModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
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
            acceptRequest(mUser[position].requesterId!!)
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }
    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
        val profileImage: CircleImageView = itemView.findViewById(R.id.profileImage_requester) as CircleImageView
        val name:TextView = itemView.findViewById(R.id.requester_verification) as TextView
        val acceptButton:Button = itemView.findViewById(R.id.accept_request) as Button
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
                TODO("Not yet implemented")
            }

        })

    }

    private fun acceptRequest(requesterId: String){

        FirebaseAuth.getInstance().currentUser!!.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following").child(requesterId)
                .setValue(true)
        }

        FirebaseAuth.getInstance().currentUser!!.uid.let { it1->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(requesterId)
                .child("Followers").child(it1.toString())
                .setValue(true)
        }

    }

}