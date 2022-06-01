package com.hola360.pranksounds.ui.callscreen.callingscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.util.Log
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.ui.callscreen.callingscreen.CallingActivity
import com.hola360.pranksounds.ui.callscreen.callingscreen.service.IncomingCallService
import com.hola360.pranksounds.utils.Constants

class CallingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val i = Intent(Intent.ACTION_MAIN, null, context, CallingActivity::class.java)
        Log.e("----", "onReceive: ${(intent?.extras?.getParcelable<Call>("call") as Call).name}", )


        if (intent?.action == Constants.ALARM_SERVICE_ACTION) {
//            val call = intent.extras?.getParcelable<Call>("call")
//            i.putExtra("call", call as Parcelable)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
//                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
//                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
//                .addCategory(Intent.CATEGORY_LAUNCHER)
//
//            context!!.startActivity(i)
            startAlarmService(context!!, intent)
        }
    }

    private fun startAlarmService(context: Context, intent: Intent) {
        val intentService = Intent(context, IncomingCallService::class.java)
        val call = intent.extras?.getParcelable<Call>("call")
        intentService.putExtra("call", call as Parcelable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intentService)
        else
            context.startService(intentService)
    }
}