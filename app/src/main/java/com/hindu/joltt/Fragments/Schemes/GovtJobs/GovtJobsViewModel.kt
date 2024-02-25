package com.hindu.joltt.Fragments.Schemes.GovtJobs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Callback.IGovtJobs
import com.hindu.joltt.Model.GjobModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GovtJobsViewModel : ViewModel(), IGovtJobs {

    private var JobsLiveData: MutableLiveData<List<GjobModel>>? = null
    private val JobCallback:IGovtJobs = this
    private var messageError: MutableLiveData<String>? = null


    val jobModel: MutableLiveData<List<GjobModel>>?

        get() {
            if (JobsLiveData == null){
                JobsLiveData = MutableLiveData()
                messageError = MutableLiveData()

                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }
            }
            return JobsLiveData
        }

    private fun loadData(){
        val jobList = ArrayList<GjobModel>()
        val data = FirebaseDatabase.getInstance().reference.child("GovtJobs")
        data.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                jobList.clear()
                for (snapshot in snapshot.children){
                    val data = snapshot.getValue((GjobModel::class.java))
                    jobList.add(data!!)
                }

                JobCallback.onEventLoadSuccess(jobList)
            }

            override fun onCancelled(error: DatabaseError) {
                JobCallback.onEventLoadFailed(error.message)
            }

        })
    }

    override fun onEventLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }
    override fun onEventLoadSuccess(list: List<GjobModel>) {
       val mutableLiveData = JobsLiveData
        mutableLiveData!!.value = list
    }
}