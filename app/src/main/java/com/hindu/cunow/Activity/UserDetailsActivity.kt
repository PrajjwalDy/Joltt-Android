package com.hindu.cunow.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_edit_profile.editText_experience
import kotlinx.android.synthetic.main.activity_edit_profile.editText_skills
import kotlinx.android.synthetic.main.activity_user_details.collegeName_welcome
import kotlinx.android.synthetic.main.activity_user_details.et_admission_year
import kotlinx.android.synthetic.main.activity_user_details.et_branch_welcome
import kotlinx.android.synthetic.main.activity_user_details.et_course_welcome
import kotlinx.android.synthetic.main.activity_user_details.et_exp_welcome
import kotlinx.android.synthetic.main.activity_user_details.et_skills_welcome
import kotlinx.android.synthetic.main.activity_user_details.genderFemale
import kotlinx.android.synthetic.main.activity_user_details.genderMale
import kotlinx.android.synthetic.main.activity_user_details.getLocation
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
import java.io.IOException
import java.util.Locale

class UserDetailsActivity : AppCompatActivity() {
    var checker = ""

    private val fromRight: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_right
        )
    }
    private val toLeft: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_left) }

    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        proceed_btn_0.setOnClickListener {
            ll_0.startAnimation(toLeft)
            ll_0.visibility = View.GONE


            ll_1.startAnimation(fromRight)
            ll_1.visibility = View.VISIBLE

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(proceed_btn_0.windowToken, 0)

        }

        genderFemale.setOnClickListener {
            checker = "female"
            ll_0.visibility = View.GONE
            ll_1.startAnimation(toLeft)
            ll_1.visibility = View.GONE


            ll_2.startAnimation(fromRight)
            ll_2.visibility = View.VISIBLE
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(genderFemale.windowToken, 0)
        }

        genderMale.setOnClickListener {
            checker = "male"
            ll_0.visibility = View.GONE
            ll_1.startAnimation(toLeft)
            ll_1.visibility = View.GONE


            ll_2.startAnimation(fromRight)
            ll_2.visibility = View.VISIBLE
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(genderMale.windowToken, 0)
        }

        nextBtn_2.setOnClickListener {
            ll_0.visibility = View.GONE
            ll_2.startAnimation(toLeft)
            ll_2.visibility = View.GONE


            ll_3.startAnimation(fromRight)
            ll_3.visibility = View.VISIBLE
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(nextBtn_2.windowToken, 0)
            //checkPermission()
        }

        nextBtn_3.setOnClickListener {
            ll_0.visibility = View.GONE
            ll_3.startAnimation(toLeft)
            ll_3.visibility = View.GONE


            ll_4.startAnimation(fromRight)
            ll_4.visibility = View.VISIBLE
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(nextBtn_3.windowToken, 0)
        }

        nextBtn_4.setOnClickListener {
            updateInformation()
        }

        getLocation.setOnClickListener {
            checkPermission()
        }
    }

    //UPDATE INFORMATION
    private fun updateInformation() {
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
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    startActivity(Intent(this, InterestActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this,"something went Wrong",Toast.LENGTH_SHORT).show()
                }
            }

    }

    //Update Skills
    private fun updateSkills() {
        if (et_skills_welcome.text.isEmpty()) {
            //DoNothing
        } else {
            val sentence = et_skills_welcome.text.toString().replace(" ", "")
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

        if (et_exp_welcome.text.isEmpty()) {
            //do nothing
        } else {
            val sentence = et_exp_welcome.text.toString().replace(" ", "")
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

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkGPS()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    private fun checkGPS() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(
            this.applicationContext
        ).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->

            try {
                val response = task.getResult(
                    ApiException::class.java
                )

                getUserLocation()

            } catch (e: ApiException) {
                e.printStackTrace()

                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {

                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(this, 200)

                    } catch (sendIntentException: IntentSender.SendIntentException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE->{

                    }
                }
            }

        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
            val location  = task.getResult()

            if (location != null){
                try {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addressList = geocoder.getFromLocation(location.latitude,location.longitude,1)

                    val address = addressList[0]
                    val city = address.locality
                    val state = address.adminArea
                    val country = address.countryName


                    val addressLine = "$city, $state, $country"
                    location_et_welcome.setText(addressLine)

                }catch (e: IOException){
                }
            }
        }
    }
}