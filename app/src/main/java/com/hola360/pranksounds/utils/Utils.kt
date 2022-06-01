package com.hola360.pranksounds.utils

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.databinding.LayoutSeekbarThumbBinding
import com.hola360.pranksounds.databinding.PopUpWindowLayoutBinding
import com.hola360.pranksounds.ui.callscreen.popup.ActionModel
import kotlinx.coroutines.launch

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*


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
    fun isAndroidO_MR1(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
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

    fun createThumb(
        progress: Int,
        maxProgress: Int,
        binding: LayoutSeekbarThumbBinding,
        resources: Resources
    ): Drawable? {
        binding.tvProgress.text =
            String.format("00:%02d/00:%02d", progress / 1000, maxProgress / 1000)
        binding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            binding.root.measuredWidth,
            binding.root.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        binding.root.layout(
            0,
            0,
            binding.root.measuredWidth,
            binding.root.measuredHeight
        )
        binding.root.draw(canvas)
        return BitmapDrawable(resources, bitmap)
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

    fun setRingtone(
        context: Context?,
        duration: Long,
        file: File,
        uri: Uri,
        type: Int,
        fileName: String
    ): Boolean {
        return if (isAndroidQ()) {
            setAsRingtone(context!!, duration, file, type, fileName)
        } else {
            RingtoneManager.setActualDefaultRingtoneUri(context, type, uri)
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setAsRingtone(
        context: Context,
        duration: Long,
        file: File,
        type: Int,
        fileName: String
    ): Boolean {
        val newUri = createFile(
            context,
            fileName,
            duration,
            Environment.DIRECTORY_RINGTONES,
            null,
            type
        )
        try {
            context.contentResolver.openOutputStream(newUri!!).use { os ->
                val size = file.length().toInt()
                val bytes = ByteArray(size)
                try {
                    val buf =
                        BufferedInputStream(FileInputStream(file))
                    buf.read(bytes, 0, bytes.size)
                    buf.close()
                    os!!.write(bytes)
                    os.close()
                    os.flush()
                } catch (e: IOException) {
                    return false
                }
            }
        } catch (ignored: Exception) {
            return false
        }
        return try {
            RingtoneManager.setActualDefaultRingtoneUri(
                context, type, newUri
            )
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    private fun createFile(
        context: Context,
        fileName: String,
        duration: Long,
        publicFolder: String,
        subFolder: String?,
        type: Int,
    ): Uri? {
        val now = Date()
        val mimeType =
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(Constants.AUDIO_EXTENSION)
        val fileCollection: Uri = if (isAndroidQ()) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val fullName = fileName + "." + Constants.AUDIO_EXTENSION
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fullName)
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.Files.FileColumns.DATE_ADDED, now.time / 1000)
        contentValues.put(MediaStore.Files.FileColumns.DATE_MODIFIED, now.time / 1000)
        if (!isAndroidQ()) {
            contentValues.put(MediaStore.Audio.Media.ARTIST, context.getString(R.string.app_name))
        }
        contentValues.put(MediaStore.Audio.Media.DURATION, duration)
        contentValues.put(MediaStore.Audio.Media.IS_MUSIC, true)
        when (type) {
            RingtoneManager.TYPE_NOTIFICATION -> {
                contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
            }
            RingtoneManager.TYPE_RINGTONE -> {
                contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true)
            }
            RingtoneManager.TYPE_ALARM -> {
                contentValues.put(MediaStore.Audio.Media.IS_ALARM, true)
            }
        }

        if (isAndroidQ()) {
            val parentFolder: String = if (subFolder != null && subFolder.isNotEmpty()) {
                publicFolder + File.separator + Constants.FOLDER_PATH
            } else {
                publicFolder
            }
            contentValues.put(
                MediaStore.Files.FileColumns.RELATIVE_PATH,
                parentFolder
            )
        } else {
            val parentFolderFile: File = if (subFolder != null && subFolder.isNotEmpty()) {
                File(
                    Environment.getExternalStoragePublicDirectory(
                        publicFolder
                    ), Constants.FOLDER_PATH
                )
            } else {
                Environment.getExternalStoragePublicDirectory(
                    publicFolder
                )
            }
            if (!parentFolderFile.exists()) {
                parentFolderFile.mkdirs()
            }
            val outputFile = File(
                parentFolderFile,
                fullName
            )
            contentValues.put(
                MediaStore.Files.FileColumns.DATA,
                outputFile.absolutePath
            )
        }
        return context.contentResolver.insert(fileCollection, contentValues)
    }

    fun checkDisplayOverOtherAppPermission(context: Context): Boolean {
        return if (isAndroidQ()) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    private fun openAppInformation(context: Context) {
//        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.packageName))
        context.startActivity(intent)

    }

    fun setUpDialogGrantPermission(context: Context) {
        val builder = AlertDialog.Builder(context)
        var res = false
        builder.setMessage(context.resources.getString(R.string.confirm_message))
            .setTitle(context.resources.getString(R.string.confirm_display_over_title))
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                openAppInformation(context)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    fun getActionPopup(isFull: Boolean, context: Context): MutableList<ActionModel>{
        var actions = mutableListOf<ActionModel>()
        if (isFull) {
            actions.add(ActionModel(R.drawable.ic_edit, context.getString(R.string.edit)))
            actions.add(ActionModel(R.drawable.ic_delete, context.getString(R.string.delete)))
        }
        else {
            actions.add(ActionModel(R.drawable.ic_edit, context.getString(R.string.edit)))
        }
        return actions
    }

}