package com.hindu.joltt.Fragments.HashTag

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
import com.hindu.cunow.databinding.FragmentHasTagBinding
import com.hindu.joltt.Adapter.HashTagAdapter

class HasTagFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    private var hasTagAdapter: HashTagAdapter? = null
    private var _binding: FragmentHasTagBinding? =null
    private val binding get() = _binding!!

    private lateinit var viewModel: HasTagViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(HasTagViewModel::class.java)

        _binding = FragmentHasTagBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.hashTagModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            hasTagAdapter = context?.let { it1-> HashTagAdapter(it1,it) }
            recyclerView!!.adapter = hasTagAdapter
            hasTagAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.hasTag_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }
}