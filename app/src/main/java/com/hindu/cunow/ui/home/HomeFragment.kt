package com.hindu.cunow.ui.home

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.text.observe(viewLifecycleOwner, Observer {

            root.post_img.setOnClickListener { view->
                postText(root)

            }

            root.post_txt.setOnClickListener { view->
                postText(root)
            }

        })
        return root
    }

    private fun postText(view: View){
        Snackbar.make(view,"adding post....",Snackbar.LENGTH_SHORT).show()

        val ref = FirebaseDatabase.getInstance().reference.child("Post")
        val postId = ref.push().key

        val postMap = HashMap<String,Any>()
        postMap["postId"] = postId!!
        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
        postMap["caption"] = caption_only.text.toString()

        ref.child(postId).updateChildren(postMap)

        Snackbar.make(view,"post added successfully",Snackbar.LENGTH_SHORT).show()

        caption_only.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}