package com.hola360.pranksounds

import android.content.ComponentCallbacks2
import android.content.Context
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.databinding.ActivityMainBinding
import com.hola360.pranksounds.ui.callscreen.CallScreenSharedViewModel
import com.hola360.pranksounds.ui.sound_funny.detail_category.SharedViewModel
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.ToastUtils
import com.hola360.pranksounds.utils.listener.ControlPanelListener
import kotlinx.coroutines.*


class MainActivity : BaseActivity(), ControlPanelListener, ComponentCallbacks2 {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var callScreenSharedViewModel: CallScreenSharedViewModel
    private var taskJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMediaPlayer()
        initViewModel()
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            )
            setOnCompletionListener {
                sharedViewModel.isComplete.value = true
                sharedViewModel.isPlaying.value = mediaPlayer.isPlaying
            }
        }
    }

    private fun initViewModel() {
        sharedViewModel = SharedViewModel.getInstance(application)
        val factory = CallScreenSharedViewModel.Factory(this.application)
        callScreenSharedViewModel =
            ViewModelProvider(this, factory)[CallScreenSharedViewModel::class.java]

        sharedViewModel.currentPosition.observe(this) {
            it?.let {
                if (sharedViewModel.soundList.value!!.size > 0) {
                    val sound = sharedViewModel.soundList.value!![it]
                    val uri = Uri.parse(Constants.SUB_URL + sound.soundUrl)
                    mediaPlayer.apply {
                        reset()
                        setDataSource(applicationContext, uri)
                        prepareAsync()
                        setOnPreparedListener {
                            sharedViewModel.soundDuration.value = duration
                            start()
                            sharedViewModel.isPlaying.value = mediaPlayer.isPlaying
                            sharedViewModel.updateDelay.value = duration / 100
                        }
                    }
                }
            }
        }

        sharedViewModel.isPlaying.observe(this) {
            it?.let {
                if (it) {
                    initialJob()
                } else {
                    cancelJob()
                }
            }
        }

        sharedViewModel.seekBarProgress.observe(this) {
            it?.let {
                if (it == sharedViewModel.soundDuration.value!!) {
                    cancelJob()
                }
            }
        }
    }

    private fun initialJob() {
        cancelJob()
        taskJob = CoroutineScope(Dispatchers.Main).launch {
            while (mediaPlayer.isPlaying) {
                sharedViewModel.seekBarProgress.value =
                    mediaPlayer.currentPosition
                delay(sharedViewModel.updateDelay.value!!.toLong())
            }
        }
    }

    private fun cancelJob() {
        taskJob?.cancel()
    }

    override fun getFragmentID(): Int {
        return R.id.navHostFragmentContentMain
    }

    override fun bind() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onPlayPauseClick() {
        mediaPlayer.apply {
            if (isPlaying) {
                pause()
            } else {
                start()
            }
            sharedViewModel.isPlaying.value = mediaPlayer.isPlaying
        }
    }

    override fun onReset() {
        mediaPlayer.reset()
    }

    override fun onStartTracking() {
        mediaPlayer.pause()
    }

    override fun onPlaySound(sound: Sound) {
        val uri = Uri.parse(Constants.SUB_URL + sound.soundUrl)
        mediaPlayer.apply {
            reset()
            setDataSource(applicationContext, uri)
            prepareAsync()
            setOnPreparedListener {
                sharedViewModel.soundDuration.value = duration
                start()
                sharedViewModel.isPlaying.value = mediaPlayer.isPlaying
            }
        }
    }

    override fun onPanelClick(sound: Sound) {}

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun onSeekBarChange(fromUser: Boolean, progress: Int) {
        if (fromUser) {
            mediaPlayer.seekTo(progress)
            mediaPlayer.start()
            sharedViewModel.isPlaying.value = true
            sharedViewModel.seekBarProgress.value = progress
            sharedViewModel.isComplete.value = false
        }
    }

    override fun onDetachFragment() {
        mediaPlayer.reset()
        sharedViewModel.isPlaying.value = false
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager? =
                        applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        ToastUtils.release()
    }

    override fun onTrimMemory(level: Int) {
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                finish()
            }
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                recreate()
            }
        }
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        recreate()
    }
}