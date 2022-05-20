package com.hola360.pranksounds.ui.callscreen.callingscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.ui.callscreen.callingscreen.CallingActivity

class CallingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val i = Intent("com.hola360.pranksounds", null, context, CallingActivity::class.java)
        if (intent?.action == "com.hola360.pranksounds") {
            val myCall = intent.getParcelableExtra<Call>("call")
            Log.e("reciever", "${myCall?.avatarUrl} ${intent.getStringExtra("TEST")}", )

//            val intentToService = Intent("Data", null, context, MyService::class.java)
//            if (myCall != null) {
//                i.putExtra("call", myCall)
//                context.startService(intentToService)
//            }
        }


        var call = Call()
        val args = intent?.extras?.getParcelable<Call>("call")
        if (args != null)
            call =  args as Call

//        Log.e("reciever", ": ${call.avatarUrl}", )
        if (call != null) {
            i.putExtra("call", call as Parcelable)
        }

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(i)
    }
}