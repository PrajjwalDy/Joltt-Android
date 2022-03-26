package com.hindu.cunow.Fragments.CircleDetails

import android.content.Context
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
import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.circle_details_fragment.*
import kotlinx.android.synthetic.main.circle_details_fragment.view.*

class CircleDetails : Fragment() {

    private lateinit var circleId:String
    private lateinit var admin: String
    private lateinit var firebaseUser:FirebaseUser

    private lateinit var viewModel: CircleDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View= inflater.inflate(R.layout.circle_details_fragment, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.circleId = pref.getString("circleId","none")!!
            this.admin = pref.getString("admin","none")!!
        }

        getCircleDetails(root)
        getAdmin(root)


        return root
    }

    private fun getCircleDetails(root:View){
        val dataRef = FirebaseDatabase.getInstance().reference.child("Circle").child(circleId)

        dataRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(CircleModel::class.java)
                    Glide.with(context!!).load(data!!.icon).into(circleIcon_details)
                    circle_name_details.text = data.circleName
                    circle_description_details.text = data.circle_description
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getAdmin(root: View){

        val userData = FirebaseDatabase.getInstance().reference.child("Users").child(admin)
        userData.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    Glide.with(context!!).load(user!!.profileImage).into(root.profileImage_circleAdmin)
                    root.fullName_admin.text = user.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}