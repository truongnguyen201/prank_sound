package com.hola360.pranksounds.utils.listener

import com.hola360.pranksounds.ui.callscreen.setcall.WaitCallPeriod

object Converter {
    fun convertTime(wait: WaitCallPeriod): Long {
        return when (wait) {
            WaitCallPeriod.Now -> 0
            WaitCallPeriod.FiveSeconds -> 5 * 1000
            WaitCallPeriod.ThirtySeconds -> 15 * 1000
            WaitCallPeriod.OneMinute -> 60 * 1000
        }
    }
}