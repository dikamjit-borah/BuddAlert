package com.hobarb.locatadora.controllers;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hobarb.locatadora.utilities.CONSTANTS;
import com.hobarb.locatadora.utilities.SharedPrefs;

public class ContactsController {

    CollectionReference user;
    FirebaseFirestore db;
    Context context;
    String identifier;
    public ContactsController(Context context){
        db    = FirebaseFirestore.getInstance();
        user = db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE);
        this.context = context;
        SharedPrefs sharedPrefs = new SharedPrefs(context);
        identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER);
    }

}
