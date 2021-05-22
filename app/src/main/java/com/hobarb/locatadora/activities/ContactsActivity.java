package com.hobarb.locatadora.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hobarb.locatadora.R;
import com.hobarb.locatadora.adapters.ContactsAdapter;
import com.hobarb.locatadora.controllers.FirebaseController;
import com.hobarb.locatadora.models.ContactsModel;
import com.hobarb.locatadora.models.RemindersModel;
import com.hobarb.locatadora.utilities.CONSTANTS;
import com.hobarb.locatadora.utilities.GlobalFunctions;
import com.hobarb.locatadora.utilities.SharedPrefs;
import com.hobarb.locatadora.utilities.views.Loader;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    private static final int PICK_CONTACT = 101;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 102;
    String name, cNumber;
    RecyclerView contacts_rv;
    ArrayList<ContactsModel> contactsModels = new ArrayList<>();
    ContactsAdapter contactsAdapter;
    TextView isContacts;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        getSupportActionBar().setTitle(R.string.contacts);

        contacts_rv = findViewById(R.id.rv_contacts_ac_contacts);
        isContacts = findViewById(R.id.tv_ifContact_ac_contacts);

        fetchContacts();

        findViewById(R.id.btn_addContacts_ac_addContacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermissions();

            }
        });

        findViewById(R.id.btn_saveContacts_ac_addContacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadContacts();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchContacts()  {

        Loader loader = new Loader(ContactsActivity.this);
        loader.showAlertDialog();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPrefs sharedPrefs = new SharedPrefs(getApplicationContext());
        String identifier = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.IDENTIFIER);

        CollectionReference docRef = db.collection(CONSTANTS.FIRESTORESTUFF.MAINTABLE).document(identifier).collection(CONSTANTS.FIRESTORESTUFF.CONTACTS);
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if(task.getResult().size()>=1)
                    {
                        for ( DocumentSnapshot documentSnapshot:task.getResult().getDocuments()) {

                            ContactsModel contactsModel = new ContactsModel(documentSnapshot.get(CONSTANTS.MAPKEYS.CONTACT_NAME).toString(), documentSnapshot.get(CONSTANTS.MAPKEYS.CONTACT_NUMBER).toString());
                            contactsModels.add(contactsModel);
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                        contactsAdapter = new ContactsAdapter(getApplicationContext(), contactsModels);
                        contactsAdapter.notifyDataSetChanged();
                        contacts_rv.setLayoutManager(linearLayoutManager);
                        contacts_rv.setAdapter(contactsAdapter);


                    }
                    else {
                        isContacts.setVisibility(View.VISIBLE);
                        }
                    loader.dismissAlertDialog();


                } else {
                    Toast.makeText(getApplicationContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void uploadContacts() {

        FirebaseController firebaseController = new FirebaseController(this);
        firebaseController.addContactsToDb(contactsModels);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.

            openContacts();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                openContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }



    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                             cNumber = phones.getString(phones.getColumnIndex("data1"));

                        }
                         name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        ContactsModel contactsModel = new ContactsModel(name, cNumber);
                        contactsModels.add(contactsModel);




                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                    contactsAdapter = new ContactsAdapter(ContactsActivity.this, contactsModels);
                    contacts_rv.setLayoutManager(linearLayoutManager);
                    contacts_rv.setAdapter(contactsAdapter);

                    Toast.makeText(this, ""+name + cNumber, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}