package com.hobarb.locatadora.controllers

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.hobarb.locatadora.utilities.CONSTANTS
import com.hobarb.locatadora.utilities.SharedPrefs
import com.hobarb.locatadora.utilities.views.Loader
import java.util.HashSet

object Contacts {

    fun fetchContacts(context: Context) {
        val loader = Loader(context)
        loader.showAlertDialog()
        CONSTANTS.ALARM_STUFF.MY_CONTACTS.clear()
        val db = FirebaseFirestore.getInstance()
        val sharedPrefs = SharedPrefs(context)
        val identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER)
        val docRef = db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE).document(identifier)
            .collection(CONSTANTS.FIRESTORESTUFF.CONTACTS)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.size() > 1) {
                    for (documentSnapshot in task.result.documents) {
                        CONSTANTS.ALARM_STUFF.MY_CONTACTS.add(documentSnapshot[CONSTANTS.MAPKEYS.CONTACT_NUMBER].toString())

                    }

                    val set: MutableSet<String> = HashSet()
                    set.addAll(CONSTANTS.ALARM_STUFF.MY_CONTACTS)

                    val sharedpreferences = context.getSharedPreferences(
                        CONSTANTS.SHARED_PREF_KEYS.APP_PREFERENCES,
                        AppCompatActivity.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedpreferences.edit()
                    editor.putStringSet(CONSTANTS.SHARED_PREF_KEYS.MY_CONTACTS_KEY, set)
                    editor.commit()
                    CONSTANTS.ALARM_STUFF.stop_alarm = false
                } else {
                    Toast.makeText(
                        context,
                        "No contacts to display" + task.exception,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                loader.dismissAlertDialog()
            } else {
                Toast.makeText(context, "" + task.exception, Toast.LENGTH_SHORT).show()
            }
        }
    }


}