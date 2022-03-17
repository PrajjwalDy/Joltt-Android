package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import com.hindu.cunow.Adapter.VibesAdapter
import com.hindu.cunow.Callback.IVibesCallback
import com.hindu.cunow.Model.VibesModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_vibes.*

class VibesActivity : AppCompatActivity(), IVibesCallback {

    lateinit var adapter:VibesAdapter
    lateinit var vibes:DatabaseReference

    lateinit var IVibesCallback:IVibesCallback

    override fun onVibesLoadFailed(str: String) {
       Toast.makeText(this, "Load Failed $str",Toast.LENGTH_SHORT).show()
    }

    override fun onVibesLoadSuccess(list: List<VibesModel>) {
        adapter = VibesAdapter(list as ArrayList<VibesModel>, this)
        vibes_viewPager.adapter = adapter
        list.reverse()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vibes)

        vibes = FirebaseDatabase.getInstance().getReference("Vibes")

        //Init Event
        IVibesCallback = this

        loadVibes()
    }

    private fun loadVibes() {
        vibes.addListenerForSingleValueEvent(object :ValueEventListener{

            var vibes:MutableList<VibesModel> = ArrayList()

            override fun onDataChange(snapshot: DataSnapshot) {
                for(snapshot in snapshot.children){
                    val vibesData = snapshot.getValue(VibesModel::class.java)
                    vibes.add(vibesData!!)
                }
                IVibesCallback.onVibesLoadSuccess(vibes)
            }

            override fun onCancelled(error: DatabaseError) {
                IVibesCallback.onVibesLoadFailed(error.message)
            }

        })
    }


}