package com.hindu.joltt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.R
import com.hindu.joltt.Model.UserModel
import de.hdodenhof.circleimageview.CircleImageView

class ExploreUserAdapter(private val mContext:Context,
                         private val mUser:List<UserModel>):RecyclerView.Adapter<ExploreUserAdapter.ViewHolder>(){


                             inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                                 val profileImage:CircleImageView = itemView.findViewById(R.id.profileImage_explore) as CircleImageView
                                 val fullName:TextView = itemView.findViewById(R.id.fullName_explore) as TextView
                                 val bio:TextView = itemView.findViewById(R.id.userBio_explore) as TextView
                                 val verification:CircleImageView = itemView.findViewById(R.id.userVerified_explore) as CircleImageView

                                 fun bind(list: UserModel){
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
        val view = LayoutInflater.from(mContext).inflate(R.layout.explore_people_layout, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mUser[position])
        holder.itemView.setOnClickListener {

            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("uid",mUser[position].uid)
            pref.apply()
            Navigation.findNavController(holder.itemView).navigate(R.id.action_peopleFragment_to_userProfile)
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }
}