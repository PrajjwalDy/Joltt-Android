package com.hindu.cunow.Fragments.clubs

import android.media.VolumeAutomation
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IClubsCallback
import com.hindu.cunow.Model.ClubModel

class ClubsViewModel : ViewModel(), IClubsCallback {
    private var clubLiveData:MutableLiveData<List<ClubModel>>? = null

    private val clubCallback:IClubsCallback = this
    private var messageError:MutableLiveData<String>? = null

    val clubView:MutableLiveData<List<ClubModel>>?
    get() {
        if (clubLiveData == null){
            clubLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadClub()
        }
        return clubLiveData
    }

    private fun loadClub(){
        val list = ArrayList<ClubModel>()
        val database = FirebaseDatabase.getInstance().reference
            .child("Clubs")
        database.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (snashot in snapshot.children){
                    val dataModel = snapshot.getValue(ClubModel::class.java)
                    list.add(dataModel!!)
                }
                clubCallback.onClubListLoadSuccess(list)
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
        val mutableLiveDate = clubLiveData
        mutableLiveDate!!.value = list
    }
}