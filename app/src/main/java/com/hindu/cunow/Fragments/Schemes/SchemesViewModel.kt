package com.hindu.cunow.Fragments.Schemes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IInternshipCallback
import com.hindu.cunow.Callback.ISchemeCallback
import com.hindu.cunow.Model.InternshipModel
import com.hindu.cunow.Model.SchemeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SchemesViewModel : ViewModel(), ISchemeCallback {
    private var schemeLiveData: MutableLiveData<List<SchemeModel>>? = null
    private val schemeCallback: ISchemeCallback = this
    private var messageError: MutableLiveData<String>? = null

    val schemeViewModel: MutableLiveData<List<SchemeModel>>?
        get() {
            if (schemeLiveData == null) {
                schemeLiveData = MutableLiveData()
                messageError = MutableLiveData()
                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }
            }
            return schemeLiveData
        }

    private fun loadData(){
        val interList = ArrayList<SchemeModel>()
        val data = FirebaseDatabase.getInstance().reference.child("GovtSchemes")
        data.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                interList.clear()
                for(snapshot in snapshot.children){
                    val data = snapshot.getValue((SchemeModel::class.java))
                    interList.add(data!!)
                }
                schemeCallback.onSchemeLoadSuccess(interList)
            }

            override fun onCancelled(error: DatabaseError) {
                schemeCallback.onSchemeLoadFailed(error.message)
            }

        })
    }

    override fun onSchemeLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onSchemeLoadSuccess(list: List<SchemeModel>) {
        val mutableLiveData = schemeLiveData
        mutableLiveData!!.value = list
    }
}