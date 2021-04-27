package com.hobarb.locatadora.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hobarb.locatadora.R
import com.hobarb.locatadora.utilities.CONSTANTS
import com.hobarb.locatadora.utilities.SharedPrefs

class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        findViewById<CardView>(R.id.cv_signInWithGoogle).setOnClickListener {

            configureGoogleSignIn()
            signIn()

            Toast.makeText(applicationContext, "Sign in with Google", Toast.LENGTH_SHORT).show()

        }
        findViewById<CardView>(R.id.cv_signInWithPhone).setOnClickListener {

            val intent = Intent(this@LoginActivity, PhoneLoginActivity::class.java)
            startActivity(intent)

        }
    }

    private fun configureGoogleSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(applicationContext, "" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

        Toast.makeText(applicationContext, "Signed in as " + user!!.displayName, Toast.LENGTH_SHORT)
            .show()

        val sharedPrefs: SharedPrefs = SharedPrefs(applicationContext)
        sharedPrefs.writePrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER, user!!.displayName)
        goToUserActivity()


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