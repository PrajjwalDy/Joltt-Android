package com.hindu.cunow.Fragments.AboutMe.MyDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IinterestCallback
import com.hindu.cunow.Model.InterestModel

class MyDetailsViewModel : ViewModel(), IinterestCallback {
    private var interestLiveData: MutableLiveData<List<InterestModel>>? = null

    var myInterest:List<String>? = null
    private val interestCallback: IinterestCallback = this
    private var messageError: MutableLiveData<String>? = null


    val myInterestModel: MutableLiveData<List<InterestModel>>?
        get() {
            if (interestLiveData == null){
                interestLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadInterest()
            }
            return interestLiveData
        }

    private fun loadInterest() {
        val interestList = ArrayList<InterestModel>()
        myInterest = ArrayList()
        val database = FirebaseDatabase.getInstance().reference.child("UserInterest")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        database.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){
                   for (snapshot in snapshot.children){
                       (myInterest as ArrayList<String>).add(snapshot.key!!)
                   }
                   readInterest()
               }
                 interestCallback.onInterestListLoadSuccess(interestList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun readInterest() {
        val interestList = ArrayList<InterestModel>()
        val intRef = FirebaseDatabase.getInstance().reference.child("interests")
        intRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    interestList.clear()
                    for (snapshot in snapshot.children){
                        val interest = snapshot.getValue(InterestModel::class.java)
                        for (key in myInterest!!){
                            if (interest!!.inteID == key){
                                interestList.add(interest)
                            }
                        }
                    }
                }
                interestCallback.onInterestListLoadSuccess(interestList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onInterestListLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onInterestListLoadSuccess(list: List<InterestModel>) {
        val mutableLiveData = interestLiveData
        mutableLiveData!!.value = list
    }
}