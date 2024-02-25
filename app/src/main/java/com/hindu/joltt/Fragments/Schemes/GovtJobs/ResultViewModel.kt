package com.hindu.joltt.Fragments.Schemes.GovtJobs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Callback.IResultCallback
import com.hindu.joltt.Model.GjobModel
import com.hindu.joltt.Model.ResultModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultViewModel : ViewModel(), IResultCallback {
    private var ResultLiveData: MutableLiveData<List<ResultModel>>? = null
    private val IResultCallback: IResultCallback = this
    private var messageError: MutableLiveData<String>? = null


    val resultModel: MutableLiveData<List<ResultModel>>?

        get() {
            if (ResultLiveData == null){
                ResultLiveData = MutableLiveData()
                messageError = MutableLiveData()

                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }
            }
            return ResultLiveData
        }


    private fun loadData(){
        val jobList = ArrayList<ResultModel>()
        val data = FirebaseDatabase.getInstance().reference.child("ExamResult")
        data.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                jobList.clear()
                for (snapshot in snapshot.children){
                    val data = snapshot.getValue((ResultModel::class.java))
                    jobList.add(data!!)
                }

                IResultCallback.onEventLoadSuccess(jobList)
            }

            override fun onCancelled(error: DatabaseError) {
                IResultCallback.onEventLoadFailed(error.message)
            }

        })
    }

    override fun onEventLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onEventLoadSuccess(list: List<ResultModel>) {
        val mutableLiveData = ResultLiveData
        mutableLiveData!!.value = list
    }
}