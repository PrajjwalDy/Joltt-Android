package com.hindu.joltt.Fragments.Jobs

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

class JobsViewModel : ViewModel(), IClubsCallback {
    private var clubLiveData: MutableLiveData<List<ClubModel>>? = null
    private val clubCallback: IClubsCallback = this
    private var messageError: MutableLiveData<String>? = null

    val clubModel: MutableLiveData<List<ClubModel>>?
        get()  {
            if (clubLiveData == null){
                clubLiveData = MutableLiveData()
                messageError = MutableLiveData()
                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }

            }
            return clubLiveData
        }

    private fun loadData(){
        val clubList = ArrayList<ClubModel>()
        val data = FirebaseDatabase.getInstance().reference.child("Jobs")
        data.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clubList.clear()
                for (snapshot in snapshot.children){
                    val data = snapshot.getValue((ClubModel::class.java))
                    clubList.add(data!!)
                }
                clubCallback.onClubListLoadSuccess(clubList)
            }

            override fun onCancelled(error: DatabaseError) {
                clubCallback.onClubListLoadFailed(error.message)
            }

        })
    }

    override fun onClubListLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onClubListLoadSuccess(list: List<ClubModel>) {
        val mutableLiveData = clubLiveData
        mutableLiveData!!.value = list
    }
}