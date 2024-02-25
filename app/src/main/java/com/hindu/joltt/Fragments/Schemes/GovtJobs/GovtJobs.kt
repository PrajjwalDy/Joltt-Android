package com.hindu.joltt.Fragments.Schemes.GovtJobs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentGovtJobsBinding
import com.hindu.joltt.Adapter.gJobAdapter

class GovtJobs : Fragment() {

    private lateinit var viewModel: GovtJobsViewModel
    var recyclerView:RecyclerView? = null
    private var gJobAdapter: gJobAdapter? = null

    private var _binding:FragmentGovtJobsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(GovtJobsViewModel::class.java)
        _binding = FragmentGovtJobsBinding.inflate(inflater, container, false)
        val root:View = binding.root

        viewModel.jobModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            gJobAdapter = context?.let { it1->gJobAdapter(it1,it) }
            recyclerView!!.adapter = gJobAdapter
            gJobAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.gJobsRV)
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

}