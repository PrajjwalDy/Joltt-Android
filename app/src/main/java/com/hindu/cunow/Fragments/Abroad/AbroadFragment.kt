package com.hindu.cunow.Fragments.Abroad

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.Adapter.AbroadAdapter
import com.hindu.cunow.Model.AbroadModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentAbroadBinding

class AbroadFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private var abroadAdapter:AbroadAdapter? = null

    private lateinit var viewModel: AbroadViewModel
    private var _binding:FragmentAbroadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AbroadViewModel::class.java)
        _binding = FragmentAbroadBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.abroadView!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            abroadAdapter = context?.let { it1-> AbroadAdapter(it1,it) }
            recyclerView!!.adapter = abroadAdapter
            abroadAdapter!!.notifyDataSetChanged()

        })

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.abroadRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout =true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager

    }

}