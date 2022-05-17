package com.hola360.pranksounds.ui.callscreen.callingscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hola360.pranksounds.ui.callscreen.callingscreen.CallingActivity

class CallingReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val i = Intent(p0, CallingActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        p0!!.startActivity(i)
    }
}