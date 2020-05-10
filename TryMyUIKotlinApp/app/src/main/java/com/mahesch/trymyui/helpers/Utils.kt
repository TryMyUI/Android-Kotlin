package com.mahesch.trymyui.helpers

import android.app.ActionBar
import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.amulyakhare.textdrawable.TextDrawable
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.TabActivity
import com.mahesch.trymyui.helpers.StringsConstants.Companion.FACE_VIDEO_DIRECTORY_NAME
import com.mahesch.trymyui.helpers.StringsConstants.Companion.RecorderFolderName
import com.mahesch.trymyui.helpers.StringsConstants.Companion.TryMyUIVideoFile
import com.mahesch.trymyui.helpers.StringsConstants.Companion.TryMyUIVideoTempFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class Utils {


    companion object {

        private val TAG = Utils::class.simpleName?.toUpperCase()

        fun isInternetAvailable(context: Context): Boolean{

            val isConnected: Boolean

            val connectivityManager = ApplicationClass.getInstance()?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            Log.e(TAG,"connectivityManager "+connectivityManager)

            val networkInfo: NetworkInfo? = connectivityManager?.activeNetworkInfo

            Log.e(TAG,"activeNetworkInfo "+connectivityManager.activeNetworkInfo)

            if(networkInfo != null){
                isConnected = networkInfo.isConnected
            }
            else{
                isConnected = false
            }


            return isConnected
        }


        fun isValidEmail(email: String): Boolean{

            var isValid: Boolean = false

            isValid = if(email == null || email.trim().isEmpty()) {
                false
            } else {
                Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", email)
            }

            return isValid
        }

        fun isValidPassword(password: String): Boolean{

            return password != null && password.trim().length>3
        }


        fun isValidName(name: String): Boolean{

            return !(name == null || name.trim().isEmpty())
        }

        fun isValidTestId(test_id : String): Boolean {
            return !(test_id == null || test_id.trim().isEmpty())
        }

        fun showInternetCheckToast(context: Context){
            Toast.makeText(context,context.resources.getString(R.string.internet_check), Toast.LENGTH_SHORT).show()
        }

        fun showToast(context: Context,message: String){
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }

        fun moveToHome(activity: AppCompatActivity){
            var intent = Intent(activity, TabActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }

        fun moveToNextActivityAndFinishCurrent(currentActivity: AppCompatActivity,class_ : Class<AppCompatActivity>){
            var intent = Intent(currentActivity, class_::class.java)
            currentActivity.startActivity(intent)
            currentActivity.finish()
        }

        fun moveToNextActivityAndFinishCurrentActivity(context: Context,class_: Class<AppCompatActivity>){
            var intent = Intent(context, class_::class.java)
            context.startActivity(intent)
            (context as AppCompatActivity).finish()
        }

        fun moveToNextActivityAndFinishCurrentFragment(fragmentActivity: FragmentActivity,class_ : Class<AppCompatActivity>){
            var intent = Intent(fragmentActivity, class_::class.java)
            fragmentActivity.startActivity(intent)
            fragmentActivity.finish()
        }

        fun moveToNextActivityAndKeepCurrent(currentActivity: AppCompatActivity,class_ : Class<AppCompatActivity>){
            var intent = Intent(currentActivity, class_::class.java)
            currentActivity.startActivity(intent)
        }

        fun patternMatchForAndroid(str : String?) : Boolean{

            var isForAndroid = false

            var tokens = ArrayList<String>()

            var st = StringTokenizer(str)

            //SPLIT BY SPACE
            while (st.hasMoreElements()){
                tokens.add(st.nextElement().toString())
            }

            var android = "Android"
            var mobile = "mobile"

            for (i in 0 until tokens.size){
                if(tokens.get(i).contains(android,true)){
                    isForAndroid = true
                    break
                }else if(tokens.get(i).contains(mobile,true)){
                    isForAndroid = true
                }
            }

            return isForAndroid
        }


        fun isRecordingServiceRunning(serviceClass: Class<*>?,context: Context): Boolean {

            var activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            for(service in activityManager.getRunningServices(Integer.MAX_VALUE)){
                if (serviceClass!!.name == service.service.className) {
                    return true
                }
            }
            return false
        }


        fun checkGooglePlayServices(context: Context) : Boolean{

            var apiAvailability = GoogleApiAvailability.getInstance()
            var resultCode = apiAvailability.isGooglePlayServicesAvailable(context)

            if(resultCode != ConnectionResult.SUCCESS){
                    if(apiAvailability.isUserResolvableError(resultCode)){
                        apiAvailability.getErrorDialog(context as Activity,resultCode,9000).show()
                    } else { Log.i("GCM check play service", "This device is not supported.") }
                return false
            }
            return true
        }


         fun actionBarSetup(actionbar: ActionBar,actionbarTitle: String){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                actionbar?.title = "   $actionbarTitle"
                actionbar?.setHomeButtonEnabled(true)
                actionbar?.setDisplayHomeAsUpEnabled(true)
                actionbar?.show()
            }
        }


        fun getScreenSizeType(context: Context): Int {
            val screenSize = context.resources.configuration.screenLayout and
                    Configuration.SCREENLAYOUT_SIZE_MASK
            val screen_size_value: Int
            screen_size_value = when (screenSize) {
                Configuration.SCREENLAYOUT_SIZE_LARGE -> {
                    Log.e(TAG, "screenSize large $screenSize")
                    1
                }
                Configuration.SCREENLAYOUT_SIZE_NORMAL -> {
                    Log.e(TAG, "screenSize normal $screenSize")
                    2
                }
                Configuration.SCREENLAYOUT_SIZE_SMALL -> {
                    Log.e(TAG, "screenSize small $screenSize")
                    3
                }
                Configuration.SCREENLAYOUT_SIZE_XLARGE -> {
                    Log.e(TAG, "scrrensize xlarge $screenSize")
                    4
                }
                else -> {
                    Log.e(TAG, "screenSize $screenSize")
                    0
                }
            }
            return screen_size_value
        }

        fun getNativeAppTestApkPath() : String{
            var fileName = StringsConstants.apkDownloadedFolder

            var directory = Environment.getExternalStorageDirectory()

            var outputRoot = File(directory,fileName)
            outputRoot.mkdirs()

            var outputApkFileName = StringsConstants.outputFileName

            var f = File(outputRoot,outputApkFileName)

            return f.absolutePath
        }

        fun checkAppAlreadyInstall(context: Context) : String{
            val packageManager =  context.packageManager

            val fullPath = getNativeAppTestApkPath()

            val packInfo = packageManager.getPackageArchiveInfo(fullPath,0)

            var packageName = ""

            if(packInfo != null){
                packageName = packInfo.packageName
            }

            return packageName
        }

        fun openLinkInAppStore(packageName : String,context: Context){

            var intent: Intent? = null

            try {
                intent = Intent(Intent.ACTION_VIEW)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setData(Uri.parse("market://details?id=$packageName"))
                context.startActivity(intent)
            }
            catch (anfe : ActivityNotFoundException){

                intent = Intent(Intent.ACTION_VIEW)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=$packageName"))
                context.startActivity(intent)
            }
        }

         fun isMyServiceRunning(serviceClass: Class<*>,context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }


        fun DeleteFolderOfRecording() {
            val videoFolderName: String = StringsConstants.RecorderFolderName + "recorder"
            val moviesDirectory = Environment.getExternalStorageDirectory()
            val outputRoot = File(moviesDirectory, videoFolderName)
            if (outputRoot.isDirectory) {
                for (child in outputRoot.listFiles()) {
                   deleteRecursive(child)
                }
            }
            outputRoot.delete()
        }


        fun DeleteFaceRecordingFolder() {
            val videoFolderName: String = StringsConstants.FACE_VIDEO_DIRECTORY_NAME
            val moviesDirectory = Environment.getExternalStorageDirectory()
            Log.e(TAG, "videoFolderName $videoFolderName")
            Log.e(TAG, "moviesDirectory $moviesDirectory")
            val outputRoot = File(moviesDirectory, videoFolderName)

            if (outputRoot.isDirectory) {
                for (child in outputRoot.listFiles()) {
                    deleteRecursive(child)
                }
            } else {
                Log.e(TAG, "outputRoot is not directory")
            }
            val isDeleted = outputRoot.delete()
            Log.e(TAG, "isDeleted $isDeleted")
        }

        /*delete folder using use test*/
        fun DeleteAPKFolderUsingUseTestId(useTestId: String) {
            val videoFolderName: String = StringsConstants.ApkDownloadedFolder + useTestId
            val moviesDirectory = Environment.getExternalStorageDirectory()
            val outputRoot = File(moviesDirectory, videoFolderName)

            if (outputRoot.isDirectory) {
                for (child in outputRoot.listFiles()) {
                    deleteRecursive(child)
                }
            }
            outputRoot.delete()
        }

        private fun deleteRecursive(child: File) {
            child.delete()
        }

        fun getStatusBarHeight(context: Context): Int {
            return Math.ceil(25 * context.getResources().getDisplayMetrics().density.toDouble()).toInt()
        }

        fun getOutputVideoFileNameForAmazonS3Bucket(videoFileName: String): String {
            val videoFolderName: String =
                RecorderFolderName + "recorder"
            val moviesDirectory = Environment.getExternalStorageDirectory()
            val outputRoot = File(moviesDirectory, videoFolderName)
            outputRoot.mkdirs()
            val outputVideoFileName: String
            outputVideoFileName = if (videoFileName.equals("", ignoreCase = true)) {
                TryMyUIVideoFile + ".mp4"
            } else {
                TryMyUIVideoTempFile + UUID.randomUUID() + ".mp4"
            }
            val outputFile =
                File(outputRoot, outputVideoFileName).absolutePath
            Log.e(
                TAG,
                "xxxxxxxxxxxxxxxxxxxxxx outputFile $outputFile"
            )
            return outputFile
        }


        fun getFinalFaceVideoFilePath(): String? {

            // External sdcard file location
            val mediaStorageDir = File(
                Environment.getExternalStorageDirectory(),
               FACE_VIDEO_DIRECTORY_NAME
            )
            // Create storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(
                        TAG, "Oops! Failed create "
                                + FACE_VIDEO_DIRECTORY_NAME + " directory"
                    )
                    return null
                }
            }
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            var mediaFile: File

            mediaFile = File(mediaStorageDir, "facevideo.mp4")

            Log.e(TAG,"mdeia file absolute PATH "+mediaFile.absolutePath)

            return mediaFile.absolutePath
        }

        fun makeUrlInTextClickable(text: String?): String? {
            if (text == null) return ""

            var textWithUrl: String = text

            var textWithUrl1 = ""

            textWithUrl = textWithUrl.replace("\r", "        ")
            textWithUrl = textWithUrl.replace("\\n", "<br>")

            val words = textWithUrl.split(" ").toTypedArray()

            val regex = "^[(http[s]?:\\/\\/(www\\.)?|ftp:\\/\\/(www\\.)?|www\\.){1}|([0-9A-Za-z-\\.@:%_\\+~#=]+)]+((\\.[a-zA-Z]{2,9})+)(\\/(.)*)?(\\?(.)*)?".toRegex()

            val final_String: String? = null

            for (word in words) {

                var word1: String? = null

                if (word.matches(regex)) {
                    word1 = if (!word.toLowerCase().startsWith("http")) {
                        "<a href=\"http://$word\">$word</a>"
                    } else {
                        "<a href=\"$word\">$word</a>"
                    }
                } else {
                    Log.e(TAG,"word in else "+word);
                }

                Log.e(TAG,"textWithUrl sfg "+textWithUrl1);
                Log.e(TAG,"textWithUrl+word sfg "+textWithUrl1+word);

                textWithUrl1 = "$textWithUrl1 "+" $word"

                Log.e(TAG,"textWithUrl1 after add sfg "+textWithUrl1);
            }
            return textWithUrl1
        }

       fun GetProgressValue(seekBar: SeekBar, value: Int,context: Context): Int {
            var data = 1
            if (value > 0 && value <= 100) {
                data = 1
                seekBar.progressDrawable.setColorFilter(
                    context.getResources().getColor(R.color.rate1),
                    PorterDuff.Mode.MULTIPLY
                )
                val drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .height(context.getResources().getDimension(R.dimen.my_seekbar_height) as Int)
                    .width(context.resources.getDimension(R.dimen.my_seekbar_width) as Int)
                    .withBorder(context.resources.getDimension(R.dimen.my_seekbar_border) as Int)
                    .fontSize(context.resources.getDimension(R.dimen.my_seekbar_fontsize) as Int)
                    .endConfig()
                    .buildRound(data.toString() + "", context.resources.getColor(R.color.rate1))
                seekBar.thumb = drawable
            } else if (value > 100 && value <= 200) {
                data = 2
                seekBar.progressDrawable.setColorFilter(
                    context.resources.getColor(R.color.rate2),
                    PorterDuff.Mode.MULTIPLY
                )
                val drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .height(context.resources.getDimension(R.dimen.my_seekbar_height).toInt())
                    .width(context.resources.getDimension(R.dimen.my_seekbar_width).toInt())
                    .withBorder(context.resources.getDimension(R.dimen.my_seekbar_border).toInt())
                    .fontSize(context.resources.getDimension(R.dimen.my_seekbar_fontsize).toInt())
                    .endConfig()
                    .buildRound(data.toString() + "", context.resources.getColor(R.color.rate2))
                seekBar.thumb = drawable
            } else if (value > 200 && value <= 300) {
                data = 3
                seekBar.progressDrawable.setColorFilter(
                    context.resources.getColor(R.color.rate3),
                    PorterDuff.Mode.MULTIPLY
                )
                val drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .height(context.resources.getDimension(R.dimen.my_seekbar_height).toInt())
                    .width(context.resources.getDimension(R.dimen.my_seekbar_width).toInt())
                    .withBorder(context.resources.getDimension(R.dimen.my_seekbar_border).toInt())
                    .fontSize(context.resources.getDimension(R.dimen.my_seekbar_fontsize).toInt())
                    .endConfig()
                    .buildRound(data.toString() + "", context.resources.getColor(R.color.rate3))
                seekBar.thumb = drawable
            } else if (value > 300 && value <= 400) {
                data = 4
                seekBar.progressDrawable.setColorFilter(
                    context.resources.getColor(R.color.rate4),
                    PorterDuff.Mode.MULTIPLY
                )
                val drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .height(context.resources.getDimension(R.dimen.my_seekbar_height).toInt())
                    .width(context.resources.getDimension(R.dimen.my_seekbar_width).toInt())
                    .withBorder(context.resources.getDimension(R.dimen.my_seekbar_border).toInt())
                    .fontSize(context.resources.getDimension(R.dimen.my_seekbar_fontsize).toInt()) //* size in px *//*
                    .endConfig()
                    .buildRound(data.toString() + "", context.resources.getColor(R.color.rate4))
                seekBar.thumb = drawable
            } else if (value > 400 && value <= 500) {
                data = 5
                seekBar.progressDrawable.setColorFilter(
                    context.resources.getColor(R.color.rate5),
                    PorterDuff.Mode.MULTIPLY
                )
                val drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .height(context.resources.getDimension(R.dimen.my_seekbar_height).toInt())
                    .width(context.resources.getDimension(R.dimen.my_seekbar_width).toInt())
                    .withBorder(context.resources.getDimension(R.dimen.my_seekbar_border).toInt())
                    .fontSize(context.resources.getDimension(R.dimen.my_seekbar_fontsize).toInt()) //* size in px *//*
                    .endConfig()
                    .buildRound(data.toString() + "", context.resources.getColor(R.color.rate5))
                seekBar.thumb = drawable
            } else if (value > 500 && value <= 600) {
                data = 6
                seekBar.progressDrawable.setColorFilter(
                    context.resources.getColor(R.color.rate6),
                    PorterDuff.Mode.MULTIPLY
                )
                val drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .height(context.resources.getDimension(R.dimen.my_seekbar_height).toInt())
                    .width(context.resources.getDimension(R.dimen.my_seekbar_width).toInt())
                    .withBorder(context.resources.getDimension(R.dimen.my_seekbar_border).toInt())
                    .fontSize(context.resources.getDimension(R.dimen.my_seekbar_fontsize).toInt()) //* size in px *//*
                    .endConfig()
                    .buildRound(data.toString() + "", context.resources.getColor(R.color.rate6))
                seekBar.thumb = drawable
            } else if (value > 600 && value <= 700) {
                data = 7
                seekBar.progressDrawable.setColorFilter(
                    context.resources.getColor(R.color.rate7),
                    PorterDuff.Mode.MULTIPLY
                )
                val drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .height(context.resources.getDimension(R.dimen.my_seekbar_height).toInt())
                    .width(context.resources.getDimension(R.dimen.my_seekbar_width).toInt())
                    .withBorder(context.resources.getDimension(R.dimen.my_seekbar_border).toInt())
                    .fontSize(context.resources.getDimension(R.dimen.my_seekbar_fontsize).toInt()) //* size in px *//*
                    .endConfig()
                    .buildRound(data.toString() + "", context.resources.getColor(R.color.rate7))
                seekBar.thumb = drawable
            }
            return data
        }

        fun appendingFaceRecordingFile(all_face_recording_video_files: List<*>) {
            var count = 0
            Log.e(
                TAG,
                "Total files before merge " + all_face_recording_video_files.size
            )
            synchronized(all_face_recording_video_files) {
                do {
                    val parserMp4 = ParseFaceVideo()
                    val valueOf =
                        all_face_recording_video_files[count].toString()
                    Log.e(
                       TAG,
                        "appending file $valueOf $count"
                    )
                    val mainVideoFile: String =
                       getFinalFaceVideoFilePath()!!
                    Log.e(
                        TAG,
                        "mainVideoFile faceRecorder $mainVideoFile"
                    )
                    Log.e(
                        TAG,
                        "valueOf $valueOf"
                    )
                    parserMp4.append(mainVideoFile, valueOf)
                    count++
                } while (count < all_face_recording_video_files.size)
            }
        }

        fun appendingFile(all_file_to_merge: List<*>) {
            var count = 0

            Log.e(TAG, "total file " + "----------" + all_file_to_merge.size + "")

            synchronized(all_file_to_merge) {
                val i: Iterator<String> = all_file_to_merge.iterator() as Iterator<String> // Must be in synchronized block

                do {
                    val parserMp4 = ParserMp4()
                    val valueof = all_file_to_merge[count].toString()

                    Log.d("appending file", "$valueof**********$count")

                    val MainVideoFile: String = getOutputVideoFileNameForAmazonS3Bucket("")
                    Log.e(TAG, "mainVideoFile screenrecorder $MainVideoFile")

                    parserMp4.append(MainVideoFile, valueof)
                    Log.d("total file " + "----------", "parserMp4")

                    count++
                }
                while (count < all_file_to_merge.size)
            }
        }






    }





}


