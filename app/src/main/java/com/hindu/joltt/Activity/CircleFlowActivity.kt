package com.hindu.joltt.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.CircleFlowAdapter
import com.hindu.joltt.Model.CircleFlowModel
import com.hindu.joltt.Model.CircleModel
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_circle_flow.circleFlow_message
import kotlinx.android.synthetic.main.activity_circle_flow.circleName_CF
import kotlinx.android.synthetic.main.activity_circle_flow.progress_line_ll
import kotlinx.android.synthetic.main.activity_circle_flow.selectMedia_CF
import kotlinx.android.synthetic.main.activity_circle_flow.send_CF
import kotlinx.android.synthetic.main.activity_circle_flow.sendingImage_CF
import kotlinx.android.synthetic.main.activity_circle_flow.sendingLayout

class CircleFlowActivity : AppCompatActivity() {
    private var circleId = ""
    private  var myUrl = ""
    private var imageUri : Uri? = null
    private var storagePostImageRef: StorageReference? = null
    private var media = ""

    private var flowList:MutableList<CircleFlowModel>? = null
    private var flowAdapter: CircleFlowAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_flow)

        storagePostImageRef = FirebaseStorage.getInstance().reference.child("Circle Flow")

        val intent = intent
        circleId = intent.getStringExtra("circleId").toString()

        val recyclerView: RecyclerView= findViewById(R.id.circle_flow_RV)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true


        flowList = ArrayList()
        flowAdapter = CircleFlowAdapter(this, flowList as ArrayList<CircleFlowModel>)
        recyclerView.adapter = flowAdapter

        selectMedia_CF.setOnClickListener {
            cropImage()
            media = "yes"
        }

        send_CF.setOnClickListener {view->
            if (imageUri != null){
                uploadImage()
            }else{
                sendMessage(view)
            }
        }
        retrieveFlow(recyclerView)
        circleInfo()

    }

    private fun cropImage(){
        CropImage.activity()
            .start(this)

    }

    private fun sendMessage(view: View) {
        if (circleFlow_message.text.isEmpty()) {
            Snackbar.make(view, "please write something..", Snackbar.LENGTH_SHORT).show()
        } else {
            val dataRef = FirebaseDatabase.getInstance().reference
                .child("CircleFlow")
                .child(circleId)

            val commentId = dataRef.push().key

            val dataMap = HashMap<String, Any>()
            dataMap["circleFlowId"] = commentId!!
            dataMap["circleFlowText"] = circleFlow_message.text.toString()
            dataMap["circleFlowSender"] = FirebaseAuth.getInstance().currentUser!!.uid
            dataMap["messageImage"] = false

            dataRef.push().setValue(dataMap)
            circleFlow_message.text.clear()
        }

    }

    private fun uploadImage(){
        progress_line_ll.visibility = View.VISIBLE
        sendingImage_CF.visibility = View.GONE

        val fileReference = storagePostImageRef!!
            .child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask: StorageTask<*>
        uploadTask = fileReference.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it

                }
            }
            return@Continuation fileReference.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri>{ task ->
            if (task.isSuccessful){
                val downloadUrl = task.result
                myUrl = downloadUrl.toString()

                val ref = FirebaseDatabase.getInstance().reference.child("CircleFlow").child(circleId)
                val postId = ref.push().key

                val postMap = HashMap<String,Any>()
                postMap["circleFlowId"] = postId!!
                postMap["circleFlowSender"] = FirebaseAuth.getInstance().currentUser!!.uid
                postMap["circleFlowText"] = circleFlow_message.text.toString()
                postMap["circleFlowImg"] = myUrl
                postMap["messageImage"] = true

                ref.child(postId).updateChildren(postMap)

                Toast.makeText(this,"Image shared successfully", Toast.LENGTH_SHORT).show()
                progress_line_ll.visibility = View.GONE
                media = ""
                imageUri = null
            }else{
                Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show()
                progress_line_ll.visibility = View.GONE
            }
        })

    }

    private fun retrieveFlow(recyclerView: RecyclerView){
        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("CircleFlow")
            .child(circleId)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    flowList!!.clear()
                    for (snapshot in snapshot.children){
                        val comment = snapshot.getValue(CircleFlowModel::class.java)
                        flowList!!.add(comment!!)
                    }
                    flowAdapter!!.notifyDataSetChanged()

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun circleInfo(){
        val circleData= FirebaseDatabase.getInstance().reference
            .child("Circle")
            .child(circleId)
        circleData.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(CircleModel::class.java)
                    circleName_CF.text = data!!.circleName

                    if (!data.mPermission && data.admin != FirebaseAuth.getInstance().currentUser!!.uid){
                        sendingLayout.visibility = View.GONE
                    }else{
                        sendingLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            sendingImage_CF.setImageURI(imageUri)
            sendingImage_CF.visibility =View.VISIBLE
        }
    }

}