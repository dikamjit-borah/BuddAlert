package com.hobarb.locatadora.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionClient
import com.hobarb.locatadora.MainActivity

class BackgroundDetectedActivitiesService : Service() {

    private lateinit var mIntentService: Intent
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient

    internal var mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        val serverInstance: BackgroundDetectedActivitiesService
            get() = this@BackgroundDetectedActivitiesService
    }

    override fun onCreate() {
        super.onCreate()

        mActivityRecognitionClient = ActivityRecognitionClient(this)
        mIntentService = Intent(this, DetectedActivitiesIntentService::class.java)
        mPendingIntent = PendingIntent.getService(
            this,
            1,
            mIntentService,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        requestActivityUpdatesButtonHandler()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        mActivityRecognitionClient = ActivityRecognitionClient(this)
        mIntentService = Intent(this, DetectedActivitiesIntentService::class.java)
        mPendingIntent = PendingIntent.getService(
            this,
            1,
            mIntentService,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        requestActivityUpdatesButtonHandler()
        return Service.START_STICKY
    }

    fun requestActivityUpdatesButtonHandler() {
        val task = mActivityRecognitionClient?.requestActivityUpdates(
            MainActivity.DETECTION_INTERVAL_IN_MILLISECONDS,
            mPendingIntent
        )
        task!!.addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "Successfully requested activity updates",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        task!!.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                "Requesting activity updates failed to start",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    fun removeActivityUpdatesButtonHandler() {
        val task = mActivityRecognitionClient?.removeActivityUpdates(
            mPendingIntent
        )
        task?.addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "Removed activity updates successfully!",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        task?.addOnFailureListener {
            Toast.makeText(
                applicationContext, "Failed to remove activity updates!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeActivityUpdatesButtonHandler()
    }

    companion object {
        private val TAG = BackgroundDetectedActivitiesService::class.java?.getSimpleName()
    }
}