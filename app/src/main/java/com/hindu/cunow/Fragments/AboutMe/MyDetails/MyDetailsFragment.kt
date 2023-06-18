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
                    root.year_profile.text = "Year: "+users.year

                    if (users.male){
                        root.genderImage_profile.setImageResource(R.drawable.male)
                        gender_txt.text = "Male"
                        if (users.single){
                            root.RS_Image_profile.setImageResource(R.drawable.single_male)
                            root.relation_txt.text = "Single"
                        }else if (users.committed){
                            root.RS_Image_profile.setImageResource(R.drawable.com_male)
                            root.relation_txt.text = "Committed"
                        }else if (users.crush){
                            root.RS_Image_profile.setImageResource(R.drawable.crush_male)
                            root.relation_txt.text = "Have a Crush"
                        }
                    }else if (users.female){
                        root.genderImage_profile.setImageResource(R.drawable.female)
                        gender_txt.text = "Female"
                        if (users.single){
                            root.RS_Image_profile.setImageResource(R.drawable.single)
                            root.relation_txt.text = "Single"
                        }else if (users.committed){
                            root.RS_Image_profile.setImageResource(R.drawable.com_female)
                            root.relation_txt.text = "Committed"
                        }else if (users.crush){
                            root.RS_Image_profile.setImageResource(R.drawable.crush_female)
                            root.relation_txt.text = "Have a Crush"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }



}