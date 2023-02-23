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
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_create_page_post.*

class CreatePagePostActivity : AppCompatActivity() {

    private var privacy = "public"
    private  var myUrl = ""
    private var pageId = ""
    private var pageAdmin = ""
    private var pageName = ""
    private var imageUri : Uri? = null
    private var storagePostImageRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_page_post)

        val intent = intent
        pageId = intent.getStringExtra("Id").toString()
        pageName = intent.getStringExtra("name").toString()
        pageAdmin = intent.getStringExtra("admin").toString()

        storagePostImageRef = FirebaseStorage.getInstance().reference.child("Posted Images")
        cropImage()

        sharePageImage_btn.setOnClickListener {
            uploadImage()
        }
    }

    private fun cropImage(){
        CropImage.activity()
            .setAspectRatio(3,4)
            .start(this)
    }

    private fun uploadImage(){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()

        val fileReference = storagePostImageRef!!
            .child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask: StorageTask<*>
        uploadTask = fileReference.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                    progressDialog.dismiss()
                }
            }
            return@Continuation fileReference.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri>{ task ->
            if (task.isSuccessful){
                val downloadUrl = task.result
                myUrl = downloadUrl.toString()

                val ref = FirebaseDatabase.getInstance().reference.child("Post")
                val postId = ref.push().key

                val postMap = HashMap<String,Any>()
                postMap["postId"] = postId!!
                postMap["publisher"] = pageId
                postMap["caption"] = caption_PageImage.text.toString()
                postMap["image"] = myUrl
                postMap["iImage"] = true
                postMap["video"] = false
                postMap["page"] = true
                postMap["pageAdmin"] = pageAdmin
                postMap["pageName"] = pageName
                postMap["public"] = privacy == "public"

                ref.child(postId).updateChildren(postMap)

                Toast.makeText(this,"Image shared successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@CreatePagePostActivity, MainActivity::class.java))
                finish()
                progressDialog.dismiss()
            }else{
                Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            pageImage_preview.setImageURI(imageUri)
        }

    }

}