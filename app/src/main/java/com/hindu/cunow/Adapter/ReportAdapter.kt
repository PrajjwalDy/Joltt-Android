package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.Model.ReportModel
import com.hindu.cunow.R

class ReportAdapter(private val mContext:Context,
                    private val mReport:List<ReportModel>,private val postId: String):RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

                        inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                            private val reportText:TextView = itemView.findViewById(R.id.reportText) as TextView
                            val checkBox:CheckBox = itemView.findViewById(R.id.reportCheckbox) as CheckBox

                            fun bind(list:ReportModel){
                                reportText.text = list.reportText
                            }
                        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.report_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mReport.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mReport[position])
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        holder.checkBox.setOnClickListener {
            addReport(postId,userId,mReport[position].reportTextId!!)
        }
    }

    private fun addReport(postId:String,userId:String, reportId:String){

        FirebaseDatabase.getInstance().reference.child("Report")
            .child(postId).child(userId)
            .child(reportId).setValue(true)
    }
}