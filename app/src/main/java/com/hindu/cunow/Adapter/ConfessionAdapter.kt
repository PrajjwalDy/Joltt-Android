package com.hindu.cunow.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.HelpActivity
import com.hindu.cunow.Activity.ReportPostActivity
import com.hindu.cunow.Model.ConfessionModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.more_option_confession.view.*

class ConfessionAdapter(private val mContext: Context,
                        private val mConfession:List<ConfessionModel>,
): RecyclerView.Adapter<ConfessionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConfessionAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.confession_room_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConfessionAdapter.ViewHolder, position: Int) {
        val zoom = AnimationUtils.loadAnimation(mContext, R.anim.zoom)
        holder.bind(mConfession[position])
        val cList = mConfession[position]

        isLike(cList.confessionId!!,holder.like)
        totalLike(cList.confessionId!!,holder.totalLike)

        holder.like.setOnClickListener {
            like(holder.like,cList.confesserId!!,cList.confessionId!!,zoom)
        }

        holder.moreOption.setOnClickListener {
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.more_option_confession, null)

            val dialogBuilder = AlertDialog.Builder(mContext)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.reportConfession.setOnClickListener {
                val intent = Intent(mContext,ReportPostActivity::class.java)
                intent.putExtra("postId",mConfession[position].confessionId)
                mContext.startActivity(intent)
                alertDialog.dismiss()
            }

            if (cList.confesserId != FirebaseAuth.getInstance().currentUser!!.uid){
                dialogView.deleteConfession.visibility = View.GONE
            }
            dialogView.deleteConfession.setOnClickListener {
                FirebaseDatabase.getInstance().reference.child("Confession")
                    .child(cList.confessionId!!)
                    .removeValue()
                alertDialog.dismiss()
            }
        }

    }

    override fun getItemCount(): Int {
        return mConfession.size
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        //val comment:ImageView = itemView.findViewById(R.id.confessionComment) as ImageView
        val confessionImage:ImageView = itemView.findViewById(R.id.confessionImage) as ImageView
        val like:ImageView = itemView.findViewById(R.id.confessionLike) as ImageView
        val confessionText:TextView = itemView.findViewById(R.id.confessionText) as TextView
        val totalLike:TextView = itemView.findViewById(R.id.totalLike_confession) as TextView
        val moreOption:ImageView= itemView.findViewById(R.id.moreOptionConfession) as ImageView
        val imageCard:CardView = itemView.findViewById(R.id.imageCard)as CardView


        //member function
        fun bind(list:ConfessionModel){
            confessionText.text = list.confessionText
            //confessionCaption.text = list.captionConfession
            if (list.cImage != null){
                imageCard.visibility = View.VISIBLE
                confessionImage.visibility = View.VISIBLE
                Glide.with(mContext).load(list.cImage).into(confessionImage)
            }

        }

    }

    private fun like(likeButton:ImageView,publisherId:String,confessionId:String,zoom:Animation){

        likeButton.startAnimation(zoom)
        if (likeButton.tag == "Like"){
            FirebaseDatabase.getInstance().reference
                .child("ConfessionLike")
                .child(confessionId)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)
            addNotification(publisherId,confessionId)
        }else{
            FirebaseDatabase.getInstance().reference
                .child("ConfessionLike")
                .child(confessionId)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()
            likeButton.tag = "Like"
        }
        isLike(confessionId,likeButton)
    }

    private fun isLike(confessionId: String,likeButton: ImageView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val likeRef = FirebaseDatabase.getInstance().reference
            .child("ConfessionLike")
            .child(confessionId)
        likeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()){
                    likeButton.setImageResource(R.drawable.filled_heart)
                    likeButton.tag = "Liked"
                }else{
                    likeButton.tag = "Like"
                    if (likeButton.tag == "Like"){
                        likeButton.setImageResource(R.drawable.blank_heart)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addNotification(publisherId: String,confessionId: String){

    }

    private fun totalLike(confessionId: String,totalLike:TextView){
        val database = FirebaseDatabase.getInstance().reference.child("ConfessionLike")
            .child(confessionId)

        database.addValueEventListener(object:ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val count = snapshot.childrenCount.toInt()
                    if (count <= 1){
                        totalLike.text = count.toString()+"Like"
                    }else{
                        totalLike.text = count.toString()+"Likes"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}