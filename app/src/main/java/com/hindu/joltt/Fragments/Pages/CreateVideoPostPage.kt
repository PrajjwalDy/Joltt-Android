package com.hindu.joltt.Fragments.Pages

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hindu.cunow.R
import com.hindu.joltt.MainActivity

class CreateVideoPostPage : AppCompatActivity() {

    private var privacy = "public"
    private var pageId =""
    private var pageAdmin = ""
    private var pageName = ""

    //constants to pick video
    private val VIDEO_PICK_GALLARY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101

    //PERMISSION REQUEST CODE
    private val CAMERA_REQUEST_CODE = 102

    //APPLY FOR CAMERA REQUEST PERMISSION
    private lateinit var cameraPermission: Array<String>
    private var videoUri: Uri? = null
    private lateinit var player: SimpleExoPlayer

    private lateinit var uploadVideo_page:AppCompatButton
    private lateinit var changeVideo_page:AppCompatButton
    private lateinit var videoPlayer_page:PlayerView

    private lateinit var captionVideo_page:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_video_post_page)

        uploadVideo_page = findViewById(R.id.uploadVideo_page)
        changeVideo_page = findViewById(R.id.changeVideo_page)
        videoPlayer_page = findViewById(R.id.videoPlayer_page)
        captionVideo_page = findViewById(R.id.captionVideo_page)


        val intent = intent
        pageId = intent.getStringExtra("pageId").toString()
        pageAdmin = intent.getStringExtra("pageAdmin").toString()
        pageName = intent.getStringExtra("pageName").toString()

        //init camera permission
        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)


        if (videoUri == null){
            videoPickDialog()
        }else{
            Toast.makeText(this,"Video Picked", Toast.LENGTH_SHORT).show()
        }

        uploadVideo_page.setOnClickListener {
            uploadVideo()
        }

        changeVideo_page.setOnClickListener {
            videoPickDialog()
        }

    }

    private fun initializePlayer(videoUri: Uri) {
        player = SimpleExoPlayer.Builder(this, DefaultRenderersFactory(this))
            .setTrackSelector(DefaultTrackSelector(this))
            .build()
        videoPlayer_page.player = player

        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "ExoPlayer")
        )
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUri))

        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()
    }

    private fun videoPickDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.video_upload_dialogbox, null)
        val dialogBuilder = android.app.AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.show()

        val pickGallery = dialogView.findViewById<LinearLayout>(R.id.pickGallery)
        val recordVideo = dialogView.findViewById<LinearLayout>(R.id.recordVideo)

        pickGallery.setOnClickListener {
            videoPickGallery()
            alertDialog.dismiss()
        }
        recordVideo.setOnClickListener {
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
                postMap["publisher"] = pageId
                postMap["caption"] = captionVideo_page.text.toString()
                postMap["videoId"] = "$downloadUri"
                postMap["iImage"] = false
                postMap["video"] = true
                postMap["page"] = true
                postMap["pageAdmin"] = pageAdmin
                postMap["pageName"] = pageName

                println("reached her3")
                Toast.makeText(this,"Video shared successfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                progressDialog.dismiss()
                ref.child(postId).setValue(postMap).addOnCompleteListener{
                    Toast.makeText(this,"Image shared successfully",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
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
                initializePlayer(videoUri!!)
            }else if (requestCode == VIDEO_PICK_GALLARY_CODE){
                videoUri = data!!.data
                initializePlayer(videoUri!!)
            }

        }else{
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

}