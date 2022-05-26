package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.app.Application
import android.content.ContentValues
import android.media.RingtoneManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.*
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.repository.DetailCategoryRepository
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.SingletonHolder
import com.hola360.pranksounds.utils.Utils
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


@Suppress("DEPRECATION")
class DetailCategoryViewModel(
    private val app: Application,
    private val catId: String
    ) : ViewModel() {

    private val repository = DetailCategoryRepository(app)
    val soundLiveData = MutableLiveData<DataResponse<MutableList<Sound>>>()
    val favoriteSoundLiveData = MutableLiveData<MutableList<Sound>>()
    var totalPageNumber: Int? = null
    var currentPage: Int? = null

    init {
        soundLiveData.value = DataResponse.DataIdle()
        getSound(1, false)
    }

    val isEmpty: LiveData<Boolean> = Transformations.map(soundLiveData) {
        soundLiveData.value!!.loadingStatus == LoadingStatus.Error
    }

    val isLoading: LiveData<Boolean> = Transformations.map(soundLiveData) {
        soundLiveData.value!!.loadingStatus == LoadingStatus.Loading
    }

    val isLoadingMore: LiveData<Boolean> = Transformations.map(soundLiveData) {
        soundLiveData.value!!.loadingStatus == LoadingStatus.LoadingMore
    }

    fun getSound(pageNumber: Int, isLoadMore: Boolean) {
        Log.e("Detail view model", "get sound when init view model")
        if (soundLiveData.value!!.loadingStatus != LoadingStatus.Loading
            && soundLiveData.value!!.loadingStatus != LoadingStatus.LoadingMore
            && soundLiveData.value!!.loadingStatus != LoadingStatus.Refresh
        ) {
            if (!isLoadMore) {
                if (totalPageNumber == null) {
                    soundLiveData.value = DataResponse.DataLoading(LoadingStatus.Loading)
                } else {
                    soundLiveData.value = DataResponse.DataLoading(LoadingStatus.Refresh)
                }
            } else {
                if (pageNumber > totalPageNumber!!) return
                soundLiveData.value = DataResponse.DataLoading(LoadingStatus.LoadingMore)
            }

            viewModelScope.launch {
                val requestPage = if (totalPageNumber != null) {
                    if (soundLiveData.value!!.loadingStatus == LoadingStatus.Refresh) {
                        1
                    } else {
                        pageNumber
                    }
                } else {
                    1
                }

                val soundDeferred = viewModelScope.async {
                    repository.getDetailCategory(
                        Constants.CAT_DETAIL_PARAM,
                        catId,
                        requestPage,
                        Constants.SOUND_ITEM_PER_PAGE
                    )
                }

                val favoriteSoundDeferred = viewModelScope.async {
                    repository.getFavoriteSound()
                }

                try {
                    val soundList = soundDeferred.await()?.data?.dataApps?.listSound
                    val favoriteList = favoriteSoundDeferred.await()

                    totalPageNumber = soundDeferred.await()!!.data?.dataApps?.totalPage
                    currentPage =
                        Integer.parseInt(soundDeferred.await()!!.data?.dataApps?.currentPage.toString())

                    soundLiveData.value = if (catId == "fav_sound") {
                        DataResponse.DataSuccess(favoriteList!!)
                    } else {
                        DataResponse.DataSuccess(soundList!!)
                    }
                    favoriteSoundLiveData.value = favoriteList!!
                } catch (ex: Exception) {
                    soundLiveData.value = DataResponse.DataError()
                }
            }
        }
    }

    fun retry(){
        getSound(1, false)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application, private val catId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailCategoryViewModel::class.java)) {
                return DetailCategoryViewModel(app, catId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}