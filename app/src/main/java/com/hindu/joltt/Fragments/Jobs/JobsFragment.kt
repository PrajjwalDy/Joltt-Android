package com.hindu.joltt.Fragments.Jobs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentJobsBinding
import com.hindu.joltt.Adapter.ClubsAdapter

class JobsFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: JobsViewModel
    private var clubsAdapter: ClubsAdapter? = null
    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    private lateinit var jobsBack:ImageView
    private lateinit var jobsTxt:TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(JobsViewModel::class.java)
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        jobsTxt = root.findViewById(R.id.jobsTxt)
        jobsBack = root.findViewById(R.id.jobsBack)




        viewModel.clubModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            clubsAdapter = context?.let { it1-> ClubsAdapter(it1,it) }
            recyclerView!!.adapter = clubsAdapter
            clubsAdapter!!.notifyDataSetChanged()
        })



        jobsBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_jobsFragment_to_navigation_dashboard)
        }
        jobsTxt.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_jobsFragment_to_navigation_dashboard)
        }


        return root
    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.clubsRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManger = LinearLayoutManager(context)
        linearLayoutManger.reverseLayout = true
        linearLayoutManger.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManger


    }
}