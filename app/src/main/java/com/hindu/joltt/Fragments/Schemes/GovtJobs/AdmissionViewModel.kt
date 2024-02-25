package com.hindu.joltt.Fragments.Schemes.GovtJobs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Callback.IAdmissionCallback
import com.hindu.joltt.Callback.IGovtJobs
import com.hindu.joltt.Model.AdmissionModel
import com.hindu.joltt.Model.GjobModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdmissionViewModel : ViewModel(), IAdmissionCallback {
    private var AdmissionLiveData: MutableLiveData<List<AdmissionModel>>? = null
    private val IAdmissionCallback: IAdmissionCallback= this
    private var messageError: MutableLiveData<String>? = null

    val admissionModel: MutableLiveData<List<AdmissionModel>>?

        get() {
            if (AdmissionLiveData == null){
                AdmissionLiveData = MutableLiveData()
                messageError = MutableLiveData()

                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }
            }

            return AdmissionLiveData
        }

    private fun loadData(){
        val jobList = ArrayList<AdmissionModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Admission")
        data.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                jobList.clear()
                for (snapshot in snapshot.children){
                    val data = snapshot.getValue((AdmissionModel::class.java))
                    jobList.add(data!!)
                }

                IAdmissionCallback.onEventLoadSuccess(jobList)
            }

            override fun onCancelled(error: DatabaseError) {
                IAdmissionCallback.onEventLoadFailed(error.message)
            }

        })
    }

    override fun onEventLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onEventLoadSuccess(list: List<AdmissionModel>) {
        val mutableLiveData = AdmissionLiveData
        mutableLiveData!!.value = list
    }

}