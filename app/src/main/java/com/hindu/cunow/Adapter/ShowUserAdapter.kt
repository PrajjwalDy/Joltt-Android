package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class ShowUserAdapter(private val mContext:Context,
                      private val mUser: List<UserModel>):RecyclerView.Adapter<ShowUserAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val profileImage: CircleImageView = itemView.findViewById(R.id.profileImage_showUser) as CircleImageView
        val fullName: TextView = itemView.findViewById(R.id.fullName_showUser) as TextView
        val bio: TextView = itemView.findViewById(R.id.userBio_showUser) as TextView
        val verification: CircleImageView = itemView.findViewById(R.id.userVerified_showUser) as CircleImageView

        fun bind(list:UserModel){
            Glide.with(mContext).load(list.profileImage).into(profileImage)
            fullName.text = list.fullName
            bio.text  = list.bio
            if (list.verification){
                verification.visibility = View.VISIBLE
            }else{
                verification.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.show_user_item, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mUser[position])

        holder.itemView.setOnClickListener {

            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("uid",mUser[position].uid)
            pref.apply()
            Navigation.findNavController(holder.itemView).navigate(R.id.action_showUserFragment_to_userProfile)
        }

        if (mUser[position].uid == FirebaseAuth.getInstance().currentUser!!.uid){
            holder.itemView.visibility = View.GONE
        }else{
            holder.itemView.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }
}