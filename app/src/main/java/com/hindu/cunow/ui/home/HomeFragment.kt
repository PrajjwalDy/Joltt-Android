package com.hindu.cunow.ui.home

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.PostAdapter
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private var postAdapter:PostAdapter?= null
    private var postList:MutableList<PostModel>? = null
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private var recyclerVeiw: RecyclerView? = null
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

        //intitView(root)
        Async(root, String()).execute()
        homeViewModel.postModel!!.observe(viewLifecycleOwner, Observer{
            postAdapter = context?.let { it1 -> PostAdapter(it1,it) }
            recyclerVeiw!!.adapter = postAdapter
            postAdapter!!.notifyDataSetChanged()

            root.post_img.setOnClickListener {

                if (root.caption_only.text.isEmpty()){
                    Snackbar.make(root,"Please write something to post",Snackbar.LENGTH_SHORT).show()
                }else{
                    postText(root)
                }

            }


        })

        return root
    }

    inner class Async(val root: View,val value:String):AsyncTask<Any?,Void,String>(){

        override fun onPreExecute() {
            intitView(root)
            super.onPreExecute()
        }
        override fun doInBackground(vararg p0: Any?): String {
            intitView(root)
            return value
        }

        override fun onPostExecute(result: String?) {
            intitView(root)
            super.onPostExecute(result)
        }

    }

    private  fun intitView(root: View){
        recyclerVeiw = root.findViewById(R.id.postRecyclerView) as RecyclerView
        recyclerVeiw!!.setHasFixedSize(true)
        recyclerVeiw!!.layoutManager = LinearLayoutManager(context)
        //postText(root)
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

    private fun postImage(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}