package org.d3if3038.answerme

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.ActivityMainBinding
import org.d3if3038.answerme.service.CommentNotifService

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var commentNotifService: CommentNotifService
    private lateinit var commentServiceIntent: Intent

    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(applicationContext.dataStore)
    }

    companion object {
        const val COMMENT_NOTIF_CHANNEL_ID = "commentNotif"
        const val PERMISSION_REQUEST_CODE = 1
    }

    @Suppress("DEPRECATION")
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        commentNotifService = CommentNotifService()
        commentServiceIntent = Intent(this, CommentNotifService::class.java)

        if (!isMyServiceRunning(commentNotifService::class.java)) {
            startService(commentServiceIntent)
        }

        setContentView(binding.root)

        if (!settingDataStore.getBoolean("is_boarded", false)) {
            startActivity(Intent(this, OnBoardingActivity::class.java))
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainContainerFragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.visibility = when(destination.id) {
                R.id.commentFragment -> View.GONE
                R.id.createQuestionFragment -> View.GONE
                else -> View.VISIBLE
            }
        }

        binding.bottomNavigation.selectedItemId = R.id.feedPages
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        val name = "Notification" //getString(R.string.channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(COMMENT_NOTIF_CHANNEL_ID, name, importance)
//            channel.description = getString(R.string.channel_desc)
        channel.description = "Notification Channel"

        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager?
        manager?.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        stopService(commentServiceIntent)

        super.onDestroy()
    }
}



