package com.hindu.cunow.Fragments.Academics

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
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
import com.hindu.cunow.Model.FacultyData
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentAcademicsBinding
import com.hindu.cunow.databinding.FragmentHomeBinding
import com.hindu.cunow.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_academics.*
import kotlinx.android.synthetic.main.fragment_academics.view.*

class AcademicsFragment : Fragment() {

    private lateinit var viewModel: AcademicsViewModel
    private var _binding: FragmentAcademicsBinding? = null
    private  val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AcademicsViewModel::class.java)

        _binding = FragmentAcademicsBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val user : String = FirebaseAuth.getInstance().currentUser!!.uid
        checkUser(root,user)
        return root
        //return inflater.inflate(R.layout.fragment_academics, container, false)
    }

    private fun checkUser(root:View,user: String){
        val progressDialog = context?.let { Dialog(it) }
        progressDialog!!.setContentView(R.layout.profile_dropdown_menu)
        progressDialog.show()

        val userData = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(user.toString())

        userData.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(UserModel::class.java)
                if(data!!.facultY){
                    root.addSubjects.visibility = View.VISIBLE
                }else{
                    root.addSubjects.visibility = View.GONE
                    root.welcome_txt_academics.text = "No subjects to preview"
                }
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                print(error)
            }
        })

        userData.keepSynced(true)

    }
}