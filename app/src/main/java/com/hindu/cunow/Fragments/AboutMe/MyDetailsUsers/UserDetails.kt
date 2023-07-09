    package com.hindu.cunow.Fragments.AboutMe.MyDetailsUsers

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.fragment_user_details.view.*
import kotlinx.android.synthetic.main.my_details_fragment.view.*

    class UserDetails : Fragment() {
        private lateinit var profileId:String
        private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View = inflater.inflate(R.layout.fragment_user_details, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("uid","none")!!
        }

        retrieveUserData(root)

        return root
    }

    private fun retrieveUserData(root:View) {
        val dataRef = FirebaseDatabase
            .getInstance().reference
            .child("Users")
            .child(
               profileId
            )

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users = snapshot.getValue(UserModel::class.java)
                    root.from_users.text = users!!.place
                    root.branch_user.text = users.branch
                    root.year_users.text = users.year

                    if (users.male){
                        root.genderImage_user.setImageResource(R.drawable.male)
                    }else if (users.female){
                        root.genderImage_user.setImageResource(R.drawable.female)
                    }

                    if (users.single){
                        root.RS_Image_users.setImageResource(R.drawable.happy)
                        root.RS_Text_Users.text = "Single"
                    }else if (users.committed){
                        root.RS_Image_users.setImageResource(R.drawable.comitted)
                        root.RS_Text_Users.text = "Committed"
                    }else if (users.crush){
                        root.RS_Image_users.setImageResource(R.drawable.crush)
                        root.RS_Text_Users.text = "Have a Crush"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}