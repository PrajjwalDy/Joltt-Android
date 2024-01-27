package com.hindu.joltt.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.ReportAdapter
import com.hindu.joltt.MainActivity
import com.hindu.joltt.Model.ReportModel
import com.uk.tastytoasty.TastyToasty

class ReportPostActivity : AppCompatActivity() {
    private var postId = ""
    private var reportList:MutableList<ReportModel>? = null
    private var reportAdapter: ReportAdapter? = null


    private lateinit var reportPost_btn:AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_post)


        reportPost_btn = findViewById(R.id.reportPost_btn)

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
            TastyToasty.makeText(this,"Post Reported",
                TastyToasty.SHORT,R.drawable.ic_report,R.color.vivaMagenta,R.color.white,false).show()
            startActivity(Intent(this, MainActivity::class.java))
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