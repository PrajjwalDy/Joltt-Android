package com.hindu.cunow.Fragments.PublicPost

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Adapter.PostGridAdapter
import com.hindu.cunow.Fragments.AboutMe.Posts.MyPostsFragemtViewModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.MyPostsFragemtFragmentBinding
import com.hindu.cunow.databinding.PublicPostFragementFragmentBinding

class PublicPostFragement : Fragment() {

    var recyclerViewGrid: RecyclerView? = null
    private var postGridAdapter: PostGridAdapter? = null

    private lateinit var viewModel: PublicPostFragementViewModel
    private var _binding:PublicPostFragementFragmentBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(PublicPostFragementViewModel::class.java)

        _binding = PublicPostFragementFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.postModel!!.observe(viewLifecycleOwner, Observer {
            initView2(root)
            postGridAdapter = context?.let { it1-> PostGridAdapter(it1,it) }
            recyclerViewGrid!!.adapter = postGridAdapter
            postGridAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun initView2(root: View){
        recyclerViewGrid = root.findViewById(R.id.explorePost_rv) as RecyclerView
        recyclerViewGrid!!.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewGrid!!.layoutManager = linearLayoutManager
    }

}