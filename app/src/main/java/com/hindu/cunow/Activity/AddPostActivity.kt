package com.hindu.cunow.Activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.hindu.cunow.MainActivity
import com.hindu.cunow.Model.HashTagModel
import com.hindu.cunow.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.post_privacy_dialog.view.*
import java.io.ByteArrayOutputStream

class AddPostActivity : AppCompatActivity() {
    private var privacy = "public"
    var myUrl = 0
    private var imageUri : Uri? = null
    private var storagePostImageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostImageRef = FirebaseStorage.getInstance().reference.child("Posted Images")

        cropImage()

        shareImage_btn.setOnClickListener {
            uploadImage()
        }

        changePrivacy_btn.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.post_privacy_dialog, null)

            val dialogBuilder = android.app.AlertDialog.Builder(this)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.post_public.setOnClickListener {view->
                privacy = "public"
                Toast.makeText(this,"Post privacy set to public", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }

            dialogView.post_private.setOnClickListener {view->
                privacy = "private"
                Toast.makeText(this,"Post privacy set to public",Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        }
    }

    private fun cropImage(){
        CropImage.activity()
            .setAspectRatio(3, 4)
            .start(this)

    }

    private fun uploadImage(){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()

        val inputStream = contentResolver.openInputStream(imageUri!!)
        val image = BitmapFactory.decodeStream(inputStream)

        val baos = ByteArrayOutputStream()
        val options = 40
        image.compress(Bitmap.CompressFormat.JPEG, options, baos)
        val data = baos.toByteArray()

        val fileReference = storagePostImageRef!!
            .child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask:StorageTask<*>
        uploadTask = fileReference.putBytes(data)

        uploadTask
        .addOnSuccessListener{
            val downloadUrl = it.metadata!!.reference!!.downloadUrl
            downloadUrl.addOnSuccessListener {
                // Save the download URL to the database
                val imageUrl = it.toString()

                val ref = FirebaseDatabase.getInstance().reference.child("Post")
                val postId = ref.push().key

                val postMap = HashMap<String,Any>()
                postMap["postId"] = postId!!
                postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                postMap["caption"] = caption_image.text.toString()
                postMap["image"] = imageUrl
                postMap["iImage"] = true
                postMap["video"] = false
                postMap["page"] = false
                postMap["public"] = privacy == "public"

                ref.child(postId).updateChildren(postMap)
                //saveTags(postId)
                buildHasTag(postId)
                Toast.makeText(this,"Image shared successfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@AddPostActivity,MainActivity::class.java))
                finish()
                FirebaseAuth.getInstance().currentUser!!.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Users").child(it1.toString())
                        .child("MyPosts").child(postId)
                        .setValue(true)
                }

                progressDialog.dismiss()
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            postImage_preview.setImageURI(imageUri)
        }
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        // A constant variable for place picker
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }
    private fun buildHasTag(postId:String){
        val sentence = caption_image.text.toString().trim{ it <= ' '}
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

    private fun getPostCount(tag:String){
        val key = tag.removeRange(0,1)
        val dataRef = FirebaseDatabase.getInstance()
            .reference.child("hashtags")
            .child(key)
            .child("posts")

        dataRef.addValueEventListener(object :ValueEventListener{
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
}