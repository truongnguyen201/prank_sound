package com.hola360.pranksounds.ui.sound_funny.sound_detail

import android.content.Context
import android.content.res.Resources
import android.media.RingtoneManager
import android.view.View
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.doOnLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.databinding.FragmentSoundDetailBinding
import com.hola360.pranksounds.databinding.LayoutSeekbarThumbBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.detail_category.SharedViewModel
import com.hola360.pranksounds.ui.sound_funny.sound_detail.adapter.ViewPagerAdapter
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import com.hola360.pranksounds.utils.item_decoration.HorizontalMarginItemDecoration
import com.hola360.pranksounds.utils.listener.ControlPanelListener

class SoundDetailFragment : BaseFragment<FragmentSoundDetailBinding>() {
    private val viewPagerAdapter = ViewPagerAdapter()
    private lateinit var sharedVM: SharedViewModel
    private lateinit var soundDetailViewModel: SoundDetailViewModel
    private val args: SoundDetailFragmentArgs by lazy {
        SoundDetailFragmentArgs.fromBundle(
            requireArguments()
        )
    }
    private lateinit var controlPanelListener: ControlPanelListener
    private lateinit var popupWindow: PopupWindow
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels - 100
    private var isUserControl = false
    private lateinit var seekbarBinding: LayoutSeekbarThumbBinding
    private lateinit var soundList: Array<Sound>
    lateinit var runnable: Runnable

    override fun getLayout(): Int {
        return R.layout.fragment_sound_detail
    }

    override fun initView() {
        soundList = args.list!!
        viewPagerAdapter.setData(soundList)

        seekbarBinding =
            LayoutSeekbarThumbBinding.inflate(requireActivity().layoutInflater, null, false)

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
                    val pos = args.position - ((args.position) / 11 + 1)
                    vp2Sound.setCurrentItem(pos, true)
                }
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        val newPosition = position + (position / 10 + 1)
                        toolbar.title = soundList[newPosition].title
                        cbFavorite.isChecked =
                            sharedVM.favoriteList.value!!.contains(soundList[newPosition].soundId)
                        controlPanelListener.onPlaySound(soundList[newPosition])
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
                thumb = Utils.createThumb(0, 0, seekbarBinding, resources)
            }

            ivNext.setOnClickListener {
                val viewPagerPosition = vp2Sound.currentItem
                vp2Sound.setCurrentItem(viewPagerPosition + 1, true)
            }

            ivPrevious.setOnClickListener {
                val viewPagerPosition = vp2Sound.currentItem
                vp2Sound.setCurrentItem(viewPagerPosition - 1, true)
            }

            ivPlayPause.setOnClickListener {
                if (controlPanelListener.isPlaying()) {
                    ivPlayPause.setImageResource(R.drawable.ic_play_circle_51dp)
                } else {
                    ivPlayPause.setImageResource(R.drawable.ic_pause_circle_51dp)
                }
                controlPanelListener.onPlayPauseClick()
            }

            cbFavorite.apply {
                setOnClickListener {
                    val position = vp2Sound.currentItem + vp2Sound.currentItem / 10 + 1
                    if (isChecked) {
                        sharedVM.addFavoriteSound(soundList[position])
                    } else {
                        sharedVM.removeFavoriteSound(soundList[position])
                    }
                }
            }

            val setAsListener = View.OnClickListener {
                val type = when ((it as TextView).text.toString()) {
                    "Ringtone" -> RingtoneManager.TYPE_RINGTONE
                    "Notification" -> RingtoneManager.TYPE_NOTIFICATION
                    else -> RingtoneManager.TYPE_ALARM
                }
                if (Utils.storagePermissionGrant(requireContext())) {
                    if (Utils.writeSettingPermissionGrant(requireContext())) {
                        setupWhenPermissionGranted(
                            type,
                            vp2Sound.currentItem + vp2Sound.currentItem / 10 + 1
                        )
                    } else {
                        Utils.requestWriteSettingPermission(requireActivity(), requireContext())
                    }
                } else {
                    requestStoragePermission()
                }

                popupWindow.dismiss()
            }
            popupWindow = Utils.showPopUpSetAs(requireActivity(), setAsListener)
        }
    }

    override fun initViewModel() {
        val factory = SoundDetailViewModel.Factory(requireActivity().application)
        soundDetailViewModel = ViewModelProvider(this, factory)[SoundDetailViewModel::class.java]

        sharedVM = SharedViewModel.getInstance(requireActivity().application)

        sharedVM.soundDuration.observe(this) {
            it?.let {
                binding.sbDuration.max = it
            }
        }

        val resource = resources
        sharedVM.seekBarProgress.observe(this) {
            it.let {
                if (!isUserControl) {
                    binding.sbDuration.apply {
                        progress = it
                        thumb = Utils.createThumb(
                            it,
                            sharedVM.soundDuration.value!!,
                            seekbarBinding,
                            resource
                        )
                    }
                }
            }
        }

        sharedVM.isPlaying.observe(this) {
            it?.let {
                binding.ivPlayPause.apply {
                    if (it) {
                        setImageResource(R.drawable.ic_pause_circle_51dp)
                    } else {
                        setImageResource(R.drawable.ic_play_circle_51dp)
                    }
                }
            }
        }

        sharedVM.isComplete.observe(this) {
            it?.let {
                if (it) {
                    val duration = sharedVM.soundDuration.value!!
                    if (duration < Constants.MIN_SOUND_DURATION
                        || binding.sbDuration.progress < duration
                    ) {
                        binding.sbDuration.progress = binding.sbDuration.max
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
            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
            page.alpha = 0.25f + (1 - kotlin.math.abs(position))
        }
    }

    private fun initItemDecoration(): HorizontalMarginItemDecoration {
        return HorizontalMarginItemDecoration(
            requireContext(),
            R.dimen.viewpager_current_item_horizontal_margin
        )
    }

    private fun requestStoragePermission() {
        resultLauncher.launch(
            Utils.getStoragePermissions()
        )
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (Utils.storagePermissionGrant(requireContext())) {
                Utils.requestWriteSettingPermission(requireActivity(), requireContext())
            } else {
                Utils.showAlertPermissionNotGrant(binding.root, requireActivity())
            }
        }

    private fun setupWhenPermissionGranted(type: Int, position: Int) {
        sharedVM.downloadAndSet(
            soundList[position].soundUrl!!,
            type,
            soundList[position].title!!
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

    override fun onDetach() {
        controlPanelListener.onDetachFragment()
        super.onDetach()
    }
}