package com.hindu.cunow.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class EditProfileActivity : AppCompatActivity() {

    var checker = ""
    var relation = ""

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfileImageRef: StorageReference? = null
    private var gitHubLink =""
    private var instagramLink = ""
    private var linkedLink = ""
    private var portfolioLink = ""
    private var threadLink = ""
    private var twitterLink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Edit_Back.setOnClickListener {
            finish()
        }

        val view = View(this)

        storageProfileImageRef = FirebaseStorage.getInstance()
            .reference
            .child("Profile Images")
        retrieveUserData()

        change_ProfileImage_text.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@EditProfileActivity)
        }

        //GENDER BUTTON
        edit_gender_male.setOnClickListener {
            checker = "male"
            edit_gender_male.setBackgroundColor(resources.getColor(R.color.red))
            edit_gender_male.setTextColor(resources.getColor(R.color.white))
            edit_gender_female.setBackgroundColor(resources.getColor(R.color.white))
            edit_gender_female.setTextColor(resources.getColor(R.color.red))
        }
        edit_gender_female.setOnClickListener {
            checker = "female"
            edit_gender_female.setBackgroundColor(resources.getColor(R.color.red))
            edit_gender_female.setTextColor(resources.getColor(R.color.white))
            edit_gender_male.setBackgroundColor(resources.getColor(R.color.white))
            edit_gender_male.setTextColor(resources.getColor(R.color.red))
        }

        //Relation Button Deceleration
        edit_single.setOnClickListener {
            relation = "single"
            edit_single.setBackgroundColor(resources.getColor(R.color.pink))
            edit_single.setTextColor(resources.getColor(R.color.white))
            edit_mingle.setBackgroundColor(resources.getColor(R.color.white))
            edit_mingle.setTextColor(resources.getColor(R.color.red))
            edit_crush.setBackgroundColor(resources.getColor(R.color.white))
            edit_crush.setTextColor(resources.getColor(R.color.red))
        }
        edit_mingle.setOnClickListener {
            relation = "mingle"
            edit_mingle.setBackgroundColor(resources.getColor(R.color.pink))
            edit_mingle.setTextColor(resources.getColor(R.color.white))
            edit_single.setBackgroundColor(resources.getColor(R.color.white))
            edit_single.setTextColor(resources.getColor(R.color.red))
            edit_crush.setBackgroundColor(resources.getColor(R.color.white))
            edit_crush.setTextColor(resources.getColor(R.color.red))
        }
        edit_crush.setOnClickListener {
            relation = "crush"
            edit_crush.setBackgroundColor(resources.getColor(R.color.pink))
            edit_crush.setTextColor(resources.getColor(R.color.white))
            edit_single.setBackgroundColor(resources.getColor(R.color.white))
            edit_single.setTextColor(resources.getColor(R.color.red))
            edit_mingle.setBackgroundColor(resources.getColor(R.color.white))
            edit_mingle.setTextColor(resources.getColor(R.color.red))
        }

        //DONE BUTTON
        proceed_editProfile.setOnClickListener { view ->
            CoroutineScope(Dispatchers.IO).launch {
                launch { updateInformation(view) }
                launch { updateExperience() }
                launch { updateSkills() }
            }
        }
    }

    //UPDATE INFORMATION
    private fun updateInformation(view: View) {
        links()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("Users")
        val dataMap = HashMap<String, Any>()
        dataMap["fullName"] = ProfileUserName_editText.text.toString()
        dataMap["bio"] = Bio_EditText.text.toString()
        dataMap["place"] = editTextAddress.text.toString()
        dataMap["branch"] = editTextBatch.text.toString()
        dataMap["year"] = editTextYear.text.toString()
        dataMap["skills"] = editText_skills.text.toString()
        dataMap["experience"] = editText_experience.text.toString()
        dataMap["male"] = checker == "male"
        dataMap["female"] = checker == "female"
        dataMap["single"] = relation == "single"
        dataMap["committed"] = relation == "mingle"
        dataMap["crush"] = relation == "crush"
        dataMap["college"] = ET_college.text.toString()
        dataMap["course"] = ET_course.text.toString()
        dataMap["githubLink"] = gitHubLink
        dataMap["linkedin"] = linkedLink
        dataMap["portfolio"] = portfolioLink
        dataMap["instagram"] = instagramLink
        dataMap["twitter"] = twitterLink
        dataMap["threads"] = threadLink



        databaseRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(dataMap)
        Snackbar.make(view, "Update successful", Snackbar.LENGTH_SHORT).show()
        finish()
    }

    //UPDATE PROFILE IMAGE
    private fun updateProfileImage() {
        if (imageUri == null) {
            Toast.makeText(this, "You haven't selected any picture", Toast.LENGTH_SHORT).show()
            //Snackbar.make(this@EditPr,"Update successful", Snackbar.LENGTH_SHORT).show()
        } else {
            val progressDialog = Dialog(this)
            progressDialog.setContentView(R.layout.common_loading_progress)
            progressDialog.show()

            val fileRef = storageProfileImageRef!!
                .child(
                    FirebaseAuth
                        .getInstance()
                        .currentUser!!
                        .uid + ".jpg"
                )
            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    myUrl = downloadUrl.toString()
                    val ref = FirebaseDatabase.getInstance().reference.child("Users")
                    val mapData = HashMap<String, Any>()
                    mapData["profileImage"] = myUrl

                    ref.child(
                        FirebaseAuth
                            .getInstance()
                            .currentUser!!
                            .uid
                    ).updateChildren(mapData)
                    progressDialog.dismiss()
                    retrieveUserData()
                    Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    progressDialog.dismiss()
                }
            }
        }
    }

    //RETRIEVE INFORMATION
    private fun retrieveUserData() {
        val dataRef = FirebaseDatabase
            .getInstance().reference
            .child("Users")
            .child(
                FirebaseAuth
                    .getInstance()
                    .currentUser!!.uid
            )

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users = snapshot.getValue(UserModel::class.java)
                    Glide.with(this@EditProfileActivity)
                        .load(users!!.profileImage)
                        .into(change_ProfileImage)
                    ProfileUserName_editText.setText(users.fullName)
                    Bio_EditText.setText(users.bio)
                    editTextAddress.setText(users.place)
                    editTextBatch.setText(users.branch)
                    ET_course.setText(users.course)
                    editTextYear.setText(users.year)
                    editText_skills.setText(users.skills)
                    editText_experience.setText(users.experience)
                    ET_college.setText(users.college)
                    ET_github.setText(users.githubLink)
                    ET_linkedin.setText(users.linkedin)
                    ET_portfolio.setText(users.portfolio)
                    ET_instagram.setText(users.instagram)
                    ET_twitter.setText(users.twitter)
                    ET_threads.setText(users.threads)

                    if (users.male) {
                        checker = "male"
                        edit_gender_male.setBackgroundColor(resources.getColor(R.color.red))
                        edit_gender_male.setTextColor(resources.getColor(R.color.white))
                    } else if (users.female) {
                        checker = "female"
                        edit_gender_female.setBackgroundColor(resources.getColor(R.color.red))
                        edit_gender_female.setTextColor(resources.getColor(R.color.white))
                    }
                    if (users.single) {
                        relation = "single"
                        edit_single.setBackgroundColor(resources.getColor(R.color.pink))
                        edit_single.setTextColor(resources.getColor(R.color.white))
                        edit_mingle.setBackgroundColor(resources.getColor(R.color.white))
                        edit_mingle.setTextColor(resources.getColor(R.color.red))
                        edit_crush.setBackgroundColor(resources.getColor(R.color.white))
                        edit_crush.setTextColor(resources.getColor(R.color.red))
                    } else if (users.committed) {
                        relation = "mingle"
                        edit_mingle.setBackgroundColor(resources.getColor(R.color.pink))
                        edit_mingle.setTextColor(resources.getColor(R.color.white))
                        edit_single.setBackgroundColor(resources.getColor(R.color.white))
                        edit_single.setTextColor(resources.getColor(R.color.red))
                        edit_crush.setBackgroundColor(resources.getColor(R.color.white))
                        edit_crush.setTextColor(resources.getColor(R.color.red))
                    } else if (users.crush) {
                        relation = "crush"
                        edit_crush.setBackgroundColor(resources.getColor(R.color.pink))
                        edit_crush.setTextColor(resources.getColor(R.color.white))
                        edit_single.setBackgroundColor(resources.getColor(R.color.white))
                        edit_single.setTextColor(resources.getColor(R.color.red))
                        edit_mingle.setBackgroundColor(resources.getColor(R.color.white))
                        edit_mingle.setTextColor(resources.getColor(R.color.red))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    //Update Skills
    private fun updateSkills() {
        if (editText_skills.text.isEmpty()) {
            //DoNothing
        } else {
            val sentence = editText_skills.text.toString().trim { it <= ' ' }
            val words = sentence.split(",")

            //Initialize an empty list of skills
            val skills = mutableListOf<String>()

            //Extract skills from the words
            for (word in words) {
                skills.add(word)
            }

            val databaseRef = FirebaseDatabase.getInstance()
                .getReference("Skills")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            for (skill in skills) {
                val key = skill.toString()
                val map = HashMap<String, Any>()
                map["skillName"] = skill
                databaseRef.child(key).updateChildren(map)
            }
        }

    }

    //Update Experience
    private fun updateExperience() {

        if (editText_experience.text.isEmpty()) {
            //do nothing
        } else {
            val sentence = editText_experience.text.toString().trim { it <= ' ' }
            val words = sentence.split(",")

            //Initialize an empty list of skills
            val experience = mutableListOf<String>()

            //Extract skills from the words
            for (word in words) {
                experience.add(word)
            }

            val databaseRef = FirebaseDatabase.getInstance()
                .getReference("Experience")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            for (experience in experience) {
                val key = experience.toString()
                val map = HashMap<String, Any>()
                map["expName"] = experience
                databaseRef.child(key).updateChildren(map)
            }
        }


    }

    //CHECK VALID LINK
    private fun links() {

        gitHubLink = if (ET_github.text.toString().isEmpty()){
            ""
        }else{
            if (isWebLinkValid(ET_github.text.toString().trim())){
                ET_github.text.toString().trim()
            } else {
                return
            }
        }

        linkedLink = if (ET_linkedin.text.toString().isEmpty()){
            ""
        }else{
            if (isWebLinkValid(ET_linkedin.text.toString().toString())){
                ET_linkedin.text.toString().toString()
            } else {
                return
            }
        }.toString()

        portfolioLink = if (ET_portfolio.text.toString().isEmpty()){
            ""
        }else{
            if (isWebLinkValid(ET_portfolio.text.toString().trim())){
                ET_portfolio.text.toString().trim()
            } else {
                return
            }
        }

        instagramLink = if (ET_instagram.text.toString().isEmpty()){
            ""
        }else{
            if (isWebLinkValid(ET_instagram.text.toString().trim())){
                ET_instagram.text.toString().trim()
            } else {
                return
            }
        }

        twitterLink = if (ET_twitter.text.toString().isEmpty()){
            ""
        }else{
            if (isWebLinkValid(ET_twitter.text.toString().trim())){
                ET_twitter.text.toString().trim()
            } else {
                return
            }
        }

        threadLink = if (ET_threads.text.toString().isEmpty()){
            ""
        }else{
            if (isWebLinkValid(ET_threads.text.toString().trim())){
                ET_threads.text.toString().trim()
            } else {
                return
            }
        }

    }
    private fun isWebLinkValid(link: String): Boolean {
        val regex = Patterns.WEB_URL
        return regex.matcher(link).matches()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            change_ProfileImage.setImageURI(imageUri)
            updateProfileImage()
        }

    }


}
