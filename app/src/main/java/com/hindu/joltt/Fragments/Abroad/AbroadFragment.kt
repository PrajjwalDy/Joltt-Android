package com.hindu.joltt.Fragments.Abroad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentAbroadBinding
import com.hindu.joltt.Adapter.AbroadAdapter
import com.hindu.joltt.Model.AbroadModel
import kotlinx.android.synthetic.main.fragment_abroad.view.abroadBack
import kotlinx.android.synthetic.main.fragment_abroad.view.abroadTxt

class AbroadFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private var abroadAdapter: AbroadAdapter? = null

    private lateinit var viewModel: AbroadViewModel
    private var _binding:FragmentAbroadBinding? = null
    private var abroadModel:MutableList<AbroadModel>? = null
    private val binding get() = _binding!!
    val filteredPosts = mutableListOf<AbroadModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AbroadViewModel::class.java)
        _binding = FragmentAbroadBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.abroadView.observe(viewLifecycleOwner, Observer {
            initView(root)
            abroadAdapter = context?.let { it1-> AbroadAdapter(it1,it) }
            recyclerView!!.adapter = abroadAdapter
            abroadAdapter!!.notifyDataSetChanged()
        })




        root.abroadBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_abroadFragment_to_navigation_dashboard)
        }

        root.abroadTxt.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_abroadFragment_to_navigation_dashboard)
        }
        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.abroadRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout =true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager

    }

   /* private fun getData()
    {
        // Step 1: Retrieve the user's interests from Firebase
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserUid = currentUser?.uid

        // Assuming you have a "users" node in your database with "interests" field as a list
        val userReference = FirebaseDatabase.getInstance().reference.child("UserInterest")
        userReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val interestData = dataSnapshot.child(currentUserUid!!).value

                if (interestData is Map<*, *>){
                    val userInterest =interestData.values.toList().filterIsInstance<String>()

                    // Step 2: Fetch posts with matching interests
                    val postsReference = FirebaseDatabase.getInstance().getReference("AbroadSchemes")

                    postsReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            val filtered = mutableListOf<AbroadModel>()

                            for (postSnapshot in dataSnapshot.children) {
                                val post = postSnapshot.getValue(AbroadModel::class.java)
                                post?.let {
                                    val postTags: String? = it.tag
                                    //filteredPosts.clear()
                                    for (interest in userInterest) {
                                        if (postTags != null && userInterest.any { interest -> postTags.contains(interest, ignoreCase = true) }) {
                                            //filteredPosts.add(it)
                                            filtered.add(it)
                                            break
                                        }
                                    }
                                }
                            }

                            // Step 3: Update the RecyclerView adapter with filtered posts

                            abroadAdapter!!.setData(filtered)
                            abroadAdapter!!.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context,"Code Reached here "+error.message, Toast.LENGTH_LONG).show()
                        }
                    })
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Code Reached here"+error.message, Toast.LENGTH_LONG).show()
            }
        })
    }*/


}