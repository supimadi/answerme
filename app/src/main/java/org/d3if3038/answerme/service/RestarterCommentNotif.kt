package org.d3if3038.answerme.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class RestarterCommentNotif : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 == null) return

        p0.startForegroundService(Intent(p0, CommentNotifService::class.java))
    }
}