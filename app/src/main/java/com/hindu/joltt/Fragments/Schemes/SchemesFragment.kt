package com.hindu.joltt.Fragments.Schemes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentSchemesBinding
import com.hindu.joltt.Adapter.SchemeAdapter

class SchemesFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: SchemesViewModel
    private var _binding: FragmentSchemesBinding? = null
    private val binding get() = _binding!!
    private var schemesAdapter: SchemeAdapter? = null

    private lateinit var governmentschemeBack: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SchemesViewModel::class.java)
        _binding = FragmentSchemesBinding.inflate(inflater,container,false)
        val root:View = binding.root

        governmentschemeBack = root.findViewById(R.id.governmentschemeBack)


        viewModel.schemeViewModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            schemesAdapter = context?.let { it1-> SchemeAdapter(it1,it) }
            recyclerView!!.adapter = schemesAdapter
            schemesAdapter!!.notifyDataSetChanged()
        })

        governmentschemeBack.setOnClickListener {
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