package com.hindu.joltt.Fragments.FollowRequest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Callback.IFollowRequestCallback
import com.hindu.joltt.Model.RequestModel

class FollowRequestViewModel : ViewModel(), IFollowRequestCallback {
    private var requestLiveData:MutableLiveData<List<RequestModel>>? = null

    private val requestCallback:IFollowRequestCallback = this
    private var messageError:MutableLiveData<String>? = null

    val followRequestViewModel:MutableLiveData<List<RequestModel>>?
    get() {
        if (requestLiveData ==null){
            requestLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadFollowRequest()
        }
        val mutabaleLiveData = requestLiveData
        return mutabaleLiveData
    }

    private fun loadFollowRequest() {
        val requestList = ArrayList<RequestModel>()
        val database = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(FirebaseAuth.getInstance()
                .currentUser!!.uid)
            .child("Requesters")

        database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    val dataModel = snapshot.getValue(RequestModel::class.java)
                    requestList.add(dataModel!!)
                }
                requestCallback.onRequestCallbackLoadSuccess(requestList)
            }

            override fun onCancelled(error: DatabaseError) {
               requestCallback.onRequestCallbackLoadFailed(error.message)
            }

        })
    }

    override fun onRequestCallbackLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onRequestCallbackLoadSuccess(list: List<RequestModel>) {
        val mutableLiveData = requestLiveData
        mutableLiveData!!.value = list
    }
}