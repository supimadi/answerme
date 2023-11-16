package org.d3if3038.answerme.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import org.d3if3038.answerme.model.Actions

class FinishBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return

        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context) == ServiceState.STARTED) {
            Intent(context, CommentNotifService::class.java).also {
                it.action = Actions.START.name

                @SuppressLint("ObsoleteSdkInt")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(it)
                    return
                }

                context.startService(it)
            }
        }
    }

}