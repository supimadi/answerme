package org.d3if3038.answerme

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.transition.platform.Hold
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.ActivityMainBinding
import org.d3if3038.answerme.model.Actions
import org.d3if3038.answerme.service.CommentNotifService
import org.d3if3038.answerme.service.ServiceState
import org.d3if3038.answerme.service.getServiceState

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(applicationContext.dataStore)
    }

    companion object {
        const val COMMENT_NOTIF_CHANNEL_ID = "commentNotif"
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        actionOnService(Actions.START)
        setContentView(binding.root)

        if (!settingDataStore.getBoolean("is_boarded", false)) {
            startActivity(Intent(this, OnBoardingActivity::class.java))
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainContainerFragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val transform = Hold().apply {
                addTarget(binding.bottomNavigation)
            }
            TransitionManager.beginDelayedTransition(binding.bottomNavigation, transform)

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

    @SuppressLint("ObsoleteSdkInt")
    private fun actionOnService(action: Actions) {
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, CommentNotifService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
                return
            }
            startService(it)
        }
    }
}



