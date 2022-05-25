package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.media.RingtoneManager
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.style.Circle
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.databinding.FragmentDetailCategoryBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.detail_category.adapter.DetailCategoryAdapter
import com.hola360.pranksounds.utils.Utils
import com.hola360.pranksounds.utils.listener.ControlPanelListener
import com.hola360.pranksounds.utils.listener.SoundListener

@Suppress("SENSELESS_COMPARISON")
class DetailCategoryFragment : BaseFragment<FragmentDetailCategoryBinding>(), SoundListener {
    private val detailCategoryAdapter = DetailCategoryAdapter()
    private lateinit var detailCategoryViewModel: DetailCategoryViewModel
    private var mLayoutManager: LinearLayoutManager? = null
    private val sharedVM by activityViewModels<SharedViewModel>()
    private val args: DetailCategoryFragmentArgs by navArgs()
    private lateinit var controlPanelListener: ControlPanelListener
    private lateinit var popUpWindow: PopupWindow
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private var currentMorePosition = 0
    var isUserControl = false
    private lateinit var paint: Paint

    override fun getLayout(): Int {
        return R.layout.fragment_detail_category
    }

    override fun initView() {
        setupForSeekbar()
        requireActivity().title = args.categoryTitle
        setUpProgressBar()
        detailCategoryAdapter.setListener(this)
        mLayoutManager = LinearLayoutManager(requireContext())
        setupPopUpWindow()
        binding.viewModel = detailCategoryViewModel

        binding.apply {
            rvSound.layoutManager = mLayoutManager
            rvSound.setHasFixedSize(true)
            rvSound.adapter = detailCategoryAdapter

            toolbar.apply {
                setNavigationOnClickListener {
                    requireActivity().onBackPressed()
                }
                title = args.categoryTitle
            }

            //load more data when scroll to the end of recyclerview
            rvSound.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!rvSound.canScrollVertically(1) && dy > 0) {
                        detailCategoryViewModel.apply {
                            if (currentPage!! < totalPageNumber!!) {
                                getSound(currentPage!! + 1, true)
                            }
                        }
                    }
                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                detailCategoryViewModel.getSound(1, false)
                controlPanel.visibility = View.GONE
                sharedVM.currentPosition.value = 0
                controlPanelListener.onReset()
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

            ibPlayPause.setOnClickListener {
                if (controlPanelListener.isPlaying()) {
                    ibPlayPause.setImageResource(R.drawable.ic_play_circle)
                    ivPlayPause.setImageResource(R.drawable.ic_play_arrow)
                } else {
                    ibPlayPause.setImageResource(R.drawable.ic_pause_circle)
                    ivPlayPause.setImageResource(R.drawable.ic_pause_arrow)
                }
                controlPanelListener.onPlayPauseClick()
            }

            ibNext.setOnClickListener {
                sharedVM.currentPosition.apply {
                    //if current position less than sound list size
                    if (value!! < sharedVM.soundList.value!!.size - 1) {
                        //if current item is banner
                        value = if (sharedVM.soundList.value!![value!! + 1].isBanner) {
                            value!! + 2
                        } else {
                            value!! + 1
                        }
                    }
                }
            }

            ibPrevious.setOnClickListener {
                sharedVM.currentPosition.apply {
                    //if current position larger than 1
                    if (value!! > 1) {
                        value = if (sharedVM.soundList.value!![value!! - 1].isBanner) {
                            value!! - 2
                        } else {
                            value!! - 1
                        }
                    }
                }
            }

            controlPanel.setOnClickListener {
                findNavController().navigate(
                    DetailCategoryFragmentDirections
                        .actionDetailCategoryFragmentToSoundDetailFragment()
                        .setPosition(sharedVM.currentPosition.value!!)
                )
            }

            noInternetLayout.btRetry.setOnClickListener { detailCategoryViewModel.retry() }
        }
    }

    private fun setupForSeekbar() {
        paint = Paint()
        paint.typeface = Typeface.SANS_SERIF
        paint.textSize = 20F
        paint.color = (-0x1)
    }

    override fun initViewModel() {
        val factory =
            DetailCategoryViewModel.Factory(requireActivity().application, args.categoryId!!)
        detailCategoryViewModel =
            ViewModelProvider(this, factory)[DetailCategoryViewModel::class.java]

        if(sharedVM == null){
            return
        }

        detailCategoryViewModel.soundLiveData.observe(this) {
            it?.let {
                when (it.loadingStatus) {
                    LoadingStatus.Success -> {
                        val data = (it as DataResponse.DataSuccess).body
                        if (!data.isNullOrEmpty()) {
                            val newPageItems = mutableListOf<Sound>()
                            val bannerItem = Sound()
                            bannerItem.isBanner = true
                            newPageItems.add(bannerItem)
                            newPageItems.addAll(data)
                            sharedVM.soundList.value!!.addAll(newPageItems)
                            detailCategoryAdapter.updateData(
                                detailCategoryViewModel.currentPage!! > 1,
                                newPageItems
                            )
                        }
                        binding.swipeRefreshLayout.isEnabled = true
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    LoadingStatus.Refresh -> {
                        sharedVM.soundList.value!!.clear()
                        sharedVM.currentPosition.value = 0
                        binding.swipeRefreshLayout.isEnabled = true
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    else -> {
                        binding.swipeRefreshLayout.isEnabled = false
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }

        detailCategoryViewModel.favoriteSoundLiveData.observe(this) {
            it?.let {
                detailCategoryAdapter.updateFavoriteData(it)
                sharedVM.favoriteList.value!!.addAll(it)
            }
        }

        //change information in panel map with sound
        sharedVM.currentPosition.observe(this) {
            it?.let {
                if (sharedVM.soundList.value!!.size > 0) {
                    val sound = sharedVM.soundList.value!![it]
                    binding.apply {
                        controlPanel.visibility = View.VISIBLE
                        tvTitle.text = sound.title
                        ivThumbnail.setImageResource(sound.thumbRes)
                        ivPlayPause.setImageResource(R.drawable.ic_pause_arrow)
                        ibPlayPause.setImageResource(R.drawable.ic_pause_circle)
                    }
                }
            }
        }

        sharedVM.soundDuration.observe(this) {
            it?.let {
                binding.sbDuration.max = it
            }
        }

        val resource = resources
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

        sharedVM.isPlaying.observe(this) {
            it?.let {
                detailCategoryAdapter.updatePlayingItem(sharedVM.currentPosition.value!!, it)
                if (!it) {
                    binding.apply {
                        ibPlayPause.setImageResource(R.drawable.ic_play_circle)
                        ivPlayPause.setImageResource(R.drawable.ic_play_arrow)
                    }
                } else {
                    binding.apply {
                        ibPlayPause.setImageResource(R.drawable.ic_pause_circle)
                        ivPlayPause.setImageResource(R.drawable.ic_pause_arrow)
                    }
                }
            }
        }
        detailCategoryViewModel.getSound(1, false)
    }

    private fun setUpProgressBar() {
        val circle1 = Circle()
        val circle2 = Circle()
        circle1.color = Color.parseColor("#F18924")
        circle2.color = Color.parseColor("#F18924")
        binding.loadMoreProgressBar.indeterminateDrawable = circle1
        binding.progressBar.indeterminateDrawable = circle2
    }

    private fun setupPopUpWindow() {
        val onClickListener = View.OnClickListener {
            val type = when ((it as TextView).text.toString()) {
                "Ringtone" -> RingtoneManager.TYPE_RINGTONE
                "Notification" -> RingtoneManager.TYPE_NOTIFICATION
                else -> RingtoneManager.TYPE_ALARM
            }
            if (Utils.storagePermissionGrant(requireContext())) {
                if (Utils.writeSettingPermissionGrant(requireContext())) {
                    setupWhenPermissionGranted(type, currentMorePosition)
                } else {
                    Utils.requestWriteSettingPermission(requireActivity(), requireContext())
                }
            } else {
                requestStoragePermission()
            }

            popUpWindow.dismiss()
        }

        popUpWindow = Utils.showPopUpSetAs(requireActivity(), onClickListener)
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
            sharedVM.soundList.value!![position].soundUrl!!,
            type,
            sharedVM.soundList.value!![position].title!!
        )
    }

    //handle when favorite button checked: add sound to favorite
    override fun onCheckedButton(sound: Sound) {
        detailCategoryViewModel.addFavoriteSound(sound)
    }

    //handle when favorite button unchecked: remove sound from favorite
    override fun onUncheckedButton(sound: Sound) {
        detailCategoryViewModel.removeFavoriteSound(sound)
    }

    //handle when click on sound item in recycler view
    override fun onItemClick(position: Int) {
        sharedVM.currentPosition.value = position
        binding.sbDuration.progress = 0
    }

    override fun onMoreIconClick(view: View, position: Int) {
        currentMorePosition = position
        val yPosition = if (view.y > screenHeight - 400) {
            ((-view.height) * 2.5).toInt()
        } else {
            ((-view.height) * 2)
        }
        popUpWindow.showAsDropDown(
            view, (screenWidth * 0.6).toInt(),
            yPosition
        )

    }

    //get controlPanelListener from activity
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

    //release media player when detach fragment
    override fun onDetach() {
        super.onDetach()
        controlPanelListener.onDetachFragment()
        sharedVM.soundList.value!!.clear()
        sharedVM.currentPosition.value = 0
    }
}