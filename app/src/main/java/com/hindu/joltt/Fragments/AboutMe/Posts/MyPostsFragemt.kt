package com.hindu.joltt.Fragments.AboutMe.Posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hindu.cunow.R
import com.hindu.cunow.databinding.MyPostsFragemtFragmentBinding
import com.hindu.joltt.Adapter.PostAdapter
import com.hindu.joltt.Adapter.PostGridAdapter

class MyPostsFragemt : Fragment() {

    var recyclerView: RecyclerView? = null
    var recyclerViewGrid:RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var postGridAdapter: PostGridAdapter? = null

    private lateinit var viewModel: MyPostsFragemtViewModel
    private var _binding: MyPostsFragemtFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var postGrid_profile:ImageView
    private lateinit var myPostsRV:RecyclerView
    private lateinit var myPostsRV_grid:RecyclerView

    private lateinit var postVertical_profile:ImageView
    private lateinit var fab_savedPost:FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(MyPostsFragemtViewModel::class.java)

        _binding = MyPostsFragemtFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root


        postGrid_profile = root.findViewById(R.id.postGrid_profile)
        myPostsRV = root.findViewById(R.id.myPostsRV)
        myPostsRV_grid = root.findViewById(R.id.myPostsRV_grid)
        postVertical_profile = root.findViewById(R.id.postVertical_profile)
        fab_savedPost = root.findViewById(R.id.fab_savedPost)


        viewModel.postModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            postAdapter = context?.let { it1-> PostAdapter(it1,it,"Profile") }
            recyclerView!!.adapter = postAdapter
            postAdapter!!.notifyDataSetChanged()

            //Grid View
            initView2(root)
            postGridAdapter = context?.let { it1-> PostGridAdapter(it1,it) }
            recyclerViewGrid!!.adapter = postGridAdapter
            postGridAdapter!!.notifyDataSetChanged()

        })

        postGrid_profile.setOnClickListener {
            myPostsRV.visibility = View.GONE
            myPostsRV_grid.visibility = View.VISIBLE
        }
        postVertical_profile.setOnClickListener {
            myPostsRV_grid.visibility = View.GONE
            myPostsRV.visibility = View.VISIBLE
        }
        fab_savedPost.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_myPostsFragemt_to_mySavedFragment2)
        }

        return root
    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.myPostsRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

    private fun initView2(root: View){
        recyclerViewGrid = root.findViewById(R.id.myPostsRV_grid) as RecyclerView
        recyclerViewGrid!!.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewGrid!!.layoutManager = linearLayoutManager
    }

}