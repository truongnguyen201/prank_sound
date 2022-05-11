package com.hola360.pranksounds.ui.callscreen.addcallscreen

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository
import kotlinx.coroutines.launch

class AddCallScreenViewModel(app: Application) : ViewModel() {
    val repository = PhoneBookRepository(app)
    val imageUri = MutableLiveData<Uri>(null)
    val callerName = MutableLiveData<String>("")
    val callerPhoneNumber = MutableLiveData<String>("")
    val isEmpty = MutableLiveData(false)

    fun setOnNameChange(name: String) {
        callerName.value = name
        checkEmpty()
    }

    fun setOnPhoneNumberChange(phoneNumber: String) {
        callerPhoneNumber.value = phoneNumber
        checkEmpty()
    }

    private fun checkEmpty() {
        isEmpty.value = !(callerName.value == ""
                || callerPhoneNumber.value == "")
    }

    fun addCallToLocal() {
        val call = if (imageUri.value != null) {
            Call(0, imageUri.value!!.path.toString(), callerName.value!!, callerPhoneNumber.value!!, true)
        }
        else {
            Call(0, "", callerName.value!!, callerPhoneNumber.value!!, true)
        }

        viewModelScope.launch {
            repository.addNewCallToLocal(call)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddCallScreenViewModel::class.java)) {
                return AddCallScreenViewModel(app) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}