package com.hindu.cunow.Fragments.Schemes

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
import com.hindu.cunow.Adapter.HackathonAdapter
import com.hindu.cunow.Adapter.SchemeAdapter
import com.hindu.cunow.Fragments.Hackathons.HackathonsViewModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentHackathonsBinding
import com.hindu.cunow.databinding.FragmentSchemesBinding
import kotlinx.android.synthetic.main.fragment_schemes.view.*

class SchemesFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: SchemesViewModel
    private var _binding: FragmentSchemesBinding? = null
    private val binding get() = _binding!!
    private var schemesAdapter: SchemeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SchemesViewModel::class.java)
        _binding = FragmentSchemesBinding.inflate(inflater,container,false)
        val root:View = binding.root

        viewModel.schemeViewModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            schemesAdapter = context?.let { it1-> SchemeAdapter(it1,it) }
            recyclerView!!.adapter = schemesAdapter
            schemesAdapter!!.notifyDataSetChanged()
        })

        root.governmentschemeBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_schemesFragment_to_navigation_dashboard)
        }

        return root
    }
    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.schemes_RV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

}