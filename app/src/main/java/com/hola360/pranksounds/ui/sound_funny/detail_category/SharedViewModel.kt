package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.app.Application
import android.content.ContentValues
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.repository.FileDownloadRepository
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Suppress("DEPRECATION")
class SharedViewModel(private val app: Application) : ViewModel() {
    private val fileDownloadRepository = FileDownloadRepository()

    var isComplete = MutableLiveData<Boolean>()
    var isPlaying = MutableLiveData<Boolean>()
    var soundList = MutableLiveData<MutableList<Sound>>()
    var favoriteList = MutableLiveData<MutableList<Sound>>()
    var currentPosition = MutableLiveData<Int>()
    var soundDuration = MutableLiveData<Int>()
    var seekBarProgress = MutableLiveData<Int>()

    init {
        favoriteList.value = mutableListOf()
        soundList.value = mutableListOf()
    }

    fun downloadAndSet(url: String, type: String, soundName: String) {
        val basePath = Utils.getBasePath()
        val dirName = basePath + Constants.DIR_PATH
        val fileName = "$dirName$soundName.mp3"

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.getContentUriForPath(fileName)
        }

        viewModelScope.launch {
            // check if the file is exists
            if (!File(fileName).exists()) {
                Log.e("File $soundName is not exists", "1")
                //check the directory is not exists -> create the dir
                if (!File(dirName).exists()) {
                    File(dirName).mkdir()
                }

                val fileDeferred = viewModelScope.async {
                    fileDownloadRepository.downloadSoundFile(url)
                }

                try {
                    //download the file from url
                    Log.e("Downloading the file $soundName", " 1")
                    val response = fileDeferred.await()!!.body()
                    val input = response!!.byteStream()
                    val fos = FileOutputStream(fileName)
                    fos.use { output ->
                        val buffer = ByteArray(4 * 1024)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                        }
                        output.flush()
                    }
                    input.close()
                    fos.close()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Toast.makeText(
                        app.applicationContext,
                        "Cannot download the file, please check your connection and try again",
                        LENGTH_SHORT
                    ).show()
                    return@launch
                }
            }

            try {
                app.contentResolver.delete(
                    uri!!,
                    MediaStore.MediaColumns.DATA + "=\"" + File(fileName).absolutePath + "\"",
                    null
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            setAs(type, uri!!, fileName, soundName)
        }
    }

    private fun setAs(type: String, uri: Uri, fileName: String, soundName: String) {
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
        val newUri = app.contentResolver.insert(uri, values)!!
        try {
            RingtoneManager.setActualDefaultRingtoneUri(app, TYPE, newUri)
            Toast.makeText(
                app.applicationContext,
                "Set $soundName as ${type.toLowerCase(Locale.ROOT)} successfully",
                LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                app.applicationContext,
                "Something got error, please try again!!",
                LENGTH_SHORT
            ).show()
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                return SharedViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown view model class")
        }
    }
}
