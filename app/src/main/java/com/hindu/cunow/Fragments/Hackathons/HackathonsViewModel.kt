package com.hindu.cunow.Fragments.Hackathons

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.hindu.cunow.Callback.IHackathonCallback
import com.hindu.cunow.Model.HackathonModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HackathonsViewModel : ViewModel(), IHackathonCallback {
    private var hackathonLiveData: MutableLiveData<List<HackathonModel>>? = null
    private val hackCallback: IHackathonCallback = this
    private var messageError:MutableLiveData<String>? = null

    val hackathonModel:MutableLiveData<List<HackathonModel>>?
    get() {
        if (hackathonLiveData == null){
            hackathonLiveData = MutableLiveData()
            messageError = MutableLiveData()
            CoroutineScope(Dispatchers.IO).launch {
                loadData()
            }
        }
        return hackathonLiveData
    }

    private fun loadData(){
        val hackList = ArrayList<HackathonModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Hackathons")
        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                hackList.clear()
                for (snapshot in snapshot.children){
                    val d1 = snapshot.getValue(HackathonModel::class.java)
                    hackList.add(d1!!)
                }
                hackCallback.onHackathonLoadSuccess(hackList)
            }

            override fun onCancelled(error: DatabaseError) {
                hackCallback.onHackathonLoadFailed(error.message)
            }

        })
    }

    override fun onHackathonLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onHackathonLoadSuccess(list: List<HackathonModel>) {
        val mutableLiveData = hackathonLiveData
        mutableLiveData!!.value = list
    }


}