package com.hindu.joltt.Fragments.Jobs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Callback.IClubsCallback
import com.hindu.joltt.Model.ClubModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JobsViewModel : ViewModel() {
    private val allJobsLiveData = MutableLiveData<List<ClubModel>>()
    private val filteredJobsLiveData = MutableLiveData<List<ClubModel>>()

    fun getAllJobsLiveData(): LiveData<List<ClubModel>> = allJobsLiveData
    fun getFilteredInternshipsLiveData(): LiveData<List<ClubModel>> = filteredJobsLiveData

    init {
        loadData()
    }


     fun loadData(){
        val clubList = ArrayList<ClubModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Jobs")
        data.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clubList.clear()
                for (snapshot in snapshot.children){
                    val data = snapshot.getValue((ClubModel::class.java))
                    clubList.add(data!!)
                }
                allJobsLiveData.value = clubList
            }

            override fun onCancelled(error: DatabaseError) {
                //clubCallback.onClubListLoadFailed(error.message)
            }

        })
    }

    fun filterJobs(selectedTheme:String){
        val allJobs = allJobsLiveData.value?:return
        val filteredJobs = if (selectedTheme.isNotEmpty()){
            allJobs.filter { it.jobsTrade == selectedTheme }
        }else{
            allJobs
        }
        filteredJobsLiveData.value = filteredJobs
    }
}