package com.hindu.joltt.Fragments.AboutMe.MySaved

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
import com.hindu.cunow.databinding.MySavedFragmentBinding
import com.hindu.joltt.Adapter.PostAdapter
import com.hindu.joltt.Adapter.PostGridAdapter
import kotlinx.android.synthetic.main.my_saved_fragment.view.myPostsRV_grid_saved
import kotlinx.android.synthetic.main.my_saved_fragment.view.myPostsRV_saved
import kotlinx.android.synthetic.main.my_saved_fragment.view.postGrid_saved
import kotlinx.android.synthetic.main.my_saved_fragment.view.postVertical_saved

class MySavedFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var recyclerViewGrid: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var postGridAdapter: PostGridAdapter? = null

    private lateinit var viewModel: MySavedViewModel
    private var _binding: MySavedFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(MySavedViewModel::class.java)

        _binding = MySavedFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.postModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            postAdapter = context?.let { it1-> PostAdapter(it1,it,"MySaved") }
            recyclerView!!.adapter = postAdapter
            postAdapter!!.notifyDataSetChanged()

            //Grid View
            initView2(root)
            postGridAdapter = context?.let { it1-> PostGridAdapter(it1,it) }
            recyclerViewGrid!!.adapter = postGridAdapter
            postGridAdapter!!.notifyDataSetChanged()

        })

        root.postGrid_saved.setOnClickListener {
            root.myPostsRV_saved.visibility = View.GONE
            root.myPostsRV_grid_saved.visibility = View.VISIBLE
        }
        root.postVertical_saved.setOnClickListener {
            root.myPostsRV_grid_saved.visibility = View.GONE
            root.myPostsRV_saved.visibility = View.VISIBLE
        }

        return root
    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.myPostsRV_saved) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

    private fun initView2(root: View){
        recyclerViewGrid = root.findViewById(R.id.myPostsRV_grid_saved) as RecyclerView
        recyclerViewGrid!!.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewGrid!!.layoutManager = linearLayoutManager
    }


}