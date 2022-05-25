package com.hola360.pranksounds.utils

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
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
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.PopUpWindowLayoutBinding
import kotlinx.coroutines.launch


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

    fun isAndroidM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun isAndroidR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    fun getPendingIntentFlags(): Int {
        return if (isAndroidM()) {
            if (isAndroidQ()) {
                PendingIntent.FLAG_IMMUTABLE
            }
            else {
                PendingIntent.FLAG_IMMUTABLE
            }

        } else {
            PendingIntent.FLAG_ONE_SHOT
        }
    }

    fun getAlarmManagerFlags(): Int {
        return if (isAndroidM()) {
            if (isAndroidQ()) {
                AlarmManager.RTC_WAKEUP
            }
            else {
                AlarmManager.RTC_WAKEUP
            }

        } else {
            AlarmManager.RTC_WAKEUP
        }
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

    fun getBasePath(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
        } else {
            Environment.getExternalStorageDirectory().absolutePath
        }
    }

    fun requestWriteSettingPermission(activity: FragmentActivity, context: Context) {
        Toast.makeText(
            context,
            activity.resources?.getString(R.string.write_setting_permission),
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent()
        intent.action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.ACTION_MANAGE_WRITE_SETTINGS
        } else {
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        }
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
    }

    fun checkDisplayOverOtherAppPermission(context: Context) : Boolean {
        return if (isAndroidQ()) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun openAppInformation(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    fun setUpDialogGrantPermission(context: Context)  {
        val builder = AlertDialog.Builder(context)
        var res = false
        builder.setMessage(context.resources.getString(R.string.confirm_message))
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                openAppInformation(context)
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}