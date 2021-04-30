package com.hobarb.locatadora.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hobarb.locatadora.R
import com.hobarb.locatadora.adapters.RemindersAdapter
import com.hobarb.locatadora.models.RemindersModel
import com.hobarb.locatadora.utilities.CONSTANTS
import com.hobarb.locatadora.utilities.SharedPrefs

class RemindersActivity : AppCompatActivity() {

    lateinit var reminders_rv:RecyclerView
    lateinit var remindersArraylist:ArrayList<RemindersModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

        reminders_rv = findViewById(R.id.rv_reminders_ac_rem)
        remindersArraylist = arrayListOf()

        getAllReminders()
       /* val reminderController = ReminderController(applicationContext)
        remindersArraylist =  reminderController.allReminders  //how to await async here, returns empty otherwise

       // Toast.makeText(applicationContext, ""+remindersArraylist, Toast.LENGTH_SHORT).show()

        for (i in remindersArraylist)
        {
            Toast.makeText(applicationContext, "" + i, Toast.LENGTH_SHORT).show()
        }
*/

        findViewById<FloatingActionButton>(R.id.fab_addReminder_ac_reminder).setOnClickListener{
            val intent = Intent(this, AddReminderActivity::class.java)
            startActivity(intent)
        }
    }

    fun getAllReminders(){
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
                    val reminder = RemindersModel(
                        documentSnapshot[CONSTANTS.MAPKEYS.EVENT_DATE].toString(),
                        documentSnapshot[CONSTANTS.MAPKEYS.EVENT_TIME].toString(),
                        documentSnapshot[CONSTANTS.MAPKEYS.EVENT_LOCATION].toString(),
                        documentSnapshot[CONSTANTS.MAPKEYS.EVENT_NAME].toString()
                    )
                    reminderModels.add(reminder)
                }
                val linearLayoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                val remindersAdapter = RemindersAdapter(applicationContext, reminderModels!!)
                reminders_rv!!.setLayoutManager(linearLayoutManager)
                reminders_rv!!.setAdapter(remindersAdapter)

            }

            else {
                Toast.makeText(applicationContext, "" + task.exception, Toast.LENGTH_SHORT).show()
            }

        }

    }
}