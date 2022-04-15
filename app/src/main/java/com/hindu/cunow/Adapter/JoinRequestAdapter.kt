package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class JoinRequestAdapter(private val mContext:Context,
                         private val mUser:List<UserModel>,
                         private val circleId:String):RecyclerView.Adapter<JoinRequestAdapter.ViewHolder>() {

                             inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                                 val profileImage: CircleImageView = itemView.findViewById(R.id.joiningRequests_profileImage)
                                 val fullName:TextView = itemView.findViewById(R.id.fullName_join)
                                 val accept:Button = itemView.findViewById(R.id.accept_request_join)
                                 val remove:Button = itemView.findViewById(R.id.reject_request_join)

                                 fun bind(list:UserModel){
                                     Glide.with(mContext).load(list.profileImage).into(profileImage)
                                     fullName.text = list.fullName
                                 }
                             }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.circle_joining_req_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mUser[position])
        holder.accept.setOnClickListener {
            addMembers(mUser[position].uid!!)
            removeRequest(mUser[position].uid!!)
        }

        holder.remove.setOnClickListener {
            removeRequest(mUser[position].uid!!)
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    private fun addMembers(userId: String){

        val ref = FirebaseDatabase.getInstance()
            .reference
            .child("Users")
            .child(userId)
            .child("Joined_Circles")
        val requestMap = HashMap<String,Any>()
        requestMap["JCId"] = circleId
        ref.child(circleId).updateChildren(requestMap)

        circleId.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Circle").child(it1.toString())
                .child("Members").child(userId)
                .setValue(true)
        }

        Toast.makeText(mContext,"Member Added", Toast.LENGTH_SHORT).show()

        /*val data = FirebaseDatabase.getInstance().reference.child("Circle")
            .child(circleId)
            .child("Members").child()*/
    }

    private fun removeRequest(userId: String){
        FirebaseDatabase.getInstance().reference
            .child("Circle")
            .child(circleId)
            .child("JoinRequests")
            .child(userId).removeValue()
    }
}