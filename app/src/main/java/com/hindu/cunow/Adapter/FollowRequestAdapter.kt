package com.hindu.cunow.Adapter

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
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
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: FollowRequestAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
        val profileImage: CircleImageView = itemView.findViewById(R.id.profileImage_requester) as CircleImageView
        val name:TextView = itemView.findViewById(R.id.requester_verification) as TextView
    }

    private fun requesterDetails(reqeusterId:String){
        val dataRef = FirebaseDatabase.getInstance().reference.child("Users").child(reqeusterId)


    }

}