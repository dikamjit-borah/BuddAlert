package com.hobarb.locatadora.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hobarb.locatadora.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.btn_ac_login).setOnClickListener {
            val intent = Intent(this,UserActivity::class.java)
            startActivity(intent)
        }


    }
}