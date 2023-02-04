package com.hindu.cunow.Fragments.Hackathons

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Adapter.HackathonAdapter
import com.hindu.cunow.Fragments.Internship.InternshipsViewModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentHackathonsBinding

class HackathonsFragment : Fragment() {

    var recyclerView:RecyclerView? = null
    private lateinit var viewModel: HackathonsViewModel
    private var _binding:FragmentHackathonsBinding? = null
    private val binding get() = _binding!!
    private var hackathonAdapter:HackathonAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(HackathonsViewModel::class.java)
        _binding = FragmentHackathonsBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.hackathonModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            hackathonAdapter = context?.let { it1-> HackathonAdapter(it1,it) }
            recyclerView!!.adapter = hackathonAdapter
            hackathonAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.hackRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager

    }


}