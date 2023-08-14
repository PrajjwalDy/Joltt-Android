package com.hindu.joltt.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_faculty_verification.add_ID_image_faculty
import kotlinx.android.synthetic.main.activity_faculty_verification.email_verify_refresh_button
import kotlinx.android.synthetic.main.activity_faculty_verification.faculty_document_upload_cv
import kotlinx.android.synthetic.main.activity_faculty_verification.faculty_eid_verify
import kotlinx.android.synthetic.main.activity_faculty_verification.faculty_email_verification_cv
import kotlinx.android.synthetic.main.activity_faculty_verification.faculty_name_verify
import kotlinx.android.synthetic.main.activity_faculty_verification.faculty_uMail_verify
import kotlinx.android.synthetic.main.activity_faculty_verification.idCard_image_preview
import kotlinx.android.synthetic.main.activity_faculty_verification.id_upload_txt
import kotlinx.android.synthetic.main.activity_faculty_verification.logout_fv
import kotlinx.android.synthetic.main.activity_faculty_verification.submit_data_faculty
import kotlinx.android.synthetic.main.activity_faculty_verification.under_verification_window

class FacultyVerificationActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfileImageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_verification)

        storageProfileImageRef = FirebaseStorage.getInstance()
            .reference
            .child("Faculty IDs")

        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener { task ->
            if (firebaseUser!!.isEmailVerified) {
                checkStatus()
            } else {
                Toast.makeText(this, "Account isn't verified yet!", Toast.LENGTH_SHORT).show()
            }
        }

        //startActivity()
        email_verify_refresh_button.setOnClickListener {
            val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener { task ->
                if (firebaseUser!!.isEmailVerified) {

                } else {
                    Toast.makeText(this, "Account isn't verified yet!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        submit_data_faculty.setOnClickListener {
            uploadData()
        }

        add_ID_image_faculty.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@FacultyVerificationActivity)
        }

        logout_fv.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@FacultyVerificationActivity, LandingPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

    }


    private fun uploadData() {
        when {
            TextUtils.isEmpty(faculty_eid_verify.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@FacultyVerificationActivity,
                    "Please enter your EID",
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(faculty_name_verify.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@FacultyVerificationActivity,
                    "Please enter your Name",
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(faculty_uMail_verify.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@FacultyVerificationActivity,
                    "Please enter your University mail",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                val dataRef = FirebaseDatabase.getInstance()
                    .reference.child("FacultyVerification")

                val dataMap = HashMap<String, Any>()
                dataMap["FID"] = FirebaseAuth.getInstance().currentUser!!.uid
                dataMap["F_Name"] = faculty_name_verify.text.toString()
                dataMap["F_UMail"] = faculty_uMail_verify.text.toString()
                dataMap["F_EID"] = faculty_eid_verify.text.toString()
                dataMap["V_status"] = "Under Process"
                dataMap["V_message"] = "Under Process"

                dataRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .updateChildren(dataMap)

                uploadImage()

                Toast.makeText(this, "application submitted for verification", Toast.LENGTH_SHORT)
                    .show()

                faculty_document_upload_cv.visibility = View.GONE
                under_verification_window.visibility = View.VISIBLE
            }
        }

    }

    private fun uploadImage() {
        if (imageUri == null) {
            Toast.makeText(this, "You haven't selected any picture", Toast.LENGTH_SHORT).show()
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
                    val ref = FirebaseDatabase.getInstance().reference.child("FacultyVerification")
                    val mapData = HashMap<String, Any>()
                    mapData["F_ID_Image"] = myUrl

                    ref.child(
                        FirebaseAuth
                            .getInstance()
                            .currentUser!!
                            .uid
                    ).updateChildren(mapData)
                    progressDialog.dismiss()
                } else {
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun checkStatus() {
        val dataRef = FirebaseDatabase.getInstance()
            .reference.child("FacultyVerification")
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)) {
                    val data = snapshot.getValue()
                    faculty_email_verification_cv.visibility = View.GONE
                    faculty_document_upload_cv.visibility = View.GONE
                    under_verification_window.visibility = View.VISIBLE
                }else{
                    faculty_email_verification_cv.visibility = View.GONE
                    faculty_document_upload_cv.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            add_ID_image_faculty.visibility = View.GONE
            id_upload_txt.visibility = View.GONE
            idCard_image_preview.visibility = View.VISIBLE
            idCard_image_preview.setImageURI(imageUri)

            //updateProfileImage()
        }

    }
}