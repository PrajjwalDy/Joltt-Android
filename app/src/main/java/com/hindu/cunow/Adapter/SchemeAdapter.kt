package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hindu.cunow.Model.SchemeModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView

class SchemeAdapter(private val mContext:Context,
                    private val mScheme:List<SchemeModel>):RecyclerView.Adapter<SchemeAdapter.ViewHolder>() {

                        inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                            private val schemeImage: ImageView = itemView.findViewById(R.id.scheme_image) as ImageView
                            private val schemeName: TextView = itemView.findViewById(R.id.scheme_name) as TextView
                            private val schemeDes: TextView = itemView.findViewById(R.id.scheme_desc) as TextView
                            private val schemeBy:TextView = itemView.findViewById(R.id.govt_name) as TextView

                            fun bind(list:SchemeModel){
                                Glide.with(mContext).load(list.schemeImage).into(schemeImage)
                                schemeBy.text = list.schemeBy
                                schemeName.text = list.schemeName
                                schemeDes.text = list.schemeDesc
                            }

                        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.schemes_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mScheme.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mScheme[position])
    }


}