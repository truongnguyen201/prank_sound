package com.hola360.pranksounds.utils

import android.util.Log
import android.view.View
import kotlinx.coroutines.*

class OnDebouncedClickListener(private val delayInMilliSeconds: Long, val action: () -> Unit) :
    View.OnClickListener {
    private var enable = true
    private var job: Job? = null

    override fun onClick(view: View?) {
        if (enable) {
            action()
            view?.isEnabled = false
            view?.isClickable = false
            enable = false
            initialJob(view)
        }
    }

    private fun initialJob(view: View?) {
        cancelJob()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(delayInMilliSeconds)
            enable = true
            view?.isEnabled = true
            view?.isClickable = true
        }
    }

    private fun cancelJob() {
        job?.cancel()
    }
}

fun View.setOnDebouncedClickListener(delayInMilliSeconds: Long = 2000, action: () -> Unit) {
    val onDebouncedClickListener = OnDebouncedClickListener(delayInMilliSeconds, action)
    setOnClickListener(onDebouncedClickListener)
}