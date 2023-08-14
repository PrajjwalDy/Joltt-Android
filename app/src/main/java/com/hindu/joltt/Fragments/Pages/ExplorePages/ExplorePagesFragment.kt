package com.hindu.joltt.Fragments.Pages.ExplorePages

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
import com.hindu.cunow.databinding.ExplorePagesFragmentBinding
import com.hindu.joltt.Adapter.PageAdapter

class ExplorePagesFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private var pageAdapter: PageAdapter? = null

    private lateinit var viewModel: ExplorePagesViewModel
    private var _binding:ExplorePagesFragmentBinding? = null

    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ExplorePagesViewModel::class.java)

        _binding = ExplorePagesFragmentBinding.inflate(inflater,container,false)
        val root:View = binding!!.root

        viewModel.pageViewModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            pageAdapter = context?.let { it1-> PageAdapter(it1,it) }
            recyclerView!!.adapter = pageAdapter
            pageAdapter!!.notifyDataSetChanged()
        })

        return root
    }
    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.explorePages_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager:LinearLayoutManager= GridLayoutManager(context,2)
        recyclerView!!.layoutManager = linearLayoutManager
    }

}