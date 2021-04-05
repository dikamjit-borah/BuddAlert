package com.hobarb.locatadora.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hobarb.locatadora.R

class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        findViewById<CardView>(R.id.cv_signInWithGoogle).setOnClickListener{

                Toast.makeText(applicationContext, "Sign in with Google", Toast.LENGTH_SHORT).show()

        }
        findViewById<CardView>(R.id.cv_signInWithPhone).setOnClickListener{

            val intent = Intent(this@LoginActivity, PhoneLoginActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            goToUserActivity()
        }
    }
    private fun goToUserActivity() {
        val intent = Intent(this@LoginActivity, UserActivity::class.java)
        startActivity(intent)
    }

}