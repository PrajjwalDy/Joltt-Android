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
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val mContext:Context,
                  private val mUser:List<UserModel>):RecyclerView.Adapter<UserAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        holder.bind(mUser[position])
        holder.itemView.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("uid",mUser[position].uid)
            pref.apply()

            Navigation.findNavController(holder.itemView).navigate(R.id.action_navigation_dashboard_to_userProfile)
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val profileImage: CircleImageView = itemView.findViewById(R.id.user_profileImage) as CircleImageView
        val fullName:TextView = itemView.findViewById(R.id.search_fullName) as TextView
        val userVerified: CircleImageView = itemView.findViewById(R.id.user_verified) as CircleImageView

        fun bind(list: UserModel){
            Glide.with(mContext).load(list.profileImage).into(profileImage)
            fullName.text = list.fullName
            if (list.verification){
                userVerified.visibility = View.VISIBLE
            }

        }

    }



}