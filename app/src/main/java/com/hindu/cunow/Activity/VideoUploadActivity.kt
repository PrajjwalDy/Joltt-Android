package com.hindu.cunow.Activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.MediaController
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_video_upload.*
import kotlinx.android.synthetic.main.post_privacy_dialog.view.*
import kotlinx.android.synthetic.main.video_upload_dialogbox.view.*

class VideoUploadActivity : AppCompatActivity() {
    private var privacy = ""
    //constants to pick video
    private val VIDEO_PICK_GALLARY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101
    //PERMISSION REQUEST CODE
    private val CAMERA_REQUEST_CODE = 102
    //APPLY FOR CAMERA REQUEST PERMISSION
    private lateinit var cameraPermission:Array<String>
    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_upload)


        //init camera permission
        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        next_step_video.setOnClickListener {
            uploadVideo()
        }


        if (videoUri == null){
            videoPickDialog()
        }else{
            Toast.makeText(this,"Video Picked",Toast.LENGTH_SHORT).show()
        }

        postPrivacy_video.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.post_privacy_dialog, null)

            val dialogBuilder = android.app.AlertDialog.Builder(this)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.post_public.setOnClickListener {view->
                privacy = "public"
                Snackbar.make(view,"Post privacy set to public", Snackbar.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }

            dialogView.post_private.setOnClickListener {view->
                privacy = "private"
                Snackbar.make(view,"Post privacy set to private", Snackbar.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        }

        changeVideo.setOnClickListener {
            videoPickDialog()
        }


    }



    private fun setVideoToVideoView() {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoPreview)

        //media controller
        videoPreview.setMediaController(mediaController)
        videoPreview.setVideoURI(videoUri)
        videoPreview.requestFocus()
        videoPreview.setOnPreparedListener { mediaPlayer ->
            val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
            val screenRatio = videoPreview.width / videoPreview.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                videoPreview.scaleX = scaleX
            } else {
                videoPreview.scaleY = 1f / scaleX
            }
        }
        videoPreview.start()
        //videoPreview.setOnPreparedListener{videoPreview.pause()}
    }

    private fun videoPickDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.video_upload_dialogbox, null)
        val dialogBuilder = android.app.AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.show()

        dialogView.pickGallery.setOnClickListener {
            videoPickGallery()
            alertDialog.dismiss()
        }
        dialogView.recordVideo.setOnClickListener {
            alertDialog.dismiss()
            if (!checkCameraPermission()){
                requestCameraPermission()
            }else{
                recordVideo()
            }
        }

    }
    private fun requestCameraPermission(){
        //RequestCameraPermission
        ActivityCompat.requestPermissions(
            this,
            cameraPermission,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermission():Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return result1 && result2
    }

    private fun videoPickGallery(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose Video"),
            VIDEO_PICK_GALLARY_CODE
        )
    }

    private fun recordVideo(){
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent,VIDEO_PICK_CAMERA_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE ->
                if (grantResults.size > 0){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted){
                        //Permission allowed
                        recordVideo()
                    }else{
                        Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun uploadVideo(){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()


        val timestamp = ""+System.currentTimeMillis()
        val filePathName = "Videos/video_$timestamp"
        //upload video using url of video to storage
        println("reached here1")
        val storageReference = FirebaseStorage.getInstance().getReference(filePathName)

            storageReference.putFile(videoUri!!).addOnSuccessListener { takeSnapshot ->

                val uriTask = takeSnapshot.storage.downloadUrl

                while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result
                if (uriTask.isSuccessful){
                    println("reached here2")

                    val ref = FirebaseDatabase.getInstance().reference.child("Post")
                    val postId = ref.push().key
                    val postMap = HashMap<String,Any>()
                    postMap["postId"] = postId!!
                    postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                    postMap["caption"] = captionVideo.text.toString()
                    postMap["videoId"] = "$downloadUri"
                    postMap["iImage"] = false
                    postMap["video"] = true

                    println("reached her3")
                    Toast.makeText(this,"Video shared successfully",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                    progressDialog.dismiss()
                    ref.child(postId).setValue(postMap).addOnCanceledListener{
                        Toast.makeText(this,"Image shared successfully",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                        progressDialog.dismiss()
                        FirebaseAuth.getInstance().currentUser!!.uid.let { it1 ->
                            FirebaseDatabase.getInstance().reference
                                .child("Users").child(it1.toString())
                                .child("MyPosts").child(postId)
                                .setValue(true)
                        }
                    }
                        .addOnFailureListener { takeSnapshot
                            progressDialog.dismiss()
                        }
                }

            }
                .addOnFailureListener{
                    progressDialog.dismiss()
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK){
            if (resultCode == VIDEO_PICK_CAMERA_CODE){
                videoUri = data!!.data
                setVideoToVideoView()
            }else if (requestCode == VIDEO_PICK_GALLARY_CODE){
                videoUri = data!!.data
                setVideoToVideoView()
            }

        }else{
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    }


