package com.hindu.cunow.Fragments.AboutMe.MyDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IExperienceCallback
import com.hindu.cunow.Callback.ISkillCallback
import com.hindu.cunow.Callback.IinterestCallback
import com.hindu.cunow.Model.ESModel
import com.hindu.cunow.Model.InterestModel

class MyDetailsViewModel : ViewModel(), IinterestCallback, ISkillCallback, IExperienceCallback {


    //Interest Load Data
    private var interestLiveData: MutableLiveData<List<InterestModel>>? = null
    var myInterest: List<String>? = null
    private val interestCallback: IinterestCallback = this
    private var messageError: MutableLiveData<String>? = null

    //Skill Load Data
    private var skillLiveData: MutableLiveData<List<ESModel>>? = null
    private val skillCallback: ISkillCallback = this

    //Experience Load Data
    private var expLiveData: MutableLiveData<List<ESModel>>? = null
    private var expCallback: IExperienceCallback = this


    //Interest Live and Function call
    val myInterestModel: MutableLiveData<List<InterestModel>>?
        get() {
            if (interestLiveData == null) {
                interestLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadInterest()
            }
            return interestLiveData
        }

    //Skill Live Data and Function Call
    val mySkillModel: MutableLiveData<List<ESModel>>?
        get() {
            if (skillLiveData == null) {
                skillLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadSkills()
            }
            return skillLiveData
        }

    //Skill Live Data and Function Call
    val myExpModel: MutableLiveData<List<ESModel>>?
        get() {
            if (expLiveData == null) {
                expLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadExperience()
            }
            return expLiveData
        }


    //Load Interest
    private fun loadInterest() {
        val interestList = ArrayList<InterestModel>()
        myInterest = ArrayList()
        val database = FirebaseDatabase.getInstance().reference.child("UserInterest")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot in snapshot.children) {
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

    //Read Interest
    private fun readInterest() {
        val interestList = ArrayList<InterestModel>()
        val intRef = FirebaseDatabase.getInstance().reference.child("interests")
        intRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    interestList.clear()
                    for (snapshot in snapshot.children) {
                        val interest = snapshot.getValue(InterestModel::class.java)
                        for (key in myInterest!!) {
                            if (interest!!.inteID == key) {
                                interestList.add(interest)
                            }
                            interestList.reverse()
                        }
                    }
                }
                interestCallback.onInterestListLoadSuccess(interestList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun loadSkills() {
        val skillList = ArrayList<ESModel>()
        val skillData = FirebaseDatabase.getInstance().getReference("Skills")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        skillData.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    val data = snapshot.getValue(ESModel::class.java)
                    skillList.add(data!!)
                }
                skillCallback.onSkillListLoadSuccess(skillList)
            }

            override fun onCancelled(error: DatabaseError) {
                skillCallback.onSkillListLoadFailed(error.message)
            }

        })
    }

    private fun loadExperience() {
        val expList = ArrayList<ESModel>()
        val expData = FirebaseDatabase.getInstance().getReference("Experience")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        expData.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    val data = snapshot.getValue(ESModel::class.java)
                    expList.add(data!!)
                }
                expCallback.onExpListLoadSuccess(expList)
            }

            override fun onCancelled(error: DatabaseError) {
                expCallback.onExpListLoadFailed(error.message)
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

    override fun onSkillListLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onSkillListLoadSuccess(list: List<ESModel>) {
        val mutableLiveData = skillLiveData
        mutableLiveData!!.value = list
    }

    override fun onExpListLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onExpListLoadSuccess(list: List<ESModel>) {
        val mutableLiveData = expLiveData
        mutableLiveData!!.value = list
    }
}