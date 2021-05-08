package com.hobarb.locatadora;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hobarb.locatadora.activities.LoginActivity;
import com.hobarb.locatadora.utilities.CONSTANTS;
import com.hobarb.locatadora.utilities.LocationUpdates;
import com.hobarb.locatadora.utilities.SharedPrefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1011;
    private static final int RQS_PICK_CONTACT = 1022;
    private MediaRecorder mRecorder;
    private String mFileName = null;
    Handler handler;
    Runnable runnable;
    int count = 0;
    TextView tv_emergency_contact;
    String con_name = "";
    String con_number = "";
    String audioLink = "url";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPrefs sharedPrefs = new SharedPrefs(this);
         con_name = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.EMERGENCY_NAME_KEY);
         con_number = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.EMERGENCY_NUMBER_KEY);





        findViewById(R.id.ll_signIn_ac_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        });

        findViewById(R.id.ll_help_ac_main).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {


                if(con_number=="" || con_number.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Set an emergency contact first!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(CheckPermissions())
                    {
                        getCurrentLocation();
                        startRecording();
                        //sendSMSwithAudio("" +audioLink);
                    }
                    else {
                        RequestPermissions();
                    }
                }

            }
        });

        tv_emergency_contact = findViewById(R.id.btn_emergencyContact_ac_main);

        if(con_number!= null && !con_number.isEmpty())
            tv_emergency_contact.setText(con_name + ", " + con_number);
        tv_emergency_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, RQS_PICK_CONTACT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RQS_PICK_CONTACT){
            if(resultCode == RESULT_OK){
                Uri contactData = data.getData();
                Cursor cursor =  managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number =       cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                SharedPrefs sharedPrefs = new SharedPrefs(this);
                    sharedPrefs.writePrefs(CONSTANTS.SHARED_PREF_KEYS.EMERGENCY_NAME_KEY,""+name);
                sharedPrefs.writePrefs(CONSTANTS.SHARED_PREF_KEYS.EMERGENCY_NUMBER_KEY,""+number);
                tv_emergency_contact.setText(name + ", " + number);


                //contactEmail.setText(email);
            }
        }
    }
    private void getCurrentLocation() {

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        Handler handler1  = new Handler();
        handler1.postDelayed(runnable = new Runnable() {
            public void run() {
               LocationUpdates.requestNewLocationData(mFusedLocationClient, getApplicationContext());

                Toast.makeText(getApplicationContext(), "TOT"+ CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE + ", " + CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE , Toast.LENGTH_SHORT).show();

                if(CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE != 0.0 && CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE!= 0.0)
                {
                    stopRepeating(handler1, runnable);
                }
                else{
                    handler1.postDelayed(runnable, 2000);
                }

            }
        }, 2000);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startRecording() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        mFileName += "/LocataDora"+ts+ ".3gp";
        Toast.makeText(MainActivity.this, "" + mFileName, Toast.LENGTH_SHORT).show();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mFileName);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Something's wrong! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                count++;
                handler.postDelayed(runnable, 1000);
                Toast.makeText(MainActivity.this, "This method is run every 1 seconds",
                        Toast.LENGTH_SHORT).show();
                if(count>7)
                {
                    stopRecording();

                    stopRepeating(handler, runnable);
                }
            }
        }, 1000);
    }

    private void sendSMSwithAudio(String s) {

        final String SMS_SENT_INTENT_FILTER = "com.yourapp.sms_send";
        final String SMS_DELIVERED_INTENT_FILTER = "com.yourapp.sms_delivered";

        String curr_loc = "https://maps.google.com/?q="+ CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE +","+CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE+"";

        String link = s.equals("")?"null":s;
        String message = "yo "  +link;

        Log.e("yoyo", message);

        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SMS_SENT_INTENT_FILTER), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SMS_DELIVERED_INTENT_FILTER), 0);

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(con_number, null, message, sentPI, deliveredPI);
        Log.e("sent", message);
        //Toast.makeText(this, "SMS sengt", Toast.LENGTH_SHORT).show();


    }


    public void stopRepeating(Handler handler, Runnable runnable) {
        handler.removeCallbacks(runnable);


    }
    private void stopRecording() {


        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(""+mFileName);
        uploadAudio(Uri.fromFile(new File(mFileName)), storageRef);


    }

    private void getAudioUrl(StorageReference storageRef) {

      storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override
          public void onSuccess(Uri uri) {
              audioLink = uri.toString();
              try {
                  Thread.sleep(2000);
              } catch (InterruptedException e) {
                  Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                  e.printStackTrace();
              }
              sendSMSwithAudio(""+audioLink);
          }
          
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
          }
      });

  }

    private void uploadAudio(Uri audioUri, StorageReference storageReference) {
       // Toast.makeText(this, "" + Uri.parse(mFileName), Toast.LENGTH_SHORT).show();

        if(audioUri != null){
            UploadTask uploadTask = storageReference.putFile(audioUri);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        getAudioUrl(storageReference);

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Fauie from uoload" , Toast.LENGTH_SHORT).show();
                    }


                //    progressBarUpload.setVisibility(View.INVISIBLE);
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "Nothing to upload", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length> 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}
