package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.app.Application
import android.content.ContentValues
import android.media.RingtoneManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.repository.DetailCategoryRepository
import com.hola360.pranksounds.utils.Constants
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

    @Suppress("DEPRECATION")
    fun setAs(soundUrl: String, type: String, soundName: String) {
        val basePath =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
            } else {
                Environment.getExternalStorageDirectory().absolutePath
            }
        val dirName = basePath + Constants.DIR_PATH
        val fileName = "$dirName$soundName.mp3"

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.getContentUriForPath(fileName)
        }

        if (File(fileName).exists()) {
            try {
                app.contentResolver.delete(
                    uri!!,
                    MediaStore.MediaColumns.DATA + "=\"" + File(fileName).absolutePath + "\"",
                    null
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (!File(dirName).exists()) {
            File(dirName).mkdir()
        }

        val client = OkHttpClient()
        val request = Request.Builder().url(Constants.SUB_URL + soundUrl).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            //write file when request success
            override fun onResponse(call: Call, response: Response) {
                try {
                    Log.e("Downloading", soundName)
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
                    setAsSomething(fileName, type, soundName)
                } catch (ex: Exception) {
                    Log.e("ERROR", ex.message.toString())
                    ex.printStackTrace()
                }
            }
        })

        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DATA, fileName)
        values.put(MediaStore.MediaColumns.TITLE, soundName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
        var TYPE = 0
        when (type) {
            "Notification" -> {
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
                values.put(MediaStore.Audio.Media.IS_ALARM, false)
                values.put(MediaStore.Audio.Media.IS_RINGTONE, false)
                values.put(MediaStore.Audio.Media.IS_MUSIC, false);
                TYPE = RingtoneManager.TYPE_NOTIFICATION
            }
            "Ringtone" -> {
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
                values.put(MediaStore.Audio.Media.IS_ALARM, false)
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
                values.put(MediaStore.Audio.Media.IS_MUSIC, false)
                TYPE = RingtoneManager.TYPE_RINGTONE
            }
            "Alarm" -> {
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
                values.put(MediaStore.Audio.Media.IS_ALARM, true)
                values.put(MediaStore.Audio.Media.IS_RINGTONE, false)
                values.put(MediaStore.Audio.Media.IS_MUSIC, false)
                TYPE = RingtoneManager.TYPE_ALARM
            }
        }
        Log.e(
            "Check delete",
            MediaStore.MediaColumns.DATA + "=\"" + File(fileName).absolutePath + "\""
        )

        val newUri = app.contentResolver.insert(uri!!, values)!!
        Log.e("New URI", newUri.toString())

        viewModelScope.launch {
            delay(1000)
            try {
                RingtoneManager.setActualDefaultRingtoneUri(app, TYPE, newUri)
                Log.e("URI", newUri.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
//        if(!File(fileName).exists()){
//            Log.i("File is not exists", "false")
//
//        } else {
//            Log.i("File is exists", "true")
//            setAsSomething(fileName, type, soundName)
//        }
    }

    fun setAsSomething(fileName: String, type: String, soundName: String) {

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