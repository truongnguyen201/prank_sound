package com.hola360.pranksounds.ui.callscreen

import android.view.View
import com.hola360.pranksounds.data.model.Call

interface CallItemListener {
    fun onItemClick(call: Call,position: Int)
    fun onMoreClick(view: View, call: Call)
}