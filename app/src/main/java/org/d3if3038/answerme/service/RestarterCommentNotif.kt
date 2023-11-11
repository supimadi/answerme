package org.d3if3038.answerme.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class RestarterCommentNotif : BroadcastReceiver() {
    @SuppressLint("ObsoleteSdkInt")
    override fun onReceive(context: Context, p1: Intent) {
        context.startService(Intent(context, CommentNotifService::class.java))

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(Intent(context, CommentNotifService::class.java))
//        } else {
//            context.startService(Intent(context, CommentNotifService::class.java))
//        }
    }
}