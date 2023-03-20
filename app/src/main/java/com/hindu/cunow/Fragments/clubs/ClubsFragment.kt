package com.hindu.cunow.Fragments.clubs

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Activity.AddClubActivity
import com.hindu.cunow.Adapter.ClubsAdapter
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentClubsBinding
import kotlinx.android.synthetic.main.fragment_clubs.view.*

class ClubsFragment : Fragment() {

    private lateinit var viewModel: ClubsViewModel
    var recyclerView:RecyclerView? = null
    private var _binding:FragmentClubsBinding? = null
    private val binding get() = _binding!!
    private var clubAdapter: ClubsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ClubsViewModel::class.java)
        _binding = FragmentClubsBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.clubView!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            clubAdapter = context?.let { it1-> ClubsAdapter(it1,it) }
            recyclerView!!.adapter = clubAdapter
            clubAdapter!!.notifyDataSetChanged()
        })

        root.FAB_create_club.setOnClickListener {
            startActivity(Intent(context,AddClubActivity::class.java))
        }

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.club_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager:LinearLayoutManager = GridLayoutManager(context,2)
        recyclerView!!.layoutManager = linearLayoutManager
    }


}