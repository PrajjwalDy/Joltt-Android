package com.hindu.cunow.Activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R
import com.theartofdev.edmodo.cropper.CropImage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.MainActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.post_privacy_dialog.view.*

class AddPostActivity : AppCompatActivity() {
    private var privacy = ""
    private  var myUrl = ""
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

        val fileReference = storagePostImageRef!!
            .child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask:StorageTask<*>
        uploadTask = fileReference.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{task->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                    progressDialog.dismiss()
                }
            }
            return@Continuation fileReference.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri>{task ->
            if (task.isSuccessful){
                val downloadUrl = task.result
                myUrl = downloadUrl.toString()

                val ref = FirebaseDatabase.getInstance().reference.child("Post")
                val postId = ref.push().key

                val postMap = HashMap<String,Any>()
                postMap["postId"] = postId!!
                postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                postMap["caption"] = caption_image.text.toString()
                postMap["image"] = myUrl
                postMap["iImage"] = true
                postMap["video"] = false
                postMap["public"] = privacy == "public"

                ref.child(postId).updateChildren(postMap)

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
            }else{
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
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
            postImage_preview.setImageURI(imageUri)
        }

    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    // Here after all the permission are granted launch the gallery to select and image.
                    if (report!!.areAllPermissionsGranted()) {

                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(galleryIntent, GALLERY)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        // A constant variable for place picker
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }
}