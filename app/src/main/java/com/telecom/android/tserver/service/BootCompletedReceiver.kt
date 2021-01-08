package com.telecom.android.tserver.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent?) {
        Log.w(TAG, "starting service...")
        context.startService(Intent(context, WebServerService::class.java))
    }

    companion object {
        const val TAG = "BootCompletedReceiver"
    }
}