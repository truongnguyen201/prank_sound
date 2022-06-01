package com.hola360.pranksounds.ui.sound_funny

import android.app.Application
import androidx.lifecycle.*
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.SoundCategory
import com.hola360.pranksounds.data.repository.SoundCategoryRepository
import com.hola360.pranksounds.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SoundFunnyViewModel(app: Application) : ViewModel() {

    private val repository = SoundCategoryRepository(app)
    val soundCategoryLiveData = MutableLiveData<DataResponse<List<SoundCategory>>>()

    init {
        soundCategoryLiveData.value = DataResponse.DataIdle()
        getCategory()
    }

    val isEmpty: LiveData<Boolean> = Transformations.map(soundCategoryLiveData) {
        soundCategoryLiveData.value!!.loadingStatus == LoadingStatus.Error
    }

    val isLoading: LiveData<Boolean> = Transformations.map(soundCategoryLiveData) {
        soundCategoryLiveData.value!!.loadingStatus == LoadingStatus.Loading
    }

    private fun getCategory() {
        if (soundCategoryLiveData.value!!.loadingStatus != LoadingStatus.Loading) {
            soundCategoryLiveData.value = DataResponse.DataLoading(LoadingStatus.Loading)
            viewModelScope.launch {
                //get list category from api
                val soundCategoryResponseDeferred = viewModelScope.async {
                    repository.getSoundCategory(Constants.SOUND_CAT_PARAM)
                }

                //get number of favorite sound in room
                val favoriteQuantityDeferred = viewModelScope.async {
                    repository.getQuantityOfFavoriteSound()
                }

                try {
                    val quantity = favoriteQuantityDeferred.await()!!
                    val categoryList =
                        soundCategoryResponseDeferred.await()!!.data.dataApps.listSoundCategory

                    //if has favorite sound, add favorite category to the category list
                    if (quantity > 0) {
                        categoryList.add(0, Constants.FAVORITE_CATEGORY)
                    }

                    soundCategoryLiveData.value =
                        DataResponse.DataSuccess(categoryList)
                } catch (ex: Exception) {
                    soundCategoryLiveData.value = DataResponse.DataError()
                }
            }
        }
    }

    fun retry() {
        getCategory()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SoundFunnyViewModel::class.java)) {
                return SoundFunnyViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}