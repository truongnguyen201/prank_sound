package com.hola360.pranksounds.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.model.SubmittedSound
import com.hola360.pranksounds.data.room.dao.CallDao
import com.hola360.pranksounds.data.room.dao.SoundDAO
import com.hola360.pranksounds.data.room.dao.SubmittedSoundDAO

@Database(
    entities = [Sound::class, Call::class, SubmittedSound::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun soundDAO(): SoundDAO
    abstract fun callDao(): CallDao
    abstract fun submittedSoundDAO(): SubmittedSoundDAO

    companion object {
        private const val DATABASE_NAME = "APP_DB"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .build()
        }

    }

}