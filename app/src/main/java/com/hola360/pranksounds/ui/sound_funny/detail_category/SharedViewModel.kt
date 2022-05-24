package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.app.Application
import android.content.ContentValues
import android.media.MediaMetadataRetriever
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

    fun downloadAndSet(url: String, type: Int, soundName: String) {
        val basePath = Utils.getBasePath()
        val dirName = basePath + Constants.DIR_PATH
        val fileName = "$dirName$soundName.mp3"
        val file = File(fileName)

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
                    //download the file from url and write to file
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

            val metaRetriever = MediaMetadataRetriever()
            metaRetriever.setDataSource(fileName)
            val duration =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val typeToast = when (type) {
                RingtoneManager.TYPE_ALARM -> "Alarm"
                RingtoneManager.TYPE_NOTIFICATION -> "Notification"
                else -> "Ringtone"
            }
            val message = if (Utils.setRingtone(
                    app.applicationContext,
                    duration!!.toLong(),
                    file,
                    uri!!,
                    type
                )
            ) {
                "Set $soundName as $typeToast successfully"
            } else {
                "Set $soundName as $typeToast does not successfully"
            }
            Toast.makeText(app.applicationContext, message, LENGTH_SHORT).show()
        }
    }

    private fun setAs(type: Int, uri: Uri, fileName: String, soundName: String) {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DATA, fileName)
        values.put(MediaStore.MediaColumns.TITLE, soundName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")

        val newUri = app.contentResolver.insert(uri, values)!!
        try {
            RingtoneManager.setActualDefaultRingtoneUri(app, type, newUri)
            Toast.makeText(
                app.applicationContext,
                "Set $soundName as $type successfully",
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
