package com.hindu.cunow.Fragments.community

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.ICommunityCallback
import com.hindu.cunow.Model.CommunityModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel(), ICommunityCallback {
    private var communityLiveData: MutableLiveData<List<CommunityModel>>? = null
    private val communityCallback: ICommunityCallback = this
    private var messageError: MutableLiveData<String>? = null

    val communityModel: MutableLiveData<List<CommunityModel>>?
        get() {
            if (communityLiveData == null) {
                communityLiveData = MutableLiveData()
                messageError = MutableLiveData()
                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }

            }
            return communityLiveData
        }

    private fun loadData(){
        val eventList = ArrayList<CommunityModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Community")
        data.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                for(snapshot in snapshot.children){
                    val data = snapshot.getValue((CommunityModel::class.java))
                    eventList.add(data!!)
                }
                communityCallback.onCommunityCallbackLoadSuccess(eventList)
            }

            override fun onCancelled(error: DatabaseError) {
                communityCallback.onCommunityCallbackLoadFailed(error.message)
            }

        })
    }

    override fun onCommunityCallbackLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onCommunityCallbackLoadSuccess(list: List<CommunityModel>) {
        val mutableLiveData = communityLiveData
        mutableLiveData!!.value = list
    }


}