package com.hindu.joltt.Fragments.Hackathons

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
import com.hindu.cunow.databinding.FragmentHackathonsBinding
import com.hindu.joltt.Adapter.HackathonAdapter
import com.hindu.joltt.Model.HackathonModel

class HackathonsFragment : Fragment() {

    var recyclerView:RecyclerView? = null
    private lateinit var viewModel: HackathonsViewModel
    private var _binding:FragmentHackathonsBinding? = null
    private val binding get() = _binding!!
    private var hackathonAdapter: HackathonAdapter? = null

    private lateinit var filterSpinner:Spinner
    private val filterList: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(HackathonsViewModel::class.java)
        _binding = FragmentHackathonsBinding.inflate(inflater,container,false)
        val root:View = binding.root


        filterSpinner = root.findViewById(R.id.filterSpinner)
        //setupFirebase()
        setupFirebase()
        setupFilterUI()

        //viewModel.filterHackathons("THEME BLOCKCHAIN")
        viewModel.getAllHackathonsLiveData().observe(viewLifecycleOwner, Observer { hackathons ->
            // Initialize RecyclerView and Adapter here
            initView(root)
            hackathonAdapter?.setItems(hackathons)
        })

        viewModel.getFilteredHackathonsLiveData().observe(viewLifecycleOwner, Observer { filteredHackathons ->
            // Update RecyclerView adapter with filtered data
            initView(root)
            hackathonAdapter?.setItems(filteredHackathons)
        })

        // Call loadData() to fetch hackathon data from Firebase
        viewModel.loadData()



        return root
    }



    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.hackRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager

        hackathonAdapter = HackathonAdapter(requireContext())
        recyclerView!!.adapter = hackathonAdapter

    }


    private fun setupFirebase() {
        val filterSet = HashSet<String>()

        val databaseReference = FirebaseDatabase.getInstance().getReference("Hackathons")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val item = snapshot.getValue(HackathonModel::class.java)
                    item?.let { it.hTheme?.let { it1 -> filterSet.add(it1) } }
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
         //Observe changes in the filter list
//        viewModel.getHackathonsLiveData().observe(viewLifecycleOwner, Observer { hackathons ->
//            val themes = hackathons.map { it.hTheme }.distinct()
//            updateFilterUI(themes)
//        })


        // Assuming you have a dropdown or buttons for filter options
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedTheme = filterSpinner.selectedItem.toString()
                if (selectedTheme == "View All"){
                    viewModel.getAllHackathonsLiveData()
                }else{
                    viewModel.filterHackathons(selectedTheme)
                }


            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

}
