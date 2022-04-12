package com.hindu.cunow.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.Image
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.CircleDetailsActivity
import com.hindu.cunow.MainActivity
import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.R
import org.w3c.dom.Text

class CircleAdapter(private val mContext:Context,
                    private val mCircle:List<CircleModel>):RecyclerView.Adapter<CircleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.circle_layout,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onBindViewHolder(holder: CircleAdapter.ViewHolder, position: Int) {
        holder.bind(mCircle[position])

        checkJoining(mCircle[position].circleId!!,holder.joinButton)

        holder.joinButton.setOnClickListener{
            if (mCircle[position].privateC){
                holder.joinButton.text = "Requested"
                requestToJoin(mCircle[position].circleId!!)
            }else{
                holder.joinButton.text = "Joined"
                joinCircle(mCircle[position].circleId!!)
            }
        }

        if (mCircle[position].admin == FirebaseAuth.getInstance().currentUser!!.uid){
            holder.joinButton.visibility = View.GONE
            //holder.itemView.visibility = View.GONE
        }


        holder.iconImage.setOnClickListener {
            val intent = Intent(mContext,CircleDetailsActivity::class.java)
            intent.putExtra("circleId",mCircle[position].circleId)
            intent.putExtra("admin",mCircle[position].admin)
            mContext.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return mCircle.size
    }

    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
        val iconImage:ImageView = itemView.findViewById(R.id.circle_icon_display) as ImageView
        val circleName:TextView = itemView.findViewById(R.id.circle_name_display) as TextView
        val circleDescription:TextView = itemView.findViewById(R.id.circle_description_display) as TextView
        val totalMembers:TextView = itemView.findViewById(R.id.total_members_display) as TextView
        val joinButton:Button = itemView.findViewById(R.id.join) as Button


        fun bind(list: CircleModel){
            circleName.text = list.circleName
            circleDescription.text = list.circle_description
            Glide.with(mContext).load(list.icon).into(iconImage)
        }

    }

    private fun joinCircle(circleId:String) {

            FirebaseDatabase.getInstance().reference
                .child("Circle").child(circleId)
                .child("Members").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)
        val ref = FirebaseDatabase.getInstance()
            .reference
            .child("Users")
            .child(FirebaseAuth
                .getInstance()
                .currentUser!!.uid)
            .child("Joined_Circles")
        val requestMap = HashMap<String,Any>()
        requestMap["JCId"] = circleId
        ref.child(circleId).updateChildren(requestMap)
    }

    private fun requestToJoin(circleId: String){
        FirebaseDatabase.getInstance().reference
            .child("Circle").child(circleId)
            .child("JoinRequests").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(true)

    }

    private fun checkJoining(circleId: String,join:Button){
        val circleData = FirebaseDatabase.getInstance().reference
            .child("Circle")
            .child(circleId)
            .child("Members")

        circleData.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(FirebaseAuth.getInstance().currentUser!!.uid).exists()){
                    join.text = "Joined"
                }else{
                    checkRequested(circleId,join)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkRequested(circleId: String,join: Button){
        val circleData = FirebaseDatabase.getInstance().reference
            .child("Circle")
            .child(circleId)
            .child("JoinRequests")

        circleData.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(FirebaseAuth.getInstance().currentUser!!.uid).exists()){
                    join.text = "Requested"
                }else{
                    join.text = "Join"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



}