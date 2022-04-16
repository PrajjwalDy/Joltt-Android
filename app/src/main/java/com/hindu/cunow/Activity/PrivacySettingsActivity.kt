package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_circle_permission.*
import kotlinx.android.synthetic.main.activity_privacy_settings.*

class PrivacySettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_settings)

        checkAccountPrivacy()

        accountPrivacy_private.setOnClickListener { view->
            _private(view)
        }
        accountPrivacy_public.setOnClickListener { view->
            _public(view)
        }

    }

    private fun checkAccountPrivacy(){
        val userData = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        userData.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user!!.private){
                        accountPrivacy_private.setBackgroundColor(resources.getColor(R.color.red))
                        accountPrivacy_private.setTextColor(resources.getColor(R.color.white))
                        accountPrivacy_public.setBackgroundColor(resources.getColor(R.color.white))
                        accountPrivacy_public.setTextColor(resources.getColor(R.color.red))
                    }else if(!user.private){
                        accountPrivacy_private.setBackgroundColor(resources.getColor(R.color.white))
                        accountPrivacy_private.setTextColor(resources.getColor(R.color.red))
                        accountPrivacy_public.setBackgroundColor(resources.getColor(R.color.red))
                        accountPrivacy_public.setTextColor(resources.getColor(R.color.white))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun _private(view:View){
        val userData = FirebaseDatabase.getInstance().reference
            .child("Users")

        val dataMap = HashMap<String,Any>()
        dataMap["private"] = true
        userData.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(dataMap)
        Snackbar.make(view,"Privacy Updated", Snackbar.LENGTH_SHORT).show()
    }

    private fun _public(view: View){
        val userData = FirebaseDatabase.getInstance().reference
            .child("Users")

        val dataMap = HashMap<String,Any>()
        dataMap["private"] = false
        userData.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(dataMap)
        Snackbar.make(view,"Privacy Updated", Snackbar.LENGTH_SHORT).show()
        checkAccountPrivacy()
    }

}