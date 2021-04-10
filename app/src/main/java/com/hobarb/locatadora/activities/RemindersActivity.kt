package com.hobarb.locatadora.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hobarb.locatadora.R

class RemindersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

        findViewById<FloatingActionButton>(R.id.fab_addReminder_ac_reminder).setOnClickListener{
            val intent = Intent(this,AddReminderActivity::class.java)
            startActivity(intent)
        }
    }
}