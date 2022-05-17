package com.hola360.pranksounds.ui.callscreen.addcallscreen
import android.app.Application
import androidx.lifecycle.*
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import kotlinx.coroutines.launch

class AddCallScreenViewModel(app: Application, val call: Call?) : ViewModel() {
    val repository = PhoneBookRepository(app)
    val callLiveData = MutableLiveData<Call>(null)
    var curCallModel: Call? = null

    init {
        curCallModel = call ?: Call()
    }
    var isUpdate = false

    val isEmpty: LiveData<Boolean> = Transformations.map(callLiveData){
        curCallModel!!.name.isNullOrEmpty() || curCallModel!!.phone.isNullOrEmpty()
    }

    fun setOnNameChange(name: String) {
        curCallModel!!.name = name
        callLiveData.value = curCallModel
    }

    fun setOnPhoneNumberChange(phoneNumber: String) {
        curCallModel!!.phone = phoneNumber
        callLiveData.value = curCallModel
    }

    fun setCall(callTrans: Call) {
        curCallModel = callTrans
        callLiveData.value = curCallModel
        isUpdate = true
    }

    fun addCallToLocal() {
        var newCall: Call = curCallModel!!

        if (!newCall.isLocal && call != null) {
            newCall.avatarUrl = Constants.SUB_URL + newCall.avatarUrl
        }
        newCall.isLocal = true
        viewModelScope.launch {
            repository.addNewCallToLocal(newCall)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application,val call: Call?) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddCallScreenViewModel::class.java)) {
                return AddCallScreenViewModel(app,call) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}