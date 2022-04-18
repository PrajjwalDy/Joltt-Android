package com.hindu.cunow.Fragments.AboutMe.MyDetails

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.my_details_fragment.*
import kotlinx.android.synthetic.main.my_details_fragment.view.*

class MyDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = MyDetailsFragment()
    }

    private lateinit var viewModel: MyDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.my_details_fragment, container, false)

        retrieveUserData(root)

        return root
    }

    private fun retrieveUserData(root:View) {
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
                    root.from_profile.text = users!!.place
                    root.branch_profile.text = users.branch
                    root.year_profile.text = users.year
                    root.section_profile.text = users.section

                    if (users.male){
                        root.genderImage_profile.setImageResource(R.drawable.male)
                    }else if (users.female){
                        root.genderImage_profile.setImageResource(R.drawable.female)
                    }

                    if (users.single){
                        root.RS_Image_profile.setImageResource(R.drawable.happy)
                        root.RS_Text_profile.text = "Single"
                    }else if (users.committed){
                        root.RS_Image_profile.setImageResource(R.drawable.comitted)
                        root.RS_Text_profile.text = "Committed"
                    }else if (users.crush){
                        root.RS_Image_profile.setImageResource(R.drawable.crush)
                        root.RS_Text_profile.text = "Have a Crush"
                    }

                    if (users.hostler){
                        root.hostelName_profile.text = "of "+users.hostelName
                    }else{
                        root.isHosteler_profile.text = "Day-Scholar from"
                        root.hostelName_profile.text = users.hostelName
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }



}