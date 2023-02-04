package com.hindu.cunow.Fragments.Event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IEventCallback
import com.hindu.cunow.Model.EventModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventViewModel : ViewModel(), IEventCallback {
    private var eventLiveData: MutableLiveData<List<EventModel>>? = null
    private val eventCallback: IEventCallback = this
    private var messageError: MutableLiveData<String>? = null

    val eventModel: MutableLiveData<List<EventModel>>?
        get() {
            if (eventLiveData == null) {
                eventLiveData = MutableLiveData()
                messageError = MutableLiveData()
                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }

            }
            return eventLiveData
        }


    private fun loadData(){
        val eventList = ArrayList<EventModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Events")
        data.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                for(snapshot in snapshot.children){
                    val data = snapshot.getValue((EventModel::class.java))
                    eventList.add(data!!)
                }
                eventCallback.onEventLoadSuccess(eventList)
            }

            override fun onCancelled(error: DatabaseError) {
                eventCallback.onEventLoadFailed(error.message)
            }

        })
    }
    override fun onEventLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onEventLoadSuccess(list: List<EventModel>) {
        val mutableLiveData = eventLiveData
        mutableLiveData!!.value = list
    }

}