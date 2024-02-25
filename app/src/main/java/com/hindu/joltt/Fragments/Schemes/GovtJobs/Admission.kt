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
import com.hindu.cunow.databinding.FragmentAdmissionBinding
import com.hindu.joltt.Adapter.EventAdapter
import com.hindu.joltt.Adapter.admissionAdapter

class Admission : Fragment() {

    private lateinit var viewModel: AdmissionViewModel
    var recyclerView: RecyclerView? = null
    private var admissionAdapter: admissionAdapter? = null

    private var _binding:FragmentAdmissionBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this).get(AdmissionViewModel::class.java)
        _binding = FragmentAdmissionBinding.inflate(inflater, container, false)
        val root:View = binding.root

        viewModel.admissionModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            admissionAdapter = context?.let { it1-> admissionAdapter(it1,it) }
            recyclerView!!.adapter = admissionAdapter
            admissionAdapter!!.notifyDataSetChanged()
        })

        return root
    }
    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.admissionRV)
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

}