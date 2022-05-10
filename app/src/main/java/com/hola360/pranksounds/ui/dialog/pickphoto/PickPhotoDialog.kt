package com.hola360.pranksounds.ui.dialog.pickphoto

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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

class PickPhotoDialog(
    private val onClickListener: OnClickListener
    ) : BaseDialog<DialogPickPhotoBinding>(), PhotoAdapter.ListenClickItem{
    lateinit var viewModel: PickPhotoDialogViewModel
    lateinit var adapter: PhotoAdapter
//    var
    lateinit var layoutManager: GridLayoutManager
    var albumState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = PhotoAdapter(this)
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
        binding.apply {
            recycleView.layoutManager = LinearLayoutManager(requireContext())
            recycleView.setHasFixedSize(true)
            recycleView.adapter = adapter
        }
        binding.viewModel = viewModel
        viewModel.loadData()
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
        } else {
            dismiss()
            onClickListener.onPickPhoto(photoModel)
        }
    }
    companion object {
        const val COLUMNS = 3
        fun create(listener: PickPhotoDialog.OnClickListener): PickPhotoDialog {
            return PickPhotoDialog(listener)
        }
    }

    interface OnClickListener {
        fun onPickPhoto(photoModel: PhotoModel)
    }
}