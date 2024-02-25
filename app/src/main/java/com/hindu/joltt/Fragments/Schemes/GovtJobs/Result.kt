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
import com.hindu.cunow.databinding.FragmentResultBinding
import com.hindu.joltt.Adapter.admissionAdapter
import com.hindu.joltt.Adapter.resultAdapter

class Result : Fragment() {

    private lateinit var viewModel: ResultViewModel
    var recyclerView: RecyclerView? = null

    private var resultAdapter:resultAdapter? = null
    private var _binding:FragmentResultBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(ResultViewModel::class.java)
        _binding = FragmentResultBinding.inflate(inflater,container, false)
        val root:View = binding.root

        viewModel.resultModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            resultAdapter = context?.let { it1-> resultAdapter(it1, it) }
            recyclerView!!.adapter = resultAdapter
            resultAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.resultRV)
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

}