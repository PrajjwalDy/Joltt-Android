package com.hindu.joltt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.hindu.joltt.Model.CircleFlowModel
import com.hindu.joltt.Model.UserModel
import de.hdodenhof.circleimageview.CircleImageView

class CircleFlowAdapter(private val mContext:Context,
                        private val mCircle:List<CircleFlowModel>):RecyclerView.Adapter<CircleFlowAdapter.ViewHolder>(){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        position: Int
    ): CircleFlowAdapter.ViewHolder {
        return if (position ==1)
        {
            val view = LayoutInflater.from(mContext).inflate(R.layout.circle_flow_item, parent, false)
            ViewHolder(view)
        }
        else
        {
            val view = LayoutInflater.from(mContext).inflate(R.layout.circle_flow_second, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: CircleFlowAdapter.ViewHolder, position: Int) {
        holder.bind(mCircle[position])
        loadSender(mCircle[position].circleFlowSender!!,holder.profileImage!!,holder.fullName!!)

    }

    override fun getItemCount(): Int {
        return mCircle.size
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val circleFlowImage:ImageView? = itemView.findViewById(R.id.circleFlow_Image) as? ImageView
        val circleFlowText:TextView? = itemView.findViewById(R.id.circleFlow_text) as? TextView
        val profileImage:CircleImageView? = itemView.findViewById(R.id.profileImage_CF) as? CircleImageView
        val circleFlowImage_sender:ImageView? = itemView.findViewById(R.id.circleFlow_Image_sender) as? ImageView
        val fullName:TextView? = itemView.findViewById(R.id.fullNameCF)

        fun bind(list: CircleFlowModel){
                circleFlowText!!.text = list.circleFlowText

            if (list.messageImage){
                if (!list.circleFlowSender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                    circleFlowImage!!.visibility = View.VISIBLE
                    Glide.with(mContext).load(list.circleFlowImg).into(circleFlowImage)
                    circleFlowText.visibility = View.GONE
                }else if (list.circleFlowSender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                    circleFlowText.visibility = View.GONE
                    circleFlowImage_sender!!.visibility = View.VISIBLE
                    Glide.with(mContext).load(list.circleFlowImg).into(circleFlowImage_sender)
                }
            }else{
                if (!list.circleFlowSender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                    circleFlowImage!!.visibility = View.GONE
                }else if (list.circleFlowSender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                    circleFlowImage_sender!!.visibility = View.GONE
                }
            }

        }
    }

    private fun loadSender(sederId:String,profileImage:ImageView,fullName:TextView){
        val userDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(sederId)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profileImage)
                    fullName.text = data.fullName

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun getItemViewType(position: Int): Int {
        return if (mCircle[position].circleFlowSender.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
            0
        }else{
            1
        }

    }


}