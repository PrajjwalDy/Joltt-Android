package com.hindu.joltt.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.R
import com.theartofdev.edmodo.cropper.CropImage

class VerificationRequest : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfileImageRef: StorageReference? = null

    private lateinit var Verification_Back:ImageView
    private lateinit var id_proofImage:ImageView
    private lateinit var proof_txt:TextView

    private lateinit var requester_verification:AppCompatButton

    private lateinit var edit_TextAddress:EditText
    private lateinit var editTextAdhaar:EditText
    private lateinit var editTextUID:EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_request)

        Verification_Back = findViewById(R.id.Verification_Back)
        id_proofImage = findViewById(R.id.id_proofImage)
        proof_txt = findViewById(R.id.proof_txt)

        requester_verification = findViewById(R.id.requester_verification)

        edit_TextAddress = findViewById(R.id.edit_TextAddress)
        editTextAdhaar = findViewById(R.id.editTextAdhaar)
        editTextUID = findViewById(R.id.editTextUID)





        storageProfileImageRef = FirebaseStorage.getInstance()
            .reference
            .child("ID Proof")

        Verification_Back.setOnClickListener {
            finish()
        }

        id_proofImage.setOnClickListener {
            CropImage.activity()
                .start(this@VerificationRequest)
        }

        proof_txt.setOnClickListener {
            CropImage.activity()
                .start(this@VerificationRequest)
        }

        requester_verification.setOnClickListener {
            updateProfileImage()
        }
    }

    private fun requestVerification() {
        if(edit_TextAddress.text.isEmpty() || editTextAdhaar.text.isEmpty() ||  editTextUID.text.isEmpty()){
            Toast.makeText(this,"Field must not be blank",Toast.LENGTH_LONG).show()
        }else{
            val ref = FirebaseDatabase.getInstance().reference.child("Verification_Request")
            val requestId = ref.push().key

            val postMap = HashMap<String, Any>()
            postMap["requestId"] = requestId!!
            postMap["fullName_verify"] = edit_TextAddress.text.toString()
            postMap["adhaarNo"] = editTextAdhaar.text.toString()
            postMap["uid_verify"] = editTextUID.text.toString()

            ref.child(requestId).updateChildren(postMap)
            edit_TextAddress.text.clear()
            editTextAdhaar.text.clear()
            editTextUID.text.clear()
            Toast.makeText(this,"requested",Toast.LENGTH_LONG).show()
        }


    }

    private fun updateProfileImage() {
        if (imageUri == null) {
            Toast.makeText(this, "You haven't selected any proof", Toast.LENGTH_SHORT).show()
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
                    val ref = FirebaseDatabase.getInstance().reference.child("Verification_Request")
                    val requestId = ref.push().key
                    val postMap = HashMap<String, Any>()
                    postMap["idProof"] = myUrl
                    postMap["requestId"] = requestId!!
                    postMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                    postMap["fullName_verify"] = edit_TextAddress.text.toString()
                    postMap["adhaarNo"] = editTextAdhaar.text.toString()
                    postMap["uid_verify"] = editTextUID.text.toString()

                    ref.child(requestId).updateChildren(postMap)
                    edit_TextAddress.text.clear()
                    editTextAdhaar.text.clear()
                    editTextUID.text.clear()
                    Toast.makeText(this,"requested",Toast.LENGTH_LONG).show()

                    ref.child(
                        FirebaseAuth
                            .getInstance()
                            .currentUser!!
                            .uid
                    ).updateChildren(postMap)
                    progressDialog.dismiss()
                    Toast.makeText(this, "Verification Request", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    progressDialog.dismiss()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
                val result = CropImage.getActivityResult(data)
                imageUri = result.uri
                id_proofImage.setImageURI(imageUri)
            }
    }



}