package com.hola360.pranksounds.data.repository

import com.hola360.pranksounds.App
import com.hola360.pranksounds.data.model.PhotoModel
import com.hola360.pranksounds.utils.MediaLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreRepository(val app: App) {

    suspend fun getAllImages(): List<PhotoModel> = withContext(Dispatchers.Default) {
        MediaLoader.loadAllImages(app)
    }

    suspend fun getAlbums(photoModels: List<PhotoModel>): MutableList<PhotoModel> =
        withContext(Dispatchers.Default) {
            val albums = mutableListOf<PhotoModel>()
            if (photoModels.isNotEmpty()) {
                val maps = photoModels.groupBy { it.albumId }
                if (maps.isNotEmpty()) {
                    maps.values.forEach {
                        it[0].numberImage = it.size
                        albums.add(it[0])
                    }
                }
            }
            albums
        }

    suspend fun getAlbumDetail(
        photoModels: List<PhotoModel>,
        albumId: Long
    ): MutableList<PhotoModel> =
        withContext(Dispatchers.Default) {
            val albums = mutableListOf<PhotoModel>()
            if (photoModels.isNotEmpty()) {
                val maps = photoModels.filter { it.albumId == albumId }
                if (maps.isNotEmpty()) {
                    albums.addAll(maps)
                }
            }
            albums
        }
}