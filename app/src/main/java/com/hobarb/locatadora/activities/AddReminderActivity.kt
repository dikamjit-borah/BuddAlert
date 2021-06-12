package com.hobarb.locatadora.activities

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.button.MaterialButton
import com.hobarb.locatadora.R
import com.hobarb.locatadora.controllers.FirebaseController
import com.hobarb.locatadora.utilities.secrets
import java.util.*


class AddReminderActivity : AppCompatActivity() {

    lateinit var addDateLL:LinearLayout
    lateinit var addTimeLL:LinearLayout
    lateinit var dateTv:TextView
    lateinit var timeTv:TextView

    var selDate = "";
    var selMonth = "";
    var selYear = "";

    var hour:String = ""
    var min:String = ""
    var period:String = ""
    
    var eventDate:String = ""
    var eventTime:String = ""


    var eventName:String = ""
    var eventLocation = ""
    var eventLatLng = ""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)
        supportActionBar!!.setTitle(R.string.reminders);

        val destination_tv = findViewById<TextView>(R.id.tv_destination_ac_addRem)
        val latLong_tv = findViewById<TextView>(R.id.tv_latlng_ac_addRem)

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

                destination_tv.text = "" + place.name
                latLong_tv.text = "" + place.latLng
                eventLocation = ""+place.name

            }


            override fun onError(status: Status) {
                // TODO: Handle the error.
                destination_tv.text = "" + status.statusMessage
                latLong_tv.text = "" + status.resolution

            }

        })


        addDateLL = findViewById(R.id.ll_date_ac_addReminder)
        addTimeLL = findViewById(R.id.ll_time_ac_addReminder)
        dateTv = findViewById(R.id.tv_date_ac_addRem)
        timeTv = findViewById(R.id.tv_time_ac_addRem)




        addDateLL.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Set the Date ")

            val view: View = LayoutInflater.from(this).inflate(R.layout.layout_datepicker, null)
            builder.setView(view)
            val dp: DatePicker = view.findViewById(R.id.dp_lay_dp)
            dp.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                selDate = dayOfMonth.toString();
                selMonth = (monthOfYear + 1).toString();
                selYear = (year).toString();

                if(selDate.toInt()<10)
                    selDate = "0"+selDate;

                //Toast.makeText(applicationContext,"Date Set" + dayOfMonth+"-"+monthOfYear,Toast.LENGTH_LONG).show()
            }
            builder.setPositiveButton("Okay") { dialogInterface, which ->
                if (selMonth != 11.toString() && selMonth != 12.toString())
                    eventDate = selDate + "-0" + selMonth + "-" + selYear
                else
                    eventDate = selDate + "-" + selMonth + "-" + selYear

                dateTv.setText("" + eventDate)
                Toast.makeText(applicationContext, "Date Set", Toast.LENGTH_LONG).show()
            }
            builder.create().show()
        }

        addTimeLL.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Set the Time")
            val view: View = LayoutInflater.from(this).inflate(R.layout.layout_timepicker, null)
            builder.setView(view)

            val tp: TimePicker = view.findViewById(R.id.tp_lay_tp)
            tp.setOnTimeChangedListener{ _, hourOfDay, minute ->
                var hourOfDay = hourOfDay

                var format = ""
                when {
                    hourOfDay == 0 -> {
                        hourOfDay += 12
                        format = "AM"
                    }
                    hourOfDay == 12 -> format = "PM"
                    hourOfDay > 12 -> {
                        hourOfDay -= 12
                        format = "PM"
                    }
                    else -> format = "AM"
                }


                     hour = if (hourOfDay < 10) "0" + hourOfDay else hourOfDay.toString()
                     min = if (minute < 10) "0" + minute else minute.toString()
                    period = format
             }

            builder.setPositiveButton("Okay") { dialogInterface, which ->
                eventTime = "" + hour + "-" + min + " " + period
                timeTv.setText(eventTime)
                Toast.makeText(applicationContext, "Date Set", Toast.LENGTH_LONG).show()
            }


            builder.create().show()
        }

        val et_eventName = findViewById<EditText>(R.id.eT_eventName_ac_addRem)

        findViewById<MaterialButton>(R.id.mb_setReminder_ac_addRem).setOnClickListener {
            eventName = et_eventName.text.toString()
        val firebaseController =
            FirebaseController(
                this@AddReminderActivity
            )
            Toast.makeText(applicationContext, "" + eventName, Toast.LENGTH_SHORT).show()
            firebaseController.addReminder(eventDate, eventTime, eventName, eventLocation)
       
        }


    }
}