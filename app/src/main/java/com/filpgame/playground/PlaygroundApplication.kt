package com.filpgame.playground

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.estimote.coresdk.common.config.EstimoteSDK
import com.estimote.coresdk.observation.region.beacon.BeaconRegion
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.service.BeaconManager
import java.util.*


/**
 * @author filpgame
 * @since 2017-03-31
 */
class PlaygroundApplication : Application(), BeaconManager.BeaconMonitoringListener {
    lateinit var beaconManager: BeaconManager

    override fun onCreate() {
        super.onCreate()
        EstimoteSDK.initialize(applicationContext, "playground-7vf", "ed6495dcf0c8b5274ef69503e07417ac")
        EstimoteSDK.enableDebugLogging(true)
        beaconManager = BeaconManager(applicationContext)
        beaconManager.connect {
            beaconManager.startMonitoring(BeaconRegion("monitored region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 38037, 37346))
        }
//        EstimoteSDK.initialize(applicationContext, appId, appToken);
        beaconManager = BeaconManager(applicationContext)
        beaconManager.setMonitoringListener(this)
    }

    override fun onExitedRegion(p0: BeaconRegion?) {
        showNotification("Putz!", "Saiu Notificação")
    }

    override fun onEnteredRegion(beacon: BeaconRegion?, beacons: MutableList<Beacon>?) {
        showNotification("OPA!", "Chegou Notificação")
    }

    fun showNotification(title: String, message: String) {
        val notifyIntent = Intent(this, MainActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivities(this, 0, arrayOf(notifyIntent), PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = Notification.Builder(this).run {
            setSmallIcon(android.R.drawable.ic_dialog_info)
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            build()
        }

        notification.defaults = notification.defaults or Notification.DEFAULT_SOUND
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}