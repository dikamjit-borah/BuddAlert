package com.hobarb.locatadora.activities

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.DatePicker.OnDateChangedListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.hobarb.locatadora.R


class AddReminderActivity : AppCompatActivity() {

    lateinit var addDateLL:LinearLayout
    lateinit var addTimeLL:LinearLayout
    lateinit var dateTv:TextView
    lateinit var timeTv:TextView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)
        addDateLL = findViewById(R.id.ll_date_ac_addReminder)
        addTimeLL = findViewById(R.id.ll_time_ac_addReminder)
        dateTv = findViewById(R.id.tv_date_ac_addRem)
        timeTv = findViewById(R.id.tv_time_ac_addRem)

        addDateLL.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Set the Date ")
            var selDate = "";
            var selMonth = "";
            var selYear = "";
            val view: View = LayoutInflater.from(this).inflate(R.layout.layout_datepicker, null)
            builder.setView(view)
            val dp: DatePicker = view.findViewById(R.id.dp_lay_dp)
            dp.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                selDate = dayOfMonth.toString();
                selMonth = (monthOfYear + 1).toString();
                selYear = (year + 1).toString();

                //Toast.makeText(applicationContext,"Date Set" + dayOfMonth+"-"+monthOfYear,Toast.LENGTH_LONG).show()
            }
            builder.setPositiveButton("Okay") { dialogInterface, which ->
                if (selMonth != 11.toString() && selMonth != 12.toString())
                    dateTv.setText(selDate + "-0" + selMonth + "-" + selYear)
                else
                    dateTv.setText(selDate + "-" + selMonth + "-" + selYear)
                Toast.makeText(applicationContext, "Date Set", Toast.LENGTH_LONG).show()
            }
            builder.create().show()
        }

        addTimeLL.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Set the Time")
            val view: View = LayoutInflater.from(this).inflate(R.layout.layout_timepicker, null)
            builder.setView(view)
            var hour:String = ""
            var min:String = ""
            var period:String = ""
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
                timeTv.setText("" + hour + "-" + min + " " + period)
                Toast.makeText(applicationContext, "Date Set", Toast.LENGTH_LONG).show()
            }


            builder.create().show()
        }

        findViewById<MaterialButton>(R.id.mb_setReminder_ac_addRem).setOnClickListener {
            Toast.makeText(applicationContext, "totoot", Toast.LENGTH_SHORT).show()
        }
    }
}