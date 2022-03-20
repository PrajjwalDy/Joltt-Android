package com.hindu.cunow.Fragments.AboutMe.Posts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Adapter.PostAdapter
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentHomeBinding
import com.hindu.cunow.databinding.MyPostsFragemtFragmentBinding

class MyPostsFragemt : Fragment() {

    var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null

    private lateinit var viewModel: MyPostsFragemtViewModel
    private var _binding: MyPostsFragemtFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(MyPostsFragemtViewModel::class.java)

        _binding = MyPostsFragemtFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.postModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            postAdapter = context?.let { it1-> PostAdapter(it1,it) }
            recyclerView!!.adapter = postAdapter
            postAdapter!!.notifyDataSetChanged()

        })

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

}