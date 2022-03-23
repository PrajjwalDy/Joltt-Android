package com.hindu.cunow.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    var checker = ""
    var hosteler = ""
    var relation = ""

    private var myUrl = ""
    private var imageUri:Uri? = null
    private var storageProfileImageRef:StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val view  = View(this)

        storageProfileImageRef = FirebaseStorage.getInstance()
            .reference
            .child("Profile Images")
        retrieveUserData()

        change_ProfileImage_text.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@EditProfileActivity)
        }

        edit_gender_male.setOnClickListener {
            checker = "male"
            edit_gender_male.setBackgroundColor(resources.getColor(R.color.red))
            edit_gender_male.setTextColor(resources.getColor(R.color.white))
            edit_gender_female.setBackgroundColor(resources.getColor(R.color.white))
            edit_gender_female.setTextColor(resources.getColor(R.color.red))
        }
        edit_gender_female.setOnClickListener {
            checker = "female"
            edit_gender_female.setBackgroundColor(resources.getColor(R.color.red))
            edit_gender_female.setTextColor(resources.getColor(R.color.white))
            edit_gender_male.setBackgroundColor(resources.getColor(R.color.white))
            edit_gender_male.setTextColor(resources.getColor(R.color.red))
        }

        //Relation Button Deceleration
        edit_single.setOnClickListener {
            relation = "single"
            edit_single.setBackgroundColor(resources.getColor(R.color.pink))
            edit_single.setTextColor(resources.getColor(R.color.white))
            edit_mingle.setBackgroundColor(resources.getColor(R.color.white))
            edit_mingle.setTextColor(resources.getColor(R.color.red))
            edit_crush.setBackgroundColor(resources.getColor(R.color.white))
            edit_crush.setTextColor(resources.getColor(R.color.red))
        }
        edit_mingle.setOnClickListener {
            relation = "mingle"
            edit_mingle.setBackgroundColor(resources.getColor(R.color.pink))
            edit_mingle.setTextColor(resources.getColor(R.color.white))
            edit_single.setBackgroundColor(resources.getColor(R.color.white))
            edit_single.setTextColor(resources.getColor(R.color.red))
            edit_crush.setBackgroundColor(resources.getColor(R.color.white))
            edit_crush.setTextColor(resources.getColor(R.color.red))
        }
        edit_crush.setOnClickListener {
            relation = "crush"
            edit_crush.setBackgroundColor(resources.getColor(R.color.pink))
            edit_crush.setTextColor(resources.getColor(R.color.white))
            edit_single.setBackgroundColor(resources.getColor(R.color.white))
            edit_single.setTextColor(resources.getColor(R.color.red))
            edit_mingle.setBackgroundColor(resources.getColor(R.color.white))
            edit_mingle.setTextColor(resources.getColor(R.color.red))
        }


        edit_hosteler.setOnClickListener {
            hosteler = "yes"
            edit_hosteler.setBackgroundColor(resources.getColor(R.color.red))
            edit_hosteler.setTextColor(resources.getColor(R.color.white))
        }


        saveInformation.setOnClickListener {
            updateInformation(it)
        }

    }

    private fun updateInformation(view: View){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.common_loading_progress)
        progressDialog.show()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("Users")
        val dataMap = HashMap<String,Any>()
        dataMap["fullName"] = ProfileUserName_editText.text.toString()
        dataMap["bio"] = Bio_EditText.text.toString()
        dataMap["place"] = editTextAddress.text.toString()
        dataMap["branch"] = editTextBatch.text.toString()
        dataMap["year"] = editTextYear.text.toString()
        dataMap["hostler"] = hosteler == "yes"
        dataMap["section"] = editTextSection.text.toString()
        dataMap["hostelName"] = editTextHostel.text.toString()
        dataMap["male"] = checker == "male"
        dataMap["female"] = checker == "female"
        dataMap["single"] = relation == "single"
        dataMap["committed"] = relation == "mingle"
        dataMap["crush"] = relation == "crush"

        databaseRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(dataMap)
        progressDialog.dismiss()
        Snackbar.make(view,"Update successful", Snackbar.LENGTH_SHORT).show()
        finish()
    }

    private fun updateProfileImage() {
        if (imageUri == null) {
            Toast.makeText(this,"You haven't selected any picture",Toast.LENGTH_SHORT).show()
            //Snackbar.make(this@EditPr,"Update successful", Snackbar.LENGTH_SHORT).show()
        } else {
            val progressDialog = Dialog(this)
            progressDialog.setContentView(R.layout.common_loading_progress)
            progressDialog.show()

            val fileRef = storageProfileImageRef!!
                .child(
                    FirebaseAuth
                        .getInstance()
                        .currentUser!!
                        .uid + ".jpg"
                )
            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    myUrl = downloadUrl.toString()
                    val ref = FirebaseDatabase.getInstance().reference.child("Users")
                    val mapData = HashMap<String, Any>()
                    mapData["profileImage"] = myUrl

                    ref.child(
                        FirebaseAuth
                            .getInstance()
                            .currentUser!!
                            .uid
                    ).updateChildren(mapData)
                    progressDialog.dismiss()
                    retrieveUserData()
                    Toast.makeText(this,"Profile picture updated successfully",Toast.LENGTH_SHORT).show()
                } else {
                    progressDialog.dismiss()
                }
            }

        }
    }


    private fun retrieveUserData() {
        val dataRef = FirebaseDatabase
            .getInstance().reference
            .child("Users")
            .child(
                FirebaseAuth
                    .getInstance()
                    .currentUser!!.uid
            )

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users = snapshot.getValue(UserModel::class.java)
                    Glide.with(this@EditProfileActivity)
                        .load(users!!.profileImage)
                        .into(change_ProfileImage)
                    ProfileUserName_editText.setText(users.fullName)
                    Bio_EditText.setText(users.bio)
                    editTextAddress.setText(users.place)
                    editTextBatch.setText(users.branch)
                    editTextYear.setText(users.year)
                    editTextSection.setText(users.section)
                    editTextHostel.setText(users.hostelName)
                    if (users.male) {
                        checker ="male"
                        edit_gender_male.setBackgroundColor(resources.getColor(R.color.red))
                        edit_gender_male.setTextColor(resources.getColor(R.color.white))
                    } else if (users.female) {
                        checker ="female"
                        edit_gender_female.setBackgroundColor(resources.getColor(R.color.red))
                        edit_gender_female.setTextColor(resources.getColor(R.color.white))
                    }
                    if (users.hostler) {
                        hosteler="yes"
                        edit_hosteler.setBackgroundColor(resources.getColor(R.color.red))
                        edit_hosteler.setTextColor(resources.getColor(R.color.white))
                    } else {
                        edit_dayScholar.setBackgroundColor(resources.getColor(R.color.red))
                        edit_dayScholar.setTextColor(resources.getColor(R.color.white))
                    }
                    if (users.single){
                        relation = "single"
                        edit_single.setBackgroundColor(resources.getColor(R.color.pink))
                        edit_single.setTextColor(resources.getColor(R.color.white))
                        edit_mingle.setBackgroundColor(resources.getColor(R.color.white))
                        edit_mingle.setTextColor(resources.getColor(R.color.red))
                        edit_crush.setBackgroundColor(resources.getColor(R.color.white))
                        edit_crush.setTextColor(resources.getColor(R.color.red))
                    }else if (users.committed){
                        relation = "mingle"
                        edit_mingle.setBackgroundColor(resources.getColor(R.color.pink))
                        edit_mingle.setTextColor(resources.getColor(R.color.white))
                        edit_single.setBackgroundColor(resources.getColor(R.color.white))
                        edit_single.setTextColor(resources.getColor(R.color.red))
                        edit_crush.setBackgroundColor(resources.getColor(R.color.white))
                        edit_crush.setTextColor(resources.getColor(R.color.red))
                    } else if (users.crush){
                        relation = "crush"
                        edit_crush.setBackgroundColor(resources.getColor(R.color.pink))
                        edit_crush.setTextColor(resources.getColor(R.color.white))
                        edit_single.setBackgroundColor(resources.getColor(R.color.white))
                        edit_single.setTextColor(resources.getColor(R.color.red))
                        edit_mingle.setBackgroundColor(resources.getColor(R.color.white))
                        edit_mingle.setTextColor(resources.getColor(R.color.red))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
            {
                val result = CropImage.getActivityResult(data)
                imageUri = result.uri
                change_ProfileImage.setImageURI(imageUri)
                updateProfileImage()
            }

        }

    }
