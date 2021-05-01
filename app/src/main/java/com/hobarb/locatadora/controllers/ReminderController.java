package com.hobarb.locatadora.controllers;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hobarb.locatadora.utilities.CONSTANTS;
import com.hobarb.locatadora.utilities.SharedPrefs;

import java.util.HashMap;
import java.util.Map;

public class ReminderController {
    CollectionReference user;
    FirebaseFirestore db;
    Context context;
    public ReminderController(Context context){
     db    = FirebaseFirestore.getInstance();
     user = db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE);
     this.context = context;
    }

    public void addReminder(String eventDate, String eventTime, String eventName, String eventLocation)
    {

        SharedPrefs sharedPrefs = new SharedPrefs(context);
        String identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER);

        Map<String, Object> reminders = new HashMap<>();
        reminders.put(CONSTANTS.MAPKEYS.EVENT_DATE, eventDate);
        reminders.put(CONSTANTS.MAPKEYS.EVENT_TIME, eventTime);
        reminders.put(CONSTANTS.MAPKEYS.EVENT_NAME, eventName);
        reminders.put(CONSTANTS.MAPKEYS.EVENT_LOCATION, eventLocation);
        user.document(identifier).collection(CONSTANTS.FIRESTORESTUFF.REMINDERS).add(reminders).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {

                    Toast.makeText(context, "Reminder created", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    //finish();
                }
                else
                {
                    //Toast.makeText(context, "Reminder could not be created", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
