package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.HashTagModel
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class HashTagAdapter(private val mContext:Context,private val mHash:List<HashTagModel>)
    :RecyclerView.Adapter<HashTagAdapter.ViewHolder>() {


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        private val tagName:TextView = itemView.findViewById(R.id.tagName) as TextView
        val postCount:TextView = itemView.findViewById(R.id.postCount) as TextView

         fun bind(list:HashTagModel){
            tagName.text = list.tagName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.tag_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mTag = mHash[position]
        holder.bind(mHash[position])
        CoroutineScope(Dispatchers.IO).launch {
            getPostCount(mTag.tagName!!,holder.postCount)
        }
        holder.itemView.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("hashtag",mHash[position].tagName)
            pref.apply()

            Navigation.findNavController(holder.itemView).navigate(R.id.action_navigation_dashboard_to_fullPostView2)
        }
    }

    override fun getItemCount(): Int {
        return mHash.size
    }

    private fun getPostCount(tag:String, postCount:TextView){
        val key = tag.removeRange(0,1)
        val dataRef = FirebaseDatabase.getInstance()
            .reference.child("hashtags")
            .child(key)
            .child("posts")

        dataRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    postCount.text = snapshot.childrenCount.toString() +" posts"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}