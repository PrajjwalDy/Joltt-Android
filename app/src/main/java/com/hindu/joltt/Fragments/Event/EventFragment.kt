package com.hindu.joltt.Fragments.Event

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
import com.hindu.cunow.databinding.FragmentEventBinding
import com.hindu.joltt.Adapter.EventAdapter

class EventFragment : Fragment() {
    var recyclerView:RecyclerView? = null
    private var eventAdapter: EventAdapter? = null
    private lateinit var viewModel: EventViewModel
    private var _binding:FragmentEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventBack:ImageView
    private lateinit var eventTxt:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        _binding = FragmentEventBinding.inflate(inflater,container,false)
        val root:View = binding.root

        eventBack = root.findViewById(R.id.eventBack)
        eventTxt = root.findViewById(R.id.eventTxt)


        viewModel.eventModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            eventAdapter = context?.let { it1-> EventAdapter(it1,it) }
            recyclerView!!.adapter = eventAdapter
            eventAdapter!!.notifyDataSetChanged()
        })


        eventBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_eventFragment_to_navigation_dashboard)
        }
        eventTxt.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_eventFragment_to_navigation_dashboard)
        }
        return root
    }
    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.event_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManger = LinearLayoutManager(context)
        linearLayoutManger.reverseLayout = true
        linearLayoutManger.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManger
    }
}