package com.hindu.joltt.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.ConfessionAdapter
import com.hindu.joltt.Model.ConfessionModel


class ViewConfessionFragment : Fragment() {
    private var recyclerView:RecyclerView? = null
    private var confessionAdapter: ConfessionAdapter? = null
    private var mConfession:MutableList<ConfessionModel>? = null

    private lateinit var confessionId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.fragment_view_confession, container, false)

        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if (pref != null){
            this.confessionId = pref.getString("confessionId","none")!!
        }

        recyclerView = root.findViewById(R.id.fullConfession_rv)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mConfession = ArrayList()
        confessionAdapter = context?.let { ConfessionAdapter(it,mConfession as ArrayList<ConfessionModel>) }
        recyclerView?.adapter = confessionAdapter

        returnConfession(confessionId)



        return root
    }

    private fun returnConfession(input:String){
        val array = FirebaseDatabase.getInstance().reference
            .child("Confession")
            .orderByChild("confessionId")
            .startAt(input)
            .endAt(input+"\uf88f")
        array.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mConfession!!.clear()
                for (snapshot in snapshot.children){
                    val value = snapshot.getValue(ConfessionModel::class.java)
                    if (value != null){
                        mConfession?.add(value)
                    }
                }
                confessionAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}