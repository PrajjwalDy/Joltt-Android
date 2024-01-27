package com.hindu.joltt.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


class AddConfessionActivity : AppCompatActivity() {

    private  var myUrl = ""
    private var imageUri : Uri? = null
    private var storagePostImageRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_confession)

        storagePostImageRef = FirebaseStorage.getInstance().reference.child("Confession Images")

        cropImage()


    }

    private fun cropImage(){
        CropImage.activity()
            .setAspectRatio(3,4)
            .start(this)
    }

}