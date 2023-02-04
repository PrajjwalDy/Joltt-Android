package com.hindu.cunow.Adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.hindu.cunow.Model.EventModel
import com.hindu.cunow.R
import org.w3c.dom.Text

class EventAdapter(private val mContext: Context,
                   private val mEvent:List<EventModel>):RecyclerView.Adapter<EventAdapter.ViewHolder>() {

                       inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                           private val eventImage: ImageView = itemView.findViewById(R.id.eventImage) as ImageView
                           private val eventName:TextView = itemView.findViewById(R.id.eventName) as TextView
                           private val eventDes:TextView = itemView.findViewById(R.id.eventDesc) as TextView
                           private val button:Button = itemView.findViewById(R.id.interested_button) as Button

                           fun bind(list:EventModel){
                               Glide.with(mContext).load(list.eventImg).into(eventImage)
                               eventName.text = list.eventName
                               eventDes.text = list.eventDescription
                               getStatus(list.eventId!!,button)
                               button.setOnClickListener {
                                   addInterest(list.eventId,button)
                               }
                           }
                       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.event_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mEvent.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mEvent[position])
    }

    private fun addInterest(id:String, button: Button){

        when(button.text){
            "Interested"->{
                FirebaseDatabase.getInstance().reference
                    .child("Events")
                    .child(id)
                    .child("Interested")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .setValue(true)
            }else->{
            FirebaseDatabase.getInstance().reference
                .child("Events")
                .child(id)
                .child("Interested")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()
            }
        }


    }

    private fun getStatus(id:String,button: Button){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Events")
            .child(id)
            .child("Interested")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                    button.text = "Undo"
                }else{
                    button.text = "Interested"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}