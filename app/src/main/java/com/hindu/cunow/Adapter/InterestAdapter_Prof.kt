package com.hindu.cunow.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.Model.InterestModel
import com.hindu.cunow.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.select.Evaluator.Id

class InterestAdapter_Prof(private val mContext: Context,
                           private val mInterest:List<InterestModel>):RecyclerView.Adapter<InterestAdapter_Prof.ViewHolder>() {

                          inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
                               val interest: TextView = itemView.findViewById(R.id.interestName_profile) as TextView
                               //val card:CardView = itemView.findViewById(R.id.interestCV) as CardView
                              fun bind(list:InterestModel){
                                  interest.text = list.interestTV
                              }
                          }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.interest_layout_profile, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mInterest.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mInterest[position])
    }
}