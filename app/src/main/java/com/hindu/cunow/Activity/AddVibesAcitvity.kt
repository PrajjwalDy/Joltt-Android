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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_add_vibes_acitvity.*
import kotlinx.android.synthetic.main.activity_video_upload.*
import kotlinx.android.synthetic.main.vibes_item_layout.*
import kotlinx.android.synthetic.main.video_upload_dialogbox.view.*

class AddVibesAcitvity : AppCompatActivity() {

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
        setContentView(R.layout.activity_add_vibes_acitvity)

        //init camera permission
        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        upload_vibes.setOnClickListener {
            uploadVideo()
        }

        if(videoUri == null){
            videoPickDialog()
        }else{
            Toast.makeText(this,"Video Picked", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setVideoToVideoView() {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(vibesPreview)

        //media controller
        vibesPreview.setMediaController(mediaController)
        vibesPreview.setVideoURI(videoUri)
        vibesPreview.requestFocus()
        vibesPreview.setOnPreparedListener { mediaPlayer ->
            val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
            val screenRatio = vibesPreview.width / vibesPreview.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                vibesPreview.scaleX = scaleX
            } else {
                vibesPreview.scaleY = 1f / scaleX
            }
        }
        vibesPreview.start()
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

    private fun uploadVideo(){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()


        val timestamp = ""+System.currentTimeMillis()
        val filePathName = "Vibes/vibes_$timestamp"
        //upload video using url of video to storage
        println("reached here1")
        val storageReference = FirebaseStorage.getInstance().getReference(filePathName)

        storageReference.putFile(videoUri!!).addOnSuccessListener { takeSnapshot ->

            val uriTask = takeSnapshot.storage.downloadUrl

            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result
            if (uriTask.isSuccessful){
                println("reached here2")

                val ref = FirebaseDatabase.getInstance().reference.child("Vibes")
                val postId = ref.push().key
                val postMap = HashMap<String,Any>()
                postMap["vibeId"] = postId!!
                postMap["viberId"] = FirebaseAuth.getInstance().currentUser!!.uid
                postMap["vibeDescription"] = caption_vibes.text.toString()
                postMap["vibe"] = "$downloadUri"

                Toast.makeText(this,"Vibe shared successfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, VibesActivity::class.java))
                finish()
                progressDialog.dismiss()
                //progressDialog.dismiss()
                ref.child(postId).setValue(postMap).addOnCanceledListener{
                    Toast.makeText(this,"Something Went wrong",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    progressDialog.dismiss()
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