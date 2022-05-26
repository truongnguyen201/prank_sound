package com.hola360.pranksounds.ui.sound_funny.sound_detail

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.repository.DetailCategoryRepository
import kotlinx.coroutines.launch

class SoundDetailViewModel(private val app: Application) : ViewModel() {

    private val detailCategoryRepository = DetailCategoryRepository(app)
    private val repository = DetailCategoryRepository(app)

    fun addFavoriteSound(sound: Sound) {
        viewModelScope.launch {
            detailCategoryRepository.addFavoriteSound(sound)
            Toast.makeText(
                app.applicationContext, "Added ${sound.title} to favorite list",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun removeFavoriteSound(sound: Sound) {
        viewModelScope.launch {
            detailCategoryRepository.removeFavoriteSound(sound)
            Toast.makeText(
                app.applicationContext, "Removed ${sound.title} from favorite list",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getDataWhenChangeTheme() {

    }

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