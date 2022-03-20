package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.activity_verification_request.*

class VerificationRequest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_request)

        requester_verification.setOnClickListener {
            requestVerification()
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




}