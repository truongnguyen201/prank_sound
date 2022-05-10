package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.style.Circle
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.databinding.FragmentDetailCategoryBinding
import com.hola360.pranksounds.databinding.PopUpWindowLayoutBinding
import com.hola360.pranksounds.ui.base.BaseFragment
import com.hola360.pranksounds.ui.sound_funny.detail_category.adapter.DetailCategoryAdapter
import com.hola360.pranksounds.utils.Utils
import com.hola360.pranksounds.utils.listener.ControlPanelListener
import com.hola360.pranksounds.utils.listener.SoundListener

class DetailCategoryFragment : BaseFragment<FragmentDetailCategoryBinding>(), SoundListener {
    private lateinit var detailCategoryAdapter: DetailCategoryAdapter
    private lateinit var detailCategoryViewModel: DetailCategoryViewModel
    private var mLayoutManager: LinearLayoutManager? = null
    private val sharedVM by activityViewModels<SharedViewModel>()
    private val args: DetailCategoryFragmentArgs by navArgs()
    private lateinit var controlPanelListener: ControlPanelListener
    private lateinit var popUpWindow: PopupWindow
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels - 100
    var isUserControl = false

    override fun getLayout(): Int {
        return R.layout.fragment_detail_category
    }

    override fun initView() {
        requireActivity().title = args.categoryTitle
        setUpProgressBar()
        detailCategoryAdapter = DetailCategoryAdapter(requireContext())
        detailCategoryAdapter.setListener(this)
        mLayoutManager = LinearLayoutManager(requireContext())
        setupPopUpWindow()

        binding.apply {
            rvSound.layoutManager = mLayoutManager
            rvSound.setHasFixedSize(true)
            rvSound.adapter = detailCategoryAdapter

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
                drawThumb(this, 0, 0)
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
        }
    }

    private fun drawThumb(seekBar: SeekBar, progress: Int, duration: Int) {
        val bitmap =
            ContextCompat.getDrawable(requireContext(), R.drawable.seek_bar_thumb)?.toBitmap()
        val bmp = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        val c = Canvas(bmp)
        val text = String.format("00:%02d/00:%02d", progress / 1000, duration / 1000)
        val p = Paint()
        p.typeface = Typeface.DEFAULT
        p.textSize = 20F
        p.color = (-0x1)
        c.drawText(
            text, (bmp.width - p.measureText(text)) / 2,
            (c.height / 2 - (p.descent() + p.ascent()) / 2), p
        )
        seekBar.thumb = (BitmapDrawable(resources, bmp))
    }

    @SuppressLint("NotifyDataSetChanged", "Recycle")
    override fun initViewModel() {
        val factory =
            DetailCategoryViewModel.Factory(requireActivity().application, args.categoryId!!)
        detailCategoryViewModel =
            ViewModelProvider(this, factory)[DetailCategoryViewModel::class.java]

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

        detailCategoryViewModel.isLoading.observe(this) {
            it?.let {
                binding.progressBar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }

        detailCategoryViewModel.isLoadingMore.observe(this) {
            it?.let {
                binding.loadMoreProgressBar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }

        detailCategoryViewModel.favoriteSoundLiveData.observe(this) {
            it?.let {
                detailCategoryAdapter.updateFavoriteData(it)
            }
        }

        //when the media player played complete, set icon in the panel to pause icon
        sharedVM.isComplete.observe(this) {
            it?.let {
                if (it) {
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

        sharedVM.seekBarProgress.observe(this) {
            val animator = ObjectAnimator.ofInt(binding.sbDuration, "progress", it!! - 10, it)
            it.let {
                if (!isUserControl) {
                    binding.sbDuration.apply {
                        animator.duration = 60
                        animator.interpolator = LinearInterpolator()
                        animator.start()
                        drawThumb(this, it, sharedVM.soundDuration.value!!)
                    }
                }
            }
        }

        sharedVM.isPlaying.observe(this) {
            it?.let {
                detailCategoryAdapter.updatePlayingItem(sharedVM.currentPosition.value!!, it)
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
            if (Utils.storagePermissionGrant(requireContext())) {
                setupWhenStoragePermissionGranted()
            } else {
                requestStoragePermission()
            }
            val type = (it as TextView).text
            Toast.makeText(context, type, LENGTH_LONG).show()
            popUpWindow.dismiss()
        }

        val popUpInflater =
            requireActivity().applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpBinding = PopUpWindowLayoutBinding.inflate(popUpInflater)

        popUpBinding.apply {
            tvNotification.setOnClickListener(onClickListener)
            tvRingtone.setOnClickListener(onClickListener)
            tvAlarm.setOnClickListener(onClickListener)
        }

        popUpWindow = PopupWindow(
            popUpBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 20F
            contentView.setOnClickListener { dismiss() }
        }
    }

    private fun requestStoragePermission() {
        resultLauncher.launch(
            Utils.getStoragePermissions()
        )
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (Utils.storagePermissionGrant(requireContext())
            ) {
                setupWhenStoragePermissionGranted()
            } else {
                Utils.showAlertPermissionNotGrant(binding.root, requireActivity())
            }
        }

    private fun setupWhenStoragePermissionGranted() {

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
    }

    override fun onMoreIconClick(view: View, position: Int) {
        popUpWindow.showAsDropDown(
            view, (screenWidth * 0.68).toInt(),
            ((-view.height) * 0.7).toInt()
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