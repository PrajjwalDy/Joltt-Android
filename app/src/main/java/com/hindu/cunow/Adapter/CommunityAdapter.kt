package com.hindu.cunow.Adapter

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
import com.hindu.cunow.Activity.CommunityRepliesActivity
import com.hindu.cunow.Model.CommunityModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.InvalidMarkException

class CommunityAdapter(private val mContext:Context,
                       private val mCommunity:List<CommunityModel>
                       ):RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.commnuity_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mCommunity.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mCommunity[position])
        val list = mCommunity[position]

        holder.upvote.setOnClickListener {
            upVote(holder.upvote,list.communityId!!,holder.upvoteCount)
        }
        holder.downVote.setOnClickListener {
            downVote(holder.downVote,list.communityId!!,holder.downVoteCount)
        }

    }

    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
         val profileImage: CircleImageView = itemView.findViewById(R.id.profileImage_comm) as CircleImageView
         private val userName: TextView = itemView.findViewById(R.id.userName_comm) as TextView
         private val communityCaption1: TextView = itemView.findViewById(R.id.query_article_TV1) as TextView
         private val communityCaption2: TextView = itemView.findViewById(R.id.query_article_TV2) as TextView
         val upvote:ImageView = itemView.findViewById(R.id.upVote) as ImageView
         val upvoteCount:TextView = itemView.findViewById(R.id.upVoteCount) as TextView
         val downVote:ImageView = itemView.findViewById(R.id.downVote) as ImageView
         val downVoteCount:TextView = itemView.findViewById(R.id.downVote_count) as TextView
         val replyButton:ImageView = itemView.findViewById(R.id.reply_img) as ImageView
         val replyCount:TextView = itemView.findViewById(R.id.reply_count) as TextView

        fun bind(list:CommunityModel){
            communityCaption1.text = list.communityCaption
            communityCaption2.text = list.communityCaption
            CoroutineScope(Dispatchers.IO).launch {
                userInfo(userName,profileImage,list.communityPublisher!!)
            }
            CoroutineScope(Dispatchers.IO).launch {
                launch { isUpVote(list.communityId!!,upvote) }
                launch { isDownVote(list.communityId!!,downVote) }
                launch { downVoteCount(list.communityId!!,downVoteCount) }
                launch { upVoteCount(list.communityId!!,upvoteCount) }
                launch { replyCount(list.communityId!!,replyCount) }
            }

            replyButton.setOnClickListener {
                val replyIntent = Intent(mContext,CommunityRepliesActivity::class.java)
                replyIntent.putExtra("communityId",list.communityId)
                mContext.startActivity(replyIntent)
            }
            communityCaption1.setOnClickListener {
                communityCaption1.visibility = View.GONE
                communityCaption2.visibility = View.VISIBLE
            }

            communityCaption2.setOnClickListener {
                communityCaption2.visibility = View.GONE
                communityCaption1.visibility = View.VISIBLE
            }


        }
    }

    private fun isUpVote(id:String, button:ImageView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val voteRef = FirebaseDatabase.getInstance().reference
            .child("Community")
            .child(id)
            .child("upVote")

        voteRef.addValueEventListener(object :ValueEventListener{
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
            .child(id)
            .child("downVote")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .removeValue()

        if (button.tag == "upVote"){
            FirebaseDatabase.getInstance().reference
                .child("Community")
                .child(id)
                .child("upVote")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)

        }else{
            FirebaseDatabase.getInstance().reference
                .child("Community")
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
            .child(id)
            .child("downVote")

        voteRef.addValueEventListener(object :ValueEventListener{
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
            .child(id)
            .child("upVote")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .removeValue()
        if (button.tag == "downVote"){
            FirebaseDatabase.getInstance().reference
                .child("Community")
                .child(id)
                .child("downVote")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)

        }else{
            FirebaseDatabase.getInstance().reference
                .child("Community")
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
            .child(id)
            .child("upVote")
        ref.addValueEventListener(object :ValueEventListener{
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
            .child(id)
            .child("downVote")
        ref.addValueEventListener(object :ValueEventListener{
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

    private fun userInfo(userName:TextView,profilePic:CircleImageView,userId:String){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users").child(userId)
        ref.addValueEventListener(object :ValueEventListener{
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

    private fun replyCount(id: String,count: TextView){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Community")
            .child(id)
            .child("replies")
        ref.addValueEventListener(object :ValueEventListener{
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
}