package com.hobarb.locatadora.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.hobarb.locatadora.R
import com.hobarb.locatadora.utilities.CONSTANTS
import com.hobarb.locatadora.utilities.SharedPrefs

class UserActivity : AppCompatActivity() {
    lateinit var sharedPrefs: SharedPrefs
    lateinit var user_uid:String
    lateinit var identifier:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val addLocAlarm:LinearLayout = findViewById(R.id.ll_addLocAlarm_ac_user);
        val destHistory:LinearLayout = findViewById(R.id.ll_destHistory_ac_user);
        val contacts:LinearLayout = findViewById(R.id.ll_addContacts_ac_user);
        val reminders:LinearLayout = findViewById(R.id.ll_reminder_ac_user);

        sharedPrefs = SharedPrefs(applicationContext);

        identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER)


        findViewById<TextView>(R.id.tv_identifier_ac_user).setText(""+identifier)

        addLocAlarm.setOnClickListener {
            val intent = Intent(this,AddAlarmActivity::class.java)
            startActivity(intent)
        }

        destHistory.setOnClickListener {
            val intent = Intent(this,HistoryActivity::class.java)
            startActivity(intent)
        }

        contacts.setOnClickListener {
            val intent = Intent(this,ContactsActivity::class.java)
            startActivity(intent)
        }

        reminders.setOnClickListener {
            val intent = Intent(this,RemindersActivity::class.java)
            startActivity(intent)
        }


    }
}