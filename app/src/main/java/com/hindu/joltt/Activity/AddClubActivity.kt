package com.hindu.joltt.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.InterestAdapter
import com.hindu.joltt.Model.InterestModel
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_club.clubName_et
import kotlinx.android.synthetic.main.activity_add_club.club_description_et
import kotlinx.android.synthetic.main.activity_add_club.imagePreview_club
import kotlinx.android.synthetic.main.activity_add_club.ll_view_1
import kotlinx.android.synthetic.main.activity_add_club.ll_view_2
import kotlinx.android.synthetic.main.activity_add_club.ll_view_3
import kotlinx.android.synthetic.main.activity_add_club.next_btn_v1
import kotlinx.android.synthetic.main.activity_add_club.next_btn_v2
import kotlinx.android.synthetic.main.activity_add_club.next_btn_v3
import kotlinx.android.synthetic.main.activity_add_club.selectClub_icon
import java.io.ByteArrayOutputStream

class AddClubActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    var clubId=""

    private var firebaseUser:FirebaseUser? =null
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private var interestList:MutableList<InterestModel>? = null
    private var interestAdapter: InterestAdapter? = null

    var database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_club)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val recyclerView: RecyclerView = findViewById(R.id.club_tags_RV)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(this,2)
        recyclerView.layoutManager= linearLayoutManager
        interestList = ArrayList()
        interestAdapter = InterestAdapter(this,interestList as ArrayList)
        recyclerView.adapter = interestAdapter
        loadData()

        next_btn_v1.setOnClickListener {
            addDetails()
        }
        next_btn_v2.setOnClickListener {
            ll_view_2.visibility = View.GONE
            ll_view_3.visibility = View.VISIBLE
            ll_view_1.visibility = View.GONE
        }

        selectClub_icon.setOnClickListener {
            cropImage()
        }

        next_btn_v3.setOnClickListener {
            addClubImage()
        }



    }

    private fun cropImage(){
        CropImage.activity()
            .setAspectRatio(1, 1)
            .start(this)
    }

    private fun addDetails(){
        val db = database.child("Clubs")
        val id = db.push().key

        val dataMap =  HashMap<String,Any>()
        dataMap["clubId"] = id!!
        dataMap["clubName"] = clubName_et.text.toString()
        dataMap["clubDesc"] = club_description_et.text.toString()
        dataMap["clubOwner"] = FirebaseAuth.getInstance().currentUser!!.uid

        db.child(id).updateChildren(dataMap)
        ll_view_1.visibility = View.GONE
        ll_view_2.visibility = View.VISIBLE
        clubId = id
    }



    private fun addClubImage(){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.porgress_dialog)
        progressDialog.show()

        val inputStream = contentResolver.openInputStream(imageUri!!)
        val image = BitmapFactory.decodeStream(inputStream)

        val baos = ByteArrayOutputStream()
        val options = 40
        image.compress(Bitmap.CompressFormat.JPEG, options, baos)
        val data = baos.toByteArray()

        val fileReference = storageRef!!
            .child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask: StorageTask<*>
        uploadTask = fileReference.putBytes(data)

        uploadTask
            .addOnSuccessListener{
                val downloadUrl = it.metadata!!.reference!!.downloadUrl
                downloadUrl.addOnSuccessListener{
                    val imageUrl = it.toString()

                    val db = database.child("Clubs")
                        .child(clubId)
                    val map = HashMap<String,Any>()
                    map["clubImage"] = imageUrl

                    db.child(clubId).updateChildren(map)
                }
                progressDialog.dismiss()
                Toast.makeText(this,"Club Created successfully",Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!= null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            imagePreview_club.setImageURI(imageUri)
        }
    }

    private fun loadData(){
        val dbRef = FirebaseDatabase.getInstance().reference.child("interests")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    interestList!!.clear()
                    for (snapshot in snapshot.children){
                        val data = snapshot.getValue(InterestModel::class.java)
                        interestList!!.add(data!!)
                    }
                    interestAdapter!!.notifyDataSetChanged()
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }




}