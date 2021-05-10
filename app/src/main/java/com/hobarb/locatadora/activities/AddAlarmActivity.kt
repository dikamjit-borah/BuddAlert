package com.hobarb.locatadora.activities

import android.R.attr.phoneNumber
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hobarb.locatadora.R
import com.hobarb.locatadora.services.BackgroundServices
import com.hobarb.locatadora.utilities.CONSTANTS
import com.hobarb.locatadora.utilities.GlobalFunctions
import com.hobarb.locatadora.utilities.SharedPrefs
import com.hobarb.locatadora.utilities.secrets
import java.net.URLEncoder
import java.util.*


class AddAlarmActivity : AppCompatActivity() {
    lateinit var serviceIntent: Intent
    lateinit var error_tv:TextView
    lateinit var destination: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)
        supportActionBar!!.setTitle(R.string.location_alarm);
        serviceIntent = Intent(applicationContext, BackgroundServices::class.java)


        error_tv = findViewById(R.id.tv_error_ac_selDest)

         destination = findViewById(R.id.tv_destination_ac_selDest)
        val latLong: TextView = findViewById(R.id.tv_latLong_ac_selDest)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, secrets.API_KEY, Locale.US)
        }


        // Initialize the AutocompleteSupportFragment.


        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.

                destination.text = "" + place.name
                latLong.text = "" + place.latLng
                CONSTANTS.BG_STUFF.DESTINATION_LAT_LNG = "" + place.latLng

            }


            override fun onError(status: Status) {
                // TODO: Handle the error.
                destination.text = "" + status.statusMessage
                latLong.text = "" + status.resolution

            }

        })


        val switch = findViewById<SwitchMaterial>(R.id.sw_notify_ac_addAlarm)
        val interval_ll = findViewById<LinearLayout>(R.id.ll_interval_ac_addAlarm)
        switch.setOnClickListener{
            if(switch.isChecked)
            {
                interval_ll.visibility = View.VISIBLE
            }
            else
            {
                interval_ll.visibility = View.INVISIBLE 
            }
        }

        val interval_sb = findViewById<SeekBar>(R.id.sb_interval_ac_addAlarm)
        val interval_tv = findViewById<TextView>(R.id.tv_interval_ac_addAlarm)
        interval_tv.setText(interval_sb.getProgress().toString() + " mins")
        interval_sb.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var pval = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                pval = progress
                interval_tv.setText(pval.toString() + " mins")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //interval_tv.setText(pval.toString() + "/" + seekBar.max)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // interval_tv.setText(pval.toString() + "/" + seekBar.max)
            }
        })

        val db = FirebaseFirestore.getInstance()

        val user = db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE)

        findViewById<MaterialButton>(R.id.btn_setAlarm_ac_login).setOnClickListener {
            uploadToHistory(user)
            goToTrackUserActivity()



/*

            Toast.makeText(applicationContext, "ÿoyoyoyo", Toast.LENGTH_SHORT).show()
            val curr_loc =
                "https://maps.google.com/?q=<" + CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE + ">,<" + CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE + ">"

            val message =
                "Hello my friends, I am en route " + CONSTANTS.BG_STUFF.DESTINATION + ". Currently I am here -> " + curr_loc

            val phnNo = "+918471925921"

          */
/*  val intent = Intent(Intent.ACTION_SEND)
            intent.data =
                Uri.parse("http://api.whatsapp.com/send?phone=" + phnNo.toString() + "&text=" + message)
            startActivity(intent)*//*


            val url = "https://api.whatsapp.com/send?phone=" + phnNo + "&text=" + URLEncoder.encode(message, "UTF-8")

            val it = intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(url)

            startActivity(intent)
*/



        }



    }

    private fun getUserCurrentLocation() {
        //getLastLocation()
    }





    private fun goToTrackUserActivity() {
        val intent = Intent(this@AddAlarmActivity, TrackUserActivity::class.java)
        intent.putExtra("destination", destination.text)
        startActivity(intent)
    }

    private fun uploadToHistory(user: CollectionReference) {
        val locationData = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                hashMapOf(
                    CONSTANTS.MAPKEYS.DATETIME to GlobalFunctions.getDateTime(),
                    CONSTANTS.MAPKEYS.LOCATION to findViewById<TextView>(R.id.tv_destination_ac_selDest).text.toString(),
                    CONSTANTS.MAPKEYS.LATLNG to findViewById<TextView>(R.id.tv_latLong_ac_selDest).text.toString()
                )
            }
            catch (e: Exception)
            {
                error_tv.setText("*" + e)
            }


        } else {
            error_tv.setText("*This app works on Android 8 and above");

        }

        val sharedPrefs = SharedPrefs(applicationContext)
        val identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER)
        user.document(identifier).collection(CONSTANTS.FIRESTORESTUFF.HISTORY).add(
            locationData
        ).addOnSuccessListener { documentReference ->
            Toast.makeText(applicationContext, "Alarm set!", Toast.LENGTH_SHORT).show()

        }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Error - " + e.message, Toast.LENGTH_SHORT).show()
            }
    }






}