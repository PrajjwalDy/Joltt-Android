package com.hindu.cunow.Adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.CommentActivity
import com.hindu.cunow.Activity.EventDetails
import com.hindu.cunow.Model.EventModel
import com.hindu.cunow.R
import org.w3c.dom.Text

class EventAdapter(private val mContext: Context,
                   private val mEvent:List<EventModel>):RecyclerView.Adapter<EventAdapter.ViewHolder>() {

                       inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                           private val eventImage: ImageView = itemView.findViewById(R.id.eventImage) as ImageView
                           private val eventName:TextView = itemView.findViewById(R.id.eventName) as TextView
                           private val eventTime:TextView = itemView.findViewById(R.id.eventTime) as TextView
                           private val eventDate:TextView = itemView.findViewById(R.id.eventDate) as TextView
                           private val eventMode:Button = itemView.findViewById(R.id.eventMode) as Button


                           fun bind(list:EventModel){
                               Glide.with(mContext).load(list.eventImg).into(eventImage)
                               eventName.text = list.eventName
                               eventDate.text = list.eventDate
                               eventMode.text = list.eventMode
                               eventTime.text = list.eventTime

                               itemView.setOnClickListener {
                                   val builder = CustomTabsIntent.Builder()
                                   val customTabsIntent = builder.build()
                                   customTabsIntent.launchUrl(mContext, Uri.parse(list.eventLink))
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
        val event = mEvent[position]

        holder.bind(mEvent[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, EventDetails::class.java)
            intent.putExtra("eventId",event.eventId)
        }
    }

}