package com.hindu.joltt.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
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

class CreatePageActivity : AppCompatActivity() {

    private var iconUrl =""
    private var imageUri: Uri? = null

    private var storagePageReference:StorageReference? = null

    //OBJECTS CREATE PAGE
    private lateinit var selectIcon_TV:TextView
    private lateinit var createPage_Btn:AppCompatButton
    private lateinit var pageDescription:EditText
    private lateinit var pageName:EditText
    private lateinit var LL_pageDetails_ETs:LinearLayout
    private lateinit var pageIcon_Image:ImageView
    private lateinit var pageDetails_Head:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_page)

        //OBJECTS DECLARATION
        selectIcon_TV = findViewById(R.id.selectIcon_TV)
        createPage_Btn = findViewById(R.id.createPage_Btn)
        pageDescription = findViewById(R.id.pageDescription)
        pageName = findViewById(R.id.pageName)
        LL_pageDetails_ETs = findViewById(R.id.LL_pageDetails_ETs)
        pageIcon_Image = findViewById(R.id.pageIcon_Image)
        pageDetails_Head = findViewById(R.id.pageDetails_Head)



        storagePageReference = FirebaseStorage.getInstance().reference.child("PageData")

        selectIcon_TV.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this)
        }

        createPage_Btn.setOnClickListener {

            when{
                createPage_Btn.text == "Next"->{

                    if (pageDescription.text.isEmpty() && pageName.text.isEmpty()){
                        Toast.makeText(
                            this,
                            "Please enter the required credential to create the page",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        LL_pageDetails_ETs.visibility = View.GONE
                        selectIcon_TV.visibility = View.VISIBLE
                        pageIcon_Image.visibility = View.VISIBLE
                        createPage_Btn.text = "Create"
                        pageDetails_Head.text = "Choose Your Page Icon"
                    }

                }
                createPage_Btn.text == "Create"->{
                    if (imageUri == null){
                        Toast.makeText(
                            this,
                            "Please choose the icon to proceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        createPage()
                    }
                }

            }



        }
    }

    private fun createPage(){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()

        val fileReference = storagePageReference!!
            .child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask:StorageTask<*>
        uploadTask = fileReference.putFile(imageUri!!)

        uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation fileReference.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri>{task ->

            if (task.isSuccessful){
                val downloadUrl = task.result
                iconUrl = downloadUrl.toString()

                val ref = FirebaseDatabase.getInstance().reference.child("Pages")

                val pageId = ref.push().key

                val pageDataMap = HashMap<String,Any>()
                pageDataMap["pageId"] = pageId!!
                pageDataMap["pageIcon"] = iconUrl
                pageDataMap["pageName"] = pageName.text.toString()
                pageDataMap["pageDescription"] = pageDescription.text.toString()
                pageDataMap["pageAdmin"] = FirebaseAuth.getInstance().currentUser!!.uid

                ref.child(pageId).updateChildren(pageDataMap)

                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("FollowingPages")
                    .child(pageId)
                    .setValue(true)

                FirebaseDatabase.getInstance().reference.child("Pages")
                    .child(pageId)
                    .child("pageFollowers")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .setValue(true)

                Toast.makeText(this,"Page Created Successfully", Toast.LENGTH_SHORT).show()
                finish()
                progressDialog.dismiss()
            }else{
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK&& data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            pageIcon_Image.setImageURI(imageUri)
            selectIcon_TV.text = "Change Icon"
        }
    }

}