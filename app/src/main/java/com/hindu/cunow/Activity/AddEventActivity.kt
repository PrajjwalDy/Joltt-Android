package com.hindu.cunow.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.activity_add_post.*
import java.io.ByteArrayOutputStream

class AddEventActivity : AppCompatActivity() {
    private var imageUri : Uri? = null
    private var storageEventImageRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        storageEventImageRef = FirebaseStorage.getInstance().reference.child("Event Images")

        addEvent_img_tv.setOnClickListener {
            cropImage()
        }

        submit_event.setOnClickListener {
            addEvent()
        }
    }

    private fun addEvent() {
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()

        val inputStream = contentResolver.openInputStream(imageUri!!)
        val image = BitmapFactory.decodeStream(inputStream)

        val baos = ByteArrayOutputStream()
        val options = 40
        image.compress(Bitmap.CompressFormat.JPEG, options, baos)
        val data = baos.toByteArray()

        val fileReference = storageEventImageRef!!
            .child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask: StorageTask<*>
        uploadTask = fileReference.putBytes(data)

        uploadTask
            .addOnSuccessListener {
                val downloadUrl = it.metadata!!.reference!!.downloadUrl
                downloadUrl.addOnSuccessListener {
                    // Save the download URL to the database
                    val imageUrl = it.toString()

                    val ref = FirebaseDatabase.getInstance().reference.child("Events")
                    val postId = ref.push().key

                    val postMap = HashMap<String, Any>()
                    postMap["eventId"] = postId!!
                    postMap["adder"] = FirebaseAuth.getInstance().currentUser!!.uid
                    postMap["eventName"] = eventName_ET.text.toString()
                    postMap["eventImg"] = imageUrl
                    postMap["eventHost"] = eventHost_ET.text.toString()
                    postMap["eventDescription"] = eventDes_ET.text.toString()


                    ref.child(postId).updateChildren(postMap)
                    Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AddEventActivity, MainActivity::class.java))
                    finish()
                    progressDialog.dismiss()
                }
            }
    }

    private fun cropImage(){
        CropImage.activity()
            .setAspectRatio(3, 4)
            .start(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            add_eventImg.setImageURI(imageUri)
        }
    }

}