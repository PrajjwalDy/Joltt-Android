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

        /*getCircleDetails(root)
        getAdmin(root)*/


        return root
    }






}