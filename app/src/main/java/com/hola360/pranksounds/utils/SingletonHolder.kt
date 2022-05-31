package com.hola360.pranksounds.utils

import android.util.Log

open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null) {
            Log.e("Singleton", "Instance is not null, return current instance")
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                Log.e("Singleton", "Instance is null, create new instance")
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}
