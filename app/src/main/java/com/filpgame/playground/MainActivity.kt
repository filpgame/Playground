package com.filpgame.playground

import android.graphics.Color.rgb
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.estimote.coresdk.cloud.model.Color.*
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker
import com.filpgame.playground.estimote.BeaconID
import com.filpgame.playground.estimote.EstimoteCloudBeaconDetails
import com.filpgame.playground.estimote.EstimoteCloudBeaconDetailsFactory
import com.filpgame.playground.estimote.ProximityContentManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
            }
        }
        return true
    }

    private val TAG = "MainActivity"

    private val BACKGROUND_COLORS = hashMapOf(
            ICY_MARSHMALLOW to rgb(109, 170, 199),
            BLUEBERRY_PIE to rgb(98, 84, 158),
            MINT_COCKTAIL to rgb(155, 186, 160)
    )

    private val proximityContentManager: ProximityContentManager by lazy {
        ProximityContentManager(this, listOf(BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 38037, 37346)), EstimoteCloudBeaconDetailsFactory())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        proximityContentManager.setListener { content ->
            val text: String
            val backgroundColor: Int?
            if (content != null) {
                val beaconDetails = content as EstimoteCloudBeaconDetails?
                text = "You're in " + beaconDetails!!.beaconName + "'s range!"
                backgroundColor = BACKGROUND_COLORS[beaconDetails.beaconColor]
            } else {
                text = "No beacons in range."
                backgroundColor = null
            }
            Log.d(TAG, text)
            Log.d(TAG, backgroundColor?.toString() ?: "null")

        }
    }

    override fun onResume() {
        super.onResume()

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met")
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html")
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary")
        } else {
            Log.d(TAG, "Starting ProximityContentManager content updates")
            proximityContentManager.startContentUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Stopping ProximityContentManager content updates")
        proximityContentManager.stopContentUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        proximityContentManager.destroy()
    }
}
