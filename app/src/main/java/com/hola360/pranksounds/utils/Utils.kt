package com.hola360.pranksounds.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hola360.pranksounds.BuildConfig.VERSION_CODE
import com.hola360.pranksounds.R

object Utils {
    var STORAGE_PERMISSION_UNDER_STORAGE_SCOPE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    var STORAGE_PERMISSION_STORAGE_SCOPE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    fun isAndroidR() : Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    fun storagePermissionGrant(context: Context) : Boolean {
        return allStoragePermissionGrant(context, getStoragePermissions())
    }

    fun allStoragePermissionGrant(context: Context, array: Array<String>) : Boolean {
        var res = true
        for (permission in array) {
            if (ContextCompat.checkSelfPermission(context, permission)
            != PackageManager.PERMISSION_GRANTED) {
                res = false
                break
            }
        }
        return res
    }

    fun getStoragePermissions() : Array<String> {
        return if (isAndroidR()) {
            STORAGE_PERMISSION_STORAGE_SCOPE
        }
        else {
            STORAGE_PERMISSION_UNDER_STORAGE_SCOPE
        }
    }

    fun hasShowRequestPermissionRationale(
        context: Context?,
        vararg permissions: String?
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as Activity?)!!,
                        permission!!
                    )
                ) {
                    return true
                }
            }
        }
        return false
    }

    fun showAlertPermissionNotGrant(view: View, activity: Activity) {
        if (!hasShowRequestPermissionRationale(activity, *getStoragePermissions())) {
            val snackBar = Snackbar.make(
                view,
                activity.resources.getString(R.string.goto_settings),
                Snackbar.LENGTH_LONG
            )
            snackBar.setAction(
                activity.resources.getString(R.string.settings)
            ) { view: View? ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }
            snackBar.show()
        } else {
            Toast.makeText(
                activity,
                activity.resources.getString(R.string.grant_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}