package com.hindu.joltt.Activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hindu.cunow.R

class CreateCircle : AppCompatActivity() {
    var private = "no"
    private var iconUrl = ""
    private var imageUri: Uri? = null
    private var storageCircleReference:StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_circle)

        storageCircleReference = FirebaseStorage.getInstance().reference.child("Circle Icons")

    }

//    private fun createCircle(){
//        val progressDialog = Dialog(this)
//        progressDialog.setContentView(R.layout.porgress_dialog)
//        progressDialog.show()
//
//        val fileReference = storageCircleReference!!
//            .child(System.currentTimeMillis().toString()+".jpg")
//
//        val uploadTask:StorageTask<*>
//        uploadTask = fileReference.putFile(imageUri!!)
//
//        uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task->
//            if (!task.isSuccessful){
//                task.exception?.let {
//                    throw it
//                }
//            }
//            return@Continuation fileReference.downloadUrl
//        }).addOnCompleteListener(OnCompleteListener<Uri>{task ->
//            if (task.isSuccessful){
//                val downloadUrl = task.result
//                iconUrl = downloadUrl.toString()
//
//                val ref = FirebaseDatabase.getInstance().reference.child("Circle")
//                val circleId = ref.push().key
//
//                val circleMap = HashMap<String,Any>()
//                circleMap["circleId"] = circleId!!
//                circleMap["circleName"] = circle_name_editText.text.toString()
//                circleMap["icon"] = iconUrl
//                circleMap["privateC"] = private == "yes"
//                circleMap["admin"] = FirebaseAuth.getInstance().currentUser!!.uid
//                circleMap["circle_description"] = circle_description_editText.text.toString()
//
//                ref.child(circleId).updateChildren(circleMap)
//
//                Toast.makeText(this,"Circle Created Successfully",Toast.LENGTH_SHORT).show()
//                FirebaseDatabase.getInstance().reference
//                    .child("Circle").child(circleId)
//                    .child("Members").child(FirebaseAuth.getInstance().currentUser!!.uid)
//                    .setValue(true)
//
//                val join_ref = FirebaseDatabase.getInstance()
//                    .reference
//                    .child("Users")
//                    .child(FirebaseAuth
//                        .getInstance()
//                        .currentUser!!.uid)
//                    .child("Joined_Circles")
//                val requestMap = HashMap<String,Any>()
//                requestMap["JCId"] = circleId
//                join_ref.child(circleId).updateChildren(requestMap)
//                finish()
//                progressDialog.dismiss()
//
//
//            }else{
//                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
//                progressDialog.dismiss()
//            }
//
//
//        })
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK&& data != null){
//            val result = CropImage.getActivityResult(data)
//            imageUri = result.uri
//            circle_icon_create.setImageURI(imageUri)
//            select_icon.text = "Change Icon"
//        }
//    }
}