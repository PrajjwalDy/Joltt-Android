package com.hindu.cunow.Fragments.Circle.MyCircles

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
import com.hindu.cunow.Adapter.CircleAdapter
import com.hindu.cunow.Adapter.JoinedCircleAdapter
import com.hindu.cunow.Fragments.Circle.ExploreCircles.ExploreCirclesFragmentsViewModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ExploreCirclesFragmentsFragmentBinding
import com.hindu.cunow.databinding.MyCirclesFragmentBinding

class MyCirclesFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private var circleAdapter: CircleAdapter? = null


    private var _binding: MyCirclesFragmentBinding? =null

    private val binding get() = _binding!!

    private lateinit var viewModel: MyCirclesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MyCirclesViewModel::class.java)
        _binding = MyCirclesFragmentBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.circleViewModel!!.observe(viewLifecycleOwner, Observer {
            init(root)
            circleAdapter = context?.let { it1-> CircleAdapter(it1,it) }
            recyclerView!!.adapter = circleAdapter
            circleAdapter!!.notifyDataSetChanged()
        })

        return root
    }

    private fun init(root: View) {

        recyclerView = root.findViewById(R.id.myCircle_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager

    }


}