package com.hindu.joltt.Fragments.Internship

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentInternshipsBinding

class InternshipsFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: InternshipsViewModel
    private var internshipAdapter:InternshipAdapter? = null
    private var _binding:FragmentInternshipsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(InternshipsViewModel::class.java)
        _binding = FragmentInternshipsBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.internshipModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            internshipAdapter = context?.let { it1-> InternshipAdapter(it1,it) }
            recyclerView!!.adapter = internshipAdapter
            internshipAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.intershipRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManger = LinearLayoutManager(context)
        linearLayoutManger.reverseLayout = true
        linearLayoutManger.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManger
    }


}