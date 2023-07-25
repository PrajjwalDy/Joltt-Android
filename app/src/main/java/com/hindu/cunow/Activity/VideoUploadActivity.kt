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
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import com.iceteck.silicompressorr.SiliCompressor
import kotlinx.android.synthetic.main.activity_add_post.caption_image
import kotlinx.android.synthetic.main.activity_video_upload.*
import kotlinx.android.synthetic.main.post_privacy_dialog.view.*
import kotlinx.android.synthetic.main.video_upload_dialogbox.view.*

class VideoUploadActivity : AppCompatActivity() {
    private var privacy = "public"

    //constants to pick video
    private val VIDEO_PICK_GALLARY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101

    //PERMISSION REQUEST CODE
    private val CAMERA_REQUEST_CODE = 102

    //APPLY FOR CAMERA REQUEST PERMISSION
    private lateinit var cameraPermission: Array<String>
    private var videoUri: Uri? = null
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_upload)

        //init camera permission
        cameraPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        uploadVideo.setOnClickListener {
            uploadVideo()
        }


        if (videoUri == null) {
            videoPickDialog()
        } else {
            Toast.makeText(this, "Video Picked", Toast.LENGTH_SHORT).show()
        }

        postPrivacy_video.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.post_privacy_dialog, null)

            val dialogBuilder = android.app.AlertDialog.Builder(this)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.post_public.setOnClickListener { view ->
                privacy = "public"
                Snackbar.make(view, "Post privacy set to public", Snackbar.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }

            dialogView.post_private.setOnClickListener { view ->
                privacy = "private"
                Snackbar.make(view, "Post privacy set to private", Snackbar.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        }

        changeVideo.setOnClickListener {
            videoPickDialog()
        }

    }

    //VIDEO PICK DIALOG
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
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                recordVideo()
            }
        }

    }

    //REQUEST CAMERA PERMISSION
    private fun requestCameraPermission() {
        //RequestCameraPermission
        ActivityCompat.requestPermissions(
            this,
            cameraPermission,
            CAMERA_REQUEST_CODE
        )
    }

    //PERMISSION CAMERA
    private fun checkCameraPermission(): Boolean {
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

    //DIALOG VIDEO PICKER
    private fun videoPickGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose Video"),
            VIDEO_PICK_GALLARY_CODE
        )
    }

    //RECORD VIDEO FROM CAMERA
    private fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE ->
                if (grantResults.size > 0) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        //Permission allowed
                        recordVideo()
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun uploadVideo() {
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()


        val timestamp = System.currentTimeMillis()
        val filePathName = "Videos/video_$timestamp"
        //upload video using url of video to storage
        println("reached here1")
        val storageReference = FirebaseStorage.getInstance().getReference(filePathName)

        storageReference.putFile(videoUri!!).addOnSuccessListener { takeSnapshot ->

            val uriTask = takeSnapshot.storage.downloadUrl

            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result
            if (uriTask.isSuccessful) {
                println("reached here2")

                val ref = FirebaseDatabase.getInstance().reference.child("Post")
                val postId = ref.push().key
                val postMap = HashMap<String, Any>()
                postMap["postId"] = postId!!
                postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                postMap["caption"] = captionVideo.text.toString()
                postMap["videoId"] = "$downloadUri"
                postMap["iImage"] = false
                postMap["video"] = true
                postMap["page"] = false

                println("reached her3")
                Toast.makeText(this, "Video shared successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                progressDialog.dismiss()
                ref.child(postId).setValue(postMap).addOnCompleteListener {
                    buildHasTag(postId)
                    Toast.makeText(this, "Post shared successfully", Toast.LENGTH_SHORT).show()
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
                    .addOnFailureListener {
                        takeSnapshot
                        progressDialog.dismiss()
                    }
            }

        }
            .addOnFailureListener {
                progressDialog.dismiss()
            }

    }


    //ON ACTIVITY RESULT

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (resultCode == VIDEO_PICK_CAMERA_CODE) {
                videoUri = data!!.data
                initializePlayer(videoUri!!)
            } else if (requestCode == VIDEO_PICK_GALLARY_CODE) {
                videoUri = data!!.data
                initializePlayer(videoUri!!)
            }

        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    //INITIALIZE PLAYER
    private fun initializePlayer(videoUri: Uri) {
        player = SimpleExoPlayer.Builder(this, DefaultRenderersFactory(this))
            .setTrackSelector(DefaultTrackSelector(this))
            .build()
        videoPlayer_upload.player = player

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

    private fun buildHasTag(postId:String){
        val sentence = captionVideo.text.toString().trim{ it <= ' '}
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

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

}


