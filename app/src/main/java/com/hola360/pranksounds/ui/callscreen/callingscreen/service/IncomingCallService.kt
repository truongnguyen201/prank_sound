package com.hola360.pranksounds.ui.callscreen.callingscreen.service

import android.R
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Priority
import com.hola360.pranksounds.App
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.ui.callscreen.callingscreen.CallingActivity
import com.hola360.pranksounds.ui.callscreen.callingscreen.notification.CallingNotification
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import java.util.*


class IncomingCallService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        buildNotification(intent!!)
        return START_STICKY
    }

    private fun buildNotification(intent: Intent) {
        val notificationIntent = Intent(this, CallingActivity::class.java)
        val call = intent.extras?.getParcelable<Call>("call")
        notificationIntent.putExtra("call", call as Parcelable)

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        startActivity(notificationIntent)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification: Notification =
            NotificationCompat.Builder(this, App.channelId)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(pendingIntent, true)
                .setContentTitle(call.name)
                .setContentText(call.phone)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_menu_call)
                .build()

        startForeground(Constants.CHANNEL_ID, notification)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }
}