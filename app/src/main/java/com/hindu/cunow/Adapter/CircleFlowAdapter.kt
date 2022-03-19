package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.CircleFlowModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class CircleFlowAdapter(private val mContext:Context,
                        private val mCircle:List<CircleFlowModel>):RecyclerView.Adapter<CircleFlowAdapter.ViewHolder>(){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CircleFlowAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.circle_flow_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CircleFlowAdapter.ViewHolder, position: Int) {
        holder.bind(mCircle[position])
        loadSender(mCircle[position].circleFlowSender!!,holder.profileImage_CF)
    }

    override fun getItemCount(): Int {
        return mCircle.size
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val circleFlowImage:ImageView = itemView.findViewById(R.id.circleFlow_Image) as ImageView
        val circleFlowText:TextView = itemView.findViewById(R.id.circleFlow_text) as TextView
        val profileImage_CF:CircleImageView = itemView.findViewById(R.id.profileImage_CF) as CircleImageView

        fun bind(list:CircleFlowModel){
            if (list.circleFlowText == null){
                circleFlowText.visibility = View.GONE
            }else{
                circleFlowText.text = list.circleFlowText
            }

            if (list.circleFlowImg != null){
                circleFlowImage.visibility = View.VISIBLE
                Glide.with(mContext).load(list.circleFlowImg).into(circleFlowImage)
            }else{
                circleFlowImage.visibility = View.GONE
            }
        }
    }

    private fun loadSender(sederId:String, profileImage:ImageView){
        val userDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(sederId)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}