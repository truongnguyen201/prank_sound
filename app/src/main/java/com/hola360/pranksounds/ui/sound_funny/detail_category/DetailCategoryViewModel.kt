package com.hola360.pranksounds.ui.sound_funny.detail_category

import android.app.Application
import androidx.lifecycle.*
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.repository.DetailCategoryRepository
import com.hola360.pranksounds.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class DetailCategoryViewModel(
    app: Application,
    private val catId: String
) : ViewModel() {

    private val repository = DetailCategoryRepository(app)

    val soundLiveData = MutableLiveData<DataResponse<MutableList<Sound>>>()
    val favoriteSoundLiveData = MutableLiveData<MutableList<String>>()
    var totalPage: Int? = null
    var currentPage: Int? = null

    init {
        soundLiveData.value = DataResponse.DataIdle()
        fetchFavoriteData()
    }

    val isEmpty: LiveData<Boolean> = Transformations.map(soundLiveData) {
        soundLiveData.value!!.loadingStatus == LoadingStatus.Error
    }

    val isLoading: LiveData<Boolean> = Transformations.map(soundLiveData) {
        soundLiveData.value!!.loadingStatus == LoadingStatus.Loading
    }

    val isLoadingMore: LiveData<Boolean> = Transformations.map(soundLiveData) {
        soundLiveData.value!!.loadingStatus == LoadingStatus.LoadingMore
    }

    fun fetchData(isLoadMore: Boolean, catId: String, pageNumber: Int) {
        if (soundLiveData.value!!.loadingStatus != LoadingStatus.Loading
            && soundLiveData.value!!.loadingStatus != LoadingStatus.LoadingMore
            && soundLiveData.value!!.loadingStatus != LoadingStatus.Refresh
        ) {
            if (!isLoadMore) {
                if (totalPage == null) {
                    soundLiveData.value = DataResponse.DataLoading(LoadingStatus.Loading)
                } else {
                    soundLiveData.value = DataResponse.DataLoading(LoadingStatus.Refresh)
                }
            } else {
                if (pageNumber > totalPage!!) {
                    return
                } else {
                    soundLiveData.value = DataResponse.DataLoading(LoadingStatus.LoadingMore)
                }
            }

            if (catId == Constants.FAVORITE_ID) {
                fetchDB(pageNumber)
            } else {
                fetchAPI(pageNumber)
            }
        }
    }

    private fun fetchFavoriteData() {
        viewModelScope.launch {
            val favoriteSoundDeferred = viewModelScope.async {
                repository.getFavoriteSoundID()
            }
            try {
                val favoriteList = favoriteSoundDeferred.await()
                favoriteSoundLiveData.value = favoriteList!!
            } catch (ex: Exception) {
                soundLiveData.value = DataResponse.DataError()
            }
        }

    }

    private fun fetchAPI(pageNumber: Int) {
        viewModelScope.launch {
            val requestPage = getRequestPage(pageNumber)

            val soundDeferred = viewModelScope.async {
                repository.getDetailCategory(
                    Constants.CAT_DETAIL_PARAM,
                    catId,
                    requestPage,
                    Constants.SOUND_ITEM_PER_PAGE
                )
            }

            try {
                val response = soundDeferred.await()
                val soundList = response?.data?.dataApps?.listSound
                totalPage = response!!.data?.dataApps?.totalPage
                currentPage =
                    Integer.parseInt(response.data?.dataApps?.currentPage.toString())

                soundLiveData.value = DataResponse.DataSuccess(soundList!!)
            } catch (ex: Exception) {
                soundLiveData.value = DataResponse.DataError()
            }
        }
    }

    private fun fetchDB(pageNumber: Int) {
        viewModelScope.launch {
            val requestPage = getRequestPage(pageNumber)

            try {
                val q = repository.getQuantityOfFavoriteSound()
                if (q != null) {
                    totalPage = q / 10 + 1
                }

                val soundList = repository.getPageDB(requestPage)
                currentPage = requestPage
                soundLiveData.value = DataResponse.DataSuccess(soundList!!)
            } catch (ex: Exception) {
                ex.printStackTrace()
                soundLiveData.value = DataResponse.DataError()
            }
        }
    }

    private fun getRequestPage(pageNumber: Int): Int {
        return if (totalPage != null) {
            if (soundLiveData.value!!.loadingStatus == LoadingStatus.Refresh) {
                1
            } else {
                pageNumber
            }
        } else {
            1
        }
    }

    fun retry() {
        fetchData(false, catId, 1)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application, private val catId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailCategoryViewModel::class.java)) {
                return DetailCategoryViewModel(app, catId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}