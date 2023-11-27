package org.d3if3038.answerme.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.d3if3038.answerme.MainActivity
import org.d3if3038.answerme.R
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.model.Actions
import org.d3if3038.answerme.model.Comment
import java.util.Random

class CommentNotifService : Service() {
    private val firebaseDb: CollectionReference by lazy {
        Firebase.firestore.collection("posts")
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(applicationContext.dataStore)
    }

    private var firebaseListener: ListenerRegistration? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            Log.i("NOTIFICATION_SERVICE","Intent Null: Perhaps service already started.")
            return START_STICKY
        }

        Log.d("NOTIFICATION_SERVICE", intent.action.toString())
        when(intent.action) {
            Actions.START.name -> startService()
            Actions.STOP.name -> stopService()
            else -> Log.e("NOTIFICATION_SERVICE", "No Action Recived")
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("NOTIFICATION_SERVICE", "Notification service is started.")

        startForeground(100, createForegroundNotif())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NOTIFICATION_SERVICE", "Notification service is stopped.")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, CommentNotifService::class.java).also {
            it.setPackage(packageName)
            it.action = Actions.START.name
        }

        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startService() {
        if (isServiceStarted) return

        isServiceStarted = true
        setServiceState(applicationContext, ServiceState.STARTED)

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire(10*60*1000L /*10 minutes*/)
                }
            }

        val username = settingDataStore.getString("username", "")
        GlobalScope.launch {
            launch(Dispatchers.IO) {
                firebaseListener = firebaseDb
                    .whereEqualTo("username", username)
                    .whereEqualTo("deleted", false)
                    .addSnapshotListener { value, error ->
                        if (error != null) return@addSnapshotListener
                        if (value == null || value.isEmpty) return@addSnapshotListener

                        value.documentChanges.forEach {
                            val document = it.document

                            if (it.type == DocumentChange.Type.REMOVED)
                                return@forEach

                            val comments: List<*>?
                            try {
                                comments = document.get("comments") as List<*>
                            } catch (e: NullPointerException) {
                                return@forEach
                            }

                            val latComment = comments[comments.size - 1] as HashMap<*, *>
                            val author = latComment["username"].toString()

                            if (author == username) return@forEach
                            Log.d("NOTIFICATION_SERVICE", comments.size.toString())

                            createCommentNotification(
                                document.getString("title")!!,
                                author,
                                document.getString("documentId")
                            )
                        }
                    }
            }

        }

    }

    private fun stopService() {
        try {
            wakeLock?.let {
                if (it.isHeld) it.release()
            }

            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Log.e("NOTIFICATION_SERVICE", e.message.toString())
        }

        firebaseListener?.remove()

        isServiceStarted = false
        setServiceState(applicationContext, ServiceState.STOPPED)

    }

    private fun createCommentNotification(postTitle: String, commentAuthor: String, documentId: String?) {
        val notificationChannelId = "NEW COMMENT CHANNEL"

        val chan = NotificationChannel(
            notificationChannelId,
            "Comments Notification Service",
            NotificationManager.IMPORTANCE_HIGH
        ).let {
            it.description = "Comments Notification"
            it
        }

        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        val notification = notificationBuilder
            .setOngoing(false)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.new_comment_on, postTitle))
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentText(
                applicationContext.getString(
                    R.string.get_a_new_comment_from_s,
                    commentAuthor
                ))

        val bundle = Bundle()
        bundle.putString("documentId", documentId)

        val pendingIntent =  NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation)
            .setDestination(R.id.commentFragment)
            .setArguments(bundle)
            .createPendingIntent()

        if (documentId != null) {
            notification
                .setContentIntent(pendingIntent)
        }

        val id = Random(System.currentTimeMillis()).nextInt(1000)
        manager.notify(id, notification.build())
    }

    private fun createForegroundNotif(): Notification {
        val notificationChannelId = "COMMENTS NOTIFICATION SERVICE"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            notificationChannelId,
            "Comments Notification Service",
            NotificationManager.IMPORTANCE_LOW
        ).let {
            it.description = "Comments Notification Service"
            it
        }

        notificationManager.createNotificationChannel(channel)

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }


        val builder: Notification.Builder = Notification.Builder(
            this,
            notificationChannelId
        )

        return builder
            .setContentTitle("Comment Notification Service")
            .setContentText("This notification, indicate the service is running.")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }
}