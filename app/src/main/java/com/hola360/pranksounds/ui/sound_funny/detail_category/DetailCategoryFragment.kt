package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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
import com.hola360.pranksounds.databinding.LayoutSeekbarThumbBinding
import com.hola360.pranksounds.ui.base.BaseScreenWithViewModelFragment
import com.hola360.pranksounds.ui.sound_funny.detail_category.adapter.DetailCategoryAdapter
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import com.hola360.pranksounds.utils.clickWithDebounce
import com.hola360.pranksounds.utils.listener.ControlPanelListener
import com.hola360.pranksounds.utils.listener.SoundListener

class DetailCategoryFragment : BaseScreenWithViewModelFragment<FragmentDetailCategoryBinding>(),
    SoundListener {
    private lateinit var detailCategoryViewModel: DetailCategoryViewModel
    private var mLayoutManager: LinearLayoutManager? = null
    private lateinit var sharedVM: SharedViewModel
    private val args: DetailCategoryFragmentArgs by navArgs()
    private lateinit var detailCategoryAdapter: DetailCategoryAdapter
    private lateinit var controlPanelListener: ControlPanelListener
    private lateinit var popUpWindow: PopupWindow
    private val isPopupWindowInit get() = this::popUpWindow.isInitialized
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private var currentMorePosition = 0
    private var mLastClickTime: Long = 0
    var isUserControl = false
    private lateinit var seekbarBinding: LayoutSeekbarThumbBinding
    private var isDestroyView = false

    override fun getLayout(): Int {
        return R.layout.fragment_detail_category
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setUpProgressBar()
        detailCategoryAdapter.setListener(this)
        mLayoutManager = LinearLayoutManager(requireContext())
        setupPopUpWindow()
        binding.viewModel = detailCategoryViewModel

        seekbarBinding =
            LayoutSeekbarThumbBinding.inflate(requireActivity().layoutInflater, null, false)

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
                            if (currentPage!! < totalPage!!) {
                                fetchData(true, args.categoryId!!, currentPage!! + 1)
                            }
                        }
                    }
                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                detailCategoryViewModel.fetchData(false, args.categoryId!!, 1)
                controlPanel.visibility = View.GONE
                sharedVM.currentPosition.value = 0
                controlPanelListener.onReset()
            }

            sbDuration.apply {
                thumb = Utils.createThumb(0, 0, seekbarBinding, resources)
                setPadding(Constants.SEEKBAR_PADDING, 0, Constants.SEEKBAR_PADDING, 0)
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
                    changeImageResourceOnPause()
                } else {
                    changeImageResourceOnPlay()
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
                        .setList(sharedVM.soundList.value!!.toTypedArray())
                        .setCategoryId(args.categoryId)
                        .setCategoryTitle(args.categoryTitle)
                )
            }

            noInternetLayout.btRetry.setOnClickListener { detailCategoryViewModel.retry() }
        }
    }

    override fun initViewModel() {
        val factory =
            DetailCategoryViewModel.Factory(requireActivity().application, args.categoryId!!)
        detailCategoryViewModel =
            ViewModelProvider(this, factory)[DetailCategoryViewModel::class.java]
        sharedVM = SharedViewModel.getInstance(requireActivity().application)
        detailCategoryAdapter = DetailCategoryAdapter(args.categoryId!!)

        detailCategoryViewModel.soundLiveData.observe(this) {
            it?.let {
                when (it.loadingStatus) {
                    LoadingStatus.Success -> {
                        val data = (it as DataResponse.DataSuccess).body
                        if (data.isNotEmpty()) {
                            val newPageItems = mutableListOf<Sound>()
                            val bannerItem = Sound()
                            bannerItem.isBanner = true
                            newPageItems.add(bannerItem)
                            newPageItems.addAll(data)
                            sharedVM.soundList.value!!.addAll(newPageItems)
                            detailCategoryAdapter.updateData(
                                detailCategoryViewModel.currentPage!! > 1, newPageItems
                            )
                            binding.swipeRefreshLayout.isEnabled = true
                            binding.swipeRefreshLayout.isRefreshing = false
                        } else {
                            if (args.categoryId == Constants.FAVORITE_ID &&
                                sharedVM.favoriteList.value!!.size == 0
                            ) {
                                binding.apply {
                                    llEmptyFavorite.visibility = View.VISIBLE
                                    rvSound.visibility = View.GONE
                                    swipeRefreshLayout.isEnabled = false
                                    swipeRefreshLayout.isRefreshing = false
                                    btPlayMore.setOnClickListener {
                                        findNavController().navigate(
                                            DetailCategoryFragmentDirections.actionDetailCategoryFragmentToSoundFunnyFragment()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    LoadingStatus.Refresh -> {
                        sharedVM.soundList.value!!.clear()
                        sharedVM.currentPosition.value = 0
                        binding.swipeRefreshLayout.isEnabled = true
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    else -> {
                        binding.apply {
                            swipeRefreshLayout.apply {
                                isRefreshing = false
                                isEnabled = false
                            }
                            controlPanel.visibility = View.GONE
                        }
                    }
                }
            }
        }

        detailCategoryViewModel.favoriteSoundLiveData.observe(this) {
            it?.let {
                sharedVM.favoriteList.value!!.addAll(it)
                detailCategoryAdapter.updateFavoriteData(it)
            }
        }

        sharedVM.favoriteList.observe(this) {
            it?.let {
                detailCategoryAdapter.updateFavoriteData(it)
            }
        }

        //change information in panel map with sound
        sharedVM.currentPosition.observe(this) {
            it?.let {
                if (sharedVM.soundList.value!!.size > 0) {
                    val sound = sharedVM.soundList.value!![it]
                    binding.apply {
                        tvTitle.text = sound.title
                        ivThumbnail.setImageResource(sound.thumbRes)
                        ivPlayPause.setImageResource(R.drawable.ic_pause_arrow)
                        ibPlayPause.setImageResource(R.drawable.ic_pause_circle)
                        rvSound.scrollToPosition(it)
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
            it.let {
                if (!isUserControl) {
                    binding.sbDuration.apply {
                        progress = it!!
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
                detailCategoryAdapter.updatePlayingItem(sharedVM.currentPosition.value!!, it)
                if (it) {
                    changeImageResourceOnPlay()
                } else {
                    changeImageResourceOnPause()
                }
            }
        }

        sharedVM.isComplete.observe(this) {
            it?.let {
                if (it) {
                    val duration = sharedVM.soundDuration.value
                    if (duration != null) {
                        if (duration < Constants.MIN_SOUND_DURATION
                            || binding.sbDuration.progress < duration
                        ) {
                            binding.sbDuration.progress = binding.sbDuration.max
                        }
                    }
                }
            }
        }

//        detailCategoryViewModel.fetchData(false, args.categoryId!!, 1)
    }

    private fun changeImageResourceOnPlay() {
        binding.apply {
            ibPlayPause.setImageResource(R.drawable.ic_pause_circle)
            ivPlayPause.setImageResource(R.drawable.ic_pause_arrow)
        }
    }

    private fun changeImageResourceOnPause() {
        binding.apply {
            ibPlayPause.setImageResource(R.drawable.ic_play_circle)
            ivPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    private fun setUpProgressBar() {
        val circle1 = Circle()
        val circle2 = Circle()
        val color = resources.getString(R.string.design_color)
        circle1.color = Color.parseColor(color)
        circle2.color = Color.parseColor(color)
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
            sharedVM.soundList.value!![position].title!!,
            sharedVM.soundList.value!![position].soundId!!
        )
    }

    //handle when favorite button checked: add sound to favorite
    override fun onCheckedButton(sound: Sound) {
        sharedVM.addFavoriteSound(sound)
    }

    //handle when favorite button unchecked: remove sound from favorite
    override fun onUncheckedButton(sound: Sound) {
        sharedVM.removeFavoriteSound(sound)
    }

    //handle when click on sound item in recycler view
    override fun onItemClick(position: Int) {
        sharedVM.currentPosition.value = position
        binding.apply {
            sbDuration.progress = 0
            controlPanel.visibility = View.VISIBLE
        }
    }

    override fun onMoreIconClick(view: View, position: Int) {
        currentMorePosition = position
        val yPosition = if (view.y > screenHeight - 600) {
            ((-view.height) * 2.5).toInt()
        } else {
            ((-view.height) * 1)
        }
        popUpWindow.showAsDropDown(
            view, (screenWidth * 0.6).toInt(),
            yPosition
        )
    }

    override fun onFavoriteEmpty() {
        binding.llEmptyFavorite.visibility = View.VISIBLE
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
        if (isPopupWindowInit) {
            popUpWindow.apply {
                if (isShowing) {
                    dismiss()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isDestroyView) {
            binding.controlPanel.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isDestroyView = true
    }
}