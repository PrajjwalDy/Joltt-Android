package com.hindu.cunow.Fragments.FollowRequest

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Adapter.FollowRequestAdapter
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ConfessionRoomFragmentBinding
import com.hindu.cunow.databinding.FollowRequestFragmentBinding

class FollowRequest : Fragment() {

    var recycleView: RecyclerView? = null
    private var requestAdapter: FollowRequestAdapter? = null

    private lateinit var ViewModel: FollowRequestViewModel
    private var _binding: FollowRequestFragmentBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = FollowRequest()
    }

    private lateinit var viewModel: FollowRequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(FollowRequestViewModel::class.java)
        _binding = FollowRequestFragmentBinding.inflate(inflater,container,false)
        val root: View = binding.root
        initView(root)

        viewModel.followRequestViewModel!!.observe(viewLifecycleOwner, Observer {
            requestAdapter = context?.let { it1-> FollowRequestAdapter(it1,it)}
            recycleView!!.adapter = requestAdapter
            requestAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun initView(root:View){
        recycleView = root.findViewById(R.id.follow_Request_RV) as RecyclerView
        recycleView!!.setHasFixedSize(true)
        recycleView!!.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recycleView!!.layoutManager = linearLayoutManager

    }

}