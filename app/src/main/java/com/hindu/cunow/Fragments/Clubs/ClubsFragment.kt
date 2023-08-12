package com.hindu.cunow.Fragments.Clubs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.get
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Adapter.ClubsAdapter
import com.hindu.cunow.Adapter.CourseAdapter
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentClubs2Binding
import kotlinx.android.synthetic.main.fragment_clubs2.view.*

class ClubsFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private lateinit var viewModel: ClubsViewModel
    private var clubsAdapter:ClubsAdapter? = null
    private var _binding:FragmentClubs2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ClubsViewModel::class.java)
        _binding = FragmentClubs2Binding.inflate(inflater,container, false)
        val root:View = binding.root

        viewModel.clubModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            clubsAdapter = context?.let { it1-> ClubsAdapter(it1,it) }
            recyclerView!!.adapter = clubsAdapter
            clubsAdapter!!.notifyDataSetChanged()
        })

        root.jobsBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_clubsFragment_to_navigation_dashboard)
        }
        root.jobsTxt.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_clubsFragment_to_navigation_dashboard)
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