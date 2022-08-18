package com.hindu.cunow.Fragments.Circle

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.hindu.cunow.Adapter.ThreadAdapter
import com.hindu.cunow.Model.CircleFlowModel
import com.hindu.cunow.Model.CircleModel
import com.theartofdev.edmodo.cropper.CropImage
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_circle_flow.*

import kotlinx.android.synthetic.main.activity_thread.*
import kotlinx.android.synthetic.main.chat_item_left.*

class ThreadActivity : AppCompatActivity() {
    private var circleId = ""
    private  var myUrl = ""
    private var imageUri : Uri? = null
    private var storagePostImageRef: StorageReference? = null
    private var media = ""

    private var flowList:MutableList<CircleFlowModel>? = null
    private var threadAdapter:ThreadAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        storagePostImageRef = FirebaseStorage.getInstance().reference.child("Circle Flow")

        val intent = intent
        circleId = intent.getStringExtra("circleId").toString()
        threadInfo()

        val recyclerView: RecyclerView = findViewById(R.id.thread_flow_RV)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        flowList = ArrayList()
        threadAdapter = ThreadAdapter(this,flowList as ArrayList<CircleFlowModel>)
        recyclerView.adapter = threadAdapter
        retrieveFlow(recyclerView)

        selectMedia_TF.setOnClickListener{
            cropImage()
            media = "yes"
        }
        send_TF.setOnClickListener{view->
                sendMessage(view)
        }

        sendImage_TF.setOnClickListener{
            if (imageUri != null){
                uploadImage()
            }else{
                Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendMessage(view:View) {
        if (threadFlow_ET.text.isEmpty()) {
            Snackbar.make(view, "please write something..", Snackbar.LENGTH_SHORT).show()
        } else {
            val dataRef = FirebaseDatabase.getInstance().reference
                .child("CircleFlow")
                .child(circleId)

            val commentId = dataRef.push().key

            val dataMap = HashMap<String, Any>()
            dataMap["circleFlowId"] = commentId!!
            dataMap["circleFlowText"] = threadFlow_ET.text.toString()
            dataMap["circleFlowSender"] = FirebaseAuth.getInstance().currentUser!!.uid
            dataMap["messageImage"] = false

            dataRef.push().setValue(dataMap)
            threadFlow_ET.text.clear()
        }
    }

    private fun uploadImage() {
        RV_ll.visibility = View.VISIBLE
        sendImage_TF_ll.visibility = View.GONE
        uploadingImage_anim.visibility = View.VISIBLE
        uploadingImage_txt.visibility = View.VISIBLE

        val fileReference = storagePostImageRef!!
            .child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask: StorageTask<*>
        uploadTask = fileReference.putFile(imageUri!!)

        uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it

                }
            }
            return@Continuation fileReference.downloadUrl
            }).addOnCompleteListener(OnCompleteListener<Uri>{task->
            if (task.isSuccessful){
                val downloadUrl = task.result
                myUrl = downloadUrl.toString()

                val ref = FirebaseDatabase.getInstance().reference.child("CircleFlow").child(circleId)
                val postId = ref.push().key

                val dataMap = HashMap<String,Any>()
                dataMap["circleFlowId"] = postId!!
                dataMap["circleFlowSender"] = FirebaseAuth.getInstance().currentUser!!.uid
                dataMap["circleFlowText"] = image_message_ET.text.toString()
                dataMap["circleFlowImg"] = myUrl
                dataMap["messageImage"] = true

                ref.child(postId).updateChildren(dataMap)

                Toast.makeText(this,"Thread add successfully",Toast.LENGTH_SHORT).show()
                uploadingImage_anim.visibility = View.GONE
                uploadingImage_txt.visibility = View.GONE
                media = ""
                imageUri = null

            }
        })
    }

    private fun cropImage(){
        CropImage.activity()
            .start(this)
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
                        threadAdapter!!.notifyDataSetChanged()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    private fun threadInfo(){
        val circleData= FirebaseDatabase.getInstance().reference
            .child("Circle")
            .child(circleId)
        circleData.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(CircleModel::class.java)
                    thread_name_appbar.text = data!!.circleName

                    if (!data.mPermission && data.admin != FirebaseAuth.getInstance().currentUser!!.uid){
                        sendingLayout_ll.visibility = View.GONE
                    }else{
                        sendingLayout_ll.visibility = View.VISIBLE
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
            uploadImage_thread.setImageURI(imageUri)
            sendImage_TF_ll.visibility = View.VISIBLE
            RV_ll.visibility = View.GONE
        }
    }
}