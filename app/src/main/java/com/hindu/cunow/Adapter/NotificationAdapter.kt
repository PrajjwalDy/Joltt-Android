package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.NotificationModel
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationAdapter(private val nContext:Context,
                          private  val nList:List<NotificationModel>):RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.ViewHolder {
        val view = LayoutInflater.from(nContext).inflate(R.layout.notification_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        val notification = nList[position]
        holder.bind(nList[position])


        holder.itemView.setOnClickListener {
            if (notification.postN){
                val pref = nContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("postId",nList[position].postID)
                pref.apply()

                Navigation.
                findNavController(holder
                    .itemView).navigate(R.id
                    .action_navigation_notifications_to_fullPostView)

            }else if (notification.pageN){
                val pref = nContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("pageId",nList[position].pageId)
                pref.apply()

                Navigation.
                findNavController(holder
                    .itemView).navigate(R.id
                    .action_navigation_notifications_to_pageNotificationFragment)


                /*val intent = Intent(nContext,PageDetailsActivity::class.java)
                intent.putExtra("pageId",notification.postID)
                intent.putExtra("pageAdmin",FirebaseAuth.getInstance().currentUser!!.uid)
                nContext.startActivity(intent)*/

            }else if (notification.confession){
                val pref = nContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("confessionId",nList[position].postID)
                pref.apply()
                Navigation.findNavController(holder.itemView).navigate(R.id.action_navigation_notifications_to_viewConfessionFragment)
            } else{
                val pref = nContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("uid",nList[position].notifierId)
                pref.apply()

                Navigation.findNavController(holder.itemView).navigate(R.id.action_navigation_notifications_to_userProfile)
            }
        }
    }

    override fun getItemCount(): Int {
        return nList.size
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val postImage: ImageView = itemView.findViewById(R.id.postImageNotification) as ImageView
        val profileImage:CircleImageView =itemView.findViewById(R.id.notification_profileImage) as CircleImageView
        val userName:TextView = itemView.findViewById(R.id.usernameNotification) as TextView
        val notificationText:TextView = itemView.findViewById(R.id.notificationText) as TextView

        fun bind(list: NotificationModel){

            if(list.pageN){
                userName.visibility = View.GONE
                profileImage.visibility = View.GONE
                postImage.visibility = View.GONE
            }else{
                userName.visibility = View.VISIBLE
                profileImage.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    loadNotifier(list.notifierId!!,profileImage,userName)
                }
            }

            if(list.confession){
                postImage.visibility = View.GONE
            }else{
                postImage.visibility = View.VISIBLE
            }
            notificationText.text = list.notificationText
            CoroutineScope(Dispatchers.IO).launch {
                loadPostImage(list.postID!!,postImage)
            }
        }
    }
    private suspend fun loadPostImage(postId:String,postImage:ImageView){
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Post")
            .child(postId)
        postRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val data  = snapshot.getValue(PostModel::class.java)
                    Glide.with(nContext).load(data!!.image).into(postImage)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }

    private suspend fun loadNotifier(notifierId:String,profileImage:CircleImageView,userName:TextView){
//        val progressDialog = nContext.let { Dialog(it) }
//        progressDialog.setContentView(R.layout.profile_dropdown_menu)
//        progressDialog.show()
        val userDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(notifierId)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(nContext).load(data!!.profileImage).into(profileImage)
                    userName.text = data.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
    })
        //progressDialog.dismiss()
    }

}