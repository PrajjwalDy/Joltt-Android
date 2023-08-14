package com.hindu.joltt.Activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.hindu.cunow.R
import com.hindu.joltt.MainActivity
import com.hindu.joltt.Model.JoltScoreModel
import com.theartofdev.edmodo.cropper.CropImage
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_add_post.caption_image
import kotlinx.android.synthetic.main.activity_add_post.imagePickOption_RL
import kotlinx.android.synthetic.main.activity_add_post.pickImage
import kotlinx.android.synthetic.main.activity_add_post.pickVideo
import kotlinx.android.synthetic.main.activity_add_post.postImage_preview
import kotlinx.android.synthetic.main.activity_add_post.shareImage_btn
import kotlinx.android.synthetic.main.fragment_home.addText_ET
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.UUID

class AddPostActivity : AppCompatActivity() {
    private var privacy = "public"
    var myUrl = 0
    private var imageUri: Uri? = null
    private var storagePostImageRef: StorageReference? = null

    private val GALLERY_REQUEST_CODE = 1234
    private val WRITE_EXTERNAL_STORAGE_CODE = 1

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostImageRef = FirebaseStorage.getInstance().reference.child("Posted Images")

        checkPermission()
        requestPermission()


        pickImage.setOnClickListener {
            if (checkPermission()) {
                pickFromGallery()
            } else {
                Toast.makeText(this, "Please Allow the Required Permission", Toast.LENGTH_SHORT)
                    .show()
                requestPermission()
            }
        }

        pickVideo.setOnClickListener {
            startActivity(Intent(this, VideoUploadActivity::class.java))
            finish()
        }

        shareImage_btn.setOnClickListener {
            if (imageUri == null) {
                uploadOnlyText()
            } else {
                uploadImage()
            }

        }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->


                if (result.resultCode == RESULT_OK) {

                    var extras: Bundle? = result.data?.extras

                    var imageUri: Uri

                    var imageBitmap = extras?.get("data") as Bitmap

                    var imageResult: WeakReference<Bitmap> = WeakReference(
                        Bitmap.createScaledBitmap(
                            imageBitmap, imageBitmap.width, imageBitmap.height, false
                        ).copy(
                            Bitmap.Config.RGB_565, true
                        )
                    )

                    var bm = imageResult.get()

                    imageUri = saveImage(bm, this)

                    launchImageCrop(imageUri)
                }
            }

        //PRIVACY BUTTON

        /*changePrivacy_btn.setOnClickListener {
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
        }*/
    }

    //CHECK PERMISSION
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

    //REQUEST PERMISSION
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

    //CHOOSE IMAGE FORM GALLERY
    private fun pickFromGallery() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    //UPLOAD IMAGE
    private fun uploadImage() {
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
            .child(System.currentTimeMillis().toString() + ".jpg")

        val uploadTask: StorageTask<*>
        uploadTask = fileReference.putBytes(data)

        uploadTask
            .addOnSuccessListener {
                val downloadUrl = it.metadata!!.reference!!.downloadUrl
                downloadUrl.addOnSuccessListener {
                    // Save the download URL to the database
                    val imageUrl = it.toString()

                    val ref = FirebaseDatabase.getInstance().reference.child("Post")
                    val postId = ref.push().key

                    val postMap = HashMap<String, Any>()
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
                    CoroutineScope(Dispatchers.IO).launch {
                        getJoltScore(2)
                    }

                    ///end

                    Toast.makeText(this, "Image shared successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AddPostActivity, MainActivity::class.java))
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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            postImage_preview.setImageURI(imageUri)
        }

        when (requestCode) {

            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                } else {

                }
            }

        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri: Uri? = UCrop.getOutput(data!!)

            setImage(resultUri!!)

            if (resultUri != null) {
                imageUri = resultUri
            }
        }

    }

    //UPLOAD ONLY TEXT
    private fun uploadOnlyText() {
        if (addText_ET.text.isEmpty()) {
            Toast.makeText(this, "Please Write something", Toast.LENGTH_SHORT).show()
        } else {
            val dataRef = FirebaseDatabase.getInstance().reference.child("Post")
            val postId = dataRef.push().key
            val dataMap = HashMap<String, Any>()

            dataMap["postId"] = postId!!
            dataMap["caption"] = caption_image.text.toString()
            dataMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
            dataMap["iImage"] = false
            dataMap["video"] = false
            dataMap["page"] = false
            dataMap["public"] = privacy == "public"
            dataRef.child(postId).updateChildren(dataMap)
            buildHasTag(postId)

            getJoltScore(2)

            Toast.makeText(this, "Post added Successfully", Toast.LENGTH_SHORT).show()
            caption_image.text.clear()
        }
    }

    //IMAGE CROPPING FUNCTION
    private fun launchImageCrop(uri: Uri) {
        val destination: String = StringBuilder(UUID.randomUUID().toString()).toString()
        val options: UCrop.Options = UCrop.Options()

        UCrop.of(Uri.parse(uri.toString()), Uri.fromFile(File(cacheDir, destination)))
            .withOptions(options)
            .withAspectRatio(3F, 2F)
            .withMaxResultSize(1080, 1080)
            .start(this)
    }

    //SET IMAGE TO IMAGE VIEW
    private fun setImage(uri: Uri) {
        postImage_preview.visibility = View.VISIBLE
        Glide.with(this)
            .load(uri)
            .into(postImage_preview)
        imagePickOption_RL.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            WRITE_EXTERNAL_STORAGE_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(this, "Enable permissions", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    // HASHTAG FUNCTION
    private fun buildHasTag(postId: String) {
        val sentence = caption_image.text.toString().trim { it <= ' ' }
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
            val key = hashtag.toString().removeRange(0, 1)
            val tagMap = HashMap<String, Any>()
            tagMap["tagName"] = hashtag
            hashtagsRef.child(key).updateChildren(tagMap)
            hashtagsRef.child(key).child("posts").child(postId).setValue(true)
            getPostCount(hashtag)
        }
    }

    //POST COUNT
    private fun getPostCount(tag: String) {
        val key = tag.removeRange(0, 1)
        val dataRef = FirebaseDatabase.getInstance()
            .reference.child("hashtags")
            .child(key)
            .child("posts")

        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val hashtagsRef = FirebaseDatabase.getInstance().reference
                        .child("hashtags")
                        .child(key)
                    val tagMap = HashMap<String, Any>()
                    tagMap["postCount"] = snapshot.childrenCount.toInt()
                    hashtagsRef.updateChildren(tagMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun saveImage(image: Bitmap?, context: Context): Uri {

        var imageFolder = File(context.cacheDir, "Joltt")
        var uri: Uri? = null

        try {
            imageFolder.mkdirs()
            var file: File = File(imageFolder, "joltt_image.png")
            var stream: FileOutputStream = FileOutputStream(file)
            image?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(
                context.applicationContext,
                "com.hindu.cunow" + ".provider",
                file
            )

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return uri!!

    }

    //JOLT POINT
    private fun getJoltScore(point: Int) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseRef = FirebaseDatabase.getInstance().reference.child("JoltPoint")
            .child(firebaseUser)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(JoltScoreModel::class.java)
                var joltScore = 0
                if (data != null) {
                    joltScore = data.joltScore
                }
                addJoltPoint(joltScore, point)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }
    private fun addJoltPoint(joltPoint: Int, point: Int) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseRef = FirebaseDatabase.getInstance().reference.child("JoltPoint")

        val newJoltScore = joltPoint + point
        val pointMap = HashMap<String, Any>()
        pointMap["joltScore"] = newJoltScore
        databaseRef.child(firebaseUser).updateChildren(pointMap)
        Toast.makeText(this, "Code reached here", Toast.LENGTH_SHORT).show()
    }

}