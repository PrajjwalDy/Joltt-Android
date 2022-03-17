package com.hindu.cunow.Fragments.Circle.JoinedCircles

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Adapter.JoinedCircleAdapter
import com.hindu.cunow.R
import com.hindu.cunow.databinding.JoinedCirclesFragmentBinding

class JoinedCirclesFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private var circleAdapter: JoinedCircleAdapter? = null

    private var _binding: JoinedCirclesFragmentBinding? =null

    private val binding get() = _binding!!

    private lateinit var viewModel: JoinedCirclesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[JoinedCirclesViewModel::class.java]
        _binding = JoinedCirclesFragmentBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.circleViewModel.observe(viewLifecycleOwner, Observer {
            initView(root)
            circleAdapter = context?.let { it1-> JoinedCircleAdapter(it1,it) }
            recyclerView!!.adapter = circleAdapter
            circleAdapter!!.notifyDataSetChanged()
        })



        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.joined_circles_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager

    }

}