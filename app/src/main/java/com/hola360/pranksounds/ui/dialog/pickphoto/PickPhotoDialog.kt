package com.hola360.pranksounds.ui.dialog.pickphoto

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ybq.android.spinkit.style.Circle
import com.hola360.pranksounds.App
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.PhotoModel
import com.hola360.pranksounds.databinding.DialogPickPhotoBinding
import com.hola360.pranksounds.generated.callback.OnClickListener
import com.hola360.pranksounds.ui.dialog.base.BaseDialog
import com.hola360.pranksounds.ui.dialog.pickphoto.adapter.PhotoAdapter
import com.hola360.pranksounds.ui.dialog.pickphoto.data.PickModelDataType
import com.hola360.pranksounds.utils.Utils

@Suppress("DEPRECATION")
class PickPhotoDialog : BaseDialog<DialogPickPhotoBinding>(), PhotoAdapter.ListenClickItem {

    lateinit var viewModel: PickPhotoDialogViewModel
    lateinit var adapter: PhotoAdapter
    private lateinit var layoutManager: GridLayoutManager
    private var albumState: Parcelable? = null
    private lateinit var onClickListener: OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        retainInstance = true
//        Log.e("Im Create", "HAHA")
        super.onCreate(savedInstanceState)
        adapter = PhotoAdapter(this)
    }


    override fun onResume() {
        super.onResume()
        Log.e("Im Resume", "HAHA")
    }

    override fun initViewModel() {
        val factory = PickPhotoDialogViewModel.Factory(requireActivity().application as App)
        viewModel = ViewModelProvider(this, factory)[PickPhotoDialogViewModel::class.java]

        viewModel.photoLiveData.observe(this) {
            it?.let {
                if (it.loadingStatus == LoadingStatus.Success) {
                    val body = (it as DataResponse.DataSuccess).body
                    layoutManager = GridLayoutManager(
                        requireContext(),
                        if (body.pickModelDataType == PickModelDataType.LoadAlbum) {
                            1
                        } else {
                            COLUMNS
                        }
                    )

                    binding.recycleView.layoutManager = layoutManager
                    if (body.pickModelDataType == PickModelDataType.LoadAlbum) {
                        binding.recycleView.layoutManager!!.onRestoreInstanceState(albumState)
                    }
                    adapter.update(body)
                }
            }
        }

        viewModel.isExitDialog.observe(this) {
            it?.let {
                if (it) {
                    dismiss()
//                    onClickListener?.onClickDismiss()
                }
            }
        }
    }

    override fun initView() {
        setUpProgressBar()
        binding.apply {
            recycleView.layoutManager = LinearLayoutManager(requireContext())
            recycleView.setHasFixedSize(true)
            recycleView.adapter = adapter
        }
        binding.viewModel = viewModel

        if (Utils.storagePermissionGrant(requireContext())) {
            viewModel.loadData()
        } else {
            requestStoragePermission()
        }
//        viewModel.loadData()
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
                viewModel.loadData()
            } else {
                Utils.showAlertPermissionNotGrant(binding.root, requireActivity())
                dismiss()
            }
        }


    override fun getLayout(): Int {
        return R.layout.dialog_pick_photo
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onClick(
        position: Int,
        pickModelDataType: PickModelDataType,
        photoModel: PhotoModel
    ) {
        if (pickModelDataType == PickModelDataType.LoadAlbum) {
            albumState = binding.recycleView.layoutManager!!.onSaveInstanceState()
            viewModel.loadAlbumDetail(photoModel.albumId)
            viewModel.albumName = photoModel.albumName
        } else {
            dismiss()
            onClickListener.onPickPhoto(photoModel)
        }
    }

    private fun setUpProgressBar() {
        val circle = Circle()
        circle.color = ContextCompat.getColor(requireActivity(), R.color.design_color)
        binding.progressBarLoadImg.indeterminateDrawable = circle
    }

    companion object {
        const val COLUMNS = 3

        @JvmStatic
        fun create(): PickPhotoDialog {
            return PickPhotoDialog()
        }
    }

    fun setOnClickListener(listener: PickPhotoDialog.OnClickListener) {
        this.onClickListener = listener
    }

    interface OnClickListener {
        fun onPickPhoto(photoModel: PhotoModel)
    }
}