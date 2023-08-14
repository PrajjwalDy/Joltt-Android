package com.hindu.joltt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Model.UserModel
import de.hdodenhof.circleimageview.CircleImageView

class CircleMemberAdapter(
    private val mContext: Context,
    private val mUser: List<UserModel>,
    private val circleId: String,
    private val admin: String
) : RecyclerView.Adapter<CircleMemberAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: CircleImageView =
            itemView.findViewById(R.id.profileImage_member) as CircleImageView
        val fullName: TextView = itemView.findViewById(R.id.memberName) as TextView
        val remove: ImageView = itemView.findViewById(R.id.removeMember) as ImageView
        val addMember: ImageView = itemView.findViewById(R.id.addMembers_img) as ImageView

        fun bind(list: UserModel) {
            Glide.with(mContext).load(list.profileImage).into(profileImage)
            fullName.text = list.fullName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.circle_members_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mUser[position])

        isJoined(mUser[position].uid!!, holder.remove, holder.addMember)

        if (admin == FirebaseAuth.getInstance().currentUser!!.uid) {
            holder.remove.visibility = View.VISIBLE
            holder.addMember.visibility = View.VISIBLE
        } else {
            holder.remove.visibility = View.GONE
            holder.addMember.visibility = View.GONE
        }
        //holder.remove.visibility = View.GONE

        holder.remove.setOnClickListener {
            removeMember(mUser[position].uid!!)
        }

        holder.addMember.setOnClickListener {
            if (mUser[position].private) {
                Toast.makeText(mContext, "You cannot add private accounts", Toast.LENGTH_SHORT)
                    .show()
            } else {
                addMembers(mUser[position].uid!!)
            }

        }


    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    private fun removeMember(userId: String) {
        FirebaseDatabase.getInstance().reference.child("Circle")
            .child(circleId).child("Members").child(userId).removeValue()
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(userId).child("Joined_Circles").child(circleId)
            .removeValue()

        Toast.makeText(mContext, "Member removed successfully", Toast.LENGTH_SHORT).show()
    }

    private fun isJoined(userId: String, remove: ImageView, addMember: ImageView) {
        val circleData = FirebaseDatabase.getInstance().reference.child("Circle")
            .child(circleId).child("Members")

        circleData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(userId).exists()) {
                    //remove.visibility = View.VISIBLE
                    addMember.visibility = View.GONE
                } else {
                    remove.visibility = View.GONE
                    addMember.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addMembers(userId: String) {

        circleId.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Circle").child(it1.toString())
                .child("Members").child(userId)
                .setValue(true)
        }
        FirebaseDatabase.getInstance().reference
            .child("Circle").child(circleId)
            .child("Members").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(true)
        val ref = FirebaseDatabase.getInstance()
            .reference
            .child("Users")
            .child(
                FirebaseAuth
                    .getInstance()
                    .currentUser!!.uid
            )
            .child("Joined_Circles")
        val requestMap = HashMap<String, Any>()
        requestMap["JCId"] = circleId
        ref.child(circleId).updateChildren(requestMap)

        Toast.makeText(mContext, "Member Added", Toast.LENGTH_SHORT).show()

        /*val data = FirebaseDatabase.getInstance().reference.child("Circle")
            .child(circleId)
            .child("Members").child()*/
    }

}