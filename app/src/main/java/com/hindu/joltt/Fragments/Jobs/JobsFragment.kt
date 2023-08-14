package com.hindu.joltt.Fragments.Jobs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentJobsBinding
import com.hindu.joltt.Adapter.ClubsAdapter
import kotlinx.android.synthetic.main.fragment_jobs.view.jobsBack
import kotlinx.android.synthetic.main.fragment_jobs.view.jobsTxt

class JobsFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: JobsViewModel
    private var clubsAdapter: ClubsAdapter? = null
    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(JobsViewModel::class.java)
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        viewModel.clubModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            clubsAdapter = context?.let { it1-> ClubsAdapter(it1,it) }
            recyclerView!!.adapter = clubsAdapter
            clubsAdapter!!.notifyDataSetChanged()
        })



        root.jobsBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_jobsFragment_to_navigation_dashboard)
        }
        root.jobsTxt.setOnClickListener {
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