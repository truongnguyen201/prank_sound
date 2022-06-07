package com.hola360.pranksounds.ui.dialog.confirmdelete

import android.content.Context
import androidx.lifecycle.*
import com.hola360.pranksounds.App
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call

class ConfirmDeleteDialogViewModel(context: Context, call: Call): ViewModel() {
    var callLiveData = MutableLiveData<Call>()

    init {
        callLiveData.value = call
    }

    val title: LiveData<String> = Transformations.map(callLiveData) {
        (context.resources.getString(R.string.confirm_delete_start)
        + it.name + context.resources.getString(R.string.confirm_delete_start_end))
    }

    fun getCall(): Call {
        return callLiveData.value!!
    }

    fun setCall(call: Call) {
        callLiveData.value = call
    }

    class Factory(private val context: Context, val call: Call) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ConfirmDeleteDialogViewModel::class.java))
                return ConfirmDeleteDialogViewModel(context, call) as T
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}