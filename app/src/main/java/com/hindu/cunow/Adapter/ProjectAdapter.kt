package com.hindu.cunow.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.ProjectDetailsActivity
import com.hindu.cunow.Model.ProjectModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.apply_project_dialog.view.*
import kotlinx.android.synthetic.main.project_details_dialog_box.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectAdapter(private val mContext:Context,
                     private val mProject:List<ProjectModel>):RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {


                         inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){
                             private val projectImage: ImageView = itemView.findViewById(R.id.project_image) as ImageView
                             private val projectName: TextView = itemView.findViewById(R.id.projectName) as TextView
                             private val button: Button = itemView.findViewById(R.id.apply_to_project_button) as Button
                             private val profileImage:CircleImageView = itemView.findViewById(R.id.project_profileImage) as CircleImageView
                             private val userName: TextView = itemView.findViewById(R.id.projectName) as TextView

                             fun bind(list:ProjectModel){
                                 Glide.with(mContext).load(list.projectImage).into(projectImage)
                                 projectName.text = list.projectName
                                 CoroutineScope(Dispatchers.IO).launch {
                                     userInfo(profileImage,userName,list.projectHolderId!!)
                                 }
                                 if (list.projectHolderId == FirebaseAuth.getInstance().currentUser!!.uid){
                                     button.visibility = View.GONE
                                 }else{
                                     button.visibility = View.VISIBLE
                                 }

                                 button.setOnClickListener {
                                     val dialogView = LayoutInflater.from(mContext).inflate(R.layout.apply_project_dialog, null)

                                     val dialogBuilder = AlertDialog.Builder(mContext)
                                         .setView(dialogView)

                                     val alertDialog = dialogBuilder.show()

                                     dialogView.submit_application.setOnClickListener {
                                         addApplication(list.projectId!!,dialogView.about_applicant,dialogView.applicant_whatsapp)
                                         alertDialog.dismiss()
                                     }
                                 }

                                 itemView.setOnClickListener{
                                     if (list.projectHolderId == FirebaseAuth.getInstance().currentUser!!.uid){
                                         val intent = Intent(mContext,ProjectDetailsActivity::class.java)
                                         intent.putExtra("projectId",list.projectId)
                                         mContext.startActivity(intent)
                                     }else{
                                         val dialogView = LayoutInflater.from(mContext).inflate(R.layout.project_details_dialog_box, null)

                                         val dialogBuilder = AlertDialog.Builder(mContext)
                                             .setView(dialogView)

                                         val alertDialog = dialogBuilder.show()

                                         dialogView.projectName_details.text = list.projectId
                                         dialogView.projectDesc_details.text = list.projectDescription

                                         dialogView.close_details_btn.setOnClickListener{
                                             alertDialog.dismiss()
                                         }
                                     }
                                 }

                             }

                         }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.project_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mProject.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mProject[position])
    }

    private fun userInfo(profileImage:CircleImageView, name:TextView,Id:String,){
        val userDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(Id)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profileImage)
                    name.text = data.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addApplication(id:String,about:EditText,whatsapp:EditText){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Projects")
            .child(id)
            .child("applications")

        val applicationId = ref.push().key

        val dataMap = HashMap<String,Any>()
        dataMap["applicationId"] = applicationId.toString()
        dataMap["applicantId"] = FirebaseAuth.getInstance().currentUser!!.uid
        dataMap["aboutApplicant"] = about.text.toString()
        dataMap["appStatus"] = 0
        dataMap["applicantWhatsapp"] = whatsapp.text.toString()

        ref.child(applicationId!!).updateChildren(dataMap)
        about.text.clear()

    }
}