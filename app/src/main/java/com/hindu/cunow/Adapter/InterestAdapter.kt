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

class InterestAdapter(private val mContext: Context,
                      private val mInterest:List<InterestModel>):RecyclerView.Adapter<InterestAdapter.ViewHolder>() {

    var checker = "click"
                          inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
                               val interest: TextView = itemView.findViewById(R.id.interestName) as TextView
                               val card:CardView = itemView.findViewById(R.id.interestCV) as CardView
                              fun bind(list:InterestModel){
                                  interest.text = list.interestTV
                              }
                          }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.interest_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mInterest.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mInterest[position])
        holder.itemView.setOnClickListener {
                if (checker =="click"){
                    holder.card.setCardBackgroundColor(Color.parseColor("#FF3A63"))
                    holder.interest.setTextColor(Color.WHITE)
                    checker = "clicked"
                    CoroutineScope(Dispatchers.IO).launch {
                        addInterest(mInterest[position].inteID!!)
                    }
                }else{
                    holder.card.setCardBackgroundColor(Color.parseColor("#BAEBDF"))
                    holder.interest.setTextColor(Color.parseColor("#226880"))
                    checker ="click"
                    CoroutineScope(Dispatchers.IO).launch {
                        removeInterest(mInterest[position].inteID!!)
                    }
                }
        }
    }

    private suspend fun addInterest(ID:String){
        FirebaseDatabase.getInstance().reference.child("UserInterest")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(ID).setValue(true)
    }

    private fun removeInterest(ID: String){
        FirebaseDatabase.getInstance().reference.child("UserInterest")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(ID).removeValue()
    }
}