package com.hindu.cunow.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import com.hindu.cunow.Activity.ReportPostActivity
import com.hindu.cunow.Model.CommunityReplyModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.more_option_confession.view.deleteConfession
import kotlinx.android.synthetic.main.more_option_confession.view.reportConfession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunityRepliesAdapter(private val mContext:Context,
                              private val mReplies:List<CommunityReplyModel>,private val communityId:String):RecyclerView.Adapter<CommunityRepliesAdapter.ViewHolder>() {


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        private val profileImage: CircleImageView = itemView.findViewById(R.id.profilePic_replies) as CircleImageView
        private val replyText: TextView = itemView.findViewById(R.id.replyText_community) as TextView
        private val userName: TextView = itemView.findViewById(R.id.userName_reply_community) as TextView
        private val downVote:ImageView = itemView.findViewById(R.id.downVote_reply)
        private val downVoteCount: TextView = itemView.findViewById(R.id.downVote_count_reply) as TextView
        private val upVote:ImageView = itemView.findViewById(R.id.upVote_reply)
        private val upVoteCount: TextView = itemView.findViewById(R.id.upVoteCount_reply) as TextView
        val moreOption:ImageView = itemView.findViewById(R.id.communityReplies_MO) as ImageView

        fun bind(list:CommunityReplyModel){
            replyText.text = list.replyText

            CoroutineScope(Dispatchers.IO).launch {
                launch { userInfo(userName,profileImage,list.replierId!!) }
                launch { isUpVote(list.replyId!!,upVote) }
                launch { isDownVote(list.replyId!!,downVote) }
                launch { upVoteCount(list.replyId!!,upVoteCount) }
                launch { downVoteCount(list.replyId!!,downVoteCount) }
            }
            upVote.setOnClickListener {
                upVote(upVote,list.replyId!!,upVoteCount)
            }
            downVote.setOnClickListener {
                downVote(downVote,list.replyId!!,downVoteCount)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.community_replies_item, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mReplies.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mReplies[position])
        val cList = mReplies[position]

        holder.moreOption.setOnClickListener {
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.more_option_confession, null)

            val dialogBuilder = AlertDialog.Builder(mContext)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.reportConfession.setOnClickListener {
                val intent = Intent(mContext, ReportPostActivity::class.java)
                intent.putExtra("postId",mReplies[position].replyId+"+communityRepliesId")
                mContext.startActivity(intent)
                alertDialog.dismiss()
            }

            if (cList.replierId != FirebaseAuth.getInstance().currentUser!!.uid){
                dialogView.deleteConfession.visibility = View.GONE
            }
            dialogView.deleteConfession.setOnClickListener {
                FirebaseDatabase.getInstance().reference.child("Community")
                    .child(cList.communityId!!)
                    .child(cList.replyId!!)
                    .removeValue()
                alertDialog.dismiss()
            }
        }
    }

    private fun isUpVote(id:String, button:ImageView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val voteRef = FirebaseDatabase.getInstance().reference
            .child("Community")
            .child(communityId)
            .child("replies")
            .child(id)
            .child("upVote")

        voteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()){
                    button.setImageResource(R.drawable.thumbsup_filled)
                    button.tag = "upVoted"
                }else{
                    button.tag = "upVote"
                    if (button.tag == "upVote"){
                        button.setImageResource(R.drawable.thumbsup_blank)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun upVote(button:ImageView,id:String,count: TextView){
        FirebaseDatabase.getInstance().reference
            .child("Community")
            .child(communityId)
            .child("replies")
            .child(id)
            .child("downVote")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .removeValue()

        if (button.tag == "upVote"){
            FirebaseDatabase.getInstance().reference
                .child("Community")
                .child(communityId)
                .child("replies")
                .child(id)
                .child("upVote")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)

        }else{
            FirebaseDatabase.getInstance().reference
                .child("Community")
                .child(communityId)
                .child("replies")
                .child(id)
                .child("upVote")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()
            button.tag = "upVote"
        }
        CoroutineScope(Dispatchers.IO).launch {
            launch { isUpVote(id,button) }
            launch { upVoteCount(id,count) }
        }
    }

    private fun isDownVote(id:String, button:ImageView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val voteRef = FirebaseDatabase.getInstance().reference
            .child("Community")
            .child(communityId)
            .child("replies")
            .child(id)
            .child("downVote")

        voteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()){
                    button.setImageResource(R.drawable.dislike)
                    button.tag = "downvoted"
                }else{
                    button.tag = "downVote"
                    if (button.tag == "downVote"){
                        button.setImageResource(R.drawable.dislike_blank)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun downVote(button:ImageView,id:String,count:TextView){
        FirebaseDatabase.getInstance().reference
            .child("Community")
            .child(communityId)
            .child("replies")
            .child(id)
            .child("upVote")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .removeValue()
        if (button.tag == "downVote"){
            FirebaseDatabase.getInstance().reference
                .child("Community")
                .child(communityId)
                .child("replies")
                .child(id)
                .child("downVote")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)

        }else{
            FirebaseDatabase.getInstance().reference
                .child("Community")
                .child(communityId)
                .child("replies")
                .child(id)
                .child("downVote")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()
            button.tag = "downVote"
        }
        CoroutineScope(Dispatchers.IO).launch {
            launch { isDownVote(id,button) }
            launch { downVoteCount(id,count) }
        }
    }

    private fun upVoteCount(id:String, count:TextView){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Community")
            .child(communityId)
            .child("replies")
            .child(id)
            .child("upVote")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val vote = snapshot.childrenCount.toInt()
                    count.text = vote.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun downVoteCount(id:String, count:TextView){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Community")
            .child(communityId)
            .child("replies")
            .child(id)
            .child("downVote")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val vote = snapshot.childrenCount.toInt()
                    count.text = vote.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private suspend fun userInfo(userName:TextView,profilePic:CircleImageView,userId:String){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users").child(userId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profilePic)
                    userName.text = data.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}