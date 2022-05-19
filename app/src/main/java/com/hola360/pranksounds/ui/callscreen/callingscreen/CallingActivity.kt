package com.hola360.pranksounds.ui.callscreen.callingscreen

import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hola360.pranksounds.databinding.ActivityCallingBinding

class CallingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallingBinding
    private val url =
        "https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2021/10/9/961804/Ca-Si-Lisa-Blackpink.jpg"
    private lateinit var gradient: GradientDrawable
    private lateinit var evaluator: ArgbEvaluator
    private lateinit var gradientAnimator: ValueAnimator
    private lateinit var waveAnimation: ScaleAnimation
    private var swatch: Palette? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(binding.motionLayout)
        setupBackground()
        setupWaveAnimation(binding.vAnswer, binding.vDismiss)
        binding.apply {
            motionLayout.apply {
                setTransitionListener(object : MotionLayout.TransitionListener {
                    override fun onTransitionStarted(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int
                    ) {

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
                        binding.root.setBackgroundColor(swatch!!.getLightVibrantColor(Color.MAGENTA))
                        waveAnimation.cancel()
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

    private fun setupBackground() {
        gradient = binding.root.background as GradientDrawable
        evaluator = ArgbEvaluator()
        gradientAnimator = TimeAnimator.ofFloat(0.0f, 1.0f)
        gradientAnimator.duration = 2000
        gradientAnimator.repeatCount = ValueAnimator.INFINITE
        gradientAnimator.repeatMode = ValueAnimator.REVERSE

        Glide.with(applicationContext)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    swatch = createPaletteSync(resource)
                    val start = swatch!!.getVibrantColor(Color.DKGRAY)
                    val mid = swatch!!.getLightVibrantColor(Color.RED)
                    val end = swatch!!.getLightMutedColor(Color.MAGENTA)
                    Log.e("Swatch", "$start, $mid, $end")
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
}