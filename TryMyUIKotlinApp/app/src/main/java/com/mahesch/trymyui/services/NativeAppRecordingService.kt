package com.mahesch.trymyui.services

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.media.MediaRecorder.VideoSource
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.provider.Browser
import android.provider.Settings
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Size
import android.view.*
import android.view.TextureView.SurfaceTextureListener
import android.view.View.OnTouchListener
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import com.amulyakhare.textdrawable.TextDrawable
import com.kyleduo.switchbutton.SwitchButton
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.LoginActivity
import com.mahesch.trymyui.activity.StartRecorderActivity
import com.mahesch.trymyui.activity.StartRecorderActivity.Companion.recordingInfo
import com.mahesch.trymyui.activity.TabActivity
import com.mahesch.trymyui.activity.UploadingVideoActivity
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.helpers.StringsConstants.Companion.DISPLAY_NAME
import com.mahesch.trymyui.helpers.StringsConstants.Companion.FACE_VIDEO_DIRECTORY_NAME
import com.mahesch.trymyui.helpers.StringsConstants.Companion.FinishRecordingScreen
import com.mahesch.trymyui.helpers.StringsConstants.Companion.FrameOfMindScreen
import com.mahesch.trymyui.helpers.StringsConstants.Companion.ImpressionTestQuestionScreen
import com.mahesch.trymyui.helpers.StringsConstants.Companion.ImpressionTestScreen
import com.mahesch.trymyui.helpers.StringsConstants.Companion.RecordingTimeUpScreen
import com.mahesch.trymyui.helpers.StringsConstants.Companion.ShowTaskScreen
import com.mahesch.trymyui.helpers.StringsConstants.Companion.TryMyUIVideoTempFile
import com.mahesch.trymyui.helpers.StringsConstants.Companion.WireFrameOrPrototype
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.TaskModel
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.login.LoginException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class NativeAppRecordingService  : Service(), View.OnClickListener,TerminateTestPresenter.ITerminateTest {


    val TAG = NativeAppRecordingService::class.java.simpleName.toUpperCase()


    companion object{

        var availableTestModel : AvailableTestModel? = null

        var mStartTime: Long = 0

        var isServiceStarted = false
        var isTabHere = false
        var taskCount = 0
        var doneTask = false
        var goToFirstTaskTextSetting = false
        var recordingTimeUp = false
        var specialQualification = false
        var special_qualification = ""
        var timerHasStarted = false
        var doImpressionTest = false
        var isKindTest = false
        var completeImpressionTest = false
        var doneFirstClick = false
        var isRecorderStarted = false
        var isRecording = false
        var inital_state_switch = false
        var first_time_from_special_criteria_to_wireframe = false

        var countForTryAgainVideoUpload = 0
        var countForTryAgainFaceVideoUpload = 0

        var taskTimings = HashMap<String,String>()
        var seqRatings = HashMap<String,String>()
        var completeTaskRating = HashMap<String,String>()
        var allSubVideoFiles = Collections.synchronizedList(ArrayList<Any>())
        var allFaceRecordingVideoFiles = Collections.synchronizedList(ArrayList<Any>())
        var fileNameTemp = ""
        var milliSecRemain = 0
        var faceRecordingFileNameTemp = ""
        var startRecordingCheckTestAvailableAPI = false
        var impressionTestTimeRemaining = 0
        var startImpressionTest = false
        var impressionTestQuestion = false
        var taskTimingCounter = 1
        var recordingPausedState = false
        var whichScreenedAlreadyOpened = ""
        var isButtonContinuePress = false
        var testAPKFile = ""
        var appPackageName = ""
        var mediaProjectionManager : MediaProjectionManager? = null
        var projection : MediaProjection? = null
        var virtualDisplay : VirtualDisplay? = null
        var recorder : MediaRecorder? = null
        var x_init_cord = 0
        var y_init_cord:Int = 0
        var x_init_margin:Int = 0
        var y_init_margin:Int = 0
        var countDownTimer: VideoRecordingCountDownTimer? = null
        var ImpressionTestTimeRemaing = 0
        var milisecRemain: Long = 0

        var tasksArray : ArrayList<TaskModel> = ArrayList<TaskModel>()
        var startTime = 0L
        var windowManager : WindowManager? = null
        val szWindow = Point()
        var orientation = 0
        var removeViewLayout: RelativeLayout? = null
        var revereCountdownLayout : RelativeLayout? = null
        var showTaskWindowLayout : LinearLayout? = null
        var chatHeadView: LinearLayout? = null
        var removeImg: ImageView? = null
        var tvImpressionTimerTxt : TextView? = null
        var chatheadImg: LinearLayout? = null
        var timerTextview: TextView? = null
        var uiImage: ImageView? = null
        var tapHere: LinearLayout? = null
        var bubble_paused_recording: LinearLayout? = null
        var face_recording_texture_view: AutoFitTextureView? = null
        var ll_dnd_screen : LinearLayout? = null
        var btn_dnd_continue : Button? = null
        var fl_start_recording : FrameLayout? = null
        var btn_start_recording : Button? = null
        var fl_impression_test : FrameLayout? = null
        var fl_impression_test_question : FrameLayout? = null
        var ll_btn_start_impression_test : LinearLayout? = null
        var tv_btn_impression_test_again: TextView? = null
        var tv_btn_impression_test_done: TextView? = null
        var tv_impression_test: TextView? = null
        var ll_pauserecording: LinearLayout? = null
        var btn_qualification: Button? = null
        var tv_spec_criteria_sub: TextView? = null
        var tv_spec_criteria_title: TextView? = null
        var ll_pause_tab_bar: LinearLayout? = null
        var ll_finish_recording: LinearLayout? = null
        var ll_recording_timeup : LinearLayout? = null
        var btn_finish_recording: Button? = null
        var btn_cancel_recording: Button? = null
        var btn_uploading: Button? = null
        var rl_transparent_overlay : RelativeLayout? = null
        var ll_btn_next_previous: LinearLayout? = null
        var iv_btn_play_frame_mind: ImageView? = null
        var ll_btn_pause : LinearLayout? = null
        var ll_tv_click_open_test_website: LinearLayout? = null
        var tv_open_test_website: TextView? = null
        var tv_open_test_website_popup: TextView? = null
        var ll_btn_next: LinearLayout? = null
        var ll_btn_previous: LinearLayout? = null
        var tv_next_done: TextView? = null
        var ll_start_recording: LinearLayout? = null
        var ll_spec_qual: LinearLayout? = null
        var ll_frame_mind: LinearLayout? = null
        var btn_continue: Button? = null
        var tv_back_totask_from_frame: TextView? = null
        var tv_back_totask_from_finish: TextView? = null
        var tv_scenario: TextView? = null
        var tv_instructions: TextView? = null
        var tv_spec_criteria: TextView? = null
        var tv_read_frame_mind: TextView? = null
        var tv_finish_rec_desc: TextView? = null
        var linearLayoutShowTask : LinearLayout? = null
        var textViewTask : TextView? = null
        var textViewTaskTittle : TextView? = null
        var textViewgotoframe : TextView?  = null
        var textViewSEQRate : TextView? = null
        var customSeekbar: SeekBar? = null
        var linearLayout_easy_difficult_text : LinearLayout? = null
        var textViewTaskComplete : TextView? = null
        var mChangeSb: SwitchButton? = null
        var paramsShowTaskWindow : WindowManager.LayoutParams? = null
        var preview_size: Size? = null
        var video_size: Size? = null
        var face_video_file: File? = null
        var mSensorOrientation: Int? = null
        var dataIntentScreenCapture: Intent? = null
        var resultCodeDataScreenCapture = 0
        var currentTaskId = 0
        var cameraDevice: CameraDevice? = null
        var cameraId: String? = null
        var mCameraCaptureSession: CameraCaptureSession? = null
        var captureRequestBuilder: CaptureRequest.Builder? = null
        val REQUEST_CAMERA_PERMISSION = 200
        var backgroundHandler: Handler? = null
        var backgroundHandlerThread: HandlerThread? = null
        var face_media_recorder: MediaRecorder? = null

         val interval: Long = 1000

    }



    lateinit var sharedPrefHelper : SharedPrefHelper
    val mBinder: IBinder = LocalBinder()



   // lateinit var recordingInfo : StartRecorderActivity.RecordingInfo
    lateinit var nativeAppRecordingService : NativeAppRecordingService




 /*   private var cameraId: String? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private val REQUEST_CAMERA_PERMISSION = 200
    private var backgroundHandler: Handler? = null
    private var backgroundHandlerThread: HandlerThread? = null
    private var face_media_recorder: MediaRecorder? = null*/






    override fun onCreate() {
        super.onCreate()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        sharedPrefHelper = SharedPrefHelper(this)
        if(intent != null){

            nativeAppRecordingService = NativeAppRecordingService()

            init()

            MoveToForeGround.moveToForeGround(this,sharedPrefHelper?.getAvaliableTestId()!!)

            availableTestModel = intent.getSerializableExtra("availableTestConstants") as AvailableTestModel
            appPackageName = intent.getStringExtra("packageName")

            if (availableTestModel == null)
                moveToDashBoard()


            if(availableTestModel?.is_kind_partial_site!!){
                isKindTest = true
            }

            if(availableTestModel?.do_impression_test!!)
                doImpressionTest = true

            if(availableTestModel?.recorder_orientation.equals("Landscape",true))
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


            tasksArray =  GetTaskArrayFromJson.getTaskArray(availableTestModel?.task!!,appPackageName,this)
            Log.e(TAG,"tasksArray "+ tasksArray)

            startTime = (availableTestModel?.recording_timeout_minutes!!*60*1000).toLong()

            handleStart()

            startBackgroundThread()

        }
        else{
            stopService(Intent(this,NativeAppRecordingService::class.java))
            stopSelf()

            hideTouchPointer()

            moveToDashBoard()

        }

        return Service.START_STICKY
    }

    private fun init(){


        Utils.DeleteFolderOfRecording()
        Utils.DeleteFaceRecordingFolder()

        recorder = MediaRecorder()
        mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        Log.e(TAG,"mediaProjectionManager "+mediaProjectionManager)

        if(isRecording){
            try
            {
                if(projection != null)
                    projection?.stop()

                if(recorder != null)
                {
                    recorder?.stop()
                    recorder?.release()
                }

                if(virtualDisplay != null)
                    virtualDisplay?.release()
            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }

    }

    private fun handleStart(){
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        setWindowSize()

        setRemoveViewWindow(layoutInflater)

        setReverseCountdownWindow(layoutInflater)

        setShowTaskWindow(layoutInflater)

        setChatHeadWindow(layoutInflater)

        initializeViews()




    }

    private fun setWindowSize(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager!!.defaultDisplay.getSize(szWindow)
        } else {
            val w: Int = windowManager?.defaultDisplay?.width!!
            val h: Int = windowManager?.defaultDisplay?.height!!
            szWindow.set(w, h)
        }

       /* val w: Int = windowManager?.defaultDisplay?.width!!
        val h: Int = windowManager?.defaultDisplay?.height!!
        szWindow.set(w, h)*/
    }

    private fun setWindowManagerAttributes() : WindowManager.LayoutParams{

        /*WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED or
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND or*/

        var localParam: WindowManager.LayoutParams?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            localParam = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT)

        }
        else
        {
            localParam = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                        WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED or
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT)

        }
        localParam?.screenOrientation = orientation

        return localParam

    }


    private fun setRemoveViewWindow(layoutInflater: LayoutInflater){

        removeViewLayout = layoutInflater.inflate(R.layout.native_app_recording_remove, null) as RelativeLayout

        removeViewLayout!!.visibility = View.GONE

        removeImg = removeViewLayout!!.findViewById<View>(R.id.remove_img) as ImageView

        var paramsRemove = setWindowManagerAttributes()
        paramsRemove?.gravity = Gravity.TOP or Gravity.LEFT
        windowManager?.addView(removeViewLayout, paramsRemove)

    }


    private fun setReverseCountdownWindow(layoutInflater: LayoutInflater){

        revereCountdownLayout = layoutInflater.inflate(R.layout.reverse_count_down_textview, null) as RelativeLayout

        var paramsReverseCountDown = setWindowManagerAttributes()

        tvImpressionTimerTxt = revereCountdownLayout!!.findViewById<TextView>(R.id.usage)
        tvImpressionTimerTxt?.visibility = View.GONE

        windowManager?.addView(revereCountdownLayout,paramsReverseCountDown)
    }

    private fun setShowTaskWindow(layoutInflater: LayoutInflater){

        if(orientation == 0) {
            showTaskWindowLayout = layoutInflater.inflate(R.layout.startrecorder_screen_landscape, null) as LinearLayout
        } else {
            showTaskWindowLayout = layoutInflater.inflate(R.layout.startrecorder_screen, null) as LinearLayout
        }

        paramsShowTaskWindow = setWindowManagerAttributes()

        paramsShowTaskWindow?.dimAmount = 0.6F
        paramsShowTaskWindow?.gravity = Gravity.CENTER or Gravity.CENTER_VERTICAL

        windowManager?.addView(showTaskWindowLayout,paramsShowTaskWindow)
    }

    private fun setChatHeadWindow(layoutInflater: LayoutInflater){
        if(orientation == 0) {
            chatHeadView = layoutInflater.inflate(R.layout.chatheadview_landscape, null) as LinearLayout
        } else {
            chatHeadView = layoutInflater.inflate(R.layout.chatheadview, null) as LinearLayout
        }

        var chatHeadParams = setWindowManagerAttributes()

        chatHeadParams.gravity = Gravity.TOP or Gravity.LEFT
        chatHeadParams.x = 0
        chatHeadParams.y = 100

        chatheadImg = chatHeadView!!.findViewById(R.id.chathead_img) as LinearLayout
        timerTextview = chatHeadView!!.findViewById(R.id.timer_native_app) as TextView
        uiImage = chatHeadView!!.findViewById(R.id.ui_img) as ImageView
        tapHere = chatHeadView!!.findViewById(R.id.tap_here) as LinearLayout
        bubble_paused_recording = chatHeadView!!.findViewById(R.id.chathead_img_paused) as LinearLayout
        face_recording_texture_view = chatHeadView!!.findViewById(R.id.face_recording_texture_view) as AutoFitTextureView

        windowManager?.addView(chatHeadView,chatHeadParams)


    }

    private fun initializeViews(){


        // Linearlayout DND screen
        ll_dnd_screen = showTaskWindowLayout?.findViewById(R.id.linearlayout_dnd_screen) as LinearLayout
        ll_dnd_screen?.visibility = View.GONE

        btn_dnd_continue = ll_dnd_screen?.findViewById(R.id.button_dnd_continue) as Button
        btn_dnd_continue?.setOnClickListener(this)


        fl_start_recording = showTaskWindowLayout?.findViewById(R.id.framelayout_on_start_recording) as FrameLayout
        fl_start_recording?.visibility = View.GONE

        // button for start recorder
        btn_start_recording =  showTaskWindowLayout?.findViewById(R.id.button_startrecording) as Button
        btn_start_recording?.setOnClickListener(this)

        //  impression test
        fl_impression_test = showTaskWindowLayout?.findViewById(R.id.framelayoutImpressionTest) as FrameLayout

        //  impression test question
        fl_impression_test_question = showTaskWindowLayout?.findViewById(R.id.framelayoutImpressionTestQuestion) as FrameLayout

        // impression test button

        // impression test button
        ll_btn_start_impression_test = showTaskWindowLayout?.findViewById(R.id.button_start_impression_test) as LinearLayout
        ll_btn_start_impression_test?.setOnClickListener(this)

        // impression test question button
        tv_btn_impression_test_again =
            showTaskWindowLayout?.findViewById(R.id.button_impression_test_again) as TextView
        tv_btn_impression_test_again?.setOnClickListener(this)

        tv_btn_impression_test_done =
            showTaskWindowLayout?.findViewById(R.id.button_done_impression_test) as TextView
        tv_btn_impression_test_done?.setOnClickListener(this)


        tv_impression_test =
            showTaskWindowLayout?.findViewById(R.id.textView_impression_test) as TextView
        tv_impression_test?.setMovementMethod(ScrollingMovementMethod())

        ll_pauserecording =
            showTaskWindowLayout?.findViewById(R.id.pause_recording_open_test_site_layout) as LinearLayout


        //  button for special qualification
        btn_qualification=
            showTaskWindowLayout?.findViewById(R.id.button_specialqualification) as Button
        btn_qualification?.setOnClickListener(this)


        tv_spec_criteria_sub =
            showTaskWindowLayout?.findViewById(R.id.textview_special_criteria_sub_instrustion) as TextView
        tv_spec_criteria_title =
            showTaskWindowLayout?.findViewById(R.id.textview_special_criteria_title) as TextView

        // top bar for pause recording and open test website

        // top bar for pause recording and open test website
        ll_pause_tab_bar =
            showTaskWindowLayout?.findViewById(R.id.linearlayout_with_pause_tab_bar) as LinearLayout
        ll_pause_tab_bar?.visibility = View.GONE

        ll_finish_recording =
            showTaskWindowLayout?.findViewById(R.id.linearlayout_finish_recording) as LinearLayout
        ll_finish_recording?.visibility = View.GONE

        ll_recording_timeup =
            showTaskWindowLayout?.findViewById(R.id.linearlayout_recording_timeup) as LinearLayout
        ll_recording_timeup?.visibility = View.GONE

        btn_finish_recording =
            showTaskWindowLayout?.findViewById(R.id.button_finish_recording) as Button
        btn_finish_recording?.setOnClickListener(this)

        btn_cancel_recording =
            showTaskWindowLayout?.findViewById(R.id.button_cancel_uploading) as Button
        btn_cancel_recording?.setOnClickListener(this)

        btn_uploading =
            showTaskWindowLayout?.findViewById(R.id.button_uploading) as Button
        btn_uploading?.setOnClickListener(this)

        //  add overlay on task popup on pause recording
        rl_transparent_overlay = showTaskWindowLayout?.findViewById(R.id.transparent_overlay) as RelativeLayout

        ll_btn_next_previous = showTaskWindowLayout?.findViewById(R.id.linearlayout_button_next_previous) as LinearLayout


        // play button
        iv_btn_play_frame_mind =  showTaskWindowLayout?.findViewById(R.id.button_play) as ImageView
        iv_btn_play_frame_mind?.setOnClickListener(this)


        //  pause button
        ll_btn_pause =  showTaskWindowLayout?.findViewById(R.id.button_pause) as LinearLayout
        ll_btn_pause?.setOnClickListener(this)

        ll_tv_click_open_test_website = showTaskWindowLayout?.findViewById(R.id.textview_click_open_test_website) as LinearLayout
        ll_tv_click_open_test_website?.setOnClickListener(this)



        tv_open_test_website = showTaskWindowLayout?.findViewById(R.id.textview_open_test_website) as TextView
        tv_open_test_website_popup = showTaskWindowLayout?.findViewById(R.id.textview_open_test_website) as TextView

        ll_btn_next = showTaskWindowLayout?.findViewById(R.id.buttonNext) as LinearLayout
        ll_btn_next?.setOnClickListener(this)

        ll_btn_previous = showTaskWindowLayout?.findViewById(R.id.buttonPrev) as LinearLayout
        ll_btn_previous?.setOnClickListener(this)

        tv_next_done = showTaskWindowLayout?.findViewById(R.id.textViewNextDone) as TextView


        ll_start_recording = showTaskWindowLayout?.findViewById(R.id.linearlayout_startrecording_screen) as LinearLayout
        ll_spec_qual = showTaskWindowLayout?.findViewById(R.id.linearlayout_special_criteria) as LinearLayout
        ll_frame_mind = showTaskWindowLayout?.findViewById(R.id.linearlayout_frame_of_mind) as LinearLayout

        //  linearlayout_frame_of_mind  frame of mind next button
        btn_continue = showTaskWindowLayout?.findViewById(R.id.button_continue) as Button
        btn_continue?.setVisibility(View.VISIBLE)
        btn_continue?.setOnClickListener(this)

        // back to frame of mind

        // back to frame of mind
        tv_back_totask_from_frame = showTaskWindowLayout?.findViewById(R.id.textview_back_to_task_from_frame_of_mind) as TextView
        tv_back_totask_from_frame?.setVisibility(View.GONE)
        tv_back_totask_from_frame?.setOnClickListener(this)

        tv_back_totask_from_finish = showTaskWindowLayout?.findViewById(R.id.textview_back_to_task_from_finish_recording) as TextView
        tv_back_totask_from_finish?.setVisibility(View.VISIBLE)
        tv_back_totask_from_finish?.setOnClickListener(this)


        tv_scenario = showTaskWindowLayout?.findViewById(R.id.textview_scenario_frame_of_mind) as TextView

        val urlsstring: String? = Utils.makeUrlInTextClickable(availableTestModel?.scenario)

        val sequence: CharSequence = Html.fromHtml(urlsstring)

        val strBuilder = SpannableStringBuilder(sequence)
        val urls =
            strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
        for (span in urls) {
            makeLinkClickable(strBuilder, span)
        }



        tv_scenario?.setText(Html.fromHtml(availableTestModel?.scenario))
        tv_scenario?.setMovementMethod(LinkMovementMethod.getInstance())


        tv_instructions =
            showTaskWindowLayout?.findViewById(R.id.textviewpreliminary_instructions) as TextView
        tv_instructions?.setMovementMethod(ScrollingMovementMethod())
        val preliminaryInstruction =
            "Please start recording, then install and open the app to your device from the Play Store.\n\nDuring your test, tap the UI bubble to view or hide the current task."
        if (!appPackageName.equals("", ignoreCase = true)) {
            tv_instructions?.setText(preliminaryInstruction)
        }

        //  set text for special criteria
        tv_spec_criteria = showTaskWindowLayout?.findViewById(R.id.textViewSpecialCriteria) as TextView


        tv_spec_criteria?.setText(Html.fromHtml(availableTestModel?.specialQalification))



        tv_read_frame_mind = showTaskWindowLayout?.findViewById(R.id.textview_read_frame_of_mind) as TextView


        tv_finish_rec_desc = showTaskWindowLayout?.findViewById(R.id.textview_finishrecording_description) as TextView
        tv_finish_rec_desc?.setMovementMethod(ScrollingMovementMethod())

        setUpTaskBar()

        ll_start_recording?.setVisibility(View.GONE)
        ll_spec_qual?.setVisibility(View.GONE)
        ll_frame_mind?.setVisibility(View.GONE)
        fl_impression_test?.setVisibility(View.GONE)
        fl_impression_test_question?.setVisibility(View.GONE)
        rl_transparent_overlay?.setVisibility(View.GONE)


        if (!appPackageName.equals("", ignoreCase = true)) {
            val content = SpannableString(getString(R.string.link_to_open_app).toString() + "")
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            tv_open_test_website?.setText(content)
            tv_open_test_website_popup?.setText(content)
        } else {
            val content = SpannableString(getString(R.string.link_to_open_web))
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            tv_open_test_website?.setText(content)
            tv_open_test_website_popup?.setText(content)
        }


        if (availableTestModel?.opt_for_face_recording!!) {

            face_recording_texture_view?.surfaceTextureListener = surfaceTextureListener
        } else {
            face_recording_texture_view?.setVisibility(View.GONE)
        }

        if (isTabHere) {
            chatheadImg?.setBackgroundResource(R.drawable.green_circle_img)
            uiImage?.setVisibility(View.VISIBLE)
            timerTextview?.setVisibility(View.VISIBLE)
            tapHere?.setVisibility(View.GONE)
            chatheadImg?.setVisibility(View.VISIBLE)
            if (availableTestModel?.opt_for_face_recording!!)
                face_recording_texture_view?.setVisibility(View.VISIBLE)
            else
                face_recording_texture_view?.setVisibility(View.GONE)
        } else {
            chatheadImg?.setVisibility(View.GONE)
            face_recording_texture_view?.setVisibility(View.GONE)
            uiImage?.setVisibility(View.GONE)
            timerTextview?.setVisibility(View.GONE)
            tapHere?.setVisibility(View.VISIBLE)
            ll_dnd_screen?.setVisibility(View.VISIBLE)
        }

        showTaskWindowLayout?.setOnTouchListener(OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {

                // start recording popup
                if (ll_start_recording?.isShown()!!) {
                    showTaskWindowLayout?.setVisibility(View.GONE)
                    ll_start_recording?.setVisibility(View.GONE)
                }
                //frame of mind
                if (ll_frame_mind?.isShown()!!) {
                    showTaskWindowLayout?.setVisibility(View.GONE)
                    ll_frame_mind?.setVisibility(View.GONE)
                    fl_start_recording?.setVisibility(
                        View.GONE
                    )
                }
                // special qualification
                if (ll_spec_qual?.isShown()!!) {
                    showTaskWindowLayout?.setVisibility(View.GONE)
                    fl_start_recording?.setVisibility(View.GONE)
                    ll_spec_qual?.setVisibility(View.GONE)
                }

                // impression test
                if (fl_impression_test?.isShown()!!) {
                    showTaskWindowLayout?.setVisibility(View.GONE)
                    fl_start_recording?.setVisibility(
                        View.GONE
                    )
                    fl_impression_test?.setVisibility(View.GONE)
                }

                // impression test question
                if (fl_impression_test_question?.isShown()!!) {
                    showTaskWindowLayout?.setVisibility(View.GONE)
                    fl_start_recording?.setVisibility(
                        View.GONE
                    )
                    fl_impression_test_question?.setVisibility(
                        View.GONE
                    )
                }
                // show task
                if (linearLayoutShowTask?.isShown()!!) {
                    showTaskWindowLayout?.setVisibility(View.GONE)
                    fl_start_recording?.setVisibility(
                        View.GONE
                    )
                    ll_pause_tab_bar?.setVisibility(
                        View.GONE
                    )
                    linearLayoutShowTask?.setVisibility(View.GONE)
                }

                // finish reecording
                if (ll_finish_recording?.isShown()!!) {
                    showTaskWindowLayout?.setVisibility(View.GONE)
                    fl_start_recording?.setVisibility(
                        View.GONE
                    )
                    ll_finish_recording?.setVisibility(
                        View.GONE
                    )
                }
            }
            true
        })

        chatHeadView?.setOnClickListener(null)

        chatHeadView?.setOnTouchListener(object : OnTouchListener {
            var time_start: Long = 0
            var time_end: Long = 0
            var isLongclick = false
            var inBounded = false
            var remove_img_width = 0
            var remove_img_height = 0
            var handler_longClick = Handler()
            var runnable_longClick = Runnable { // TODO Auto-generated method stub
                isLongclick = true
                removeViewLayout?.setVisibility(View.VISIBLE)
                chathead_longclick()
            }

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                var layoutParams =
                    chatHeadView?.getLayoutParams() as WindowManager.LayoutParams
                val x_cord = event.rawX.toInt()
                val y_cord = event.rawY.toInt()
                val x_cord_Destination: Int
                var y_cord_Destination: Int
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        time_start = System.currentTimeMillis()
                        handler_longClick.postDelayed(runnable_longClick, 600)
                        remove_img_width = removeImg!!.layoutParams.width
                        remove_img_height = removeImg!!.layoutParams.height
                        x_init_cord = x_cord
                        y_init_cord = y_cord
                        x_init_margin = layoutParams.x
                        y_init_margin = layoutParams.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val x_diff_move: Int = x_cord - x_init_cord
                        val y_diff_move: Int = y_cord - y_init_cord
                        x_cord_Destination = x_init_margin + x_diff_move
                        y_cord_Destination = y_init_margin + y_diff_move
                        if (isLongclick) {
                            val x_bound_left = szWindow.x / 2 - (remove_img_width * 1.5).toInt()
                            val x_bound_right =
                                szWindow.x / 2 + (remove_img_width * 1.5).toInt()
                            val y_bound_top = szWindow.y - (remove_img_height * 1.5).toInt()
                            if (x_cord >= x_bound_left && x_cord <= x_bound_right && y_cord >= y_bound_top) {
                                inBounded = true
                                val x_cord_remove =
                                    ((szWindow.x - remove_img_height * 1.5) / 2).toInt()
                                val y_cord_remove =
                                    (szWindow.y - (remove_img_width * 1.5 + Utils.getStatusBarHeight(this@NativeAppRecordingService))).toInt()
                                if (removeImg!!.layoutParams.height == remove_img_height) {
                                    removeImg!!.layoutParams.height =
                                        (remove_img_height * 1.5).toInt()
                                    removeImg!!.layoutParams.width =
                                        (remove_img_width * 1.5).toInt()
                                    val param_remove =
                                        removeViewLayout?.getLayoutParams() as WindowManager.LayoutParams
                                    param_remove.x = x_cord_remove
                                    param_remove.y = y_cord_remove
                                    windowManager?.updateViewLayout(
                                        removeViewLayout,
                                        param_remove
                                    )
                                }
                                layoutParams.x =
                                    x_cord_remove + Math.abs(removeViewLayout?.getWidth()!! - chatHeadView?.getWidth()!!) / 2
                                layoutParams.y =
                                    y_cord_remove + Math.abs(removeViewLayout?.getHeight()!! - chatHeadView?.getHeight()!!) / 2
                                windowManager?.updateViewLayout(
                                    chatHeadView,
                                    layoutParams
                                )
                            } else {
                                inBounded = false
                                removeImg!!.layoutParams.height = remove_img_height
                                removeImg!!.layoutParams.width = remove_img_width
                                val param_remove =
                                    removeViewLayout?.getLayoutParams() as WindowManager.LayoutParams
                                val x_cord_remove: Int = (szWindow.x - removeViewLayout?.getWidth()!!) / 2
                                val y_cord_remove: Int =
                                    szWindow.y - (removeViewLayout?.getHeight()!! + Utils.getStatusBarHeight(this@NativeAppRecordingService))
                                param_remove.x = x_cord_remove
                                param_remove.y = y_cord_remove
                                windowManager?.updateViewLayout(
                                    removeViewLayout,
                                    param_remove
                                )
                            }
                        }
                        layoutParams.x = x_cord_Destination
                        layoutParams.y = y_cord_Destination
                        windowManager?.updateViewLayout(
                            chatHeadView,
                            layoutParams
                        )
                    }
                    MotionEvent.ACTION_UP -> {
                        isLongclick = false
                        removeViewLayout?.setVisibility(View.GONE)
                        removeImg!!.layoutParams.height = remove_img_height
                        removeImg!!.layoutParams.width = remove_img_width
                        handler_longClick.removeCallbacks(runnable_longClick)
                        if (inBounded) {
                            inBounded = false
                            showRemoveDialog(x_cord)
                            inBounded = false
                        }
                        val x_diff: Int = x_cord - x_init_cord
                        val y_diff: Int = y_cord - y_init_cord
                        if (Math.abs(x_diff) < 50 && Math.abs(y_diff) < 50) {
//                            time_end = System.currentTimeMillis();
//                            if ((time_end - time_start) < 300) {
                            //So that is click event.
                            if (!startRecordingCheckTestAvailableAPI) {
//                                Log.d("chathead.....", "inside chathead click ********  StartRecordingCheckTestAvailablity_API");
                                if (!startImpressionTest) {
                                    //Log.d("chathead.....", "inside chathead click ********  startImpressionTest");
                                    chathead_click()
                                } else {
                                    Toast.makeText(
                                        this@NativeAppRecordingService,
                                        """
                                            Your Impression Test is Running.
                                            You can terminate the test by long press and drag the bubble to close sign.
                                            """.trimIndent(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@NativeAppRecordingService,
                                    "Please wait checking avaliablity of test.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            //                            }
                        }
                        y_cord_Destination = y_init_margin + y_diff
                        val BarHeight: Int = Utils.getStatusBarHeight(this@NativeAppRecordingService)
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0
                        } else if (y_cord_Destination + (chatHeadView?.getHeight()!! + BarHeight) > szWindow.y) {
                            y_cord_Destination =
                                szWindow.y - (chatHeadView?.getHeight()!! + BarHeight)
                        }
                        layoutParams.y = y_cord_Destination
                        inBounded = false
                        resetPosition(x_cord)
                    }
                    else -> {
                        Log.e(TAG,"on action detected")
                    }
                }
                return true
            }
        })

    }

    fun setUpTaskBar() {
        if (tasksArray != null)
        {
            if (taskCount == (tasksArray.size - 1))
            {
                Log.e(TAG,"button next done")
                tv_next_done?.setText(R.string.done)
            }
            else
            {
                Log.e(TAG,"btn next next")
                tv_next_done?.setText(R.string.next)
            }

            Log.e(TAG,"task count "+ taskCount)

            if (taskCount == 0)
            {
                ll_btn_previous?.setEnabled(false)
                ll_btn_previous?.setVisibility(View.INVISIBLE)
                ll_btn_next?.setEnabled(true)
            }
            else {
                ll_btn_previous?.setEnabled(true)
                ll_btn_previous?.setVisibility(View.VISIBLE)
                ll_btn_next?.setEnabled(true)
                if (taskCount == (tasksArray.size - 1)) {
                    tv_next_done?.setText(R.string.done)
                }
            }
            setTaskToView(tasksArray[taskCount])
            textViewTaskTittle?.setText(Html.fromHtml("""<b>Task ${taskCount + 1}</b> of ${tasksArray.size}"""))

        }
    }

    private fun setTaskToView(taskModel: TaskModel){

        Log.e(TAG,"taskModel.task "+taskModel.task);
        val urlsstring = Utils.makeUrlInTextClickable(taskModel.task.toString() + "")
        Log.e(TAG, "urlString $urlsstring");

        val sequence: CharSequence = Html.fromHtml(urlsstring)

        Log.e(TAG,"sequence "+sequence)
        val strBuilder = SpannableStringBuilder(sequence)
        Log.e(TAG, "strBuilder $strBuilder")

        val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
        Log.e(TAG,"urls size "+urls.size)
        Log.e(TAG,"urls "+urls)

        for (span in urls) {
            Log.e(TAG,"span "+span)
            makeLinkClickable(strBuilder, span)
        }




        if (taskModel?.opt_for_seq || taskModel?.opt_for_task_completion)
        {
            showTaskWindowLayout?.findViewById<LinearLayout>(R.id.linearLayoutShowTask_with_rating)?.setVisibility(View.VISIBLE)
            showTaskWindowLayout?.findViewById<LinearLayout>(R.id.linearLayoutShowTask_without_rating)?.setVisibility(View.GONE)

            linearLayoutShowTask = showTaskWindowLayout?.findViewById(R.id.linearLayoutShowTask_with_rating) as LinearLayout
            textViewTask = showTaskWindowLayout?.findViewById(R.id.textViewTask_with_rating) as TextView
            textViewTask?.movementMethod = LinkMovementMethod.getInstance()

            textViewTaskTittle = showTaskWindowLayout?.findViewById(R.id.textViewTaskTittle_with_rating) as TextView
            textViewgotoframe = showTaskWindowLayout?.findViewById(R.id.textview_go_to_frameofmind_with_rating) as TextView
            textViewgotoframe?.setOnClickListener(this)
            textViewSEQRate = linearLayoutShowTask?.findViewById(R.id.textview_seq_rate) as TextView
            customSeekbar = linearLayoutShowTask?.findViewById(R.id.seekbar1) as SeekBar
            linearLayout_easy_difficult_text = linearLayoutShowTask?.findViewById(R.id.layout_easy_difficult_text) as LinearLayout
            mChangeSb = linearLayoutShowTask?.findViewById(R.id.sb_custom) as SwitchButton
            textViewTaskComplete = linearLayoutShowTask?.findViewById(R.id.textViewTaskComplete) as TextView

            if (!taskModel?.opt_for_seq) {
                textViewSEQRate?.setVisibility(View.GONE)
                customSeekbar?.setVisibility(View.GONE)
                linearLayout_easy_difficult_text?.setVisibility(View.GONE)
            }
            if (!taskModel?.opt_for_task_completion) {
                mChangeSb?.setVisibility(View.GONE)
                textViewTaskComplete?.setVisibility(View.GONE)
            }
            if (taskModel?.opt_for_seq) {
                textViewSEQRate?.setVisibility(View.VISIBLE)
                customSeekbar?.setVisibility(View.VISIBLE)
                linearLayout_easy_difficult_text?.setVisibility(View.VISIBLE)
                customSeekbar?.setThumbOffset(0)
                customSeekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        b: Boolean
                    ) {
                        if (isRecording) {
                            Utils.GetProgressValue(seekBar, seekBar.progress,this@NativeAppRecordingService)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        if (isRecording) {
                            val data: Int = Utils.GetProgressValue(seekBar, seekBar.progress,this@NativeAppRecordingService)
                            val position_key: String =
                                taskModel?.task_id.toString() + "" //taskCount + 1+"";
                            seqRatings.put(
                                position_key,
                                data.toString() + ""
                            )
                        }
                    }
                })
            }
            if (taskModel?.opt_for_task_completion) {
                mChangeSb?.setVisibility(View.VISIBLE)
                textViewTaskComplete?.setVisibility(View.VISIBLE)
                mChangeSb?.setText("YES", "NO")
                mChangeSb?.setOnTouchListener(OnTouchListener { view, motionEvent ->
                    if (isRecording) {
                        inital_state_switch = true
                        //Log.d("on touch swich", "touch");
                    }
                    false
                })
                mChangeSb?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, booleanValue ->
                    if (isRecording) {
                        val position_key: String =
                            taskModel.task_id.toString() + "" //taskCount + 1+"";
                        if (inital_state_switch) {
                            if (booleanValue) {
                                completeTaskRating.put(
                                    position_key,
                                    1.toString() + ""
                                ) //true
                                mChangeSb?.setThumbColorRes(R.color.white)
                                mChangeSb?.setTextColor(resources.getColor(R.color.yes_click))
                                mChangeSb?.setBackDrawableRes(R.drawable.swich_background_green)
                            } else {
                                completeTaskRating.put(
                                    position_key,
                                    0.toString() + ""
                                ) //false
                                mChangeSb?.setThumbColorRes(R.color.white)
                                mChangeSb?.setTextColor(resources.getColor(R.color.no_click))
                                mChangeSb?.setBackDrawableRes(R.drawable.swich_background_red)
                            }
                        }
                    }
                })
            }
            textViewTaskTittle?.setText(Html.fromHtml("""<b>Task ${taskCount + 1}</b> of ${tasksArray.size}"""))

            textViewTask?.setText(strBuilder)
        }
        else
        {
            showTaskWindowLayout?.findViewById<LinearLayout>(R.id.linearLayoutShowTask_with_rating)?.setVisibility(View.GONE)
            showTaskWindowLayout?.findViewById<LinearLayout>(R.id.linearLayoutShowTask_without_rating)?.setVisibility(View.VISIBLE)
            linearLayoutShowTask =
                showTaskWindowLayout?.findViewById(R.id.linearLayoutShowTask_without_rating) as LinearLayout
            textViewTask =
                showTaskWindowLayout?.findViewById(R.id.textViewTask_without_rating) as TextView
            textViewTask?.movementMethod = LinkMovementMethod.getInstance()
            textViewTaskTittle =
                showTaskWindowLayout?.findViewById(R.id.textViewTaskTittle_without_rating) as TextView
            textViewgotoframe =
                showTaskWindowLayout?.findViewById(R.id.textview_go_to_frameofmind_without_rating) as TextView
            textViewgotoframe?.setOnClickListener(this)
            textViewTaskTittle?.setText(
                Html.fromHtml(
                    """<b> 
 Task ${taskCount + 1}</b> of ${tasksArray.size}"""
                )
            )
            textViewTask?.setText(strBuilder)
        }

        currentTaskId = taskModel.task_id

        if (taskModel.opt_for_seq || taskModel.opt_for_task_completion) {
            setSEQAndTaskRating(taskModel.task_id.toString(), taskModel)
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun setSEQAndTaskRating(pos: String, taskModel: TaskModel){

        if (taskModel.opt_for_seq) {
            val ratingset: Boolean = checkRatingOfEachTaskSEQRating(pos)
            if (ratingset) {
                val getProgressValue: String = seqRatings[pos]!!
                val value = getProgressValue.toInt() * 100
                customSeekbar!!.progress = value
                Utils.GetProgressValue(customSeekbar!!, value,this)
            } else {
                customSeekbar!!.progress = 350
                customSeekbar!!.progressDrawable.setColorFilter(
                    resources.getColor(R.color.darker_grey),
                    PorterDuff.Mode.MULTIPLY
                )
                val drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .height(resources.getDimension(R.dimen.my_seekbar_height).toInt())
                    .width(resources.getDimension(R.dimen.my_seekbar_width).toInt())
                    .withBorder(resources.getDimension(R.dimen.my_seekbar_border).toInt())
                    .fontSize(resources.getDimension(R.dimen.my_seekbar_fontsize).toInt())
                    .endConfig()
                    .buildRound("", resources.getColor(R.color.darker_grey))
                customSeekbar!!.thumb = drawable
            }
        }

        if (taskModel.opt_for_task_completion) {
            val ratingset: Boolean = checkRatingOfEachTaskTaskComplete(pos)
            if (ratingset) {
                val getTaskCompeteData: String =
                    completeTaskRating[pos]!!
                if (getTaskCompeteData.equals("1", ignoreCase = true)) {
//                switchCompat.setChecked(true);
                    mChangeSb!!.isChecked = true
                    mChangeSb!!.setThumbColorRes(R.color.white)
                    mChangeSb!!.setTextColor(resources.getColor(R.color.yes_click))
                    mChangeSb!!.setBackDrawableRes(R.drawable.swich_background_green)
                } else {
//                switchCompat.setChecked(false);
                    mChangeSb!!.isChecked = false
                    mChangeSb!!.setThumbColorRes(R.color.white)
                    mChangeSb!!.setTextColor(resources.getColor(R.color.no_click))
                    mChangeSb!!.setBackDrawableRes(R.drawable.swich_background_red)
                }
            } else {
                inital_state_switch = false
                //            switchCompat.setChecked(false);
                mChangeSb!!.isChecked = false
                //            mChangeSb.setAnimationDuration(1000);
                mChangeSb!!.setThumbColorRes(R.color.textcolor_grey)
                mChangeSb!!.setTextColor(android.R.color.transparent)
                mChangeSb!!.setBackColorRes(R.color.darker_grey)
            }
        }
    }

    fun checkRatingOfEachTaskSEQRating(): Boolean {
        val position_key: String = currentTaskId.toString() + ""
        val ifExistKeySEQ_Rating: Boolean =
            seqRatings.containsKey(position_key)
        var testTasks: TaskModel? = null
        for (testTasks_ in tasksArray) {
            if (position_key.equals("" + testTasks_.task_id, ignoreCase = true)) {
                testTasks = testTasks_
                break
            }
        }
        return if (testTasks?.opt_for_seq!!) {
            ifExistKeySEQ_Rating
        } else {
            true
        }
    }

    fun checkRatingOfEachTaskTaskComplete(): Boolean {
        val position_key: String = currentTaskId.toString() + ""
        val ifExistCompleteTaskRating: Boolean =
            completeTaskRating.containsKey(position_key)
        var testTasks: TaskModel? = null
        for (testTasks_ in tasksArray) {
            if (position_key.equals("" + testTasks_.task_id, ignoreCase = true)) {
                testTasks = testTasks_
                break
            }
        }
        return if (testTasks?.opt_for_task_completion!!) {
            ifExistCompleteTaskRating
        } else {
            true
        }
    }

    private fun checkRatingOfEachTaskSEQRating(pos: String): Boolean {

        return if (seqRatings == null) {
            false
        } else {
            var ifExistKeySEQ_Rating: Boolean =
                seqRatings.containsKey(pos)
            var taskModel: TaskModel? = null
            for (taskModel_ in tasksArray) {
                if (pos.equals("" + taskModel_.task_id, ignoreCase = true)) {
                    taskModel = taskModel_
                    break
                }
            }
            if (taskModel?.opt_for_seq!!) {
                ifExistKeySEQ_Rating
            } else {
                true
            }
        }
    }

    private fun checkRatingOfEachTaskTaskComplete(pos: String): Boolean {

        return if (completeTaskRating == null) {
            false
        } else {
            val ifExistCompleteTaskRating: Boolean =
                completeTaskRating.containsKey(pos)
            var testTasks: TaskModel? = null
            for (testTasks_ in tasksArray) {
                if (pos.equals("" + testTasks_.task_id, ignoreCase = true)) {
                    testTasks = testTasks_
                    break
                }
            }
            if (testTasks?.opt_for_task_completion!!) {
                ifExistCompleteTaskRating
            } else {
                true
            }
        }
    }


    private fun chathead_longclick() {
        hideAllScreen()
        val param_remove =
            removeViewLayout?.getLayoutParams() as WindowManager.LayoutParams
        val x_cord_remove: Int = (szWindow.x - removeViewLayout?.getWidth()!!) / 2
        val y_cord_remove: Int = szWindow.y - (removeViewLayout?.getHeight()!! + Utils.getStatusBarHeight(this))
        param_remove.x = x_cord_remove
        param_remove.y = y_cord_remove
        windowManager?.updateViewLayout(removeViewLayout, param_remove)
    }

    fun chathead_click() {
        ll_dnd_screen?.visibility = View.GONE
        if (!isRecorderStarted)
        {
            if (!recordingPausedState)
            {
                isTabHere = true
                chatheadImg?.setVisibility(View.VISIBLE)

                if (availableTestModel?.opt_for_face_recording!!)
                    face_recording_texture_view?.setVisibility(View.VISIBLE)
                else
                    face_recording_texture_view?.setVisibility(View.GONE)

                chatheadImg?.setBackgroundResource(R.drawable.green_circle_img)
                uiImage?.setVisibility(View.VISIBLE)
                timerTextview?.setVisibility(View.VISIBLE)
                tapHere?.setVisibility(View.GONE)
            }
            if (ll_start_recording?.isShown()!!)
            {
                showTaskWindowLayout?.setVisibility(View.GONE)
                ll_start_recording?.setVisibility(View.GONE)
            }
            else {
                showTaskWindowLayout?.setVisibility(View.VISIBLE)
                ll_start_recording?.setVisibility(View.VISIBLE)
            }
        }
        else
        {
            val f = File(Utils.getOutputVideoFileNameForAmazonS3Bucket(""))
            whichScreenToDisplay(availableTestModel!!)
        }
    }

    fun hideAllScreen(){
        showTaskWindowLayout?.visibility = View.GONE
    }

    private fun resetPosition(x_cord_now: Int) {
        if (x_cord_now <= szWindow.x / 2) {
            moveToLeft(x_cord_now)
        } else {
            moveToRight(x_cord_now)
        }
    }


    private fun moveToLeft(x_cord_now: Int) {
        val x = szWindow.x - x_cord_now
        object : CountDownTimer(500, 5) {
            var mParams =
                chatHeadView?.layoutParams as WindowManager.LayoutParams

            override fun onTick(t: Long) {

            }

            override fun onFinish() {
                mParams.x = 0
                windowManager?.updateViewLayout(
                    chatHeadView,
                    mParams
                )
            }
        }.start()
    }

    // Todo StartTimer

    // Todo StartTimer
    private fun moveToRight(x_cord_now: Int) {
        object : CountDownTimer(500, 5) {
            var mParams =
                chatHeadView?.getLayoutParams() as WindowManager.LayoutParams

            override fun onTick(t: Long) {

            }

            override fun onFinish() {
                mParams.x = szWindow.x - chatHeadView?.getWidth()!!
                windowManager?.updateViewLayout(
                    chatHeadView,
                    mParams
                )
            }
        }.start()
    }

    fun startTimer() {
        if (!timerHasStarted) {
            try {
                countDownTimer = VideoRecordingCountDownTimer(
                    timerTextview!!,
                    startTime,
                    interval,
                    tvImpressionTimerTxt,
                    availableTestModel
                )
                countDownTimer!!.start()
                timerHasStarted = true
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun moveToDashBoard(){

        if(sharedPrefHelper.getGuestTester()){
            sharedPrefHelper.clearSharedPreference()
            val intent = Intent(this,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }else{
            val intent = Intent(this, TabActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun showTouchPointer(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Settings.System.putInt(
                this@NativeAppRecordingService.contentResolver,
                "show_touches",
                1
            )
        }
    }

    private fun hideTouchPointer(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Settings.System.putInt(
                this@NativeAppRecordingService.contentResolver,
                "show_touches",
                0
            )
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() : NativeAppRecordingService {

            // Return this instance of LocalService so clients can call public methods
            return this@NativeAppRecordingService
        }
    }

    inner class MessageReceiver : ResultReceiver(null) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            Log.e("onReceiveResult", "OnRecieve Result")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    fun pauseRecording() {
        if (isRecording) {

            // todo Recording is pause
            try {
                if (recorder != null) {
                    recorder?.stop()
                    recorder?.reset() // set state to idle
                    recorder?.release() // release resources back to the system
                    recorder = null

                    if (!fileNameTemp.equals("", ignoreCase = true)) {
                        val tempFile =
                            File(fileNameTemp)
                        var length = tempFile!!.length()
                        length = length / 1024
                        //Log.e("lenght of file", "onClick: " + length);
                        if (tempFile != null && tempFile.exists()) {
                            if (length > 0) {

                                allSubVideoFiles.add(
                                    fileNameTemp
                                )
                            } else {
                                tempFile.delete()
                            }
                        }
                    }
                }

                if (face_media_recorder != null) {
                    face_media_recorder?.stop()
                    face_media_recorder?.reset()
                    face_media_recorder?.release()
                    face_media_recorder = null
                    Log.e(
                        TAG,
                        "faceRecordingFileNameTemp " + faceRecordingFileNameTemp
                    )
                    addFaceVideoFilesToList()
                }
            } catch (stopException: java.lang.RuntimeException) {
                if (!fileNameTemp.equals("", ignoreCase = true)) {
                    //handle cleanup here
                    val tempFile =
                        File(fileNameTemp)
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete()
                    }
                }
                if (!faceRecordingFileNameTemp.equals(
                        "",
                        ignoreCase = true
                    )
                ) {
                    //handle cleanup here
                    val tempFile =
                        File(faceRecordingFileNameTemp)
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete()
                    }
                }
            }
        }
        try {
            isRecording = false
            recorder = MediaRecorder()
            recorder?.reset()
            recorder?.setVideoSource(VideoSource.SURFACE)
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            recorder?.setVideoSize(
                recordingInfo?.width!!,
                recordingInfo?.height!!
            )
            recorder?.setVideoEncodingBitRate(15 * 1000 * 100)
            /**change this for video quality  */
            recorder?.setVideoFrameRate(20)
            recorder?.setVideoFrameRate(recordingInfo?.frameRate!!)
            //String outputVideoFileName = fileFormat.format(new Date());
            val outputfile =
                Utils.getOutputVideoFileNameForAmazonS3Bucket(TryMyUIVideoTempFile)
            fileNameTemp = outputfile!!
            recorder?.setOutputFile(outputfile)
            recorder?.prepare()
            if (projection != null) {
                projection?.stop()
                projection =
                    mediaProjectionManager?.getMediaProjection(
                        resultCodeDataScreenCapture,
                        dataIntentScreenCapture
                    )
            }
            if (virtualDisplay != null) {
                virtualDisplay?.release()
                val surface: Surface = recorder?.getSurface()!!
                virtualDisplay  =
                    projection?.createVirtualDisplay(
                        DISPLAY_NAME,
                        recordingInfo?.width!!, recordingInfo?.height!!,
                        recordingInfo?.density!!, DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION,
                        surface, null, null
                    )
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "pauserecording ioe $e")
        } catch (ise: IllegalStateException) {
            Log.e(TAG, "IllegalStateException ise $ise")
        }

        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
            timerHasStarted = false
        }
        rl_transparent_overlay?.setVisibility(View.VISIBLE)
        ll_pauserecording?.setVisibility(View.INVISIBLE)
        recordingPausedState = true
        chatheadImg?.setVisibility(View.GONE)
        face_recording_texture_view?.setVisibility(View.GONE)
        tapHere?.setVisibility(View.GONE)
        bubble_paused_recording?.setVisibility(View.VISIBLE)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.button_dnd_continue ->
            {

                ll_dnd_screen?.setVisibility(View.GONE)

                hideAllScreen()
            }


            R.id.button_pause -> pauseRecording()

            R.id.textview_click_open_test_website ->
                if (isRecording)
                {
                    if (appPackageName.equals("", ignoreCase = true))
                    {
                        webSelection()
                    }
                    else
                    {
                        AppSelection()
                    }
                    hideAllScreen()
                }

            R.id.button_play ->
                resumeRecording()

            R.id.button_specialqualification ->
                if (whichScreenedAlreadyOpened?.equals(WireFrameOrPrototype, ignoreCase = true)) {
                    // need to check if impression test or no

                    if (doImpressionTest) {

                        //  impression test
                        completeImpressionTest = true
                        whichScreenedAlreadyOpened = ImpressionTestScreen
                        whichScreenToDisplay(availableTestModel!!)
                    }
                    else {
                        whichScreenedAlreadyOpened = FrameOfMindScreen
                        whichScreenToDisplay(availableTestModel!!)
                    }
                    addTaskTiming()
                } else {

                    if (availableTestModel?.is_kind_partial_site!!)
                    {
                        first_time_from_special_criteria_to_wireframe = true
                        isKindTest = true
                        whichScreenedAlreadyOpened = WireFrameOrPrototype
                        whichScreenToDisplay(availableTestModel!!)
                    }
                    else if (doImpressionTest)
                    {

                        //  impression test
                        completeImpressionTest = true
                        whichScreenedAlreadyOpened = ImpressionTestScreen
                        whichScreenToDisplay(availableTestModel!!)
                        addTaskTiming()
                    }
                    else
                    {
                        whichScreenedAlreadyOpened = FrameOfMindScreen
                        whichScreenToDisplay(availableTestModel!!)
                        addTaskTiming()
                    }
                }

            R.id.button_startrecording ->
            {

                if (Utils.isInternetAvailable(this)) {
                    startRecorderScreen()
                } else {
                    Utils.showInternetCheckToast(this)
                }
            }

            R.id.button_start_impression_test ->
            {
                startImpressionTest = true
                whichScreenedAlreadyOpened = ImpressionTestQuestionScreen
                hideAllScreen()
            }

            R.id.button_done_impression_test ->
            {
                val keyValue2: String =
                    taskTimingCounter.toString() + ""
                val ifExistKey2: Boolean =
                    taskTimings.containsKey(keyValue2)
                if (!ifExistKey2) { // if not exist
                    val time_value: String =
                        timerTextview?.getText().toString()
                    //we get time_value in min and secounds eg: 00:00
                    val separated =
                        time_value.split(":").toTypedArray()
                    val min_ = separated[0].toInt()
                    val sec_ = separated[1].toInt()
                    val minToSec = min_ * 60 // converting min into sec
                    val total_secounds = sec_ + minToSec // only sec total
                    // No such key
                    taskTimings.put(
                        keyValue2,
                        total_secounds.toString() + ""
                    )
                    taskTimingCounter++
                }
                doImpressionTest = false
                revereCountdownLayout?.setVisibility(View.GONE)
                fl_impression_test?.setVisibility(View.GONE)
                fl_impression_test_question?.setVisibility(View.GONE)
                if (recordingTimeUp) {
                    whichScreenedAlreadyOpened =
                        RecordingTimeUpScreen
                    whichScreenToDisplay(availableTestModel!!)
                } else {
                    whichScreenedAlreadyOpened =
                        FrameOfMindScreen
                    whichScreenToDisplay(availableTestModel!!)
                }
            }


            R.id.button_impression_test_again ->
            {
                tvImpressionTimerTxt?.setVisibility(View.INVISIBLE)
                revereCountdownLayout?.setVisibility(View.INVISIBLE)
                startImpressionTest = true
                whichScreenedAlreadyOpened =
                    ImpressionTestQuestionScreen
                hideAllScreen()
                revereCountdownLayout?.setVisibility(View.VISIBLE)
            }

            R.id.button_continue ->
            {
                Log.e(TAG, "button_continue")

                isButtonContinuePress = true
                val keyValue1: String =
                    taskTimingCounter.toString() + ""
                val ifExistKey1: Boolean =
                    taskTimings.containsKey(keyValue1)
                if (!ifExistKey1) { // if not exist
                    val time_value: String =

                        timerTextview?.getText().toString()
                    //we get time_value in min and secounds eg: 00:00
                    val separated =
                        time_value.split(":").toTypedArray()
                    val min_ = separated[0].toInt()
                    val sec_ = separated[1].toInt()
                    val minToSec = min_ * 60 // converting min into sec
                    val total_secounds = sec_ + minToSec // only sec total
                    // No such key
                    taskTimings.put(
                        keyValue1,
                        total_secounds.toString() + ""
                    )

                    goToFirstTaskTextSetting = true
                    whichScreenedAlreadyOpened =
                        ShowTaskScreen
                    whichScreenToDisplay(availableTestModel!!)
                    taskTimingCounter++
                }
            }

            R.id.buttonPrev ->
            {
                if (isRecording) {

                    tv_next_done?.setText(R.string.next)
                    if (taskCount > 0 && taskCount < tasksArray.size) {
                        taskCount--
                        setTaskToView(tasksArray.get(taskCount))
                        textViewTaskTittle!!.text = Html.fromHtml(
                            """<b>Task ${taskCount + 1}</b> of ${tasksArray.size}"""
                        )
                    }
                    if (taskCount < 1) {
                        ll_btn_previous
                        ll_btn_previous?.setEnabled(false)
                        ll_btn_previous?.setVisibility(View.INVISIBLE)
                    }
                }
                if (!doneFirstClick) {
                    taskTimingCounter--
                }
            }

            R.id.buttonNext ->
                if (doneTask)
                {
                    showConfirmationDialog()
                } else
                {
                    if (isRecording)
                    {
                        var checktaskRatingSEQRating = false
                        var checktaskRatingTaskComplete = false
                        checktaskRatingSEQRating = checkRatingOfEachTaskSEQRating()
                        checktaskRatingTaskComplete = checkRatingOfEachTaskTaskComplete()

                        /* TODO Multiple condtion for SEQ and task complete*/

                        //  TODO 1. conditon both are enable
                        Log.e(TAG, "checktaskRatingSEQRating $checktaskRatingSEQRating")
                        Log.e(TAG, "checktaskRatingTaskComplete $checktaskRatingTaskComplete")

                        if (checktaskRatingSEQRating && checktaskRatingTaskComplete) {
                            onNextButtonCLick()
                        } else {
                            // open alert
                            alertForSEQRatingNotSubmit(getString(R.string.seq_rate_and_task_complete_not_done))
                        }
                    }
                }

            R.id.textview_go_to_frameofmind_with_rating -> goToFrameOfMind()
            R.id.textview_go_to_frameofmind_without_rating -> goToFrameOfMind()

            R.id.textview_back_to_task_from_frame_of_mind ->
            {
                whichScreenedAlreadyOpened =
                    ShowTaskScreen
                whichScreenToDisplay(availableTestModel!!)
            }

            R.id.textview_back_to_task_from_finish_recording ->
            {
                doneTask = false
                whichScreenedAlreadyOpened =
                    ShowTaskScreen
                whichScreenToDisplay(availableTestModel!!)
            }

            R.id.button_finish_recording ->
                if (isRecording) {

                    if (Utils.isInternetAvailable(this)) {

                        if(availableTestModel?.opt_for_face_recording!!){
                            kotlin.run {  try {
                                stopFaceRecordingVideo()
                            } catch (e: java.lang.Exception) {
                                Log.e(
                                    TAG,
                                    "stopFaceRecordingVideo e $e"
                                )
                            } }
                        }


                        releaseRecorder()
                        hideAllScreen()
                        alertWindowForUploadVideo()

                    } else {
                        // todo :retry
                        Utils.showInternetCheckToast(this)
                    }
                }

            R.id.button_cancel_uploading ->
                showConfirmationOfTerminatingTest(-1, false)

            R.id.button_uploading ->
                if (isRecording)
                {
                    if (Utils.isInternetAvailable(this)) {
                        //   moveToUploadingVideoActivity();
                        (this@NativeAppRecordingService as Activity).runOnUiThread(Runnable {
                            try {
                                if(availableTestModel?.opt_for_face_recording!!)
                                stopFaceRecordingVideo()
                            } catch (e: java.lang.Exception) {
                                Log.e(
                                    TAG,
                                    "stopFaceRecordingVideo e $e"
                                )
                            }
                        })
                        releaseRecorder()
                        hideAllScreen()
                        alertWindowForUploadVideo()
                    } else {
                        // todo :retry
                        Utils.showInternetCheckToast(this)
                    }
                }
        }
    }

    private fun moveToUploadingVideoActivity() {
        isRecording = false
        val intent =
            Intent(this@NativeAppRecordingService, UploadingVideoActivity::class.java)

        intent.putExtra("taskTime", taskTimings)
        intent.putExtra("SEQ_TaskRating", seqRatings)
        intent.putExtra("TaskCompelete", completeTaskRating)
        intent.putExtra("List_of_files_to_merge", allSubVideoFiles as Serializable?)
        intent.putExtra("face_recording_files_list", allFaceRecordingVideoFiles as Serializable?)
        intent.putExtra("availableTestConstants", availableTestModel)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        showTaskWindowLayout?.setVisibility(View.GONE)

    }


    private fun startRecorderScreen(){

        val intent = Intent(this@NativeAppRecordingService, StartRecorderActivity::class.java)

        Log.e(TAG,"availableTestModel startRecorderScreen "+availableTestModel)

        intent.putExtra("availableTestConstants", availableTestModel)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    private fun resumeRecording(){
        try {
            face_recording_texture_view?.setSurfaceTextureListener(
                surfaceTextureListener
            )
            startFaceRecordingVideo()
            recorder?.start()
            isRecording = true
            countDownTimer = VideoRecordingCountDownTimer(
                timerTextview!!,
                milisecRemain,
                interval,
                availableTestModel
            )
            countDownTimer?.start()
            timerHasStarted = true
            ll_pauserecording?.setVisibility(View.VISIBLE)
            rl_transparent_overlay?.setVisibility(View.GONE)
            recordingPausedState = false
            bubble_paused_recording?.setVisibility(View.GONE)
            tapHere?.setVisibility(View.GONE)
            chatheadImg?.setVisibility(View.VISIBLE)
            if (availableTestModel?.opt_for_face_recording!!) face_recording_texture_view?.setVisibility(
                View.VISIBLE
            ) else face_recording_texture_view?.setVisibility(View.GONE)
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
            // postFeedback(e.toString() + "")
            Log.e(TAG, "ResumeRecording ise $e")
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "ResumeRecording e $e")
        }

    }


    private fun alertWindowForUploadVideo(){
        windowManager?.removeView(showTaskWindowLayout)
        paramsShowTaskWindow?.screenOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        windowManager?.addView(
            showTaskWindowLayout,
            paramsShowTaskWindow
        )

        val dialog = Dialog(this@NativeAppRecordingService)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before  set layout-sw480dp

        dialog.setContentView(R.layout.upload_video_prompt_dialog)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER

        lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        dialog.window.attributes = lp

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            dialog.window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }


        val tv_cancel = dialog.findViewById<View>(R.id.tv_cancel) as TextView
        val tv_yes = dialog.findViewById<View>(R.id.tv_yes) as TextView

        val rl_video_upload_prompt =
            dialog.findViewById<View>(R.id.rl_video_upload_prompt) as RelativeLayout

        tv_cancel.setOnClickListener {
            Log.e(TAG, "tv_cancel pressed")
            //  rl_video_upload_prompt.setVisibility(View.GONE);
            //rl_cancel_confirm.setVisibility(View.VISIBLE);
            dialog.dismiss()
            showConfirmationOnCancelClick()
        }

        tv_yes.setOnClickListener { /* stopSelf();
                        dialog.dismiss();*/
            dialog.dismiss()
            StopService()
            moveToUploadingVideoActivity()
        }

        dialog.show()

    }


    private fun showConfirmationOnCancelClick() {
        val dialog = Dialog(this@NativeAppRecordingService)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before  set layout-sw480dp
        dialog.setContentView(R.layout.upload_video_cancel_confirm_prompt)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window.attributes = lp
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            dialog.window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }
        val tv_no = dialog.findViewById<View>(R.id.tv_no) as TextView
        val tv_yes = dialog.findViewById<View>(R.id.tv_yes) as TextView
        tv_no.setOnClickListener {
            dialog.dismiss()
            alertWindowForUploadVideo()
        }
        tv_yes.setOnClickListener {
            dialog.dismiss()
            StopService()
            stopSelf()
        }
        dialog.show()
    }


    private fun alertForSEQRatingNotSubmit(alertMessage : String){
        val inflater =
            this@NativeAppRecordingService.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val toastView: View = inflater.inflate(R.layout.custom_toast, null)


        val toast = Toast(this@NativeAppRecordingService)
        val tv = toastView.findViewById<View>(R.id.tv_custom_toast_text) as TextView
        tv.text = alertMessage

        // Set custom view in toast.

        // Set custom view in toast.
        toast.view = toastView
        toast.duration = Toast.LENGTH_LONG
        toast.setGravity(Gravity.CENTER, 0, 800)
        toast.show()
    }

    private fun showConfirmationDialog() {

        /* check if recording time is over or not if yes dont allow to go back so open linerlayoutrecordingtimeup*/
        if (recordingTimeUp) {
            whichScreenedAlreadyOpened =
                RecordingTimeUpScreen
            whichScreenToDisplay(availableTestModel!!)
        } else {
            // this condition add for done recording
            whichScreenedAlreadyOpened =
                FinishRecordingScreen
            whichScreenToDisplay(availableTestModel!!)
        }

    }

    private fun addTaskTiming(){
        val keyValue: String = taskTimingCounter.toString() + ""

        val ifExistKey: Boolean =
            taskTimings.containsKey(keyValue)

        if (!ifExistKey) { // if not exist
            val time_value: String =
                timerTextview?.getText().toString()
            //we get time_value in min and secounds eg: 00:00
            val separated = time_value.split(":").toTypedArray()
            val min_ = separated[0].toInt()
            val sec_ = separated[1].toInt()
            val minToSec = min_ * 60 // converting min into sec
            val total_secounds = sec_ + minToSec // only sec total
            // No such key
            taskTimings.put(keyValue, total_secounds.toString() + "")
            taskTimingCounter++
        }
    }

    @TargetApi(22)
    private fun webSelection() {
        availableTestModel?.url
        if (availableTestModel?.url != null) {
            if (!availableTestModel?.url?.startsWith("http")!! && !availableTestModel?.url?.startsWith(
                    "https"
                )!!
            ) {
                availableTestModel?.url = "http" + availableTestModel?.url
            }
            val intentBrowser = Intent(Intent.ACTION_VIEW)
            intentBrowser.data = Uri.parse(availableTestModel?.url)
            intentBrowser.putExtra(
                Browser.EXTRA_APPLICATION_ID,
                this@NativeAppRecordingService.packageName
            )
            val receiver =
                Intent(this@NativeAppRecordingService, BroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this@NativeAppRecordingService,
                0,
                receiver,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val chooserIntent = Intent.createChooser(
                intentBrowser,
                "Choose browser",
                pendingIntent.intentSender
            )
            chooserIntent.putExtra(
                "SERVICE CALLBACK", MessageReceiver()
            )
            chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(chooserIntent)
        }
    }

    private fun AppSelection() {
        if (availableTestModel?.native_app_url.equals("", ignoreCase = true)) {
            val equaltosign: Int = availableTestModel?.url?.indexOf('=')!!
            if (equaltosign == -1) {
                // No equal sign - check if apk file or direct package name
                if (availableTestModel?.url?.toLowerCase()?.contains(".apk")!!) {
                    val http_text: Int = availableTestModel?.url?.lastIndexOf('/')!!
                    val TestAppAPKFile: String =
                        availableTestModel?.url?.substring(http_text + 1)!!
                    val packagename = CheckAppAreadyInstall(TestAppAPKFile) + ""
                    if (packagename.equals("", ignoreCase = true)) {
                        ShowConfirmationOfAPK()
                    } else {
                        val intentApp =
                            packageManager.getLaunchIntentForPackage(packagename)
                        if (intentApp != null) {
                            val pack =
                                this@NativeAppRecordingService.packageManager
                            val app = pack.getLaunchIntentForPackage(packagename)
                            startActivity(app)
                        } else {
                            ShowConfirmationOfAPK()
                        }
                    }
                } else {
                    val http_text: Int = availableTestModel?.url?.indexOf('/')!!
                    val packagename: String =
                        availableTestModel?.url?.substring(http_text + 2)!!
                    val intentApp =
                        packageManager.getLaunchIntentForPackage(packagename)
                    if (intentApp != null) {
                        val pack =
                            this@NativeAppRecordingService.packageManager
                        val app = pack.getLaunchIntentForPackage(packagename)
                        startActivity(app)
                    } else {
                        onGoToAnotherInAppStore(packagename)
                    }
                }
            } else {
                val packagename: String =
                    availableTestModel?.url?.substring(equaltosign + 1)!!
                val intentApp = packageManager.getLaunchIntentForPackage(packagename)
                if (intentApp != null) {
                    val pack = this@NativeAppRecordingService.packageManager
                    val app = pack.getLaunchIntentForPackage(packagename)
                    startActivity(app)
                } else {
                    onGoToAnotherInAppStore(packagename)
                }
            }
        } else {
            val pm = packageManager
            val launchIntent =
                pm.getLaunchIntentForPackage(appPackageName)
            startActivity(launchIntent)
        }
    }

    fun onGoToAnotherInAppStore(appPackageName: String) {
        var intent: Intent
        try {
            intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse("market://details?id=$appPackageName")
            startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
            startActivity(intent)
        }
    }

    /* todo this method to get package name of apk file*/
    fun CheckAppAreadyInstall(TestAPK: String): String {
        val pm = packageManager
        val fullPath =
            Environment.getExternalStorageDirectory().toString() + "/" + TestAPK
        val info = pm.getPackageArchiveInfo(fullPath, 0)
        val packageName: String
        packageName = if (info == null) "" else info.packageName
        return packageName
    }

    /* todo check apk is install or not and show the confirmation*/
    fun ShowConfirmationOfAPK() {
        val http_text: Int = availableTestModel?.url?.lastIndexOf('/')!!
        testAPKFile = availableTestModel?.url?.substring(http_text + 1)!!
        val dialogBuilder =
            AlertDialog.Builder(this, R.style.AppTheme_MaterialDialogTheme)
        dialogBuilder.setTitle("Download apk")
        dialogBuilder.setMessage("You will be downloading client's native application $testAPKFile. Once download completes, install apk on to your device.")
        dialogBuilder.setPositiveButton(
            R.string.yes
        ) { dialog, which ->
            dialog.dismiss()
            DownloadApkFile(this,testAPKFile)
                .execute()
        }
        dialogBuilder.show()
    }


    private fun StopService() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Settings.System.putInt(
                this@NativeAppRecordingService.contentResolver,
                "show_touches",
                0
            )
        }
        // todo Recording cancel so reset all view in prefrence and recorder also stop
        isRecorderStarted = false
        //   DoneFirstClick = false;
        Log.e(TAG, "stopService called")
        val service =
            Intent(this@NativeAppRecordingService, NativeAppRecordingService::class.java)
        service.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        stopService(service)
        makeAppPortrait()
    }


    private fun goToFrameOfMind(){
        if (isRecording) {
            whichScreenedAlreadyOpened =
                FrameOfMindScreen
            whichScreenToDisplay(availableTestModel!!)
            if (!appPackageName.equals("", ignoreCase = true)) {
                val content =
                    SpannableString(getString(R.string.link_to_open_app).toString() + "")
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                tv_open_test_website?.setText(content)
                tv_open_test_website_popup?.setText(content)
            } else {
                val content = SpannableString(getString(R.string.link_to_open_web))
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                tv_open_test_website?.setText(content)
                tv_open_test_website_popup?.setText(content)
            }
        }
    }

    private fun onNextButtonCLick() {
        ll_btn_previous
        ll_btn_previous?.setVisibility(View.VISIBLE)
        ll_btn_previous?.setEnabled(true)

        Log.e(TAG, "DoneFirstClick " +doneFirstClick)

        if (!doneFirstClick)
        {
            Log.e(TAG, "TaskTimingCounter " + taskTimingCounter)

            val keyValue: String = taskTimingCounter.toString() + ""
            Log.e(TAG, "keyValue $keyValue")

            val ifExistKey: Boolean = taskTimings?.containsKey(keyValue)

            if (!ifExistKey) { // if not exist

                val time_value: String = timerTextview?.getText().toString()
                //we get time_value in min and secounds eg: 00:00
                val separated = time_value.split(":").toTypedArray()
                val min_ = separated[0].toInt()
                val sec_ = separated[1].toInt()
                val minToSec = min_ * 60 // converting min into sec
                val total_secounds = sec_ + minToSec // only sec total
                // No such key
                taskTimings?.put(keyValue, total_secounds.toString() + "")
            }
            taskTimingCounter++
        }
        Log.e(TAG, "task_count " + taskCount)
        Log.e(TAG, "testTasksArray size " + tasksArray?.size)

        if (taskCount == tasksArray?.size - 1) {
            doneFirstClick = true
            doneTask = true
            showConfirmationDialog()
        }
        Log.e(TAG, "task_count " + taskCount)
        Log.e(TAG, "testTasksArray size " + tasksArray?.size)

        if (taskCount < tasksArray?.size - 1) {

            taskCount++

            Log.e(TAG, "task count " + tasksArray?.get(taskCount))
            setTaskToView(tasksArray?.get(taskCount))

            // textViewTaskTittle.setText(Html.fromHtml("<b> \n Task " + (taskCount + 1) + "</b> of " + tasksArray?.size()));
            //textViewTask.setText(tasksArrayList.get(taskNumber).getTask());
            val postion_key: String = currentTaskId.toString() + "" //taskCount + 1+"";

            if (taskCount == tasksArray?.size - 1)
            {
                tv_next_done?.setText(R.string.done)
            }
        }
        else
        {
            Log.e(TAG, "something wrong")
        }
    }

    fun whichScreenToDisplay(availableTestModel: AvailableTestModel) {
        Log.e(TAG,"whichScreenToDisplay availableTestModel "+availableTestModel)

        if (!recordingTimeUp)
        {
            if (!whichScreenedAlreadyOpened?.equals("", ignoreCase = true))
            {
                Log.e(TAG, "WhichScreenAlreadyOpend " + whichScreenedAlreadyOpened)

                when (whichScreenedAlreadyOpened)
                {
                    "StartRecorderScreen" ->
                        if (ll_start_recording?.isShown!!)
                        {
                            Log.e(TAG,"yes start recording is shown")
                            showTaskWindowLayout?.setVisibility(View.GONE)
                            ll_start_recording?.setVisibility(View.GONE)
                        } else {
                            Log.e(TAG,"yes start recording is not shown")
                            showTaskWindowLayout?.setVisibility(View.VISIBLE)
                            ll_start_recording?.setVisibility(View.VISIBLE)
                        }

                    "FrameOfMindScreen" ->
                    {
                        ll_spec_qual?.setVisibility(View.GONE)
                        ll_start_recording?.setVisibility(View.GONE)
                        linearLayoutShowTask?.setVisibility(View.GONE)

                        if (ll_frame_mind?.isShown()!!) {
                            showTaskWindowLayout?.setVisibility(View.GONE)
                            ll_frame_mind?.setVisibility(View.GONE)
                            fl_start_recording?.setVisibility(View.GONE)
                        }
                        else
                        {
                            if (isButtonContinuePress)
                            {
                                showTaskWindowLayout?.setVisibility(View.VISIBLE)
                                fl_start_recording?.setVisibility(View.VISIBLE)
                                ll_pause_tab_bar?.setVisibility(View.VISIBLE)
                                ll_frame_mind?.setVisibility(View.VISIBLE)
                                tv_read_frame_mind?.setVisibility(View.GONE)
                                tv_back_totask_from_frame?.setVisibility(View.VISIBLE)
                                btn_continue?.setVisibility(View.GONE)
                                ll_btn_next_previous?.setVisibility(View.GONE)
                            }
                            else
                            {
                                showTaskWindowLayout?.setVisibility(View.VISIBLE)
                                fl_start_recording?.setVisibility(View.VISIBLE)
                                ll_pause_tab_bar?.setVisibility(View.VISIBLE)
                                ll_frame_mind?.setVisibility(View.VISIBLE)
                                btn_continue?.setVisibility(View.VISIBLE)
                                ll_btn_next_previous?.setVisibility(View.GONE)
                            }
                        }
                    }
                    "SpecialQualificationScreen" -> {
                        ll_start_recording?.setVisibility(View.GONE)

                        Log.e(TAG,"availableTestModel "+availableTestModel)
                        Log.e(TAG,"is_kind_partial_site "+availableTestModel?.is_kind_partial_site)

                        if (availableTestModel?.is_kind_partial_site!!)
                        {
                            btn_continue?.setText(R.string.continu)
                        }
                        else if (availableTestModel?.do_impression_test!!)
                        {
                            btn_qualification?.setText(R.string.continue_with_impression_test)
                        }
                        else
                        {
                            btn_qualification?.setText(R.string.contiune_with_frame_of_mind)
                        }

                        tv_spec_criteria?.setText(Html.fromHtml(availableTestModel?.specialQalification))

                        tv_spec_criteria_sub?.setVisibility(View.VISIBLE)
                        tv_spec_criteria_title?.setText(R.string.special_criteria_title)

                        if (ll_spec_qual?.isShown!!)
                        {
                            Log.e(TAG, "linearlayout_special_qualification shown")
                            showTaskWindowLayout?.setVisibility(View.GONE)
                            fl_start_recording?.setVisibility(View.GONE)
                            ll_spec_qual?.setVisibility(View.GONE)
                        }
                        else
                        {
                            Log.e(TAG, "linearlayout_special_qualification not shown")
                            showTaskWindowLayout?.setVisibility(View.VISIBLE)
                            fl_start_recording?.setVisibility(View.VISIBLE)
                            ll_spec_qual?.setVisibility(View.VISIBLE)
                        }
                    }
                    "ImpressionTestScreen" -> {
                        ll_start_recording?.setVisibility(View.GONE)
                        ll_spec_qual?.setVisibility(View.GONE)

                        if (fl_impression_test?.isShown()!!) {
                            Log.e(TAG, "framelayoutImpressionTest shown")
                            showTaskWindowLayout?.setVisibility(View.GONE)
                            fl_start_recording?.setVisibility(View.GONE)
                            fl_impression_test?.setVisibility(View.GONE)
                        }
                        else
                        {
                            Log.e(TAG, "framelayoutImpressionTest not shown")

                            showTaskWindowLayout?.setVisibility(View.VISIBLE)
                            fl_start_recording?.setVisibility(View.VISIBLE)
                            fl_impression_test?.setVisibility(View.VISIBLE)
                        }
                    }
                    "ImpressionTestQuestionScreen" -> {
                        ll_start_recording?.setVisibility(View.GONE)
                        fl_impression_test?.setVisibility(View.GONE)
                        if (fl_impression_test_question?.isShown()!!) {
                            showTaskWindowLayout?.setVisibility(View.GONE)
                            fl_start_recording?.setVisibility(View.GONE)
                            fl_impression_test_question?.setVisibility(View.GONE)
                        }
                        else
                        {
                            tvImpressionTimerTxt?.setVisibility(View.INVISIBLE)
                            showTaskWindowLayout?.setVisibility(View.VISIBLE)
                            fl_start_recording?.setVisibility(View.VISIBLE)
                            fl_impression_test_question?.setVisibility(View.VISIBLE)
                        }
                    }
                    "ShowTaskScreen" -> {
                        ll_spec_qual?.setVisibility(View.GONE)
                        ll_start_recording?.setVisibility(View.GONE)
                        ll_frame_mind?.setVisibility(View.GONE)
                        ll_finish_recording?.setVisibility(View.GONE)

                        if (linearLayoutShowTask?.isShown()!!)
                        {
                            showTaskWindowLayout?.setVisibility(View.GONE)
                            fl_start_recording?.setVisibility(View.GONE)
                            ll_pause_tab_bar?.setVisibility(View.GONE)
                            linearLayoutShowTask?.setVisibility(View.GONE)
                        }
                        else {
                            showTaskWindowLayout?.setVisibility(View.VISIBLE)
                            fl_start_recording?.setVisibility(View.VISIBLE)
                            ll_pause_tab_bar?.setVisibility(View.VISIBLE)
                            linearLayoutShowTask?.setVisibility(View.VISIBLE)
                            btn_continue?.setVisibility(View.GONE)
                            ll_btn_next_previous?.setVisibility(View.VISIBLE)
                            ll_btn_next?.setVisibility(View.VISIBLE)

                            if (taskCount < 1)
                            {
                                ll_btn_previous?.setEnabled(false)
                                ll_btn_previous?.setVisibility(View.INVISIBLE)
                            }
                            else {
                                ll_btn_previous?.setVisibility(View.VISIBLE)
                            }
                        }
                    }
                    "FinishRecordingScreen" -> {
                        ll_spec_qual?.setVisibility(
                            View.GONE
                        )
                        ll_start_recording?.setVisibility(View.GONE)
                        ll_frame_mind?.setVisibility(View.GONE)
                        linearLayoutShowTask?.setVisibility(View.GONE)
                        if (ll_finish_recording?.isShown()!!) {
                            showTaskWindowLayout?.setVisibility(View.GONE)
                            fl_start_recording?.setVisibility(
                                View.GONE
                            )
                            ll_finish_recording?.setVisibility(
                                View.GONE
                            )
                        } else {
                            showTaskWindowLayout?.setVisibility(View.VISIBLE)
                            fl_start_recording?.setVisibility(
                                View.VISIBLE
                            )
                            ll_finish_recording?.setVisibility(
                                View.VISIBLE
                            )
                            ll_pause_tab_bar?.setVisibility(
                                View.GONE
                            )
                            btn_continue?.setVisibility(View.GONE)
                            ll_btn_next_previous?.setVisibility(
                                View.GONE
                            )
                        }
                    }
                    "RecordingTimeUpScreen" -> {
                        ll_spec_qual?.setVisibility(
                            View.GONE
                        )
                        ll_start_recording?.setVisibility(View.GONE)
                        ll_frame_mind?.setVisibility(View.GONE)
                        linearLayoutShowTask?.setVisibility(View.GONE)
                        ll_finish_recording?.setVisibility(
                            View.GONE
                        )
                        fl_start_recording?.setVisibility(
                            View.GONE
                        )
                        fl_impression_test_question?.setVisibility(
                            View.GONE
                        )
                        fl_impression_test?.setVisibility(View.GONE)
                        if (ll_recording_timeup?.isShown()!!) {
                            showTaskWindowLayout?.setVisibility(View.GONE)
                            ll_recording_timeup?.setVisibility(
                                View.GONE
                            )
                        } else {
                            showTaskWindowLayout?.setVisibility(View.VISIBLE)
                            ll_recording_timeup?.setVisibility(
                                View.VISIBLE
                            )
                        }
                    }
                    "WireFrameOrPrototype" -> {
                        ll_start_recording?.setVisibility(View.GONE)
                        tv_spec_criteria?.setText(
                            Html.fromHtml(
                                availableTestModel?.is_kind_partial_site_text
                            )
                        )
                        btn_qualification?.setText(R.string.wireframe_button_text)
                        tv_spec_criteria_sub?.setVisibility(
                            View.INVISIBLE
                        )
                        tv_spec_criteria_title?.setText(R.string.please_note)
                        if (first_time_from_special_criteria_to_wireframe) {
                            first_time_from_special_criteria_to_wireframe =
                                false
                            showTaskWindowLayout?.setVisibility(View.VISIBLE)
                            fl_start_recording?.setVisibility(
                                View.VISIBLE
                            )
                            ll_spec_qual?.setVisibility(
                                View.VISIBLE
                            )
                        } else {
                            if (ll_spec_qual?.isShown()!!) {
                                showTaskWindowLayout?.setVisibility(View.GONE)
                                fl_start_recording?.setVisibility(
                                    View.GONE
                                )
                                ll_spec_qual?.setVisibility(
                                    View.GONE
                                )
                            } else {
                                showTaskWindowLayout?.setVisibility(View.VISIBLE)
                                fl_start_recording?.setVisibility(
                                    View.VISIBLE
                                )
                                ll_spec_qual?.setVisibility(
                                    View.VISIBLE
                                )
                            }
                        }
                    }
                }
            }
        } else {
            ll_spec_qual?.setVisibility(View.GONE)
            ll_start_recording?.setVisibility(View.GONE)
            ll_frame_mind?.setVisibility(View.GONE)
            linearLayoutShowTask?.setVisibility(View.GONE)
            ll_finish_recording?.setVisibility(View.GONE)
            fl_start_recording?.setVisibility(View.GONE)
            fl_impression_test_question?.setVisibility(View.GONE)
            fl_impression_test?.setVisibility(View.GONE)
            if (ll_recording_timeup?.isShown()!!) {
                showTaskWindowLayout?.setVisibility(View.GONE)
                ll_recording_timeup?.setVisibility(View.GONE)
            } else {
                showTaskWindowLayout?.setVisibility(View.VISIBLE)
                ll_recording_timeup?.setVisibility(View.VISIBLE)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager?.getDefaultDisplay()?.getSize(szWindow)
        } else {
            val w: Int =windowManager?.getDefaultDisplay()?.getWidth()!!
            val h: Int =windowManager?.getDefaultDisplay()?.getHeight()!!
            szWindow[w] = h
        }

        val layoutParams =
            chatHeadView?.getLayoutParams() as WindowManager.LayoutParams

        if (newConfig!!.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (layoutParams.y + (chatHeadView?.getHeight()!! + Utils.getStatusBarHeight(this)) > szWindow.y) {
                layoutParams.y =
                    szWindow.y - (chatHeadView?.getHeight()!! + Utils.getStatusBarHeight(this))
                windowManager?.updateViewLayout(
                    chatHeadView,
                    layoutParams
                )
            }
            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x)
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x)
            }
        }
    }

    private fun makeLinkClickable(strBuilder: SpannableStringBuilder, span: URLSpan)
    {
        val start = strBuilder.getSpanStart(span)
        Log.e(TAG,"start "+start)
        val end = strBuilder.getSpanEnd(span)
        Log.e(TAG,"end "+end)
        val flags = strBuilder.getSpanFlags(span)
        Log.e(TAG,"flags "+flags)
        val clickable: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Do something with span.getURL() to handle the link click...
                //Log.e("*********************** my click *************", " " + span.getURL().toString());
                val myurl = span.url.toString()
                Log.e(TAG,"myurl "+myurl)
                val intentBrowser = Intent(Intent.ACTION_VIEW)
                intentBrowser.data = Uri.parse(myurl)
                intentBrowser.putExtra(
                    Browser.EXTRA_APPLICATION_ID, this@NativeAppRecordingService.packageName)
                intentBrowser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentBrowser)
                whichScreenToDisplay(availableTestModel!!)
            }
        }
        strBuilder.setSpan(clickable, start, end, flags)
        strBuilder.removeSpan(span)
    }

    private fun showRemoveDialog(x_cord_now: Int){

        if(Utils.isInternetAvailable(this))
        {
            showConfirmationOfTerminatingTest(x_cord_now,true)
        }
        else{
            resetPosition(x_cord_now)
            Utils.showInternetCheckToast(this)
        }
    }

    private fun showConfirmationOfTerminatingTest(x_cord_now: Int,bubble_reset_value : Boolean){

        val dialogBuilder = AlertDialog.Builder(
            this@NativeAppRecordingService,
            R.style.AppTheme_MaterialDialogTheme
        )

        dialogBuilder.setTitle("Are you sure you want to leave this test?")
        dialogBuilder.setMessage("This will end your test and you will be redirected to your TryMyUI dashboard")
        dialogBuilder.setNegativeButton(
            R.string.cancel
        ) { dialog, which ->
            dialog.dismiss()
            if (bubble_reset_value) {
                resetPosition(x_cord_now)
            }
        }


        dialogBuilder.setPositiveButton(R.string.yes) { dialog, which ->

            dialog.dismiss()
            yesClickOfTerminatingTest()

            }


        var show_confirmation_dialog = dialogBuilder.create()
        val dialogWindow: Window = show_confirmation_dialog.getWindow()
        val dialogWindowAttributes = dialogWindow.attributes


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogWindowAttributes)
        lp.width =
            WindowManager.LayoutParams.MATCH_PARENT

        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = lp


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialogWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }

        dialogWindowAttributes.windowAnimations = R.style.DialogAnimation

        show_confirmation_dialog.show()

    }

    private fun yesClickOfTerminatingTest(){
        if(!Utils.isInternetAvailable(this))
        {
            Utils.showInternetCheckToast(this)
        }
        else{
            YesNoAlertDialog.dismissYesNoDialogue()

            Utils.DeleteFaceRecordingFolder()
            Utils.DeleteFolderOfRecording()

            stopService(Intent(this,NativeAppRecordingService::class.java))
            stopSelf()

            hideTouchPointer()

            makeAppPortrait()

            releaseRecorder()

            terminatingTest()
        }
    }


    fun releaseRecorder(){
        try {
            if (recorder != null) {
                recorder?.stop()
                recorder?.reset()
                recorder?.release()
                recorder = null
            }
            if (face_media_recorder != null) {
                face_media_recorder?.stop()
                face_media_recorder?.reset()
                face_media_recorder?.reset()
                face_media_recorder = null
            }
            if (timerHasStarted) {
                countDownTimer?.cancel()
                countDownTimer = null
            } else {
                countDownTimer = null
            }
            if (projection != null) {
                projection?.stop()
                projection = null
            }
            if (virtualDisplay != null) {
                virtualDisplay?.release()
                virtualDisplay = null
            }
            if (!fileNameTemp.equals("", ignoreCase = true)) {
                val tempFile = File(fileNameTemp)
                var length = tempFile!!.length()
                length = length / 1024
                Log.e("lenght of file", "onClick: $length")
                if (tempFile != null && tempFile.exists()) {
                    if (length > 0) {
                        allSubVideoFiles.add(fileNameTemp)
                    } else {
                        tempFile.delete()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            if (countDownTimer != null) {
                countDownTimer?.cancel()
                countDownTimer = null
            } else {
                countDownTimer = null
            }
            if (!fileNameTemp.equals("", ignoreCase = true)) {
                val tempFile = File(fileNameTemp)
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete()
                }
            }
            if (!faceRecordingFileNameTemp.equals(
                    "",
                    ignoreCase = true
                )
            ) {
                //handle cleanup here
                val tempFile =
                    File(faceRecordingFileNameTemp)
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete()
                }
            }
        }
    }

    private fun terminatingTest(){

        if(Utils.isInternetAvailable(this)){
            var terminateTest = TerminateTestPresenter(this)
            terminateTest.terminate(this,sharedPrefHelper)
        }
        else{
            Utils.showInternetCheckToast(this)
        }

    }



    private fun makeAppPortrait(){
        paramsShowTaskWindow?.screenOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        windowManager?.removeView(revereCountdownLayout)
        windowManager?.removeView(removeViewLayout)
        windowManager?.removeView(chatHeadView)
        windowManager?.removeView(showTaskWindowLayout)
    }

    inner class VideoRecordingCountDownTimer : CountDownTimer {
        var isNativeApp = false
        var timeElapsed: Long = 0
        var mCountText: TextView
        var impression_test_timer: TextView? = null
        var availableTestModel1 : AvailableTestModel? = null


        constructor(timerTextview: TextView, startTime: Long, interval: Long, impression_test_counter_timer_text: TextView?,availableTestModel: AvailableTestModel?) : super(startTime, interval)
        {
            mStartTime = startTime
            mCountText = timerTextview
            impression_test_timer = impression_test_counter_timer_text
            isNativeApp = true
            this.availableTestModel1 = availableTestModel
        }

        constructor(timerTextview: TextView, milisecRemain: Long, interval: Long,availableTestModel: AvailableTestModel?) : super(milisecRemain, interval) {
            mCountText = timerTextview
            this.availableTestModel1 = availableTestModel
            isNativeApp = true
        }

        override fun onFinish() {
            if (isNativeApp) {
                doneTask = true
                recordingTimeUp = true
                goToFirstTaskTextSetting = true
                specialQualification = true
                startImpressionTest = false
                releaseRecorder()
                whichScreenedAlreadyOpened = RecordingTimeUpScreen
                whichScreenToDisplay(availableTestModel1!!)
            }
        }

        override fun onTick(millisUntilFinished: Long) {
            //here you can have your logic to set text to edittext
            timeElapsed = mStartTime - millisUntilFinished

         /*   Log.e(TAG,"timeElapsed "+timeElapsed)
            Log.e(TAG,"mStartTime "+mStartTime)
            Log.e(TAG,"mStartTime - millisUntilFinished "+(mStartTime - millisUntilFinished))*/

            val totalSecs = timeElapsed / 1000
            val minutes = totalSecs % 3600 / 60
            val seconds = totalSecs % 60
            if (startImpressionTest) {
                ++ImpressionTestTimeRemaing
                if (ImpressionTestTimeRemaing === 12) {
                    blink(3)
                }
            }
            val df = DecimalFormat("00")
            mCountText.text = df.format(minutes) + ":" + df.format(seconds)
            milisecRemain = millisUntilFinished
        }

        private fun blink(value: Int) {
            val handler = Handler()
            Thread(Runnable {
                val timeToBlink = 1000
                try
                {
                    Thread.sleep(timeToBlink.toLong())
                }
                catch (e: java.lang.Exception)
                { }
                handler.post {
                    if (value == 0)
                    {
                        startImpressionTest = false

                        if (impression_test_timer != null)
                            impression_test_timer!!.visibility = View.GONE
                        ImpressionTestTimeRemaing = 0
                        whichScreenedAlreadyOpened = ImpressionTestQuestionScreen
                        Log.e(TAG,"availableTestModel1 blink "+availableTestModel)
                        whichScreenToDisplay(availableTestModel!!)
                    }
                    else
                    {
                        if (impression_test_timer != null)
                        {
                            impression_test_timer!!.visibility = View.VISIBLE
                            impression_test_timer!!.text = value.toString() + ""
                        }
                        val temvalue = value - 1
                        blink(temvalue)
                    }
                }
            }).start()
        }

    }


    var surfaceTextureListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            //  Log.e(TAG,"onSurfaceTextureAvailable");
            openCamera(height, width)
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            //    Log.e(TAG,"onSurfaceTextureSizeChanged");
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            //   Log.e(TAG,"onSurfaceTextureDestroyed");
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            //       Log.e(TAG,"onSurfaceTextureUpdated");
        }
    }


    private val cameraStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Log.e(TAG, "onOpened")
                cameraDevice = camera
                createCameraPreview()
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.e(TAG, "onDisconnected")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.e(TAG, "onError")
            }
        }


    private fun createCameraPreview() {
        try {

            val surfaceTexture: SurfaceTexture = face_recording_texture_view?.getSurfaceTexture()!!

            surfaceTexture.setDefaultBufferSize(100, 100)
            val surface = Surface(surfaceTexture)

            captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(surface)

            cameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(session: CameraCaptureSession)
                    {
                        Log.e(TAG, "onConfigured")
                        mCameraCaptureSession = session
                        updatePreview()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "onConfiguredfailed")
                    }
                }, null
            )
        } catch (cae: CameraAccessException) {
            Log.e(TAG, "createCameraPreview cae $cae")
        }
    }


    fun updatePreview() {
        captureRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

        try {
            mCameraCaptureSession?.setRepeatingRequest(
                captureRequestBuilder?.build(),
                null,
                backgroundHandler
            )
        } catch (cae: CameraAccessException) {
            Log.e(TAG, "updatePreview cae $cae")
        }
    }


    protected fun startBackgroundThread() {
        backgroundHandlerThread = HandlerThread("Camera Background")
        backgroundHandlerThread?.start()
        backgroundHandler =
            Handler(backgroundHandlerThread?.getLooper())
    }

    protected fun stopBackgroundThread() {
        backgroundHandlerThread?.quitSafely()
        try {
            backgroundHandlerThread?.join()
            backgroundHandlerThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    private fun openCamera(height: Int, width: Int) {
        val cameraManager =
            getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[1]
            val cameraCharacteristics =
                cameraManager.getCameraCharacteristics(cameraId)
            val streamConfigurationMap =
                cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            mSensorOrientation =
                cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
            if (streamConfigurationMap == null) {
                throw RuntimeException("Cannot get available preview/video sizes")
            }
            video_size = chooseVideoSize(
                streamConfigurationMap.getOutputSizes(
                    MediaRecorder::class.java
                )
            )
            preview_size = chooseOptimalSize(
                streamConfigurationMap.getOutputSizes(
                    SurfaceTexture::class.java
                ),
                width, height, video_size!!
            )
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                face_recording_texture_view?.setAspectRatio(
                    preview_size?.getWidth()!!,
                    preview_size?.getHeight()!!
                )
            } else {
                face_recording_texture_view?.setAspectRatio(
                    preview_size?.getHeight()!!,
                    preview_size?.getWidth()!!
                )
            }
            configureTransform(width, height)
            face_media_recorder = MediaRecorder()
            cameraManager.openCamera(
                cameraId,
                cameraStateCallback,
                null
            )
        } catch (cae: CameraAccessException) {
            Log.e(TAG, "camera access exception $cae")
        } catch (se: SecurityException) {
            Log.e(TAG, "SecurityException $se")
        }
    }


    fun setUpFaceVideoRecorder() {
        try {
            face_media_recorder = MediaRecorder()
            face_media_recorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            face_media_recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            /**
             * create video output file
             */
            face_video_file = getFaceVideoFile()
            faceRecordingFileNameTemp = face_video_file?.getAbsolutePath()!!
            /**
             * set output file in media recorder
             */
            face_media_recorder?.setOutputFile(face_video_file?.absolutePath!!)

            face_media_recorder?.setMaxDuration(-1)
            val profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW)
            if (availableTestModel?.recorder_orientation.equals(
                    "Landscape",
                    ignoreCase = true
                )
            ) {
                face_media_recorder?.setOrientationHint(0)
            } else {
                face_media_recorder?.setOrientationHint(270)
            }
            face_media_recorder?.setVideoFrameRate(profile.videoFrameRate)
            face_media_recorder?.setVideoSize(
                profile.videoFrameWidth,
                profile.videoFrameHeight
            )
            face_media_recorder?.setVideoEncodingBitRate(profile.videoBitRate)
            face_media_recorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            face_media_recorder?.prepare()
        } catch (ioe: IOException) {
            Log.e(TAG, "setUpFaceVideoRecorder ioe $ioe"
            )
        }
    }

    private fun getFaceVideoFile(): File? {

        // External sdcard file location
        val mediaStorageDir = File(Environment.getExternalStorageDirectory(), FACE_VIDEO_DIRECTORY_NAME)

        // Create storage directory if it does not exist
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d(TAG, "Oops! Failed create " + FACE_VIDEO_DIRECTORY_NAME + " directory")
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val mediaFile: File
        mediaFile = File(mediaStorageDir.path + File.separator + "VID_" + timeStamp + ".mp4")

        Log.e(TAG,"FACE MDEI FILE "+mediaFile)
        return mediaFile
    }


    private fun chooseVideoSize(size_choices: Array<Size>): Size {
        for (size in size_choices) {
            if (1920 == size.width && 1080 == size.height) {
                return size
            }
        }
        for (size in size_choices) {
            if (size.width == size.height * 4 / 3 && size.width <= 1080) {
                return size
            }
        }
        Log.e(
            TAG,
            "Couldn't find any suitable video size"
        )
        return size_choices[size_choices.size - 1]
    }


    private fun chooseOptimalSize(
        choices: Array<Size>,
        width: Int,
        height: Int,
        aspectRatio: Size
    ): Size {
        val bigEnough: MutableList<Size> =
            java.util.ArrayList()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            if (option.height == option.width * h / w && option.width >= width && option.height >= height
            ) {
                bigEnough.add(option)
            }
        }

        // Pick the smallest of those, assuming we found any
        return if (bigEnough.size > 0) {
            Collections.min(bigEnough, CompareSizesByArea())
        } else {
            Log.e(
                TAG,
                "Couldn't find any suitable preview size"
            )
            choices[0]
        }
    }

    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        if (null == face_recording_texture_view || null == preview_size) {
            return
        }
        val rotation: Int =
            windowManager?.getDefaultDisplay()?.getRotation()!!
        val matrix = Matrix()
        val viewRect = RectF(0F, 0F, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(
            0F,
            0F,
            preview_size?.getHeight()!!.toFloat(),
            preview_size?.getWidth()!!.toFloat()
        )
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale: Float = Math.max(
                viewHeight.toFloat() /  preview_size?.getHeight()!!.toFloat(),
                viewWidth.toFloat() /preview_size?.getWidth()!!.toFloat()
            )
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate(90 * (rotation - 2).toFloat(), centerX, centerY)
        }
        face_recording_texture_view?.setTransform(matrix)
    }

    fun startFaceRecordingVideo() {


        Log.e(TAG,"cameraDevice "+cameraDevice)
        Log.e(TAG,"face_recording_texture_view "+face_recording_texture_view)
        Log.e(TAG,"face_recording_texture_view?.isAvailable() "+face_recording_texture_view?.isAvailable())
        Log.e(TAG,"preview_size "+ preview_size)

        if (null == cameraDevice || !(face_recording_texture_view?.isAvailable())!! || null == preview_size) {
            Log.e(TAG, "cameraDevice is null after pause")
            return
        }
        try {
            //   closePreviewSession();
            setUpFaceVideoRecorder()
            val texture: SurfaceTexture =
                face_recording_texture_view?.getSurfaceTexture()!!
            texture.setDefaultBufferSize(
                preview_size?.getWidth()!!, preview_size?.getHeight()!!
            )
            captureRequestBuilder =
                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            val surfaces: MutableList<Surface> =
                java.util.ArrayList()

            /**
             * Surface for the camera preview set up
             */
            val previewSurface = Surface(texture)
            surfaces.add(previewSurface)
            captureRequestBuilder?.addTarget(previewSurface)

            //MediaRecorder setup for surface
            val recorderSurface: Surface? =
                face_media_recorder?.getSurface()
            if (recorderSurface != null) {
                surfaces.add(recorderSurface)
            }
            captureRequestBuilder?.addTarget(recorderSurface)

            // Start a capture session
            cameraDevice!!.createCaptureSession(
                surfaces,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        Log.e(TAG, "ONCONFIGURED")
                        mCameraCaptureSession = cameraCaptureSession
                        updatePreview()
                        face_media_recorder?.start()
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        Log.e(
                            TAG,
                            "onConfigureFailed: Failed"
                        )
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Log.e(TAG, "startFaceRecordingVideo camera access exception  e $e")
        }
    }

    fun closePreviewSession() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession?.close()
            mCameraCaptureSession = null
        }
    }

    fun stopFaceRecordingVideo() {
        // UI
        isRecording = false
        try {

            Log.e(TAG,"mCameraCaptureSession "+mCameraCaptureSession)


            mCameraCaptureSession?.stopRepeating()
            mCameraCaptureSession?.abortCaptures()
        } catch (e: CameraAccessException) {
            Log.e(TAG,"camera access exception "+e)
            e.printStackTrace()
        }
        catch (e : java.lang.Exception){
            Log.e(TAG,"Exceptionn "+e)

        }
        if (face_media_recorder != null) {
            face_media_recorder?.stop()
            face_media_recorder?.reset()
            face_media_recorder?.release()
            face_media_recorder = null
        }
        addFaceVideoFilesToList()
        FaceRecordingVideoFileAppending().execute()
    }


    inner class CompareSizesByArea : Comparator<Size?> {

        override fun compare(lhs: Size?, rhs: Size?): Int {
            return java.lang.Long.signum(
                lhs?.width?.toLong()!! * lhs?.height!! -
                        rhs?.width?.toLong()!! * rhs.height
            )
        }
    }


    inner class FaceRecordingVideoFileAppending : AsyncTask<String?, String?, String?>() {

        override fun doInBackground(vararg params: String?): String? {
            Log.e(
                TAG,
                "FaceRecordingVideoFileAppending doInBackground"
            )
            Log.e(
                TAG,
                "all_face_recording_video_files size before appending " +allFaceRecordingVideoFiles.size
            )
            Utils.appendingFaceRecordingFile(allFaceRecordingVideoFiles)
            return null
        }

        override fun onPostExecute(unused: String?) {
            Log.e("hello", "this is on create method steps 7 ********* ---->")
        }
    }

    private fun addFaceVideoFilesToList() {
        if (faceRecordingFileNameTemp.equals("", ignoreCase = true)) {
            Log.e(
                TAG,
                "faceRecordingFileNameTemp is blank"
            )
        } else {
            Log.e(
                TAG,
                "faceRecordingFileNameTemp " +faceRecordingFileNameTemp
            )
            val tempFile =
                File(faceRecordingFileNameTemp)
            val length_of_face_video = tempFile!!.length()
            if (tempFile != null && tempFile.exists()) {
                if (length_of_face_video >= 0) {
                    Log.e(
                        TAG,
                        "faceRecordingFileNameTemp is adding"
                    )
                    Log.e(
                        TAG,
                        "tempFile name " + tempFile.name
                    )
                    allFaceRecordingVideoFiles.add(tempFile)
                    Log.e(
                        TAG,
                        "all_face_recording_video_files size " + allFaceRecordingVideoFiles.size
                    )
                } else {
                    tempFile.delete()
                    Log.e(
                        TAG,
                        "length_of_face_video is zero"
                    )
                }
            } else {
                Log.e(
                    TAG,
                    "temp file is null or doesn't exist"
                )
            }
        }
    }

    override fun onSuccessTerminateTest() {
    }

    override fun onFailureTerminateTest() {
    }


}
