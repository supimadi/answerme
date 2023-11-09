package org.d3if3038.answerme.service

import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.d3if3038.answerme.MainActivity
import org.d3if3038.answerme.R
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.ui.comment.CommentActivity


class CommentNotifService() : Service() {

    private val firebaseDb: CollectionReference by lazy {
        Firebase.firestore.collection("posts")
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(applicationContext.dataStore)
    }

    companion object {
        private const val NOTIFICATION_ID = 21
    }

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

    private fun startListening() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED)
        {
            return
        }

        val username = settingDataStore.getString("username", "")
        if (username.isEmpty()) return

        val builder = NotificationCompat.Builder(applicationContext,
            MainActivity.COMMENT_NOTIF_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(applicationContext.getString(R.string.get_a_new_comment))
//            .setContentText(
//                applicationContext.getString(
//                    R.string.get_a_new_comment_from_s,
//                    postTitle,
//                    commentAuthor
//                )
//            )
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(getPendingIntent(applicationContext, docId))
//            .setAutoCancel(true)


        firebaseDb.whereEqualTo("username", username)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value == null || value.isEmpty) return@addSnapshotListener
                Log.d("FIREBASE_CHANGE_LEN", value.documents.size.toString())


                value.documentChanges.forEach {
                    Log.d("FIREBASE_CHANGE", it.document.getString("documentId").toString())
                }

                startForeground(2, builder.build())
            }

    }

    override fun onCreate() {
        super.onCreate()
        startListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startListening()

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestarterCommentNotif::class.java)

        sendBroadcast(broadcastIntent)
    }
}