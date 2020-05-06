package com.mahesch.trymyui.helpers

import android.os.Environment
import android.util.Log
import com.mahesch.trymyui.services.NativeAppRecordingService
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FaceRecording {

    val TAG = FaceRecording::class.java.simpleName.toUpperCase()
    var nativeAppRecordingService = NativeAppRecordingService()

    fun getFaceVideoFile(): File? {

        // External sdcard file location
        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory(),
            StringsConstants.FACE_VIDEO_DIRECTORY_NAME
        )
        // Create storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(
                   TAG, "Oops! Failed create "
                            + StringsConstants.FACE_VIDEO_DIRECTORY_NAME + " directory"
                )
                return null
            }
        }
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val mediaFile: File
        mediaFile = File(
            mediaStorageDir.path + File.separator
                    + "VID_" + timeStamp + ".mp4"
        )
        return mediaFile
    }
}