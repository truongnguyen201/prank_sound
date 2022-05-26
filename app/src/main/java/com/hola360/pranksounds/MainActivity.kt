package com.hola360.pranksounds

import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.setupWithNavController
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.databinding.ActivityMainBinding
import com.hola360.pranksounds.ui.callscreen.CallScreenSharedViewModel
import com.hola360.pranksounds.ui.callscreen.addcallscreen.AddCallScreenFragment
import com.hola360.pranksounds.ui.sound_funny.detail_category.SharedViewModel
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.listener.ControlPanelListener
import kotlinx.coroutines.*

class MainActivity : BaseActivity(), ControlPanelListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var callScreenSharedViewModel: CallScreenSharedViewModel
    private var taskJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SharedViewModel.Factory(application)
        sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
        val callFactory = CallScreenSharedViewModel.Factory(application)
        callScreenSharedViewModel =
            ViewModelProvider(this, callFactory)[CallScreenSharedViewModel::class.java]

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.toolbar.visibility = View.GONE
                }
                else -> {
                    val navIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back)
                    binding.toolbar.navigationIcon = navIcon
                    binding.toolbar.visibility = View.GONE
                }
            }
        }
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
                            sharedViewModel.soundDuration.value = mediaPlayer.duration
                            start()
                            sharedViewModel.isPlaying.value = mediaPlayer.isPlaying
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
                delay(200)
                sharedViewModel.seekBarProgress.value =
                    mediaPlayer.currentPosition
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

    override fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun actionBarSetupWithNavController() {
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()

        when (item.itemId) {
            R.id.add_new_call -> {
                transaction.add(R.id.navHostFragmentContentMain, AddCallScreenFragment())
                transaction.commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}