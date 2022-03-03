package com.hindu.cunow.Adapter

import android.content.Context
import android.media.Image
import android.text.Layout
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.R
import org.w3c.dom.Text

class CircleAdapter(private val mContext:Context,
                    private val mCircle:List<CircleModel>):RecyclerView.Adapter<CircleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.circle_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CircleAdapter.ViewHolder, position: Int) {
        holder.bind(mCircle[position])
        holder.joinButton.setOnClickListener{
            if (mCircle[position].parivate){
                requestToJoin(mCircle[position].circleId!!)
                holder.joinButton.text = "Requested"
            }else{
                holder.joinButton.text = "Joined"
                joinCircle(mCircle[position].circleId!!)
            }
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
            circleDescription.text = list.cirlce_description
            Glide.with(mContext).load(list.icon).into(iconImage)

        }

    }

    private fun joinCircle(circleId:String) {

            FirebaseDatabase.getInstance().reference
                .child("Circle").child(circleId)
                .child("Members").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)


    }

    private fun requestToJoin(circleId: String){
        FirebaseDatabase.getInstance().reference
            .child("Circle").child(circleId)
            .child("Requests").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(true)

    }



}