package com.hola360.pranksounds.ui.callscreen.callingscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.ui.callscreen.callingscreen.CallingActivity
import com.hola360.pranksounds.utils.Constants

class CallingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val i = Intent(Intent.ACTION_MAIN, null, context, CallingActivity::class.java)

        if (intent?.action == Constants.ALARM_SERVICE_ACTION) {
            val call = intent.extras?.getParcelable<Call>("call")
            i.putExtra("call", call as Parcelable)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                .addCategory(Intent.CATEGORY_LAUNCHER)

            context!!.startActivity(i)

        }
    }
}