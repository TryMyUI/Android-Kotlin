package com.mahesch.trymyui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.ManageFlowAfterTest
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.receivers.ConnectivityReceiver
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import com.mahesch.trymyui.services.NativeAppRecordingService
import kotlinx.android.synthetic.main.uploading_video_activity.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class UploadingVideoActivity : AppCompatActivity(),TransferListener {

    lateinit var sharedPrefHelper: SharedPrefHelper
    var testResultId : String? = null
    var cognito_id : String? = null
    var token : String? = null
    var startUploadingTime : Long? = 0
    var identityId: kotlin.String? = null
    private var isdoneRecordingAPICompleted = false

    var RetryCountNumber = 6
    var retryCountNumber_face = 3
    var countForTryAgainTaskTime = 0
    var uploadTime : Long = 0L

    var RetryMaterialDialog: Dialog? = null


    var isTaskVideoUploadCompleted = false
    var isFaceVideoUploadCompleted = false

    private var task_timings: HashMap<String, String>? = null
    private  var SEQ_TaskRate:java.util.HashMap<kotlin.String, kotlin.String>? = null
    private  var TaskCompelete:java.util.HashMap<kotlin.String, kotlin.String>? = null

    private var all_file_to_merge: List<*>? = null

    private val TAG = UploadingVideoActivity::class.java.simpleName.toUpperCase()

    var credentialsProvider: CognitoCachingCredentialsProvider? = null
    var transferUtility: TransferUtility? = null

    var observer: TransferObserver? = null
    var observer_face_recording: TransferObserver? = null

    private var availableTestModel: AvailableTestModel? = null
    private var isInternet = false

    private var isTaskUploadRetry = false
    private var isFaceUploadRetry = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uploading_video_activity)

        pb_face_video!!.progressDrawable.setColorFilter(
            resources.getColor(R.color.try_my_ui_green),
            PorterDuff.Mode.MULTIPLY
        )
        pb_face_video!!.max = 100
        pb_face_video!!.progress = 0
        pb_face_video!!.isIndeterminate = false


        pb_task_video!!.progressDrawable.setColorFilter(
            resources.getColor(R.color.try_my_ui_green),
            PorterDuff.Mode.MULTIPLY
        )
        pb_task_video!!.max = 100
        pb_task_video!!.progress = 0
        pb_task_video!!.isIndeterminate = false

        sharedPrefHelper = SharedPrefHelper(this@UploadingVideoActivity)
        testResultId = sharedPrefHelper.getTestResultId()
        cognito_id = sharedPrefHelper.getIdentityId()

        Log.e(TAG,"cognito_id "+cognito_id)

        token = sharedPrefHelper.getToken()

        gifview_face.visibility  = View.GONE

        startUploadingTime = System.currentTimeMillis()

        if(intent != null)
        {
            task_timings =
                intent.getSerializableExtra("taskTime") as HashMap<String, String>
            SEQ_TaskRate =
                intent.getSerializableExtra("SEQ_TaskRating") as HashMap<String, String>
            TaskCompelete =
                intent.getSerializableExtra("TaskCompelete") as HashMap<String, String>
            all_file_to_merge =
                intent.getSerializableExtra("List_of_files_to_merge") as List<*>
            availableTestModel =
                intent.getSerializableExtra("availableTestConstants") as AvailableTestModel
        }
        else{
            MoveToDashBoard()
        }

        if(availableTestModel?.opt_for_face_recording!!){
            ll_uploading_face_parent.visibility  = View.VISIBLE
        }
        else{
            ll_uploading_face_parent.visibility = View.GONE
            val param = LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            param.weight = 1.0f
            param.gravity = Gravity.CENTER
            param.setMargins(0, 300, 0, 300)

            ll_uploading_task_parent.layoutParams = param
        }

        initAws()
    }

    private fun initAws(){
        credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext,  /* get the context for the application */
            cognito_id,  /* Identity Pool ID get this id from api // */
            Regions.US_EAST_1 /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )

        val config = ClientConfiguration()
        config.connectionTimeout = 30000
        config.socketTimeout = 30000
        val s3: AmazonS3 = AmazonS3Client(credentialsProvider, config)

        transferUtility = TransferUtility(s3,this)
        Log.e(TAG,"transferUtility "+transferUtility)

        ll_uploading_face_transparent.visibility = View.VISIBLE

        if (all_file_to_merge != null && all_file_to_merge!!.size!! > 0) {
            val myFileAppending: MyFileAppending = MyFileAppending()
            myFileAppending.execute()
        } else {

            beginTaskVideoUpload()
        }
    }

    inner class MyFileAppending : AsyncTask<String?, String?, String?>() {

         override fun doInBackground(vararg params: String?): String? {
            Utils.appendingFile(all_file_to_merge!!)
            return null
        }

        override fun onPostExecute(unused: String?) {

            isInternet = ConnectivityReceiver.isConnected()

            if (isInternet) {
                val file = File(Utils.getOutputVideoFileNameForAmazonS3Bucket(""))

                if (file.exists())
                {
                    beginTaskVideoUpload()
                }
                else
                {
                    Toast.makeText(this@UploadingVideoActivity, "Video file not exists.", Toast.LENGTH_LONG).show()
                    terminatingTest()
                }
            } else {
                retryOptionForBeginUpload()
            }
        }
    }
    
    private fun terminatingTest() {
        isInternet = ConnectivityReceiver.isConnected()


        if (isInternet)
        {
            val mInterface: ApiService.ApiInterface = RetrofitInstance.getService()
            val token = sharedPrefHelper.getToken()
            val testResutId = sharedPrefHelper.getTestResultId()
            val OhTestId = sharedPrefHelper.getOhTestId()

            sharedPrefHelper.removeAvaliableTestId()

            Log.e("ohtestit", "ohtestid$OhTestId   $testResutId")

            if (testResutId.equals("", ignoreCase = true)) {
                MoveToDashBoard()
            } else {
                var call = mInterface.terminate(token!!, testResutId + "")

                call.enqueue(object : Callback<CommonModel>{
                    override fun onFailure(call: Call<CommonModel>, t: Throwable) {


                        MoveToDashBoard()
                    }

                    override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                        sharedPrefHelper.removeTestResultId()
                        MoveToDashBoard()
                    }
                })

            }
        } else {
            MoveToDashBoard()
        }
    }


    
    private fun retryOptionForBeginUpload(){
        isInternet = ConnectivityReceiver.isConnected()
        if (!isFinishing) {
            if (!isInternet) {
                val dialog = Dialog(this@UploadingVideoActivity)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before  set layout-sw480dp
                dialog.setContentView(R.layout.network_fail_custom_alert)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window.attributes)
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                lp.gravity = Gravity.CENTER

                val button_tap_to_retry = dialog.findViewById<View>(R.id.button_tap_to_retry) as LinearLayout

                button_tap_to_retry.setOnClickListener {
                    isInternet = ConnectivityReceiver.isConnected()

                    if (isInternet)
                    {
                        dialog.dismiss()
                        beginTaskVideoUpload()
                    }
                    else {
                        Toast.makeText(
                            this@UploadingVideoActivity,
                            R.string.no_internet_connection,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                dialog.window.attributes = lp
                dialog.show()
            } else {
                beginTaskVideoUpload()
            }
        }
    }
    


    fun MoveToDashBoard() {
        if ( sharedPrefHelper?.getGuestTester()!!) {

            // todo Logout move to login becouse guest login redirect always to login screen
            sharedPrefHelper.clearSharedPreference()
            val intent = Intent(this@UploadingVideoActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        } else {

            // todo  move to dashboard i.e TabActivity
            val intent = Intent(this@UploadingVideoActivity, TabActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun beginTaskVideoUpload() {
        if (!isFinishing) {
            isInternet = Utils.isInternetAvailable(this@UploadingVideoActivity)
            if (isInternet) {
                try {
                    if (credentialsProvider != null)
                    {
                        identityId = credentialsProvider!!.identityId
                        if (identityId != null)
                        {
                            if (!isdoneRecordingAPICompleted)
                                doneRecordingAPI(token!!, testResultId!!, identityId!!)
                        }
                        else
                        {
                            if (!isdoneRecordingAPICompleted)
                                doneRecordingAPI(token!!, testResultId!!, "no_identity_id")
                        }
                    }
                    else
                    {
                        if (!isdoneRecordingAPICompleted)
                            doneRecordingAPI(token!!, testResultId!!, "no_identity_id")
                    }
                }
                catch (e: Exception)
                {
                    postFeedback("In credentialsProvide IdentityId not able to get. DoneRecording API not call " + e.message)
                }

                val file_task_recording = File(Utils.getOutputVideoFileNameForAmazonS3Bucket(""))

                val file_face_recording = File(Utils.getFinalFaceVideoFilePath())

                if (!file_task_recording.exists())
                {
                    terminatingTest()
                }
                else {
                    if (NativeAppRecordingService.countForTryAgainVideoUpload < RetryCountNumber)
                    {
                        val oh_test_id = sharedPrefHelper.getOhTestId()
                        val s3_bucket_name = sharedPrefHelper.getS3Bucket()
                        val bucket_key =
                            "scenario_results/" + oh_test_id + "" + "/" + file_task_recording.name

                        Log.e(TAG, "file name in upload method " + file_task_recording.name)

                        observer = transferUtility?.upload(s3_bucket_name, bucket_key, file_task_recording)

                        var taskVideoUploadListener = TaskVideoUploadListener()
                        Log.e(TAG,"taskVideoUploadListener "+taskVideoUploadListener)
                        observer?.setTransferListener(TaskVideoUploadListener())

                        if (isTaskUploadRetry)
                        {
                            NativeAppRecordingService.countForTryAgainVideoUpload++
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "task retry count over", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                //NEED TO MANAGE LOGIC FOR INTERNET FAILURE
                displayFailedViewOfTask(0)
            }
        }
    }


    private fun doneRecordingAPI(token: String , testResultId : String,identityId : String){

        var jsonObject = JsonObject()
        jsonObject.addProperty("at", token)
        jsonObject.addProperty("test_result_id", testResultId)
        if (identityId == null) {
            jsonObject.addProperty("aws_identity_id", "no_identity_id")
        } else {
            jsonObject.addProperty("aws_identity_id", identityId)
        }

        val mApiInterface: ApiService.ApiInterface = RetrofitInstance.getPostApiService()

        val call = mApiInterface.doneRecording(JsonParser().parse(jsonObject.toString()) as JsonObject)

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.code() == 200)
                    isdoneRecordingAPICompleted = true
            }
        })
    }





    private fun beginFaceVideoUpload() {

        if (!isFinishing)
        {
            isInternet = Utils.isInternetAvailable(this@UploadingVideoActivity)

            if (isInternet)
            {
                if (NativeAppRecordingService.countForTryAgainVideoUpload < retryCountNumber_face)
                {
                    ll_uploading_face_parent.setBackgroundColor(resources.getColor(R.color.white))
                    displayUploadViewOfFace()

                    val file_face_recording = File(Utils.getFinalFaceVideoFilePath())
                    val oh_test_id = sharedPrefHelper.getOhTestId()
                    val s3_bucket_name = sharedPrefHelper.getS3Bucket()

                    val bucket_key_face_recording = "scenario_results/" + oh_test_id + "" + "/" + file_face_recording.name

                    Log.e(TAG, "face file name in upload method " + file_face_recording.name)

                    observer_face_recording = transferUtility!!.upload(
                        s3_bucket_name,
                        bucket_key_face_recording,
                        file_face_recording
                    )

                    observer_face_recording?.setTransferListener(FaceVideoUploadListener())

                    if (isFaceUploadRetry)
                    {
                        NativeAppRecordingService.countForTryAgainVideoUpload++
                    }
                }
                else
                {
                    Toast.makeText(this, "retry count over", Toast.LENGTH_SHORT).show()
                    sendTaskTiming()
                }
            } else
            {
                displayFailedViewOfFace(0)
            }
        }
    }

    fun postFeedback(message: String) {
        if (!isFinishing) {
            isInternet = ConnectivityReceiver.isConnected()

            if (isInternet)
            {
                val mApiInterface: ApiService.ApiInterface = RetrofitInstance.getService()

                val token = SharedPrefHelper(applicationContext).getToken()

                val postID = sharedPrefHelper.getTestResultId()

                if (sharedPrefHelper.getGuestTester())
                {
                    val  call = mApiInterface.postFeedBackGuest(sharedPrefHelper?.getEmailId()!!,
                        sharedPrefHelper?.getUsername()!!, ApiService.Channel_Type_CrashReport,
                        message, ApiService.App_Type + " failed to upload video for TestResult " + postID, "android")

                    call.enqueue(object : Callback<CommonModel>{
                        override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                        }

                        override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                        }
                    })

                } else {
                    val  call =   mApiInterface.postFeedBackWorker(token!!, ApiService.Channel_Type_CrashReport, message,
                        ApiService.App_Type + " failed to upload video for TestResult " + postID, "android")

                    call.enqueue(object : Callback<CommonModel>{
                        override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                        }

                        override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                        }
                    })


                }
            }
        }
    }


    private fun sendTaskTiming(){
        val endtime = System.currentTimeMillis()
        uploadTime = endtime - startUploadingTime!!

        val test_result_id = sharedPrefHelper.getTestResultId()
        val token = sharedPrefHelper.getToken()

        var jsonTaskComplete = JsonObject()
        var jsonTaskTimings = JsonObject()
        var jsonSeqTaskRate = JsonObject()

        for (key in task_timings!!.keys) {
            Log.e(TAG, "task_timings " + task_timings!![key])
            jsonTaskTimings.addProperty(key, task_timings!![key])
        }

        for (key in SEQ_TaskRate!!.keys) {
            jsonSeqTaskRate.addProperty(key, SEQ_TaskRate!![key])
        }

        for (key in TaskCompelete!!.keys) {
            jsonTaskComplete.addProperty(key, TaskCompelete!![key])
        }

        var jsonObjectMain = JsonObject()

        jsonObjectMain.addProperty("at", token)
        jsonObjectMain.addProperty("uploaded_time", uploadTime)
        jsonObjectMain.addProperty("test_result_id", test_result_id)
        jsonObjectMain.add("task_completion_status", jsonTaskComplete)
        jsonObjectMain.add("task_seq_ratings", jsonSeqTaskRate)
        jsonObjectMain.add("task_timings", jsonTaskTimings)

        if(checkForPostTestSurveys()){
            jsonObjectMain.addProperty("is_finished",true)
        }

        sendSaveTaskTimingData(jsonObjectMain)
    }

    private fun checkForPostTestSurveys(): Boolean {

        var isFinished = (availableTestModel?.surveyQuestions.equals("[]",   true)
                && availableTestModel?.susQuestion.equals("[]", ignoreCase = true)
                && availableTestModel?.npsQuestion.equals("[]", ignoreCase = true)
                && availableTestModel?.ux_crowd_questions.equals("[]", ignoreCase = true))

        Log.e(TAG, "isFinished $isFinished")
        return isFinished
    }


    private fun sendSaveTaskTimingData(jsonObject: JsonObject){
        val mInterface: ApiService.ApiInterface = RetrofitInstance.getPostApiService()

        val call = mInterface.saveTaskTime(JsonParser().parse(jsonObject.toString()) as JsonObject)

        call.enqueue(object : Callback<JsonObject>{

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (countForTryAgainTaskTime == 4) {

                    // send mail for error issue
                    sendCrashReportForSaveTaskTime("Error in sending save taks timing  " + t.message)
                    showMessageOnSuccessVideoUploading()
                } else {
                    countForTryAgainTaskTime++
                    sendSaveTaskTimingData(jsonObject)
                }
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var statusCode = response.code()

                    if(statusCode == 200){
                        showMessageOnSuccessVideoUploading()
                    }
            }

        })
    }


    fun sendCrashReportForSaveTaskTime(message: String) {
        val mApiInterface: ApiService.ApiInterface = RetrofitInstance.getService()
        val test_resut_id = sharedPrefHelper.getTestResultId()

        val call = mApiInterface.postCrashReport(ApiService.Channel_Type_CrashReport, message, "android",
            ApiService.App_Type + " Failed to save task timings for test result. " + test_resut_id)

        call.enqueue(object: Callback<CommonModel>{
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
            }
        })
    }

    
    private fun showMessageOnSuccessVideoUploading(){
        if (!isFinishing) {
            val dialog = Dialog(this@UploadingVideoActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)

            dialog.setContentView(R.layout.video_upload_success_message)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window.attributes)
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER

            val textView_tap_to_retry = dialog.findViewById<View>(R.id.textview_continue) as TextView
            textView_tap_to_retry.text = "PROCEED TO POST TEST SURVEY"

            val button_tap_to_retry = dialog.findViewById<View>(R.id.button_continue) as LinearLayout

            dialog.window.attributes = lp

            button_tap_to_retry.setOnClickListener {
                dialog.dismiss()
                isInternet = ConnectivityReceiver.isConnected()
                if (isInternet)
                {
                    val postID = sharedPrefHelper.getTestResultId()

                    if (postID != null)
                    {
                        val f = File(Utils.getOutputVideoFileNameForAmazonS3Bucket(""))

                        if (f.exists())
                            f.delete()

                        if (availableTestModel?.surveyQuestions != null)
                        {
                           val manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this)
                            manageFlowAfterTest.moveToWhichActivity(this)
                        }
                        else {
                            MoveToDashBoard()
                        }
                    }
                    else
                    {
                        MoveToDashBoard()
                    }
                }
                else
                {
                    Utils.showInternetCheckToast(this)
                }
            }
            dialog.show()


        }
    }

   inner class TaskVideoUploadListener : TransferListener {
        // Simply updates the UI list when notified.

        override fun onError(id: Int, e: java.lang.Exception) {
            Log.e(TAG,"onError TaskVideoUploadListener")
            displayFailedViewOfTask(1)
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            val percentage = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
            Log.e(TAG, "percentage task $percentage")
            if (percentage.toInt() > 0 && percentage.toInt() <= 100) {

                Log.e(TAG,"onError TaskVideoUploadListener")

                displayPercentageOfTaskVideoUpload(percentage.toInt())
            }
            else{
                Log.e(TAG,"percentage less than 0")
            }
        }

        override fun onStateChanged(id: Int, state: TransferState) {
            if (state == TransferState.COMPLETED) {

                Log.e(TAG,"TransferState.COMPLETED")

                RetryMaterialDialog = null
                isTaskVideoUploadCompleted = true
                ll_uploading_face_transparent.setVisibility(View.GONE)
                gifview_face.setVisibility(View.VISIBLE)
                gifview_task.setVisibility(View.GONE)
                tv_lbl_task_video_uploading.setText("Task recording video \n successfully uploaded!")
                tv_task_video_pecentage.setTextColor(getResources().getColor(R.color.try_my_ui_green))

                if (availableTestModel?.opt_for_face_recording!!) beginFaceVideoUpload() else sendTaskTiming()

                // sendTaskTiming();
            } else if (state == TransferState.WAITING_FOR_NETWORK) {
                Log.e(TAG,"TransferState.WAITING_FOR_NETWORK")

                displayFailedViewOfTask(0)
            } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                Log.e(TAG, "pause or cancelled")
                transferUtility?.pause(observer?.getId()!!)
            } else if (state == TransferState.IN_PROGRESS) {
                Log.e(TAG,"TransferState.IN_PROGRESS")

                displayUploadViewOfTask()
            }
        }
    }


    inner class FaceVideoUploadListener : TransferListener {

        override fun onStateChanged(i: Int, transferState: TransferState) {
            Log.e(TAG, "onStateChanged")
            if (transferState == TransferState.COMPLETED) {
                RetryMaterialDialog = null
                isFaceVideoUploadCompleted = true
                displayUploadViewOfFace()
                gifview_face.setVisibility(View.INVISIBLE)
                tv_lbl_face_video_uploading.setText("Face recording video \n successfully uploaded")
                tv_face_video_percentage.setTextColor(getResources().getColor(R.color.try_my_ui_green))
                sendTaskTiming()
            } else if (transferState == TransferState.WAITING_FOR_NETWORK) {

                displayFailedViewOfFace(0)
            } else if (transferState == TransferState.FAILED || transferState == TransferState.CANCELED) {
                transferUtility?.pause(observer_face_recording?.getId()!!)
            } else if (transferState == TransferState.IN_PROGRESS) {
                displayUploadViewOfFace()
            }
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            Log.e(TAG, "onProgressChanged")
            val percentage =
                bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
            if (percentage.toInt() > 0 && percentage.toInt() <= 100) {
                Log.e(TAG, "percentage face $percentage")
                displayPercentageOfFaceVideoUpload(percentage.toInt())
            }
        }

        override fun onError(i: Int, e: java.lang.Exception) {
            Log.e(TAG, "OnError FaceVideoUploadListener " + i + "exception e " + e)
            displayFailedViewOfFace(1)
        }
    }


    private fun displayPercentageOfTaskVideoUpload(percentage: Int) {
        tv_task_video_pecentage.text = "$percentage%"
        pb_task_video.progress = percentage
    }


    private fun displayPercentageOfFaceVideoUpload(percentage: Int) {
        tv_face_video_percentage.text = "$percentage%"
        pb_face_video.progress = percentage
    }


    private fun displayFailedViewOfTask(reason_code: Int) {
        ll_task_failed.visibility = View.VISIBLE
        ll_uploading_task.visibility = View.GONE
        if (reason_code == 0) {
            iv_task_video_net_fail.visibility = View.VISIBLE
            iv_task_video_fail.visibility = View.GONE
            tv_task_upload_failed_message.text = "Please check your internet connection."
        } else {
            iv_task_video_net_fail.visibility = View.GONE
            iv_task_video_fail.visibility = View.VISIBLE
            tv_face_video_failed_uploading_message.text = "Failed to upload video"
        }
    }


    private fun displayFailedViewOfFace(reason_code: Int) {
        ll_face_failed.visibility = View.VISIBLE
        ll_uploading_face.visibility = View.GONE
        if (reason_code == 0) {
            iv_face_video_net_fail.visibility = View.VISIBLE
            iv_face_video_fail.visibility = View.GONE
            tv_face_video_failed_uploading_message.text = "Please check your internet connection."
        } else {
            iv_face_video_net_fail.visibility = View.GONE
            iv_face_video_fail.visibility = View.VISIBLE
            tv_face_video_failed_uploading_message.text = "Failed to upload video"
        }
    }


    private fun displayUploadViewOfTask() {
        ll_uploading_task.visibility = View.VISIBLE
        ll_task_failed.visibility = View.GONE
    }


    private fun displayUploadViewOfFace() {
        ll_face_failed.visibility = View.GONE
        ll_uploading_face.visibility = View.VISIBLE
    }


    private fun retryTaskVideoUpload() {
        isInternet = Utils.isInternetAvailable(this@UploadingVideoActivity)
        if (isInternet) {
            displayUploadViewOfTask()
            beginTaskVideoUpload()
        } else {
            displayFailedViewOfTask(0)
        }
    }

    private fun retryFaceVideoUpload() {
        isInternet = Utils.isInternetAvailable(this@UploadingVideoActivity)
        if (isInternet) {
            displayUploadViewOfFace()
            beginFaceVideoUpload()
        } else {
            displayFailedViewOfFace(0)
        }
    }

    override fun onProgressChanged(p0: Int, p1: Long, p2: Long) {
        Log.e(TAG,"onProgress changed")
    }

    override fun onStateChanged(p0: Int, p1: TransferState?) {
        Log.e(TAG,"onStateChanged")
    }

    override fun onError(p0: Int, p1: java.lang.Exception?) {
        Log.e(TAG,"onError")
    }

}
