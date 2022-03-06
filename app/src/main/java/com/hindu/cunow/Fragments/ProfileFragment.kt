package com.hindu.cunow.Fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.EditProfileActivity
import com.hindu.cunow.Activity.SettingActivity
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.view.*

class ProfileFragment : Fragment() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profileId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.profile_fragment, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("uid","none")!!
        }

        userInfo()
        root.editProfile_button.setOnClickListener{
            startActivity(Intent(context,EditProfileActivity::class.java))
        }

        root.open_options.setOnClickListener {
            root.profile_option_ll.visibility = View.VISIBLE
            root.open_options.visibility = View.GONE
            root.close_options.visibility = View.VISIBLE
        }

        root.close_options.setOnClickListener {
            root.profile_option_ll.visibility = View.GONE
            root.open_options.visibility = View.VISIBLE
            root.close_options.visibility = View.GONE
        }

        root.settings_account.setOnClickListener {
            val intent = Intent(context,SettingActivity::class.java)
            startActivity(intent)
        }


        return  root
    }

    private fun userInfo(){
        val progressDialog = context?.let { Dialog(it) }
        progressDialog!!.setContentView(R.layout.profile_dropdown_menu)
        progressDialog.show()
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val userData = snapshot.getValue(UserModel::class.java)
                    context?.let { Glide.with(it).load(userData!!.profileImage).into(profileImage_profilePage) }
                    fullName_profilePage.text = userData!!.fullName
                    user_bio.text = userData.bio
                    if (userData.verification){
                        verification_profilePage.visibility = View.VISIBLE
                    }
                }
                progressDialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                println("some error occurred")
            }
        })
    }
}