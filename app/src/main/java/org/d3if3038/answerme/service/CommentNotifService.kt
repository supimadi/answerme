package org.d3if3038.answerme.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.d3if3038.answerme.R
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.ui.comment.CommentActivity
import java.util.Random


class CommentNotifService : Service() {
    private val firebaseDb: CollectionReference by lazy {
        Firebase.firestore.collection("posts")
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(applicationContext.dataStore)
    }

    private var notificationCounter = 100

    private var firebaseListener: ListenerRegistration? = null

    private fun getPendingIntent(context: Context, documentId: String): PendingIntent {
        val intent = Intent(context, CommentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("documentId", documentId)


        val resultPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack.
            addNextIntentWithParentStack(intent)

            // Get the PendingIntent containing the entire back stack.
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        return resultPendingIntent
    }

    private fun startForegroundNotif(postTitle: String, documentId: String?) {
        val NOTIFICATION_CHANNEL_ID = "org.answerme"
        val channelName = "Comment Notification"

        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder
            .setOngoing(false)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.new_comment_on, postTitle))
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentText(
                applicationContext.getString(
                    R.string.get_a_new_comment_from_s,
                    "Sukijak"
                ))

        if (documentId != null) {
            notification
            .setContentIntent(getPendingIntent(applicationContext, documentId))
        }

        val id = Random(System.currentTimeMillis()).nextInt(1000)
        Log.d("NOTIF_SERVICE", "Id Notif $id")

//        startForeground(id, notification.build())
//        startForeground(123, Notification())
        manager.notify(id, notification.build())
    }

    private fun startListening() {
        val username = settingDataStore.getString("username", "")
        if (username.isEmpty()) return

        firebaseListener = firebaseDb.whereEqualTo("username", username)
            .addSnapshotListener { value, error ->
                if (error != null) { return@addSnapshotListener }
                if (value == null || value.isEmpty) return@addSnapshotListener

                Log.d("FIREBASE_CHANGE_LEN", value.documents.size.toString())

                value.documentChanges.forEach {
                    val document = it.document

                    if (it.type != DocumentChange.Type.MODIFIED)
                        return@forEach

                    val data = document.data
                    Log.d("FIREBASE_CHANGE", data.toString())

                    startForegroundNotif(
                        document.getString("title")!!,
                        document.getString("documentId")
                    )

                }
            }

    }

    private fun stopListening() {
        if (firebaseListener == null) return

        firebaseListener!!.remove()
        firebaseListener = null

        Log.d("NOTIF_SERVICE", "STOPED")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startListening()

        return START_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

//    override fun onTaskRemoved(rootIntent: Intent?) {
//        super.onTaskRemoved(rootIntent)
//
//        stopListening()
//
//        val broadcastIntent = Intent()
//        broadcastIntent.action = "restartservice"
//        broadcastIntent.setClass(this, RestarterCommentNotif::class.java)
//
//        sendBroadcast(broadcastIntent)
//    }

    override fun onDestroy() {
        super.onDestroy()
        stopListening()

        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestarterCommentNotif::class.java)

        sendBroadcast(broadcastIntent)

    }
}