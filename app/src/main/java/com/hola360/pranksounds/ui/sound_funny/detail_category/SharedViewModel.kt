package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.app.Application
import android.media.MediaMetadataRetriever
import android.media.RingtoneManager
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.model.SubmittedSound
import com.hola360.pranksounds.data.repository.DetailCategoryRepository
import com.hola360.pranksounds.data.repository.FileDownloadRepository
import com.hola360.pranksounds.data.repository.SubmitSoundRepository
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.SingletonHolder
import com.hola360.pranksounds.utils.ToastUtils
import com.hola360.pranksounds.utils.Utils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class SharedViewModel private constructor(private val app: Application) : ViewModel() {
    private val fileDownloadRepository = FileDownloadRepository()
    private val categoryRepository = DetailCategoryRepository(app)
    private val submitSoundRepository = SubmitSoundRepository(app)

    var isComplete = MutableLiveData<Boolean>()
    var isPlaying = MutableLiveData<Boolean>()
    var soundList = MutableLiveData<MutableList<Sound>>()
    var favoriteList = MutableLiveData<MutableList<String>>()

    // position of playing item in the list
    var currentPosition = MutableLiveData<Int>()

    // duration of playing item
    var soundDuration = MutableLiveData<Int>()

    // progress of MediaPlayer
    var seekBarProgress = MutableLiveData<Int>()

    var updateDelay = MutableLiveData<Int>()

    companion object : SingletonHolder<SharedViewModel, Application>(::SharedViewModel)

    init {
        favoriteList.value = mutableListOf()
        soundList.value = mutableListOf()
    }

    fun downloadAndSet(url: String, type: Int, soundName: String, soundId: String) {
        val basePath = Utils.getBasePath()
        val dirName = basePath + Constants.DIR_PATH
        val newSoundName = soundName.filterNot { Constants.FILE_NAME_FILTER.indexOf(it) > -1 }
        val fileName = "$dirName$newSoundName.mp3"
        val file = File(fileName)

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.getContentUriForPath(fileName)
        }
        viewModelScope.launch {
            // check if the file is exists
            if (!File(fileName).exists()) {
                //check the directory is not exists -> create the dir
                if (!File(dirName).exists()) {
                    File(dirName).mkdir()
                }

                val fileDeferred = viewModelScope.async {
                    fileDownloadRepository.downloadSoundFile(url)
                }

                try {
                    //download the file from url and write to file
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

                    val submittedSound = submitSoundRepository.getSubmittedSound(soundId)
                    if (submittedSound == null || !submittedSound.isDownloaded) {
                        val submitResponse = submitSoundRepository.submitSound(
                            Constants.SUBMIT_TYPE,
                            soundId,
                            Constants.SUBMIT_DOWNLOAD_TYPE
                        )
                        if (submitResponse!!.data.data_apps.status) {
                            if (submittedSound == null) {
                                submitSoundRepository.addSubmitted(
                                    SubmittedSound(
                                        soundId,
                                        isDownloaded = true,
                                        isLiked = false
                                    )
                                )
                            } else {
                                submitSoundRepository.updateDownloaded(soundId)
                            }
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    ToastUtils.getInstance(app.applicationContext).showToast(
                        app.resources.getString(
                            R.string.download_error_message
                        )
                    )
                    return@launch
                }
            }

            //get duration of the .mp3 file
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
                    type,
                    soundName
                )
            ) {
                "Set $soundName as $typeToast successfully"
            } else {
                "Something got error, please try again"
            }
            ToastUtils.getInstance(app.applicationContext).showToast(message)
        }
    }

    fun addFavoriteSound(sound: Sound) {
        viewModelScope.launch {
            categoryRepository.addFavoriteSound(sound)
            val submittedSound = submitSoundRepository.getSubmittedSound(sound.soundId)
            if (submittedSound == null || !submittedSound.isLiked) {
                try {
                    val submitResponse = submitSoundRepository.submitSound(
                        Constants.SUBMIT_TYPE,
                        sound.soundId,
                        Constants.SUBMIT_LIKE_TYPE
                    )
                    if (submitResponse!!.data.data_apps.status) {
                        if (submittedSound == null) {
                            submitSoundRepository.addSubmitted(
                                SubmittedSound(
                                    sound.soundId,
                                    isDownloaded = false,
                                    isLiked = true
                                )
                            )
                        } else {
                            submitSoundRepository.updateLiked(sound.soundId)
                        }
                    }
                } catch (ex: Exception) { }
            }
            favoriteList.value = categoryRepository.getFavoriteSoundID()
        }
    }

    fun removeFavoriteSound(sound: Sound) {
        viewModelScope.launch {
            categoryRepository.removeFavoriteSound(sound)
            favoriteList.value = categoryRepository.getFavoriteSoundID()
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
