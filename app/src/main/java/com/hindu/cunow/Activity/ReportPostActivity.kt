package com.hindu.cunow.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.ReportAdapter
import com.hindu.cunow.MainActivity
import com.hindu.cunow.Model.ReportModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_report_post.*

class ReportPostActivity : AppCompatActivity() {
    private var postId = ""
    private var reportList:MutableList<ReportModel>? = null
    private var reportAdapter: ReportAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_post)

        val intent = intent
        postId = intent.getStringExtra("postId").toString()


        val recyclerView:RecyclerView = findViewById(R.id.reportRV)
        val ll = LinearLayoutManager(this)
        ll.reverseLayout = true
        recyclerView.layoutManager = ll
        ll.stackFromEnd =true

        reportList = ArrayList()
        reportAdapter = ReportAdapter(this,reportList as ArrayList<ReportModel>,postId)
        recyclerView.adapter = reportAdapter

        loadReport()


        reportPost_btn.setOnClickListener { view->
            Snackbar.make(view,"Report Request successful",Snackbar.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
    private fun loadReport() {
        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("ReportList")

        databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    reportList!!.clear()
                    for (snap in snapshot.children){
                        val data = snap.getValue(ReportModel::class.java)
                        reportList!!.add(data!!)
                    }
                    reportAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}