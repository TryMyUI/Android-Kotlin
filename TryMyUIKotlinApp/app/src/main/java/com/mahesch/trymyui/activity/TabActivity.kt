package com.mahesch.trymyui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.mahesch.trymyui.R
import com.mahesch.trymyui.adapter.ViewPagerAdapter
import com.mahesch.trymyui.fragment.AvailableTestFragment
import com.mahesch.trymyui.fragment.PerformedTestFragment
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.model.PerformedTestModel
import com.mahesch.trymyui.receivers.ConnectivityReceiver
import com.mahesch.trymyui.services.NativeAppReccordingServices
import com.mahesch.trymyui.viewmodelfactory.TabActivityViewModelFactory
import com.mahesch.trymyui.viewmodels.TabActivityViewModel
import com.seattleapplab.trymyui.models.Tests
import com.seattleapplab.trymyui.models.Tests.*
import kotlinx.android.synthetic.main.force_update_dialog.*
import kotlinx.android.synthetic.main.tab_activity.view.*

class TabActivity : AppCompatActivity(),ConnectivityReceiver.ConnectivityReceiverListener {

    companion object{
        var isPendingTest = false
        var isQualified = true
        var qualification_message: String? = null
    }

    private val TAG: String? = TabActivity::class.simpleName?.toUpperCase()

    private lateinit var tabActivityViewModel: TabActivityViewModel

    private var sharedPrefHelper : SharedPrefHelper? = null

    private var availableTestModelList  =  ArrayList<AvailableTestModel>()

    var performTestList = ArrayList<PerformedTestModel>()

    private var customerTestList = ArrayList<AvailableTestModel>()

    private lateinit var toolbar: Toolbar
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var popupWindow: PopupWindow

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tab_activity)

        initializeProgressDialog()

         toolbar = findViewById<Toolbar>(R.id.toolbar_tabactivity)
         viewPager = findViewById<ViewPager>(R.id.viewpager)
       tabs = findViewById<TabLayout>(R.id.tabs)

        sharedPrefHelper = SharedPrefHelper(this@TabActivity)

        ApplicationClass.getInstance()?.setConnectivityListener(this)

        popupWindow = PopupWindow(TabActivity@this)

        val factory =
            TabActivityViewModelFactory(
                application
            )

        tabActivityViewModel = ViewModelProvider(this,factory).get(TabActivityViewModel::class.java)

        availableTestModelList = ArrayList<AvailableTestModel>()

        if(Utils.isRecordingServiceRunning(NativeAppReccordingServices::class.java,TabActivity@this) && !isFinishing){

            displayAlertForServiceIsRunning()

        }else{
            initializeViews()
        }

        compareAppVersionWithServer()

        getDashBoardData()

    }

    @SuppressLint("RestrictedApi")
    private fun initializeViews(){

        toolbar.menu.clear()

        toolbar.ll_menu.setOnClickListener { v ->
            Log.e("TAG", "menuicon click")
            showPopUpMenu(v)
        }

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(false)

        setUpViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)




    }


    private fun initializeProgressDialog(){
        ProgressDialog.initializeProgressDialogue(this)
    }

    override fun onBackPressed() {

        if(sharedPrefHelper!!.getGuestTester()){ logout() } else{   finish() }

    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()

        dismissProgressDialog()

        dismissErrorDialog()

  //      dismissPopupWindow()
    }


    private fun dismissPopupWindow(){
        if(popupWindow != null){
            if(popupWindow.isShowing)
                popupWindow.dismiss()
        }
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()

        dismissProgressDialog()

        dismissErrorDialog()

//        dismissPopupWindow()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        if (!isFinishing)
            if (!isConnected)
                ShowInternetAlert.showInternetAlert(this@TabActivity)

    }

    private fun showProgressDialog(){
        ProgressDialog.showProgressDialog()
    }

    private fun dismissProgressDialog(){
        ProgressDialog.dismissProgressDialog()
    }

    private fun dismissErrorDialog(){
        OkAlertDialog.dismissOkAlert()
    }


    private fun getDashBoardData(){

        if(Utils.isInternetAvailable(this)){

            showProgressDialog()

            if(sharedPrefHelper!!.getGuestTester()){
                displayGuestData()
            }
            else if(sharedPrefHelper?.getUserType().equals(sharedPrefHelper?.UserTypeWorker,true))
            {
                callWorker()
            }
            else if(sharedPrefHelper?.getUserType().equals(sharedPrefHelper?.UserTypeCustomer,true))
            {
                callCustomer()
            }
            else{
                OkAlertDialog.initOkAlert(TabActivity@this)?.setOnClickListener { finish() }
                OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))
            }
        }
        else
        {
            Utils.showInternetCheckToast(this)
        }

    }




    private fun displayGuestData(){
dismissProgressDialog()
        var availableTestModel = intent.getSerializableExtra("availableTestConstant") as AvailableTestModel

        if(availableTestModel == null){
            OkAlertDialog.initOkAlert(TabActivity@this)?.setOnClickListener { finish() }
            OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))
        }
        else{

            availableTestModelList.add(availableTestModel)
        }

        setUpViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)

    }


    private fun setUpViewPager(viewPager: ViewPager){
        var adapter = ViewPagerAdapter(supportFragmentManager)

        var userType = sharedPrefHelper?.getUserType()

        if((userType.equals(sharedPrefHelper?.UserTypeWorker) && !isPendingTest) || sharedPrefHelper!!.getGuestTester()){
            adapter.addFragment(AvailableTestFragment(this,availableTestModelList),"Available Tests")
            adapter.addFragment(PerformedTestFragment(this,performTestList,0),"Performed Tests")
        }
        else if(userType.equals(sharedPrefHelper?.UserTypeWorker) && isPendingTest){

            adapter.addFragment(AvailableTestFragment(this,availableTestModelList),"Available Tests")
            adapter.addFragment(PerformedTestFragment(this,performTestList,0),"Performed Tests")
        }
        else{
            adapter.addFragment(AvailableTestFragment(this,customerTestList),"My Tests")
        }

        viewPager.adapter = adapter


    }


    private fun callWorker(){

        Log.e(TAG,"token "+sharedPrefHelper?.getToken())
        Log.e(TAG,"device "+resources.getString(R.string.device))

        tabActivityViewModel.callWorkerDashBoardData(sharedPrefHelper?.getToken(),resources.getString(R.string.device))?.observe(this,object : Observer<Tests>{

            override fun onChanged(test: Tests?) {

                Log.e(TAG,"callWorker tests message "+test?.message)
                Log.e(TAG,"callWorker tests statusCode "+test?.statusCode)

                val gson = Gson()
                val json = gson.toJson(test)

                Log.e(TAG, "callWorker json  "+json)

                dismissProgressDialog()

                if(test == null){
                    showErrorDialog(test)
                }
                else
                {

                    if(test.error == null){
                        callWorkerResponseHandling(test)
                    }
                    else{
                        var error: Throwable? = test.error
                        callWorkerErrorHandling(error)
                    }
                }

            }
        })

    }


    private fun callCustomer(){

        Log.e(TAG,"token "+sharedPrefHelper?.getToken())
        Log.e(TAG,"device "+resources.getString(R.string.device))

        tabActivityViewModel.callCustomerDashBoardData(sharedPrefHelper?.getToken(),resources.getString(R.string.device))?.observe(this,object : Observer<Tests>{

            override fun onChanged(test: Tests?) {

                Log.e(TAG,"callCustomer tests message "+test?.message)
                Log.e(TAG,"callCustomer tests statusCode "+test?.statusCode)

                val gson = Gson()
                val json = gson.toJson(test)

                Log.e(TAG, "callCustomer json  "+json)

                dismissProgressDialog()

                if(test == null){
                    showErrorDialog(test)
                }
                else
                {

                    if(test.error == null){
                        callCustomerResponseHandling(test)
                    }
                    else{
                        var error: Throwable? = test.error
                        callCustomerErrorHandling(error)
                    }
                }

            }
        })

    }


    private fun compareAppVersionWithServer(){
        tabActivityViewModel.callMobileUpdateRequired(this.resources.getString(R.string.current_app_server_version_release),
            this.resources.getString(R.string.device))?.observe(this,object : Observer<LoginResponseModel>{
            override fun onChanged(loginResponseModel: LoginResponseModel) {

                if(loginResponseModel == null){
                    Log.e(TAG,"loginResponseModel is null")
                }
                else
                {
                    if(loginResponseModel.error != null){
                        Log.e(TAG,"loginResponseModel error is not null")

                    }
                    else
                    {
                        if(loginResponseModel.statusCode == 200){
                            Log.e(TAG,"update required "+loginResponseModel.data?.update_required)

                            if(loginResponseModel.data?.update_required!!){
                                showAppUpdateDialog()
                            }
                        }
                        else
                        {
                            Log.e(TAG,"loginResponseModel statuscode not 200")

                        }
                    }
                }

            }

        })
    }

    private fun showAppUpdateDialog(){
        var dialogForceUpdate = Dialog(this)

        dialogForceUpdate.setContentView(R.layout.force_update_dialog)

        dialogForceUpdate.setCancelable(false)

        dialogForceUpdate.tv_lbl_new_version_available.text =
            "New version available"

        dialogForceUpdate.tv_whats_new.text = "What new"

        dialogForceUpdate.tv_update.setOnClickListener {
            dialogForceUpdate.dismiss()

            // TODO: Launch Playstore to update
            launchAppPlayStore()
        }

        if (!isFinishing) {
            if (!dialogForceUpdate.isShowing) {
                dialogForceUpdate.show()
            }
        }
    }

    private fun launchAppPlayStore() {
        var intent: Intent

        var packageName = ""

        try {
            packageName = packageManager.getPackageInfo(packageName, 0).packageName
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.e(TAG, "market $packageName")
            intent.data = Uri.parse("market://details?id=$packageName")
            startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.e(TAG, "googleplay $packageName")
            intent.data = Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
            startActivity(intent)
        } catch (nnfe: PackageManager.NameNotFoundException) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.e(TAG, "googleplay $packageName")
            intent.data = Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
            startActivity(intent)
        }
    }


    private fun showErrorDialog(tests: Tests?){

        var okBtn =  OkAlertDialog.initOkAlert(this)

        if(tests == null){

            OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        }
        else
        {
            OkAlertDialog.showOkAlert(tests.message)
        }

        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }

    }


    fun callWorkerResponseHandling(tests: Tests?){

        if(tests?.statusCode == 200){

            addWorkerAvailableTestModelToList(tests)
        }
        else
        {
            showErrorDialog(tests)
        }

    }

    fun callWorkerErrorHandling(error: Throwable?){
showErrorDialog(null)
    }

    private fun addWorkerAvailableTestModelToList(tests: Tests?){

        val gson = Gson()
        val json = gson.toJson(tests)

        if(tests?.data?.availableTests?.size!! > 0){

            for (i in 0..(tests?.data?.availableTests?.size!! -1)!!) {

                val availableTest = tests?.data?.availableTests?.get(i) ?.availableTest

                val  pos = i
                val id: Int? = availableTest?.id
                val url: String? = availableTest?.url
                val title: String? = availableTest?.title
                val scenario: String? = availableTest?.scenario
                val recording_timeout_minutes: Int? = availableTest?.recording_timeout_minutes
                val special_qual: String? = availableTest?.special_qual
                val interface_type: String? = availableTest?.interface_type
                val is_kind_partial_site: Boolean? = availableTest?.isIs_kind_partial_site
                val is_kind_partial_site_text: String? = availableTest?.is_kind_partial_site_text
                val tasks: String? = gson.toJson(availableTest?.tasks)
                val title_with_id: String? = availableTest?.title_with_id
                val native_app_test: String? = availableTest?.native_app_url

                val surveyQuestions: String? = gson.toJson(availableTest?.surveyQuestions)
                val susQuestion: String? = gson.toJson(availableTest?.sus_questions)
                var uxCrowdSurvey: String? = gson.toJson(availableTest?.ux_crowd_questions)
                val npsQuestion: String? = gson.toJson(availableTest?.npsQuestion_list)
                val sus_scales: String? = gson.toJson(availableTest?.susScales)

                val tester_platform: String? = availableTest?.tester_platform

                val isVoting: Boolean? = availableTest?.isVoting
                val response_type_list: java.util.ArrayList<String>? = availableTest?.response_type
                val good_question: String? = availableTest?.good_question
                val bad_question: String? = availableTest?.bad_question
                val suggestion_question: String? = availableTest?.suggestion_question

                val good_response_question_id: Int? = availableTest?.good_response_question_id
                val bad_response_question_id: Int? = availableTest?.bad_response_question_id
                val suggestion_response_question_id: Int? = availableTest?.suggestion_response_question_id
                val max_voting_limit: Int? = availableTest?.max_voting_limit

                val goodResponsesArrayList: java.util.ArrayList<GoodResponses>? =
                    availableTest?.goodResponsesArrayList
                val badResponsesArrayList: java.util.ArrayList<BadResponses>? =
                    availableTest?.badResponsesArrayList
                val suggestionResponsesArrayList: java.util.ArrayList<SuggestionResponses>? =
                    availableTest?.suggestionResponsesArrayList


                if (uxCrowdSurvey.equals("[]", ignoreCase = true)) {
                    uxCrowdSurvey = "[]"
                }

                val do_impression_test: Boolean? = availableTest?.do_impression_test
                val seq_task: Boolean? = availableTest?.getopt_for_seq()
                val task_complete: Boolean? = availableTest?.opt_for_task_completion

                val screener_test_avaialable: Boolean? = availableTest?.isScreener_test_avaialble

                val opt_for_face_recording: Boolean? = availableTest?.isOpt_for_face_recording
                val recorder_orientation: String? = availableTest?.recorder_orientation
                val technical_qualification: String? = availableTest?.technical_qual

                var availableTestModel = AvailableTestModel(id, pos, title, url, tasks, scenario, surveyQuestions, special_qual,
                    interface_type, susQuestion, tester_platform, uxCrowdSurvey, is_kind_partial_site_text, title_with_id,
                    native_app_test, recording_timeout_minutes, seq_task , task_complete, do_impression_test,
                    is_kind_partial_site, screener_test_avaialable, isVoting, response_type_list, good_question,
                    bad_question, suggestion_question, good_response_question_id, bad_response_question_id,
                    suggestion_response_question_id, max_voting_limit, goodResponsesArrayList, badResponsesArrayList,
                    suggestionResponsesArrayList, npsQuestion, sus_scales, opt_for_face_recording, recorder_orientation, technical_qualification)

                availableTestModelList?.add(availableTestModel)

            }

            Log.e(TAG,"availableTestModelList size "+availableTestModelList.size)


            setUpViewPager(viewPager)
            tabs.setupWithViewPager(viewPager)

        }
        else
        {
            showErrorDialog(tests)
        }

    }


    fun callCustomerResponseHandling(tests: Tests?){

        if(tests?.statusCode == 200){
            addCustomerAvailableTestModelToList(tests)
        }
        else
        {
            showErrorDialog(tests)
        }

    }

    fun callCustomerErrorHandling(error: Throwable?){
            showErrorDialog(null)
    }

    private fun addCustomerAvailableTestModelToList(tests: Tests?){

        val gson = Gson()
        val json = gson.toJson(tests)

        if(tests?.data?.myTests?.size!! > 0){

            for (i in 0 until (tests?.data?.myTests?.size!!)) {

                val availableTest = tests?.data?.myTests?.get(i)?.myTest

                val  pos = i
                val id: Int? = availableTest?.id
                val url: String? = availableTest?.url
                val title: String? = availableTest?.title
                val scenario: String? = availableTest?.scenario
                val recording_timeout_minutes: Int? = availableTest?.recording_timeout_minutes
                val special_qual: String? = availableTest?.special_qual
                val interface_type: String? = availableTest?.interface_type
                val is_kind_partial_site: Boolean? = availableTest?.isIs_kind_partial_site
                val is_kind_partial_site_text: String? = availableTest?.is_kind_partial_site_text
                val tasks: String? = gson.toJson(availableTest?.tasks)
                val title_with_id: String? = availableTest?.title_with_id
                val native_app_test: String? = availableTest?.native_app_url

                val surveyQuestions: String? = gson.toJson(availableTest?.surveyQuestions)
                val susQuestion: String? = gson.toJson(availableTest?.sus_questions)
                var uxCrowdSurvey: String? = gson.toJson(availableTest?.ux_crowd_questions)
                val npsQuestion: String? = gson.toJson(availableTest?.npsQuestion)
                val sus_scales: String? = gson.toJson(availableTest?.susScales)

                val tester_platform: String? = availableTest?.tester_platform

                val isVoting: Boolean? = false
                val response_type_list: java.util.ArrayList<String>? = null
                val good_question: String? = null
                val bad_question: String? = null
                val suggestion_question: String? = null

                val good_response_question_id: Int? = 0
                val bad_response_question_id: Int? = 0
                val suggestion_response_question_id: Int? = 0
                val max_voting_limit: Int? = 0

                val goodResponsesArrayList: java.util.ArrayList<GoodResponses>? =
                    null
                val badResponsesArrayList: java.util.ArrayList<BadResponses>? =
                    null
                val suggestionResponsesArrayList: java.util.ArrayList<SuggestionResponses>? =
                    null


                if (uxCrowdSurvey.equals("[]", ignoreCase = true)) {
                    uxCrowdSurvey = "[]"
                }

                val do_impression_test: Boolean? = availableTest?.do_impression_test
                val seq_task: Boolean? = availableTest?.getopt_for_seq()
                val task_complete: Boolean? = availableTest?.opt_for_task_completion

                val screener_test_avaialable: Boolean? = false

                val opt_for_face_recording: Boolean? = availableTest?.isOpt_for_face_recording
                val recorder_orientation: String? = availableTest?.recorder_orientation
                val technical_qualification: String? = availableTest?.technical_qual

                var availableTestModel = AvailableTestModel(id, pos, title, url, tasks, scenario, surveyQuestions, special_qual,
                    interface_type, susQuestion, tester_platform, uxCrowdSurvey, is_kind_partial_site_text, title_with_id,
                    native_app_test, recording_timeout_minutes, seq_task , task_complete, do_impression_test,
                    is_kind_partial_site, screener_test_avaialable, isVoting, response_type_list, good_question,
                    bad_question, suggestion_question, good_response_question_id, bad_response_question_id,
                    suggestion_response_question_id, max_voting_limit, goodResponsesArrayList, badResponsesArrayList,
                    suggestionResponsesArrayList, npsQuestion, sus_scales, opt_for_face_recording, recorder_orientation, technical_qualification)

                customerTestList?.add(availableTestModel)

            }
            setUpViewPager(viewPager)
            tabs.setupWithViewPager(viewPager)
        }
        else
        {
            showErrorDialog(tests)
        }

    }


    private fun temporaryFlow(availableTestModel: AvailableTestModel?){

        Log.e(TAG, "availableTestModel $availableTestModel")

        Log.e(TAG,"title "+availableTestModel?.title)

        SharedPrefHelper(this).saveTestResultId("309459")

        var intent = Intent(this@TabActivity,NpsActivity::class.java)
        intent.putExtra("npsQuestion",availableTestModel?.npsQuestion)
        intent.putExtra("availableTestConstants",availableTestModel)
        startActivity(intent)
        finish()
    }


    private fun displayAlertForServiceIsRunning(){
        var btn = OkAlertDialog.initOkAlert(this)

        OkAlertDialog.showOkAlert(resources.getString(R.string.display_alert_for_service_is_running))

        btn?.setOnClickListener {
            OkAlertDialog.dismissOkAlert()
            finish()
        }
    }


    private fun showPopUpMenu(view: View){
        var density = resources.displayMetrics.density

        popupWindow = PopupWindow(view,density.toInt() * 240,WindowManager.LayoutParams.WRAP_CONTENT,true)

        var layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

       var view1 = layoutInflater.inflate(R.layout.popupwindow,null)

        popupWindow.isFocusable = true
        popupWindow.contentView = view1
        popupWindow.showAtLocation(view1,Gravity.TOP or Gravity.RIGHT,0,toolbar.height+42)

        val tvFeedback = view1.findViewById<View>(R.id.tv_menu_feedback) as TextView
        val tvHowToTest = view1.findViewById<View>(R.id.tv_menu_how_to_test) as TextView
        val tvLogout = view1.findViewById<View>(R.id.tv_menu_logout) as TextView

        tvFeedback.setOnClickListener {
            val intentFeedback = Intent(this@TabActivity, FeedbackActivity::class.java)
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            finish()
        }

        tvHowToTest.setOnClickListener {
            val intentFeedback = Intent(this@TabActivity, VideoPlayerActivity::class.java)
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            finish()
        }

        tvLogout.setOnClickListener { logout() }

    }


    private fun logout(){
        isPendingTest = false
        sharedPrefHelper?.clearSharedPreference()
        Toast.makeText(this@TabActivity, getString(R.string.logout_successful), Toast.LENGTH_LONG).show()
        val intent = Intent(this@TabActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }




}
