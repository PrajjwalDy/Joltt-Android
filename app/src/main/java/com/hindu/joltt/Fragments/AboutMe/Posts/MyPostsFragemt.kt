package com.hindu.joltt.Fragments.AboutMe.Posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.cunow.databinding.MyPostsFragemtFragmentBinding
import com.hindu.joltt.Adapter.PostAdapter
import com.hindu.joltt.Adapter.PostGridAdapter
import kotlinx.android.synthetic.main.my_posts_fragemt_fragment.view.myPostsRV
import kotlinx.android.synthetic.main.my_posts_fragemt_fragment.view.myPostsRV_grid
import kotlinx.android.synthetic.main.my_posts_fragemt_fragment.view.postGrid_profile
import kotlinx.android.synthetic.main.my_posts_fragemt_fragment.view.postVertical_profile

class MyPostsFragemt : Fragment() {

    var recyclerView: RecyclerView? = null
    var recyclerViewGrid:RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var postGridAdapter: PostGridAdapter? = null

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

            //Grid View
            initView2(root)
            postGridAdapter = context?.let { it1-> PostGridAdapter(it1,it) }
            recyclerViewGrid!!.adapter = postGridAdapter
            postGridAdapter!!.notifyDataSetChanged()

        })

        root.postGrid_profile.setOnClickListener {
            root.myPostsRV.visibility = View.GONE
            root.myPostsRV_grid.visibility = View.VISIBLE
        }
        root.postVertical_profile.setOnClickListener {
            root.myPostsRV_grid.visibility = View.GONE
            root.myPostsRV.visibility = View.VISIBLE
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