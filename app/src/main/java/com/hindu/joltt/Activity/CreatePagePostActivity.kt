package com.hindu.joltt.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.R
import com.hindu.joltt.MainActivity
import com.theartofdev.edmodo.cropper.CropImage

class CreatePagePostActivity : AppCompatActivity() {

    private var privacy = "public"
    private  var myUrl = ""
    private var pageId = ""
    private var pageAdmin = ""
    private var pageName = ""
    private var imageUri : Uri? = null
    private var storagePostImageRef: StorageReference? = null

    private lateinit var sharePageImage_btn:AppCompatButton
    private lateinit var caption_PageImage:EditText
    private lateinit var pageImage_preview:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_page_post)

        sharePageImage_btn = findViewById(R.id.sharePageImage_btn)
        caption_PageImage = findViewById(R.id.caption_PageImage)
        pageImage_preview = findViewById(R.id.pageImage_preview)

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
                buildHasTag(postId)

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

    private fun buildHasTag(postId:String){
        val sentence = caption_PageImage.text.toString().trim{ it <= ' '}
        val words = sentence.split(" ")

        // Initialize an empty list of hashtags
        val hashtags = mutableListOf<String>()

        // Extract hashtags from the words
        for (word in words) {
            if (word.startsWith("#")) {
                hashtags.add(word)
            }
        }
        val hashtagsRef = FirebaseDatabase.getInstance().getReference("hashtags")

        for (hashtag in hashtags) {
            val key = hashtag.toString().removeRange(0,1)
            val tagMap = HashMap<String,Any>()
            tagMap["tagName"] = hashtag
            hashtagsRef.child(key).updateChildren(tagMap)
            hashtagsRef.child(key).child("posts").child(postId).setValue(true)
            getPostCount(hashtag)
        }
    }
    //POST COUNT
    private fun getPostCount(tag:String){
        val key = tag.removeRange(0,1)
        val dataRef = FirebaseDatabase.getInstance()
            .reference.child("hashtags")
            .child(key)
            .child("posts")

        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val hashtagsRef = FirebaseDatabase.getInstance().reference
                        .child("hashtags")
                        .child(key)
                    val tagMap = HashMap<String,Any>()
                    tagMap["postCount"] = snapshot.childrenCount.toInt()
                    hashtagsRef.updateChildren(tagMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
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