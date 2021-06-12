package com.hobarb.locatadora.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hobarb.locatadora.R
import com.hobarb.locatadora.adapters.RemindersAdapter
import com.hobarb.locatadora.models.RemindersModel
import com.hobarb.locatadora.utilities.CONSTANTS
import com.hobarb.locatadora.utilities.SharedPrefs
import com.hobarb.locatadora.utilities.views.Loader
import java.text.SimpleDateFormat
import java.util.*

class UserActivity : AppCompatActivity() {
    lateinit var sharedPrefs: SharedPrefs
    lateinit var user_uid:String
    lateinit var identifier:String
    lateinit var notiToday_rv:RecyclerView
    lateinit var loader: Loader

    lateinit var remToday_tv:TextView
    lateinit var formattedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val addLocAlarm:LinearLayout = findViewById(R.id.ll_addLocAlarm_ac_user);
        val destHistory:LinearLayout = findViewById(R.id.ll_destHistory_ac_user);
        val contacts:LinearLayout = findViewById(R.id.ll_addContacts_ac_user);
        val reminders:LinearLayout = findViewById(R.id.ll_reminder_ac_user);
        remToday_tv = findViewById(R.id.tv_remToday_ac_user)

        loader = Loader(this@UserActivity)
        sharedPrefs = SharedPrefs(applicationContext);

        identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER)

        notiToday_rv = findViewById(R.id.rv_notificationsToday_ac_user);


        fetchNotificationsToday()

        findViewById<TextView>(R.id.tv_identifier_ac_user).setText("" + identifier)

        addLocAlarm.setOnClickListener {
            val intent = Intent(this, AddAlarmActivity::class.java)
            startActivity(intent)
        }

        destHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        contacts.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        reminders.setOnClickListener {
            val intent = Intent(this, RemindersActivity::class.java)
            startActivity(intent)
        }


    }

    private fun fetchNotificationsToday() {
        loader.showAlertDialog()
        getDateToday()
        getAllReminders()

    }

    private fun getDateToday() {

        val c: Date = Calendar.getInstance().getTime()

        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        formattedDate = df.format(c)
        remToday_tv.text = "Reminders for today $formattedDate"
    }

    fun getAllReminders(){
       // loader.showAlertDialog()
        val db = FirebaseFirestore.getInstance()
        val sharedPrefs = SharedPrefs(applicationContext)
        val identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER)
        val reminderModels = java.util.ArrayList<RemindersModel>()
        val docRef: CollectionReference =
            db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE).document(identifier)
                .collection(CONSTANTS.FIRESTORESTUFF.REMINDERS)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (documentSnapshot in task.result.documents) {
                    if(documentSnapshot[CONSTANTS.MAPKEYS.EVENT_DATE].toString().equals(formattedDate.toString()))
                    {
                        val reminder = RemindersModel(
                            documentSnapshot[CONSTANTS.MAPKEYS.EVENT_DATE].toString(),
                            documentSnapshot[CONSTANTS.MAPKEYS.EVENT_TIME].toString(),
                            documentSnapshot[CONSTANTS.MAPKEYS.EVENT_LOCATION].toString(),
                            documentSnapshot[CONSTANTS.MAPKEYS.EVENT_NAME].toString()
                        )
                        reminderModels.add(reminder)

                    }
                }
                val linearLayoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
                val remindersAdapter = RemindersAdapter(applicationContext, reminderModels!!)
                notiToday_rv!!.setLayoutManager(linearLayoutManager)
                notiToday_rv!!.setAdapter(remindersAdapter)
               loader.dismissAlertDialog()
            }

            else {
                Toast.makeText(applicationContext, "" + task.exception, Toast.LENGTH_SHORT).show()
            }

        }

    }


}