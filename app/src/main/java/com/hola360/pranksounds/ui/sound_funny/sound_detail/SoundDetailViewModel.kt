package com.hola360.pranksounds.ui.sound_funny.sound_detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.data.repository.DetailCategoryRepository
import com.hola360.pranksounds.data.repository.FileDownloadRepository

class SoundDetailViewModel(private val app: Application) : ViewModel() {

    val fileDownloadRepository = FileDownloadRepository()
    val detailCategoryRepository = DetailCategoryRepository(app)

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SoundDetailViewModel::class.java)) {
                return SoundDetailViewModel(app) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}