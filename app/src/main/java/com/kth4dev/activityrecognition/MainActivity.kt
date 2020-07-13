package com.kth4dev.activityrecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.DetectedActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    internal lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == BROADCAST_DETECTED_ACTIVITY) {
                    val type = intent.getIntExtra("type", -1)
                    val confidence = intent.getIntExtra("confidence", 0)
                    handleUserActivity(type, confidence)
                }
            }
        }
        startTracking()

    }

    private fun handleUserActivity(type: Int, confidence: Int) {
        var label = getString(R.string.activity_unknown)
        when (type) {
            DetectedActivity.IN_VEHICLE -> label = "You are in Vehicle"

            DetectedActivity.ON_BICYCLE -> label = "You are on Bicycle"

            DetectedActivity.ON_FOOT -> label = "You are on Foot"

            DetectedActivity.RUNNING -> label = "You are Running"

            DetectedActivity.STILL -> label = "You are Still"

            DetectedActivity.TILTING -> label = "Your phone is Tilted"

            DetectedActivity.WALKING -> label = "You are Walking"

            DetectedActivity.UNKNOWN -> label = "Unknown Activity"
        }

        Log.i("kyawthiha", "User activity: $label, Confidence: $confidence")

        if (confidence > CONFIDENCE) {
            progress_bar.visibility= View.GONE
            tv_result?.text = label
            //txtConfidence?.text = "Confidence: $confidence"
        }
    }

    private fun startTracking() {
        val intent = Intent(this@MainActivity, BackgroundDetectedActivitiesService::class.java)
        startService(intent)
    }

    private fun stopTracking() {
        val intent = Intent(this@MainActivity, BackgroundDetectedActivitiesService::class.java)
        stopService(intent)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter(BROADCAST_DETECTED_ACTIVITY))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
    }

    companion object {
        const val BROADCAST_DETECTED_ACTIVITY = "activity_intent"
        const val DETECTION_INTERVAL_IN_MILLISECONDS: Long = 1000
        const val CONFIDENCE = 70
    }


}