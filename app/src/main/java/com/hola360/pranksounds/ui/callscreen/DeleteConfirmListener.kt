package com.hola360.pranksounds.ui.callscreen

import com.hola360.pranksounds.data.model.Call

interface DeleteConfirmListener {
    fun onOkClick(call: Call)
}