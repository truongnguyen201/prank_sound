package com.hola360.pranksounds.ui.callscreen.addcallscreen
import android.app.Application
import androidx.lifecycle.*
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import kotlinx.coroutines.launch

class AddCallScreenViewModel(app: Application) : ViewModel() {
    val repository = PhoneBookRepository(app)
    private var _callLiveData = MutableLiveData<Call>(null)
    val callLiveData: LiveData<Call> = _callLiveData

    var curCallModel: Call? = null
    var officialModel: Call? = null
    var phoneLiveData = MutableLiveData("")

    init {
        curCallModel = Call()
    }
    var isUpdate = false

    val isEmpty: LiveData<Boolean> = Transformations.map(_callLiveData){
        _callLiveData.value?.phone.isNullOrEmpty()
    }
    val isDefault: LiveData<Boolean> = Transformations.map(_callLiveData) {
        _callLiveData.value?.avatarUrl == ""
    }

    fun setOnNameChange(name: String) {
        curCallModel!!.name = name.trim()
        _callLiveData.postValue(curCallModel)
    }

    fun setOnPhoneNumberChange(phoneNumber: String) {
        curCallModel!!.phone = phoneNumber.trim()
        _callLiveData.postValue(curCallModel)
    }

    fun setAvatarUrl(url: String) {
        curCallModel!!.avatarUrl = url
        _callLiveData.postValue(curCallModel)
    }

    fun setCall(callTrans: Call?) {
        callTrans?.let {
            curCallModel = it
            _callLiveData.postValue(curCallModel)
            isUpdate = true
        }
        if (callTrans == null) {
            setIsLocal(true)
        }
        officialModel = callTrans?.copy()
    }

    fun setIsLocal(isLocal: Boolean) {
        curCallModel!!.isLocal = isLocal
        _callLiveData.postValue(curCallModel)
    }

    fun getCurrentCall(): Call? {
        return curCallModel
    }

    fun addCallToLocal() {
        _callLiveData.postValue(curCallModel)
        val newCall: Call = curCallModel!!
        if (!newCall.isLocal && newCall.avatarUrl != "") {
            newCall.avatarUrl = Constants.SUB_URL + newCall.avatarUrl
        }
        newCall.isLocal = true
        officialModel = newCall.copy()
        viewModelScope.launch {
            repository.addNewCallToLocal(newCall)
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