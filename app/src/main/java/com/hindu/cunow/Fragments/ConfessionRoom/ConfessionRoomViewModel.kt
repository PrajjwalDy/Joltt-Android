package com.hindu.cunow.Fragments.ConfessionRoom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IConfessionCallback
import com.hindu.cunow.Model.ConfessionModel

class ConfessionRoomViewModel : ViewModel(), IConfessionCallback {
    private var confessionLiveData:MutableLiveData<List<ConfessionModel>>? = null

    private val confessionCallback:IConfessionCallback = this
    private var messageError:MutableLiveData<String>? = null

    val confessionViewModel:MutableLiveData<List<ConfessionModel>>?
    get() {
        if (confessionLiveData == null){
            confessionLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadConfession()
        }
        val mutableLiveData = confessionLiveData
        return mutableLiveData
    }

    private fun loadConfession(){
        val confessionList = ArrayList<ConfessionModel>()
        val database = FirebaseDatabase.getInstance().reference.child("Confession")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                confessionList.clear()
                for (snapshot in snapshot.children){
                    val dataModel = snapshot.getValue(ConfessionModel::class.java) as ConfessionModel
                    confessionList.add(dataModel)
                }
                confessionCallback.onConfessionCallbackLoadSuccess(confessionList)
            }

            override fun onCancelled(error: DatabaseError) {
                confessionCallback.onConfessionCallbackLoadFailed(error.message)
            }

        })
    }

    override fun onConfessionCallbackLoadFailed(str: String) {
       val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onConfessionCallbackLoadSuccess(list: List<ConfessionModel>) {
        val mutableLiveDate = confessionLiveData
        mutableLiveDate!!.value = list
    }


}