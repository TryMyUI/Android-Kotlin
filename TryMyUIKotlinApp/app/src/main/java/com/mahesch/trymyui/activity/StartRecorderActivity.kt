package com.mahesch.trymyui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.media.MediaRecorder.VideoSource
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.Formatter
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jaredrummler.android.device.DeviceName
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.helpers.StringsConstants.Companion.DISPLAY_NAME
import com.mahesch.trymyui.helpers.StringsConstants.Companion.FrameOfMindScreen
import com.mahesch.trymyui.helpers.StringsConstants.Companion.ImpressionTestScreen
import com.mahesch.trymyui.helpers.StringsConstants.Companion.SpecialQualificationScreen
import com.mahesch.trymyui.helpers.StringsConstants.Companion.WireFrameOrPrototype
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.repository.CheckTestAvailabilityPresenter
import com.mahesch.trymyui.services.NativeAppRecordingService
import java.io.File
import java.io.IOException

class StartRecorderActivity : AppCompatActivity(),
    CheckTestAvailabilityPresenter.ICheckTestAvailability,
    TerminateTestPresenter.ITerminateTest{


    var myNativeappRecordingDialog: StartRecorderActivity? = null
    var sharedPrefHelper: SharedPrefHelper? = null
    var nativeAppRecordingService: NativeAppRecordingService? = null
    var test_id: String? = null
    var isInternet = false

    private val TAG = StartRecorderActivity::class.java.simpleName.toUpperCase()

    private var availableTestModel: AvailableTestModel? = null

    companion object{

        var recordingInfo: RecordingInfo? = null

        class RecordingInfo internal constructor(
            val width: Int,
            val height: Int,
            val density: Int,
            val frameRate: Int
        )

        fun calculateRecordingInfo(
            displayWidth: Int, displayHeight: Int,
            displayDensity: Int,
            isLandscapeDevice: Boolean,
            cameraWidth: Int,
            cameraHeight: Int,
            sizePercentage: Int,
            frameRate: Int
        ): RecordingInfo? {
            // Scale the display size before any maximum size calculations.
            var displayWidth = displayWidth
            var displayHeight = displayHeight
            displayWidth = displayWidth * sizePercentage / 100
            displayHeight = displayHeight * sizePercentage / 100
            if (cameraWidth == -1 && cameraHeight == -1) {
                // No cameras. Fall back to the display size.
                return RecordingInfo(displayWidth, displayHeight, displayDensity, frameRate)
            }
            var frameWidth = if (isLandscapeDevice) cameraWidth else cameraHeight
            var frameHeight = if (isLandscapeDevice) cameraHeight else cameraWidth
            if (frameWidth >= displayWidth && frameHeight >= displayHeight) {
                // Frame can hold the entire display. Use exact values.
                return RecordingInfo(displayWidth, displayHeight, displayDensity, frameRate)
            }

            // Calculate new width or height to preserve aspect ratio.
            if (isLandscapeDevice) {
                frameWidth = displayWidth * frameHeight / displayHeight
            } else {
                frameHeight = displayHeight * frameWidth / displayWidth
            }
            return RecordingInfo(frameWidth, frameHeight, displayDensity, frameRate)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        availableTestModel = intent.getSerializableExtra("availableTestConstants") as AvailableTestModel
        Log.e(TAG,"")

        if (availableTestModel == null) {
            Toast.makeText(this@StartRecorderActivity, resources.getString(R.string.went_wrong), Toast.LENGTH_LONG).show()
            moveToDashBoard()
        }

        requestedOrientation =
            if (availableTestModel?.recorder_orientation.equals("Landscape",true)) {
                //LANDSCAPE
                if (Build.VERSION.SDK_INT == 26) ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                if (Build.VERSION.SDK_INT == 26) ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

        sharedPrefHelper = SharedPrefHelper(this)

        myNativeappRecordingDialog = StartRecorderActivity()

        nativeAppRecordingService = NativeAppRecordingService()

        Log.e(TAG,"nativeAppRecordingService "+nativeAppRecordingService)
        Log.e(TAG,"mediaProjectionManager "+NativeAppRecordingService.mediaProjectionManager)

        if(!(sharedPrefHelper?.getUserType().equals("customer",true)))
        checkTestAvailability()

        checkTestAvailability()

    }

    private fun checkTestAvailability(){

        if(Utils.isInternetAvailable(this)){
            var checkTestAvailabilityPresenter = CheckTestAvailabilityPresenter(this)

            if(sharedPrefHelper?.getGuestTester()!!){
                checkTestAvailabilityPresenter.checkTestAvailabilityForGuest(makeJsonObject())
            }else{
                checkTestAvailabilityPresenter.checkTestAvailabilityForWorker(makeJsonObject())
            }
        }
        else
        {
            Utils.showInternetCheckToast(this)
        }


    }

    private fun makeJsonObject() : JsonObject{
        var joSystemInfo = JsonObject()

        var deviceName = DeviceName.getDeviceName()
        var deviceModel = Build.MODEL
        var androidVersion = android.os.Build.VERSION.RELEASE
        var wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ipAddress = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        var currentVersion = packageManager.getPackageInfo(packageName,0).versionName

        var deviceInfo =
            "$deviceName ($deviceModel) /$androidVersion/ currentVersion $currentVersion"

        joSystemInfo.addProperty("browser",""+ApplicationClass.selectedBrowserName)
        joSystemInfo.addProperty("system",""+deviceInfo)
        joSystemInfo.addProperty("ip_address",""+ipAddress)

        var jsonObject = JsonObject()

        jsonObject.addProperty("at",sharedPrefHelper?.getToken())
        jsonObject.addProperty("test_id",availableTestModel?.id)
        jsonObject.add("system_info",joSystemInfo)

        return JsonParser().parse(jsonObject.toString()) as JsonObject
    }

    private fun moveToDashBoard(){

        if (sharedPrefHelper?.getGuestTester()!!) {

            // todo Logout move to login becouse guest login redirect always to login screen
            SharedPrefHelper(this).clearSharedPreference()
            val intent = Intent(this@StartRecorderActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {

            // todo  move to dashboard i.e TabActivity
            val intent = Intent(this@StartRecorderActivity, TabActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    fun StopService() {
        sharedPrefHelper!!.removeAvaliableTestId()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Settings.System.putInt(
                this@StartRecorderActivity.contentResolver,
                "show_touches",
                0
            )
        }

        //Settings.System.putInt(StartRecorderActivity.this.getContentResolver(), "show_touches", 0);
        //Recording cancel so reset all view in prefrence
        NativeAppRecordingService.isRecorderStarted = false

        //stop service
        val service =
            Intent(this@StartRecorderActivity, NativeAppRecordingService::class.java)
        service.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        stopService(service)
        try {
            nativeAppRecordingService?.stopFaceRecordingVideo()
        } catch (e: Exception) {
            Log.e(TAG, "StopService stopFaceRecordingVideo $e")
        }
    }

    class RecordingInfo internal constructor(
        val width: Int,
        val height: Int,
        val density: Int,
        val frameRate: Int
    )

    fun getRecordingInfo(): Companion.RecordingInfo? {
        val displayMetrics = DisplayMetrics()
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getRealMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val displayDensity = displayMetrics.densityDpi
        val configuration = resources.configuration
        val isLandscape =
            configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        // Get the best camera profile available. We assume MediaRecorder
        // supports the highest.
        val camcorderProfile =
            CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
        val cameraWidth =
            camcorderProfile?.videoFrameWidth ?: -1
        val cameraHeight =
            camcorderProfile?.videoFrameHeight ?: -1
        val cameraFrameRate =
            camcorderProfile?.videoFrameRate ?: 30
        return calculateRecordingInfo(
            displayWidth, displayHeight,
            displayDensity, isLandscape, cameraWidth, cameraHeight, 50, cameraFrameRate
        )
    }

    override fun onSuccessTestAvailability(jsonObject: JsonObject) {

        NativeAppRecordingService.startRecordingCheckTestAvailableAPI = false
        if(jsonObject == null){
            showWentWrong()
        }
        else{

            Log.e(TAG, "onSuccessTestAvailability $jsonObject")

            val message = jsonObject.get("message").asString
            val status_code = jsonObject.get("status_code").asInt


            if(status_code == 200){
                var testResultId = jsonObject.get("data").asJsonObject.get("test_result_id").asInt
                var oh_test_id  = jsonObject["data"].asJsonObject["oh_test_id"].asInt

                sharedPrefHelper?.saveTestResultId(testResultId.toString())
                sharedPrefHelper?.saveOhTestId(oh_test_id.toString())

                intentForScreenCapture()
            }
            else{
                showAlertDialog(message)
            }
        }

    }

    private fun intentForScreenCapture(){
        val intent = NativeAppRecordingService.mediaProjectionManager?.createScreenCaptureIntent()

        if(intent == null){
            OkAlertDialog.dismissOkAlert()
            OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
            OkAlertDialog.showOkAlert("Your device is not able to capture screen")
        }
        else{
            startActivityForResult(intent,1111)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == 1111 && resultCode == RESULT_OK && data != null) {

                // todo recording stared recorder object created

                Log.e(TAG,"onActivityResult availableTestModel "+availableTestModel)

                NativeAppRecordingService.isRecorderStarted = true

                Log.e(TAG,"special qualification sccreen "+availableTestModel?.specialQalification)

                if (!(availableTestModel?.specialQalification.equals("")))
                {
                    Log.e(TAG,"special qual")
                    NativeAppRecordingService.whichScreenedAlreadyOpened = SpecialQualificationScreen

                    nativeAppRecordingService?.whichScreenToDisplay(availableTestModel!!)
                }
                else if (availableTestModel?.is_kind_partial_site!!)
                {
                    Log.e(TAG,"is kind partial")
                    NativeAppRecordingService.isKindTest = true
                    NativeAppRecordingService.whichScreenedAlreadyOpened = WireFrameOrPrototype
                    nativeAppRecordingService?.whichScreenToDisplay(availableTestModel!!)                }
                else if (availableTestModel?.do_impression_test!!)
                {
                    Log.e(TAG,"is impression test ")
                    NativeAppRecordingService.completeImpressionTest = true
                    NativeAppRecordingService.whichScreenedAlreadyOpened  = ImpressionTestScreen
                    nativeAppRecordingService?.whichScreenToDisplay(availableTestModel!!)
                }
                else
                {
                    Log.e(TAG,"is frame of mind ")
                    NativeAppRecordingService.whichScreenedAlreadyOpened  = FrameOfMindScreen
                    nativeAppRecordingService?.whichScreenToDisplay(availableTestModel!!)

                }

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1)
                {
                    Settings.System.putInt(this@StartRecorderActivity.contentResolver, "show_touches", 1)
                }
                runOnUiThread { nativeAppRecordingService?.startFaceRecordingVideo() }

                nativeAppRecordingService?.startTimer()

                setupRecorder(resultCode, data)
            } else
            {
                // Recording started now no need of start recording view again so set here in prefrence
                NativeAppRecordingService.isRecorderStarted = false
                terminatingTestOnCancelRecording()
            }
        } else
        {

            // Recording started now no need of start recording view again so set here in prefrence
            NativeAppRecordingService.isRecorderStarted = false
            terminatingTestOnCancelRecording()
        }
    }

    private fun setupRecorder(resultCode: Int,data: Intent){
        try {
            NativeAppRecordingService.dataIntentScreenCapture = data
            NativeAppRecordingService.resultCodeDataScreenCapture = resultCode
            NativeAppRecordingService.recorder!!.reset()
            StartRecorderActivity.recordingInfo = getRecordingInfo()
            NativeAppRecordingService.recorder!!.setVideoSource(VideoSource.SURFACE)

            // Enable Audio recording.......
            NativeAppRecordingService.recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            NativeAppRecordingService.recorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            NativeAppRecordingService.recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            NativeAppRecordingService.recorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            NativeAppRecordingService.recorder!!.setVideoSize(
                StartRecorderActivity.recordingInfo?.width!!,
                StartRecorderActivity.recordingInfo?.height!!
            )
            /**change this for video quality  */
            NativeAppRecordingService.recorder!!.setVideoEncodingBitRate(15 * 1000 * 100)
            NativeAppRecordingService.recorder!!.setVideoFrameRate(StartRecorderActivity.recordingInfo?.frameRate!!)
            //String outputVideoFileName = fileFormat.format(new Date());
            NativeAppRecordingService.recorder!!.setOutputFile(Utils.getOutputVideoFileNameForAmazonS3Bucket(""))

            runOnUiThread(Runnable {  })

            //   NativeAppRecordingService.recorder.setOutputFile(NativeAppRecordingService.face_video_file.getAbsolutePath());
            NativeAppRecordingService.projection = NativeAppRecordingService.mediaProjectionManager?.getMediaProjection(resultCode, data)
            NativeAppRecordingService.recorder!!.prepare()
            val surface = NativeAppRecordingService.recorder!!.surface

            NativeAppRecordingService.virtualDisplay = NativeAppRecordingService.projection!!.createVirtualDisplay(
                    DISPLAY_NAME,
                    StartRecorderActivity.recordingInfo?.width!!,
                    StartRecorderActivity.recordingInfo?.height!!,
                    StartRecorderActivity.recordingInfo?.density!!,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION,
                    surface,
                    null,
                    null
                )
            NativeAppRecordingService.recorder!!.start()

            // todo PauseRecording
            NativeAppRecordingService.isRecording = true
            finish()

        } catch (e: IllegalStateException) {
            Toast.makeText(
                this@StartRecorderActivity,
                "Your device is not able to capture screen!. Please make sure Mic of your device is not used by other application.",
                Toast.LENGTH_LONG
            ).show()
            val f = File(Utils.getOutputVideoFileNameForAmazonS3Bucket(""))
            if (f.exists()) {
                f.delete()
            }

            // TODO if decive is not able to capture audio and video
            // todo cancel the test
            terminatingTest()
        } catch (e: IOException) {
            Toast.makeText(
                this@StartRecorderActivity,
                "Your device is not able to capture screen!",
                Toast.LENGTH_SHORT
            ).show()
            val f = File(Utils.getOutputVideoFileNameForAmazonS3Bucket(""))
            if (f.exists()) {
                f.delete()
            }

            // TODO if decive is not able to capture audio and video
            // todo cancel the test
            terminatingTest()
            nativeAppRecordingService?.releaseRecorder()
        }
    }

    private fun terminatingTestOnCancelRecording(){
            terminatingTest()
    }

    private fun terminatingTest(){

        if(Utils.isInternetAvailable(this))
        {
            var terminateTestPresenter = TerminateTestPresenter(this)
            terminateTestPresenter.terminate(this,sharedPrefHelper!!)
        }
        else
        {
            Utils.showInternetCheckToast(this)
        }
    }



    private fun showAlertDialog(msg: String){
        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(msg)
    }

    override fun onFailureTestAvailability() {

        showWentWrong()
    }

    private fun showWentWrong(){
        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))

    }

    override fun onStop() {
        super.onStop()

        OkAlertDialog.dismissOkAlert()
    }

    override fun onDestroy() {
        super.onDestroy()

        OkAlertDialog.dismissOkAlert()
    }

    override fun onSuccessTerminateTest() {
        sharedPrefHelper?.removeTestResultId()
        sharedPrefHelper?.removeAvaliableTestId()
        nativeAppRecordingService?.chathead_click()
        nativeAppRecordingService?.hideAllScreen()
        NativeAppRecordingService.isRecorderStarted = false
        moveToDashBoard()
    }

    override fun onFailureTerminateTest() {
        sharedPrefHelper?.removeTestResultId()
        sharedPrefHelper?.removeAvaliableTestId()
        nativeAppRecordingService?.chathead_click()
        nativeAppRecordingService?.hideAllScreen()
        NativeAppRecordingService.isRecorderStarted = false
        moveToDashBoard()
    }

}
