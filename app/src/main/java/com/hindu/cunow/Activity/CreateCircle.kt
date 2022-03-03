package com.hindu.cunow.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_create_circle.*
import kotlinx.android.synthetic.main.activity_edit_profile.*

class CreateCircle : AppCompatActivity() {
    var private = "no"
    private var iconUrl = ""
    private var imageUri: Uri? = null
    private var storageCircleReference:StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_circle)

        storageCircleReference = FirebaseStorage.getInstance().reference.child("Circle Icons")

        select_icon.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this)
        }

        private_circle.setOnClickListener {
            private = "yes"
            private_circle.setBackgroundColor(resources.getColor(R.color.red))
            private_circle.setTextColor(resources.getColor(R.color.white))
            public_circle.setBackgroundColor(resources.getColor(R.color.white))
            public_circle.setTextColor(resources.getColor(R.color.red))
        }
        public_circle.setOnClickListener {
            public_circle.setBackgroundColor(resources.getColor(R.color.red))
            public_circle.setTextColor(resources.getColor(R.color.white))
            private_circle.setBackgroundColor(resources.getColor(R.color.white))
            private_circle.setTextColor(resources.getColor(R.color.red))
        }

        create_circle_button.setOnClickListener {
            createCircle()
        }

    }

    private fun createCircle(){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()

        val fileReference = storageCircleReference!!
            .child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask:StorageTask<*>
        uploadTask = fileReference.putFile(imageUri!!)

        uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation fileReference.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri>{task ->
            if (task.isSuccessful){
                val downloadUrl = task.result
                iconUrl = downloadUrl.toString()

                val ref = FirebaseDatabase.getInstance().reference.child("Circle")
                val circleId = ref.push().key

                val circleMap = HashMap<String,Any>()
                circleMap["circleId"] = circleId!!
                circleMap["circleName"] = circle_name_editText.text.toString()
                circleMap["icon"] = iconUrl
                circleMap["private"] = private == "yes"
                circleMap["admin"] = FirebaseAuth.getInstance().currentUser!!.uid
                circleMap["circle_description"] = circle_description_editText.text.toString()

                ref.child(circleId).updateChildren(circleMap)

                Toast.makeText(this,"Circle Created Successfully",Toast.LENGTH_SHORT).show()
                finish()
                progressDialog.dismiss()


            }else{
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK&& data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            circle_icon_create.setImageURI(imageUri)
            select_icon.text = "Change Icon"
        }
    }
}