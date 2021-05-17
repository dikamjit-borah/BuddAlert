
package com.hobarb.locatadora.activities
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hobarb.locatadora.R
import com.hobarb.locatadora.utilities.CONSTANTS
import com.hobarb.locatadora.utilities.SharedPrefs
import java.util.concurrent.TimeUnit


class PhoneLoginActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var et_phoneNo:EditText
    lateinit var et_otp:EditText
    lateinit var btn_continue:AppCompatButton
    lateinit var rl_otp:RelativeLayout

    lateinit var phoneNumber: String
    lateinit var sharedPrefs: SharedPrefs

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            goToUserActivity()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        auth = Firebase.auth
        et_phoneNo = findViewById(R.id.et_phoneNo_ac_login)
        btn_continue = findViewById(R.id.btn_continue_ac_login)
        rl_otp = findViewById(R.id.rl_otp_ac_login)
        et_otp = findViewById(R.id.et_otp_ac_login)
        btn_continue.setOnClickListener{
            Toast.makeText(applicationContext, storedVerificationId, Toast.LENGTH_SHORT).show()
            if (et_otp.text.toString().isNullOrEmpty())
            {
                Toast.makeText(applicationContext, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if (et_otp.text.toString().equals(storedVerificationId))
                {
                    goToUserActivity()
                }
            }
        }

        findViewById<Button>(R.id.btn_ac_login).setOnClickListener {

            if (!et_phoneNo.text.isNullOrEmpty() && et_phoneNo.text.length==10)
            {
                //Toast.makeText(applicationContext, ""+ et_phoneNo.text.toString(), Toast.LENGTH_SHORT).show()
                phoneNumber = "+91" + et_phoneNo.text.toString()
                loginWithPhone(phoneNumber)
            }

            else
            {
                Toast.makeText(applicationContext, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            }

        }


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                //Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@PhoneLoginActivity) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success")

                                sharedPrefs = SharedPrefs(applicationContext);
                                sharedPrefs.writePrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER, phoneNumber)
                                goToUserActivity()

                            val user = task.result?.user
                        } else {
                            // Sign in failed, display a message and update the UI
                            // Log.w(TAG, "signInWithCredential:failure", task.exception)
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(
                                    applicationContext,
                                    "" + task.exception,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            // Update UI
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                //Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(applicationContext, "" + e.message, Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(applicationContext, "" + e.message, Toast.LENGTH_SHORT).show()
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:$verificationId")

                rl_otp.visibility = View.VISIBLE
                btn_continue.visibility = View.VISIBLE
                // Save verification ID and resending token so we can use them later

                storedVerificationId = verificationId
                resendToken = token
            }
        }


    }

    private fun goToUserActivity() {
        val intent = Intent(this@PhoneLoginActivity, UserActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun loginWithPhone(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)





    }

}
