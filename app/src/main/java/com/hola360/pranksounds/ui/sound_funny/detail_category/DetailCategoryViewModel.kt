package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.app.Application
import android.content.ContentValues
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.*
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.repository.DetailCategoryRepository
import com.hola360.pranksounds.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class DetailCategoryViewModel(private val app: Application, private val catId: String) :
    ViewModel() {

    private val repository = DetailCategoryRepository(app)
    val soundLiveData = MutableLiveData<DataResponse<MutableList<Sound>>>()
    val favoriteSoundLiveData = MutableLiveData<MutableList<Sound>>()
    var totalPageNumber: Int? = null
    var currentPage: Int? = null

    init {
        soundLiveData.value = DataResponse.DataIdle()
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

    fun setAs(soundUrl: String, type: String, soundName: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(Constants.SUB_URL + soundUrl).build()

        val fileName: String =
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC
            ).absolutePath +
                    Constants.DIR_PATH +
                    soundName +
                    ".mp3"

        if (!File(fileName).exists()) {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val inputStream: InputStream = response.body!!.byteStream()
                        val outputStream: OutputStream = FileOutputStream(fileName)
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (inputStream.read(buffer).also { length = it } > 0) {
                            outputStream.write(buffer, 0, length)
                        }
                        outputStream.flush()
                        outputStream.close()
                        inputStream.close()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            })
        }

        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DATA, fileName)
        values.put(MediaStore.MediaColumns.TITLE, soundName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
        when (type) {
            "tvNotification" -> {
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
                values.put(MediaStore.Audio.Media.IS_ALARM, false)
                values.put(MediaStore.Audio.Media.IS_RINGTONE, false)
            }
            "tvRingtone" -> {
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
                values.put(MediaStore.Audio.Media.IS_ALARM, false)
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
            }
            "tvAlarm" -> {
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
                values.put(MediaStore.Audio.Media.IS_ALARM, true)
                values.put(MediaStore.Audio.Media.IS_RINGTONE, false)
            }
        }
        val uri = MediaStore.Audio.Media.getContentUriForPath(fileName)
        val newUri: Uri = app.contentResolver.insert(uri!!, values)!!
        RingtoneManager.setActualDefaultRingtoneUri(
            app,
            RingtoneManager.TYPE_RINGTONE,
            newUri
        )
    }

    fun addFavoriteSound(sound: Sound) {
        viewModelScope.launch {
            repository.addFavoriteSound(sound)
        }
    }

    fun removeFavoriteSound(sound: Sound) {
        viewModelScope.launch {
            repository.removeFavoriteSound(sound)
        }
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