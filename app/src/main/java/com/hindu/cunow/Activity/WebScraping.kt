package com.hindu.cunow.Activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_add_post.caption_image
import kotlinx.android.synthetic.main.activity_web_scraping.previewImage
import kotlinx.android.synthetic.main.activity_web_scraping.selectImage_test
import kotlinx.android.synthetic.main.activity_web_scraping.upload
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class WebScraping : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 1234
    private val WRITE_EXTERNAL_STORAGE_CODE=1

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var storagePostImageRef: StorageReference? = null

    lateinit var finalUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_scraping)

        storagePostImageRef = FirebaseStorage.getInstance().reference.child("TestImage")

        checkPermission()
        requestPermission()

        selectImage_test.setOnClickListener {
            pickFromGallery()
        }

        upload.setOnClickListener {
            uploadImage()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            100
        )
    }

    private fun pickFromCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activityResultLauncher.launch(intent)
    }

    private fun pickFromGallery() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }

                else{

                }
            }

        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri :Uri ?= UCrop.getOutput(data!!)

            setImage(resultUri!!)

            if (resultUri != null) {
                finalUri=resultUri
            }
        }
    }

    private fun launchImageCrop(uri: Uri) {
        var destination:String=StringBuilder(UUID.randomUUID().toString()).toString()
        var options:UCrop.Options=UCrop.Options()

        UCrop.of(Uri.parse(uri.toString()), Uri.fromFile(File(cacheDir,destination)))
            .withOptions(options)
            .withAspectRatio(0F, 0F)
            .useSourceImageAspectRatio()
            .withMaxResultSize(2000, 2000)
            .start(this)
    }

    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(previewImage)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {

            WRITE_EXTERNAL_STORAGE_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(this, "Enable permissions", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    private fun uploadImage(){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()

        val inputStream = contentResolver.openInputStream(finalUri!!)
        val image = BitmapFactory.decodeStream(inputStream)

        val baos = ByteArrayOutputStream()
        val options = 40
        image.compress(Bitmap.CompressFormat.JPEG, options, baos)
        val data = baos.toByteArray()

        val fileReference = storagePostImageRef!!
            .child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask: StorageTask<*>
        uploadTask = fileReference.putBytes(data)

        uploadTask
            .addOnSuccessListener{
                val downloadUrl = it.metadata!!.reference!!.downloadUrl
                downloadUrl.addOnSuccessListener {
                    // Save the download URL to the database
                    val imageUrl = it.toString()

                    val ref = FirebaseDatabase.getInstance().reference.child("Test")
                    val postId = ref.push().key

                    val postMap = HashMap<String,Any>()
                    postMap["postId"] = postId!!
                    postMap["testImage"] = imageUrl


                    ref.child(postId).updateChildren(postMap)
                    Toast.makeText(this, "Image UPloaded", Toast.LENGTH_SHORT).show()
                    ///end

                    progressDialog.dismiss()
                }

            }

    }

}