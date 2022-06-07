package com.hola360.pranksounds.ui.callscreen.callingscreen.receiver

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import androidx.core.app.NotificationManagerCompat
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository
import com.hola360.pranksounds.ui.callscreen.callingscreen.service.IncomingCallService
import com.hola360.pranksounds.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CallingReceiver : BroadcastReceiver() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private var repository: PhoneBookRepository? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == Constants.ALARM_SERVICE_ACTION) {
            startAlarmService(context!!, intent)
        }
        context?.apply {
            NotificationManagerCompat.from(this).cancel(Constants.CHANNEL_ID)
        }
    }

    private fun startAlarmService(context: Context, intent: Intent) {

        val call = intent.extras?.getParcelable<Call>("call")
        if (call != null) {
            if (call.isLocal) {
                repository = PhoneBookRepository(context.applicationContext as Application)
                uiScope.launch {
                    val localCallInfo = repository!!.getLocalCallById(call.id)
                    if (localCallInfo != null) {
                        startCall(context, localCallInfo)
                    }
                }
            } else {
                startCall(context, call)
            }
        }

    }

    private fun startCall(context: Context, call: Call) {
        val intentService = Intent(context, IncomingCallService::class.java)
        intentService.putExtra("call", call as Parcelable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intentService)
        else
            context.startService(intentService)
    }
}