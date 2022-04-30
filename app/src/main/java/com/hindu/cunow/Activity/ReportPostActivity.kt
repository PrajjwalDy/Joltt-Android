package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_report_post.*

class ReportPostActivity : AppCompatActivity() {
    private var postId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_post)

        val intent = intent
        postId = intent.getStringExtra("postId").toString()

        reportPost_btn.setOnClickListener { view->
            reportPost(view)
        }

    }

    private fun reportPost(view:View){
        if (postReportPoint_ET.text.isEmpty()){
            Snackbar.make(view,"please mention the point no. stated above..", Snackbar.LENGTH_SHORT).show()
        }else{
            val dataRef = FirebaseDatabase.getInstance().reference
                .child("PostReport")

            val commentId = dataRef.push().key

            val dataMap = HashMap<String,Any>()
            dataMap["reportId"] = commentId!!
            dataMap["reportPoint"] = postReportPoint_ET.text.toString()
            dataMap["reporter"] = FirebaseAuth.getInstance().currentUser!!.uid
            dataMap["postId"] = postId

            dataRef.child(commentId).updateChildren(dataMap)
            Snackbar.make(view,"Post Reported successfully", Snackbar.LENGTH_SHORT).show()
            postReportPoint_ET.text.clear()
        }
    }
}