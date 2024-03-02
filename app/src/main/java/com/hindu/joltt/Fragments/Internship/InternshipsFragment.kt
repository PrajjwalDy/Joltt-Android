package com.hindu.joltt.Fragments.Internship

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentInternshipsBinding
import com.hindu.joltt.Model.InternshipModel

class InternshipsFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: InternshipsViewModel
    private var internshipAdapter:InternshipAdapter? = null
    private var _binding:FragmentInternshipsBinding? = null
    private val binding get() = _binding!!

    private lateinit var filterSpinner: Spinner
    private val filterList: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(InternshipsViewModel::class.java)
        _binding = FragmentInternshipsBinding.inflate(inflater,container,false)
        val root:View = binding.root


        filterSpinner = root.findViewById(R.id.filterSpinnerIntern)
        setupFirebase()
        setupFilterUI()


        viewModel.getAllInternshipsLiveData().observe(viewLifecycleOwner, Observer{internships->
            initView(root)
            internshipAdapter?.setItems(internships)
        })


        viewModel.getFilteredInternshipsLiveData().observe(viewLifecycleOwner, Observer{filteredInternship->
            initView(root)
            internshipAdapter?.setItems(filteredInternship)
        })

        viewModel.loadData()

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.intershipRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManger = LinearLayoutManager(context)
        linearLayoutManger.reverseLayout = true
        linearLayoutManger.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManger

        internshipAdapter = InternshipAdapter(requireContext())
        recyclerView!!.adapter = internshipAdapter
    }


    private fun setupFirebase() {
        val filterSet = HashSet<String>()

        val databaseReference = FirebaseDatabase.getInstance().getReference("Internships")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val item = snapshot.getValue(InternshipModel::class.java)
                    item?.let { it.iTheme?.let { it1 -> filterSet.add(it1) } }
                }

                // Now, filterSet contains unique themes
                filterList.clear()
                filterList.add(0, "View All")
                filterList.addAll(filterSet)

                // Update the UI with the filter list
                updateFilterUI(filterList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun updateFilterUI(filterList: List<String?>) {
        // Create an ArrayAdapter to populate the Spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter on the Spinner
        filterSpinner.adapter = adapter
    }


    private fun setupFilterUI() {

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedTheme = filterSpinner.selectedItem.toString()
                if (selectedTheme == "View All") {
                    viewModel.getAllInternshipsLiveData().observe(viewLifecycleOwner) { internships ->
                        internshipAdapter?.setItems(internships)
                    }
                } else {
                    viewModel.getFilteredInternshipsLiveData().observe(viewLifecycleOwner) { filteredInternship ->
                        internshipAdapter?.setItems(filteredInternship)
                    }
                    viewModel.filterInternships(selectedTheme)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
}