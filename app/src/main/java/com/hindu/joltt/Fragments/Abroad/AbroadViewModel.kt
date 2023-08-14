package com.hindu.joltt.Fragments.Abroad

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Callback.IAbroadCallback
import com.hindu.joltt.Model.AbroadModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AbroadViewModel : ViewModel(), IAbroadCallback {
    private var abroadLiveData: MutableLiveData<List<AbroadModel>>? = null
    private val abroadCallback: IAbroadCallback = this
    private var messageError: MutableLiveData<String>? = null

    val abroadView: MutableLiveData<List<AbroadModel>>
        get() {
            if (abroadLiveData == null) {
                abroadLiveData = MutableLiveData()
                messageError = MutableLiveData()
                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }
            }
            val mutableLiveData = abroadLiveData
            return mutableLiveData!!
        }

    private fun loadData() {
        val list = ArrayList<AbroadModel>()
        val dbRef = FirebaseDatabase.getInstance().reference.child("AbroadSchemes")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (snapshot in snapshot.children) {
                    val data = snapshot.getValue(AbroadModel::class.java)
                    list.add(data!!)
                }
                abroadCallback.onAbroadListLoadSuccess(list)
            }


            override fun onCancelled(error: DatabaseError) {
                abroadCallback.onAbroadListLoadFailed(error.message)
            }

        })
    }

    override fun onAbroadListLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onAbroadListLoadSuccess(list: List<AbroadModel>) {
        val mutableLiveData = abroadLiveData
        mutableLiveData!!.value = list
    }


}