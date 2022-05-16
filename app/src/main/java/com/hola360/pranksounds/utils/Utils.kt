package com.hola360.pranksounds.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.hola360.pranksounds.R
import com.hola360.pranksounds.databinding.PopUpWindowLayoutBinding
import kotlinx.coroutines.*

object Utils {
    private var STORAGE_PERMISSION_UNDER_STORAGE_SCOPE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    private var WRITE_SETTING_PERMISSION = arrayOf(
        Manifest.permission.WRITE_SETTINGS
    )

    private var STORAGE_PERMISSION_STORAGE_SCOPE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    fun isAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    fun isAndroidP(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    fun isAndroidO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun isAndroidR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    fun writeSettingPermissionGrant(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            allPermissionGrant(context, getWritingPermission())
        }
    }

    fun storagePermissionGrant(context: Context): Boolean {
        return allPermissionGrant(context, getStoragePermissions())
    }

    //check all permission is granted
    private fun allPermissionGrant(context: Context, intArray: Array<String>): Boolean {
        var isGranted = true
        for (permission in intArray) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isGranted = false
                break
            }
        }
        return isGranted
    }

    //get permission mapping with API Level
    fun getStoragePermissions(): Array<String> {
        return if (isAndroidR()) {
            STORAGE_PERMISSION_STORAGE_SCOPE
        } else {
            STORAGE_PERMISSION_UNDER_STORAGE_SCOPE
        }
    }

    fun getWritingPermission(): Array<String> {
        return WRITE_SETTING_PERMISSION
    }

    //show request permission to user
    private fun hasShowRequestPermissionRationale(
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

    //handle when user does not grant permission
    fun showAlertPermissionNotGrant(view: View, activity: Activity) {
        //show seekbar: go to setting to grant permission
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
            //show toast
            Toast.makeText(
                activity,
                activity.resources.getString(R.string.grant_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    //show popup window set as
    fun showPopUpSetAs(activity: Activity, listener: View.OnClickListener): PopupWindow {
        val popUpInflater =
            activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpBinding = PopUpWindowLayoutBinding.inflate(popUpInflater)

        popUpBinding.apply {
            tvNotification.setOnClickListener(listener)
            tvRingtone.setOnClickListener(listener)
            tvAlarm.setOnClickListener(listener)
        }

        val popupWindow = PopupWindow(
            popUpBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 20F
            contentView.setOnClickListener { dismiss() }
        }

        return popupWindow
    }

    //draw thumb seekbar
    fun drawThumb(
        context: Context,
        seekBar: SeekBar,
        progress: Int,
        duration: Int,
        resources: Resources,
        paint: Paint
    ) {
        val bitmap =
            ContextCompat.getDrawable(context, R.drawable.seek_bar_thumb)?.toBitmap()!!
                .copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val text = String.format("00:%02d/00:%02d", progress / 1000, duration / 1000)

        canvas.drawText(
            text, (bitmap.width - paint.measureText(text)) / 2,
            (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2), paint
        )
        seekBar.thumb.clearColorFilter()
        seekBar.thumb = (BitmapDrawable(resources, bitmap))
    }

    fun View.delayOnLifeCycle(
        durationInMillis: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        block: () -> Unit
    ): Job? = findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
        lifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
            delay(durationInMillis)
            block()
        }
    }
}