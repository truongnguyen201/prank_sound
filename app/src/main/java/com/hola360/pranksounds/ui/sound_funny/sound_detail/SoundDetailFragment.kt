package com.hola360.pranksounds.ui.sound_funny.sound_detail

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.core.view.doOnLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.FragmentSoundDetailBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.detail_category.DetailCategoryViewModel
import com.hola360.pranksounds.ui.sound_funny.detail_category.SharedViewModel
import com.hola360.pranksounds.ui.sound_funny.sound_detail.adapter.ViewPagerAdapter
import com.hola360.pranksounds.utils.Utils
import com.hola360.pranksounds.utils.item_decoration.HorizontalMarginItemDecoration
import com.hola360.pranksounds.utils.listener.ControlPanelListener

class SoundDetailFragment : BaseFragment<FragmentSoundDetailBinding>() {
    private val viewPagerAdapter = ViewPagerAdapter()
    private val sharedVM by activityViewModels<SharedViewModel>()
    private lateinit var soundDetailViewModel : DetailCategoryViewModel
    private val args: SoundDetailFragmentArgs by navArgs()
    private lateinit var controlPanelListener: ControlPanelListener
    private lateinit var popupWindow: PopupWindow
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels - 100
    var isUserControl = false

    override fun getLayout(): Int {
        return R.layout.fragment_sound_detail
    }

    override fun initView() {
        val setAsListener = View.OnClickListener {
            popupWindow.dismiss()
        }
        popupWindow = Utils.showPopUpSetAs(requireActivity(), setAsListener)

        binding.apply {
            toolbar.apply {
                setNavigationOnClickListener {
                    requireActivity().onBackPressed()
                }
            }

            ibSetAs.setOnClickListener {
                popupWindow.showAsDropDown(
                    toolbar, (screenWidth * 0.7).toInt(),
                    -(toolbar.height * 0.8).toInt()
                )
            }



            vp2Sound.apply {
                adapter = viewPagerAdapter

                doOnLayout {
                    val pos =
                        args.position + (args.position / 10 - 1)
                    Log.e("Position", pos.toString())
                    vp2Sound.setCurrentItem(pos, false)
                }

                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        val newPosition = position + (position / 10 + 1)
                        toolbar.title = sharedVM.soundList.value!![newPosition].title
                        cbFavorite.isChecked =
                            sharedVM.favoriteList.value!!.contains(sharedVM.soundList.value!![newPosition])
                        sharedVM.currentPosition.value = newPosition
                    }
                })
                offscreenPageLimit = 1
                setPageTransformer(initPageTransformer())
                addItemDecoration(initItemDecoration())
            }

            sbDuration.apply {
                setPadding(70, 0, 70, 0)
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                        isUserControl = true
                        controlPanelListener.onStartTracking()
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        isUserControl = false
                        controlPanelListener.onSeekBarChange(true, p0!!.progress)
                    }
                })
            }

            ivNext.setOnClickListener {
                val viewPagerPosition = vp2Sound.currentItem
                vp2Sound.setCurrentItem(viewPagerPosition + 1, true)
                sharedVM.currentPosition.apply {
                    val positionInList = viewPagerPosition + (viewPagerPosition / 10 + 1)
                    //if current position less than sound list size
                    if (positionInList < sharedVM.soundList.value!!.size - 1) {
                        //if current item is banner
                        value = if (sharedVM.soundList.value!![positionInList + 1].isBanner) {
                            positionInList + 2
                        } else {
                            positionInList + 1
                        }
                    }
                }
            }

            ivPrevious.setOnClickListener {
                val viewPagerPosition = vp2Sound.currentItem
                vp2Sound.setCurrentItem(viewPagerPosition - 1, true)
                sharedVM.currentPosition.apply {
                    val positionInList = viewPagerPosition + (viewPagerPosition / 10 + 1)
                    //if current position larger than 1
                    if (positionInList > 1) {
                        value = if (sharedVM.soundList.value!![positionInList - 1].isBanner) {
                            positionInList - 2
                        } else {
                            positionInList - 1
                        }
                    }
                }
            }

            ivPlayPause.setOnClickListener {
                if (controlPanelListener.isPlaying()) {
                    ivPlayPause.setImageResource(R.drawable.ic_play_circle_51dp)
                } else {
                    ivPlayPause.setImageResource(R.drawable.ic_pause_circle_51dp)
                }
                controlPanelListener.onPlayPauseClick()
            }
        }
    }

    override fun initViewModel() {
        sharedVM.soundList.observe(this) {
            it?.let {
                if (it.size > 0) {
                    viewPagerAdapter.setData(it)
                }
            }
        }

        sharedVM.soundDuration.observe(this) {
            it?.let {
                binding.sbDuration.max = it
            }
        }

        sharedVM.isComplete.observe(this) {
            it?.let {
                binding.apply {
                    if (it) {
                        ivPlayPause.setImageResource(R.drawable.ic_play_circle_51dp)
                    } else {
                        ivPlayPause.setImageResource(R.drawable.ic_pause_circle_51dp)
                    }
                }
            }
        }

        sharedVM.currentPosition.observe(this) {
            it?.let {
                binding.apply {
                    ivPlayPause.setImageResource(R.drawable.ic_pause_circle_51dp)
                    sbDuration.progress = 0
                }
            }
        }

        val resource = resources
        val paint = Paint()
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 20F
        paint.color = (-0x1)
        sharedVM.seekBarProgress.observe(this) {
            val animator = ObjectAnimator.ofInt(binding.sbDuration, "progress", it!! - 10, it)
            it.let {
                if (!isUserControl) {
                    binding.sbDuration.apply {
                        animator.duration = 10
                        animator.interpolator = LinearInterpolator()
                        animator.start()
                        Utils.drawThumb(
                            requireContext(),
                            this,
                            it,
                            sharedVM.soundDuration.value!!,
                            resource,
                            paint
                        )
                    }
                }
            }
        }
    }

    private fun initPageTransformer(): ViewPager2.PageTransformer {
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        return ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
            // If you want a fading effect uncomment the next line:
            page.alpha = 0.25f + (1 - kotlin.math.abs(position))
        }
    }

    private fun initItemDecoration(): HorizontalMarginItemDecoration {
        return HorizontalMarginItemDecoration(
            requireContext(),
            R.dimen.viewpager_current_item_horizontal_margin
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            controlPanelListener = context as ControlPanelListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString()
                        + " must implement ControlPanelListener"
            )
        }
    }
}