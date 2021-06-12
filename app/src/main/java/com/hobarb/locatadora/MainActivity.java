package com.hobarb.locatadora;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.hobarb.locatadora.activities.LoginActivity;
import com.hobarb.locatadora.services.BackgroundDetectedActivitiesService;
import com.hobarb.locatadora.utilities.CONSTANTS;
import com.hobarb.locatadora.utilities.LocationUpdates;
import com.hobarb.locatadora.utilities.SharedPrefs;
import com.hobarb.locatadora.utilities.views.Loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {


    public BroadcastReceiver broadcastReceiver;
        public static String BROADCAST_DETECTED_ACTIVITY = "activity_intent";

         public static long DETECTION_INTERVAL_IN_MILLISECONDS = 1000;

        public static int CONFIDENCE = 70;

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
    Loader loader;

    TextView textView;

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(MainActivity.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
         loader = new Loader(MainActivity.this);
         textView = findViewById(R.id.tv);

        SharedPrefs sharedPrefs = new SharedPrefs(this);
         con_name = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.EMERGENCY_NAME_KEY);
         con_number = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.EMERGENCY_NUMBER_KEY);

        broadcastReceiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == MainActivity.BROADCAST_DETECTED_ACTIVITY) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    String label="";

                    switch (type) {
                        case DetectedActivity.IN_VEHICLE: {
                            label = getString(R.string.activity_in_vehicle);

                            break;
                        }
                        case DetectedActivity.ON_BICYCLE: {
                            label = getString(R.string.activity_on_bicycle);

                            break;
                        }
                        case DetectedActivity.ON_FOOT: {
                            label = getString(R.string.activity_on_foot);

                            break;
                        }
                        case DetectedActivity.RUNNING: {
                            label = getString(R.string.activity_running);
                            stopTracking();
                            startRecording();
                            break;
                        }
                        case DetectedActivity.STILL: {
                            label = getString(R.string.activity_still);
                            break;
                        }
                        case DetectedActivity.TILTING: {
                            label = getString(R.string.activity_tilting);

                            break;
                        }
                        case DetectedActivity.WALKING: {
                            label = getString(R.string.activity_walking);

                            break;
                        }
                        case DetectedActivity.UNKNOWN: {
                            label = getString(R.string.activity_unknown);
                            break;
                        }
                    }
                    textView.setText(""  + label);

                }
            }
        };


        findViewById(R.id.ll_signIn_ac_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        });


        findViewById(R.id.ll_help_ac_main).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {


                if(con_number=="" || con_number.isEmpty()) //or undefined key, undefined key
                {
                    Toast.makeText(MainActivity.this, "Set an emergency contact first!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(CheckPermissions())
                    {
                        loader.showRecordingAlertDialog();
                        getCurrentLocation();
                        startRecording();

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

        startTracking();


    }

    private void startTracking() {

        Intent intent = new Intent(this, BackgroundDetectedActivitiesService.class);
        startService(intent);
    }

    private void stopTracking() {
        Intent intent = new Intent(this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
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

                con_name = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.EMERGENCY_NAME_KEY);
                con_number = sharedPrefs.readPrefs(CONSTANTS.SHARED_PREF_KEYS.EMERGENCY_NUMBER_KEY);


                tv_emergency_contact.setText(con_name + ", " + con_number);



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


    
    private void startRecording() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        mFileName += "/LocataDora"+ts+ ".3gp";
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
                if(count>12)
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


        String message = "Help! I am in danger. My location -> " + curr_loc + " Audio sample -> "  +s;

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);

        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveredPI);


       sms.sendMultipartTextMessage(con_number, null, parts, sendList, deliverList);
        Toast.makeText(this, "SMS sent to " + con_number, Toast.LENGTH_SHORT).show();

        Gson gson =  new Gson();
        String json = new SharedPrefs(this).readPrefs(CONSTANTS.SHARED_PREF_KEYS.MY_CONTACTS_KEY);

        if (!json.equals("undefined key") && !json.isEmpty() )
        {
            Type type =  new TypeToken<ArrayList<String>>(){}.getType();
            ArrayList<String> contact_numbers = gson.fromJson(json, type);

            for(int i = 0; i<contact_numbers.size(); i++)
            {

                sms.sendMultipartTextMessage(contact_numbers.get(i), null, parts, sendList, deliverList);
                Toast.makeText(this, "SMS sent to " + contact_numbers.get(i), Toast.LENGTH_SHORT).show();
            }

        }
        onCall();




      /*  List<String> stringsList = new ArrayList<>(CONSTANTS.ALARM_STUFF.MY_CONTACTS);
        if(stringsList.size()>0)
        {
            for(int i = 0; i<stringsList.size(); i++)
            {
                SmsManager sms  = SmsManager.getDefault();
                sms.sendTextMessage(stringsList.get(i), null, message, sentPI, deliveredPI);
            }
        }*/






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
              loader.dismissAlertDialog();
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
            case 1232:
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                onCall();
            } else {
                Log.d("TAG", "Call Permission Not Granted");
            }
            break;

            default:
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

    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    1232);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + con_number)));
        }
    }

}
