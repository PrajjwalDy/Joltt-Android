package com.hindu.joltt.Activity

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.R

class FacultyVerificationActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfileImageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_verification)

//        storageProfileImageRef = FirebaseStorage.getInstance()
//            .reference
//            .child("Faculty IDs")
//
//        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
//        FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener { task ->
//            if (firebaseUser!!.isEmailVerified) {
//                //checkStatus()
//            } else {
//                Toast.makeText(this, "Account isn't verified yet!", Toast.LENGTH_SHORT).show()
//            }
//        }

        //startActivity()


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

}