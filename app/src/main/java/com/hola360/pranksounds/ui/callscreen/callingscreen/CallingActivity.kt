package com.hola360.pranksounds.ui.callscreen.callingscreen

import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.ActivityCallingBinding
import com.hola360.pranksounds.ui.callscreen.callingscreen.adapter.PanelAdapter
import com.hola360.pranksounds.utils.Constants
import kotlinx.coroutines.*

class CallingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallingBinding
    private lateinit var gradient: GradientDrawable
    private lateinit var evaluator: ArgbEvaluator
    private lateinit var gradientAnimator: ValueAnimator
    private lateinit var waveAnimation: ScaleAnimation
    private var countTime: Job? = null
    private var timing = 0
    private val panelAdapter = PanelAdapter()
    private var isAnswer = false
    private var swatch: Palette? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        val intent = intent
        var call = Call()
        val args = intent?.getParcelableExtra<Call>("call")
        if (args != null)
            call = args

        setContentView(binding.motionLayout)
        setupBackground(call)
        setupWaveAnimation(binding.vAnswer, binding.vDismiss)
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)

        binding.apply {
            rvPanel.adapter = panelAdapter
            rvPanel.layoutManager = gridLayoutManager
            rvPanel.setHasFixedSize(true)

            tvCallerName.text = call.name
            tvPhoneNumber.text = call.phone

            Glide.with(baseContext).load(
                if (call.isLocal) {
                    call.avatarUrl
                } else {
                    Constants.SUB_URL + call.avatarUrl
                }
            ).into(binding.ivAvatar)

            motionLayout.apply {
                setTransitionListener(object : MotionLayout.TransitionListener {
                    override fun onTransitionStarted(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int
                    ) {
                        waveAnimation.cancel()
                    }

                    override fun onTransitionChange(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int,
                        progress: Float
                    ) {

                    }

                    override fun onTransitionCompleted(
                        motionLayout: MotionLayout?,
                        currentId: Int
                    ) {
                        gradientAnimator.cancel()
                        binding.root.setBackgroundColor(swatch!!.getVibrantColor(Color.MAGENTA))

                        if (currentId == R.id.answerState) {
                            isAnswer = true
                            startCountTime()
                            ivDismiss.setOnClickListener {
                                finish()
                            }
                        } else {
                            finish()
                        }
                    }

                    override fun onTransitionTrigger(
                        motionLayout: MotionLayout?,
                        triggerId: Int,
                        positive: Boolean,
                        progress: Float
                    ) {
                    }
                })
            }
        }
    }

    private fun setupWaveAnimation(view1: View, view2: View) {
        waveAnimation = ScaleAnimation(
            1f,
            1.3f,
            1f,
            1.3f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        waveAnimation.apply {
            duration = 2000
            repeatCount = Animation.INFINITE
            repeatMode = Animation.RESTART
        }
        view1.startAnimation(waveAnimation)
        view2.startAnimation(waveAnimation)
    }

    fun createPaletteSync(bitmap: Bitmap): Palette {
        return Palette.Builder(bitmap)
            .addFilter { rgb, hsl -> true }
            .maximumColorCount(24)
            .addTarget(Target.LIGHT_VIBRANT)
            .addTarget(Target.VIBRANT)
            .addTarget(Target.LIGHT_MUTED)
            .generate()
    }

    private fun setupBackground(call: Call) {
        gradient = binding.root.background as GradientDrawable
        evaluator = ArgbEvaluator()
        gradientAnimator = TimeAnimator.ofFloat(0.0f, 1.0f)
        gradientAnimator.duration = 3000
        gradientAnimator.repeatCount = ValueAnimator.INFINITE
        gradientAnimator.repeatMode = ValueAnimator.REVERSE

        Glide.with(applicationContext)
            .asBitmap()
            .load(
                if (call.isLocal) {

                    call.avatarUrl
                } else {
                    Constants.SUB_URL + call.avatarUrl
                }
            )
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    swatch = createPaletteSync(resource)
                    val start = swatch!!.getVibrantColor(Color.DKGRAY)
                    val mid = swatch!!.getLightVibrantColor(Color.RED)
                    val end = swatch!!.getLightMutedColor(Color.MAGENTA)
                    gradientAnimator.addUpdateListener {
                        val fraction = it.animatedFraction
                        val newStart = evaluator.evaluate(fraction, start, end) as Int
                        val newMid = evaluator.evaluate(fraction, mid, start) as Int
                        val newEnd = evaluator.evaluate(fraction, end, mid) as Int
                        gradient.colors = intArrayOf(newStart, newMid, newEnd)
                    }

                    gradientAnimator.start()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun startCountTime() {
        stopCountTime()
        countTime = CoroutineScope(Dispatchers.Main).launch {
            while (isAnswer) {
                binding.tvTitleNTime.text = String.format("%02d:%02d", timing / 60, timing % 60)
                delay(1000)
                timing++
            }
        }
    }

    private fun stopCountTime() {
        countTime?.cancel()
    }
}