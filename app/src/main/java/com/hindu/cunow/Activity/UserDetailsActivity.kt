package com.hindu.cunow.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_edit_profile.Bio_EditText
import kotlinx.android.synthetic.main.activity_edit_profile.ET_college
import kotlinx.android.synthetic.main.activity_edit_profile.ET_course
import kotlinx.android.synthetic.main.activity_edit_profile.ProfileUserName_editText
import kotlinx.android.synthetic.main.activity_edit_profile.editTextAddress
import kotlinx.android.synthetic.main.activity_edit_profile.editTextBatch
import kotlinx.android.synthetic.main.activity_edit_profile.editTextYear
import kotlinx.android.synthetic.main.activity_edit_profile.editText_experience
import kotlinx.android.synthetic.main.activity_edit_profile.editText_skills
import kotlinx.android.synthetic.main.activity_edit_profile.edit_gender_male
import kotlinx.android.synthetic.main.activity_user_details.collegeName_welcome
import kotlinx.android.synthetic.main.activity_user_details.et_admission_year
import kotlinx.android.synthetic.main.activity_user_details.et_branch_welcome
import kotlinx.android.synthetic.main.activity_user_details.et_course_welcome
import kotlinx.android.synthetic.main.activity_user_details.et_exp_welcome
import kotlinx.android.synthetic.main.activity_user_details.et_skills_welcome
import kotlinx.android.synthetic.main.activity_user_details.genderFemale
import kotlinx.android.synthetic.main.activity_user_details.genderMale
import kotlinx.android.synthetic.main.activity_user_details.ll_0
import kotlinx.android.synthetic.main.activity_user_details.ll_1
import kotlinx.android.synthetic.main.activity_user_details.ll_2
import kotlinx.android.synthetic.main.activity_user_details.ll_3
import kotlinx.android.synthetic.main.activity_user_details.ll_4
import kotlinx.android.synthetic.main.activity_user_details.location_et_welcome
import kotlinx.android.synthetic.main.activity_user_details.nextBtn_2
import kotlinx.android.synthetic.main.activity_user_details.nextBtn_3
import kotlinx.android.synthetic.main.activity_user_details.nextBtn_4
import kotlinx.android.synthetic.main.activity_user_details.proceed_btn_0

class UserDetailsActivity : AppCompatActivity() {
    var checker =""

    private val fromRight: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_right)}
    private val toLeft: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_left)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        proceed_btn_0.setOnClickListener {
            ll_0.startAnimation(toLeft)
            ll_0.visibility = View.GONE


            ll_1.startAnimation(fromRight)
            ll_1.visibility = View.VISIBLE

        }

        genderFemale.setOnClickListener {
            checker = "female"
            ll_0.visibility = View.GONE
            ll_1.startAnimation(toLeft)
            ll_1.visibility = View.GONE


            ll_2.startAnimation(fromRight)
            ll_2.visibility = View.VISIBLE
        }

        genderMale.setOnClickListener {
            checker ="male"
            ll_0.visibility = View.GONE
            ll_1.startAnimation(toLeft)
            ll_1.visibility = View.GONE


            ll_2.startAnimation(fromRight)
            ll_2.visibility = View.VISIBLE
        }

        nextBtn_2.setOnClickListener {
            ll_0.visibility = View.GONE
            ll_2.startAnimation(toLeft)
            ll_2.visibility = View.GONE


            ll_3.startAnimation(fromRight)
            ll_3.visibility = View.VISIBLE
        }

        nextBtn_3.setOnClickListener {
            ll_0.visibility = View.GONE
            ll_3.startAnimation(toLeft)
            ll_3.visibility = View.GONE


            ll_4.startAnimation(fromRight)
            ll_4.visibility = View.VISIBLE
        }

        nextBtn_4.setOnClickListener {view->
            updateInformation(view)
        }

    }

    private fun updateInformation(view: View) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("Users")
        val dataMap = HashMap<String, Any>()
        dataMap["place"] = location_et_welcome.text.toString()
        dataMap["branch"] = et_branch_welcome.text.toString()
        dataMap["year"] = et_admission_year.text.toString()
        dataMap["skills"] = et_skills_welcome.text.toString()
        dataMap["experience"] = et_exp_welcome.text.toString()
        dataMap["college"] = collegeName_welcome.text.toString()
        dataMap["course"] = et_course_welcome.text.toString()
        dataMap["male"] = checker == "male"
        dataMap["female"] = checker == "female"
        updateSkills()
        updateExperience()

        databaseRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(dataMap)
        startActivity(Intent(this,InterestActivity::class.java))
        finish()
    }

    //Update Skills
    private fun updateSkills() {
        if (editText_skills.text.isEmpty()) {
            //DoNothing
        } else {
            val sentence = editText_skills.text.toString().replace(" ","")
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
            val sentence = editText_experience.text.toString().replace(" ","")
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
}