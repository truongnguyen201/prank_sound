package com.hola360.pranksounds.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.hola360.pranksounds.data.model.PhotoModel
import java.io.File

object MediaLoader {
    fun loadAllImages(
        context: Context
    ): MutableList<PhotoModel> {
        val uri = getUri()
        val selectionArgs: Array<String?> = arrayOf("image/png", "image/jpg", "image/jpeg")
        val selection =
            MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?"
        return getListFromUri(context, uri, selection, selectionArgs, false)
    }

    private fun getUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Files.getContentUri("external")
        }
    }


    private fun getListFromUri(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArg: Array<String?>?, orderAscending: Boolean
    ): MutableList<PhotoModel> {
        val list = mutableListOf<PhotoModel>()
        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.RELATIVE_PATH,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID
            )
        } else {
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID
            )
        }
        createCursor(
            context.contentResolver,
            uri,
            projection,
            selection,
            selectionArg,
            orderAscending
        ).use { cursor ->
            cursor?.let {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val albumNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val pathColumn =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
                    } else {
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    }

                while (cursor.moveToNext()) {
                    val path =
                        getPathFromCursor(context, cursor, uri, idColumn, pathColumn) ?: continue
                    val file = File(path)
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val albumId =
                        cursor.getLong(albumIdColumn)
                    val albumName =
                        cursor.getString(albumNameColumn)
                    if (!file.exists() or file.isHidden or file.isDirectory) continue
                    val photoModel = PhotoModel(
                        ContentUris.withAppendedId(
                            uri,
                            id
                        ), file, name, albumId, albumName ?: ""
                    )
                    list.add(photoModel)
                }
                cursor.close()
            }

        }
        return list
    }

    fun getPathFromCursor(
        context: Context,
        cursor: Cursor,
        uri: Uri,
        idColumn: Int,
        pathColumn: Int
    ): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(
                uri,
                id
            )
            PathUtils.getPath(context, contentUri)
        } else {
            cursor.getString(pathColumn)
        }
    }

    fun createCursor(
        contentResolver: ContentResolver,
        collection: Uri,
        projection: Array<String>,
        whereCondition: String?,
        selectionArgs: Array<String?>?, orderAscending: Boolean
    ): Cursor? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            val selection =
                createSelectionBundle(whereCondition, selectionArgs, orderAscending)
            contentResolver.query(collection, projection, selection, null)
        }
        else -> {
            contentResolver.query(collection, projection, whereCondition, selectionArgs, null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createSelectionBundle(
        whereCondition: String?,
        selectionArgs: Array<String?>?,
        orderAscending: Boolean
    ): Bundle = Bundle().apply {
        putStringArray(
            ContentResolver.QUERY_ARG_SORT_COLUMNS,
            arrayOf(MediaStore.Files.FileColumns.DATE_ADDED)
        )
        val orderDirection =
            if (orderAscending) ContentResolver.QUERY_SORT_DIRECTION_ASCENDING else ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
        putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, orderDirection)
        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, whereCondition)
        putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
    }


}