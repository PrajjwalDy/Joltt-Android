package com.hindu.joltt.Fragments.Jobs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentJobsBinding
import com.hindu.joltt.Adapter.ClubsAdapter
import com.hindu.joltt.Model.ClubModel
import com.hindu.joltt.Model.InternshipModel

class JobsFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private lateinit var viewModel: JobsViewModel
    private var clubsAdapter: ClubsAdapter? = null
    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    private lateinit var jobsBack:ImageView
    private lateinit var jobsTxt:TextView

    private lateinit var filterSpinner: Spinner
    private val filterList: MutableList<String> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(JobsViewModel::class.java)
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        jobsTxt = root.findViewById(R.id.jobsTxt)
        jobsBack = root.findViewById(R.id.jobsBack)


        filterSpinner = root.findViewById(R.id.filterSpinnerJobs)
        setupFirebase()
        setupFilterUI()


        viewModel.getAllJobsLiveData().observe(viewLifecycleOwner, Observer{internships->
            initView(root)
            clubsAdapter?.setItems(internships)
        })


        viewModel.getFilteredInternshipsLiveData().observe(viewLifecycleOwner, Observer{filteredInternship->
            initView(root)
            clubsAdapter?.setItems(filteredInternship)
        })

        viewModel.loadData()



        jobsBack.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_jobsFragment_to_navigation_dashboard)
        }
        jobsTxt.setOnClickListener {
            Navigation.findNavController(root)
                .navigate(R.id.action_jobsFragment_to_navigation_dashboard)
        }


        return root
    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.clubsRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManger = LinearLayoutManager(context)
        linearLayoutManger.reverseLayout = true
        linearLayoutManger.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManger

        clubsAdapter = ClubsAdapter(requireContext())
        recyclerView!!.adapter = clubsAdapter

    }


    private fun setupFirebase() {
        val filterSet = HashSet<String>()

        val databaseReference = FirebaseDatabase.getInstance().getReference("Jobs")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val item = snapshot.getValue(ClubModel::class.java)
                    item?.let { it.jobsTrade?.let { it1 -> filterSet.add(it1) } }
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
                    viewModel.getAllJobsLiveData().observe(viewLifecycleOwner) { internships ->
                        clubsAdapter?.setItems(internships)
                    }
                } else {
                    viewModel.getFilteredInternshipsLiveData().observe(viewLifecycleOwner) { filteredInternship ->
                        clubsAdapter?.setItems(filteredInternship)
                    }
                    viewModel.filterJobs(selectedTheme)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
}