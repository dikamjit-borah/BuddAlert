package com.hobarb.locatadora.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hobarb.locatadora.models.RemindersModel;
import com.hobarb.locatadora.utilities.CONSTANTS;
import com.hobarb.locatadora.utilities.SharedPrefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReminderController {
    CollectionReference user;
    FirebaseFirestore db;
    Context context;
    String identifier;
    public ReminderController(Context context){
     db    = FirebaseFirestore.getInstance();
     user = db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE);
     this.context = context;
     SharedPrefs sharedPrefs = new SharedPrefs(context);
     identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER);
    }

    public void addReminder(String eventDate, String eventTime, String eventName, String eventLocation)
    {



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

    public ArrayList<RemindersModel> getAllReminders()
    {
        ArrayList<RemindersModel> reminderModels = new ArrayList<>();
        CollectionReference docRef = db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE).document(identifier).collection(CONSTANTS.FIRESTORESTUFF.REMINDERS);
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for ( DocumentSnapshot documentSnapshot:task.getResult().getDocuments()) {
                        RemindersModel reminder = new RemindersModel(documentSnapshot.get(CONSTANTS.MAPKEYS.EVENT_DATE).toString(), documentSnapshot.get(CONSTANTS.MAPKEYS.EVENT_TIME).toString(), documentSnapshot.get(CONSTANTS.MAPKEYS.EVENT_LOCATION).toString(), documentSnapshot.get(CONSTANTS.MAPKEYS.EVENT_NAME).toString());
                        reminderModels.add(reminder);


                    }

                } else {
                    Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();

                }

            }
        });
        for (int i = 0; i<reminderModels.size(); i++)
        {
            Toast.makeText(context, "yoyo"+reminderModels.get(i), Toast.LENGTH_SHORT).show();
        }
        return reminderModels;
    }
}
