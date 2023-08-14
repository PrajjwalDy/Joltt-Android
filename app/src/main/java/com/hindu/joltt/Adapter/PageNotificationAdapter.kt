package com.hindu.joltt.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Fragments.Pages.PageDetailsActivity
import com.hindu.joltt.Model.PageNotificationModel
import com.hindu.joltt.Model.PostModel
import com.hindu.joltt.Model.UserModel
import de.hdodenhof.circleimageview.CircleImageView

class PageNotificationAdapter(private var mContext: Context,
                              private var mList:List<PageNotificationModel>):RecyclerView.Adapter<PageNotificationAdapter.ViewHolder>() {

                                  inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
                                      val postImage: ImageView = itemView.findViewById(R.id.postImageNotification_page) as ImageView
                                      val profileImage: CircleImageView =itemView.findViewById(R.id.notification_profileImage_page) as CircleImageView
                                      val userName: TextView = itemView.findViewById(R.id.usernameNotification_page) as TextView
                                      val notificationText: TextView = itemView.findViewById(R.id.notificationText_page) as TextView

                                      fun bind(list: PageNotificationModel){
                                          notificationText.text = list.nText
                                          loadPostImage(list.postImage,postImage)
                                          loadNotifier(list.notifier,profileImage,userName)
                                      }
                                  }

    private fun loadNotifier(notifier: String?, profileImage: CircleImageView, userName: TextView) {
        val userData = FirebaseDatabase.getInstance().reference.child("Users")
            .child(notifier!!)
        userData.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profileImage)
                    userName.text = data.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadPostImage(postImage: String?, postImage1: ImageView) {
        val data = FirebaseDatabase.getInstance().reference.child("Post")
            .child(postImage!!)
        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val dataset = snapshot.getValue(PostModel::class.java)
                    Glide.with(mContext).load(dataset!!.image).into(postImage1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.page_notification_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nf = mList[position]
        holder.bind(nf)

        holder.itemView.setOnClickListener {
            if (nf.iPost){
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("postId",nf.postImage)
                pref.apply()
                Navigation
                    .findNavController(holder.itemView)
                    .navigate(R.id
                        .action_pageNotificationFragment_to_fullPostView)
            }else {
                val intent = Intent(mContext, PageDetailsActivity::class.java)
                intent.putExtra("pageId",nf.pageId)
                intent.putExtra("pageAdmin",FirebaseAuth.getInstance().currentUser!!.uid)
                mContext.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}