package com.hola360.pranksounds.ui.dialog.pickphoto

import android.app.Application
import androidx.lifecycle.*
import com.hola360.pranksounds.App
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.PhotoModel
import com.hola360.pranksounds.data.repository.MediaStoreRepository
import com.hola360.pranksounds.ui.dialog.pickphoto.data.PickModelDataType
import com.hola360.pranksounds.ui.dialog.pickphoto.data.PickPhotoModel
import kotlinx.coroutines.launch

class PickPhotoDialogViewModel(val app: App): ViewModel() {
    var repository = MediaStoreRepository(app)
    val allPhoto = mutableListOf<PhotoModel>()
    val photoLiveData = MutableLiveData<DataResponse<PickPhotoModel>>()
    val isExitDialog = MutableLiveData(false)

    init {
        photoLiveData.value = DataResponse.DataIdle()
    }

    val isEmpty: LiveData<Boolean> = Transformations.map(photoLiveData) {
        photoLiveData.value!!.loadingStatus == LoadingStatus.Error
    }

    val isLoading: LiveData<Boolean> = Transformations.map(photoLiveData) {
        photoLiveData.value!!.loadingStatus == LoadingStatus.Loading
    }

    val isDetailMode: LiveData<Boolean> = Transformations.map(photoLiveData) {
        if (photoLiveData.value!!.loadingStatus == LoadingStatus.Success) {
            val pickPhotoModel = (photoLiveData.value as DataResponse.DataSuccess).body
            pickPhotoModel.pickModelDataType == PickModelDataType.LoadDetailAlbum
        }
        else false
    }

    val title: LiveData<String> = Transformations.map(photoLiveData) {
        if (photoLiveData.value!!.loadingStatus == LoadingStatus.Success) {
            val pickPhotoModel = (photoLiveData.value as DataResponse.DataSuccess).body
            if (pickPhotoModel.pickModelDataType == PickModelDataType.LoadDetailAlbum) {
                "Detail"
            }
            else
                app.getString(R.string.photos)
        }
        else {
            app.getString(R.string.photos)
        }
    }

    fun onClose() {
        if (photoLiveData.value!!.loadingStatus == LoadingStatus.Success) {
            val pickPhotoModel = (photoLiveData.value as DataResponse.DataSuccess).body
            if (pickPhotoModel.pickModelDataType == PickModelDataType.LoadDetailAlbum) {
                loadData()
            }
            else isExitDialog.value = true
        }
        else isExitDialog.value = true
    }

    fun loadData() {
        if (photoLiveData.value!!.loadingStatus != LoadingStatus.Loading) {
            photoLiveData.value = DataResponse.DataLoading()
            viewModelScope.launch {
                if (allPhoto.isEmpty()) {
                    val photos = repository.getAllImages()
                    if (photos.isNotEmpty()) {
                        allPhoto.addAll(photos)
                    }
                }
                if (allPhoto.isNotEmpty()) {
                    val albums = repository.getAlbums(allPhoto)
                    if (albums.isNotEmpty()) {
                        photoLiveData.value = DataResponse.DataSuccess(
                            PickPhotoModel(
                                PickModelDataType.LoadAlbum,
                                albums
                            )
                        )
                    }else {
                        photoLiveData.value = DataResponse.DataError()
                    }
                }else {
                    photoLiveData.value = DataResponse.DataError()
                }
            }
        }
    }

    fun loadAlbumDetail(albumId: Long) {
        if (photoLiveData.value!!.loadingStatus != LoadingStatus.Loading) {
            photoLiveData.value = DataResponse.DataLoading()
            viewModelScope.launch {
                val albumDetail = repository.getAlbumDetail(allPhoto, albumId)
                if (albumDetail.isNotEmpty()) {
                    photoLiveData.value = DataResponse.DataSuccess(
                        PickPhotoModel(
                            PickModelDataType.LoadDetailAlbum,
                            albumDetail
                        )
                    )
                }
                else {
                    photoLiveData.value = DataResponse.DataError()
                }
            }
        }
    }

    class Factory(private val app: App) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PickPhotoDialogViewModel::class.java))
                return PickPhotoDialogViewModel(app) as T
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}