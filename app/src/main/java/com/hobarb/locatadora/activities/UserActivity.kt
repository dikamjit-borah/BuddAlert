package com.hobarb.locatadora.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.hobarb.locatadora.R

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val addLocAlarm:LinearLayout = findViewById(R.id.ll_addLocAlarm_ac_user);
        val destHistory:LinearLayout = findViewById(R.id.ll_destHistory_ac_user);

        addLocAlarm.setOnClickListener {
            val intent = Intent(this,SelectDestinationActivity::class.java)
            startActivity(intent)
        }

        destHistory.setOnClickListener {
            val intent = Intent(this,HistoryActivity::class.java)
            startActivity(intent)
        }


    }
}