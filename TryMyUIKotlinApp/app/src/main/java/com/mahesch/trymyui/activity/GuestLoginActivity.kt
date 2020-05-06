package com.mahesch.trymyui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.OkAlertDialog
import com.mahesch.trymyui.helpers.ProgressDialog
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.viewmodelfactory.GuestLoginViewModelFactory
import com.mahesch.trymyui.viewmodels.GuestActivityViewModel
import com.seattleapplab.trymyui.models.Tests
import kotlinx.android.synthetic.main.guest_login_activitiy.*

class GuestLoginActivity : AppCompatActivity() {

    private lateinit var guestActivityViewModel: GuestActivityViewModel
    private lateinit var availableTestModel : AvailableTestModel

    private val TAG = LoginActivity::class.simpleName?.toUpperCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.guest_login_activitiy)

        initializeProgressDialog()

        et_name.setText("Mahesh")
        et_email.setText("m271@as.as")
        et_test_id.setText("123456")

        btn_guest_signin.setOnClickListener { onClickGuestLogin()  }

        val factory =
            GuestLoginViewModelFactory(
                application
            )
        guestActivityViewModel = ViewModelProvider(this,factory).get(GuestActivityViewModel::class.java)

        guestActivityViewModel.mutableLiveData?.observe(this,object : Observer<Tests>{
            override fun onChanged(guestLoginResponseModel: Tests?) {
                Log.e("GUEST LOGIN","CALLED")

                dismissProgressDialog()

                if(guestLoginResponseModel == null){
                    showErrorDialog(guestLoginResponseModel)
                }
                else {
                    if (guestLoginResponseModel.error == null) {
                        guestLoginResponseHandling(guestLoginResponseModel)
                    } else {
                        var error: Throwable? = guestLoginResponseModel.error
                        guestErrorHandling(error)
                    }
                }


            }
        })
    }


    private fun initializeProgressDialog(){
        ProgressDialog.initializeProgressDialogue(this)
    }


    private fun onClickGuestLogin(){

        if(Utils.isInternetAvailable(this)){

            if(Utils.isValidName(et_name.text.toString()) &&
                Utils.isValidEmail(et_email.text.toString()) &&
                Utils.isValidTestId(et_test_id.text.toString())){

                showProgressDialog()

                guestLogin()
            }
            else{
                if(!(Utils.isValidName(et_name.text.toString())))
                    et_name.error = "Name"

                if(!Utils.isValidEmail(et_email.text.toString()))
                    et_email.error = "Email"

                if(!Utils.isValidTestId(et_test_id.text.toString()))
                    et_test_id.error = "Test id"
            }
        }
        else{
            //PROMPT DIALOGUE
            Toast.makeText(this,"No Internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guestLogin(){

        guestActivityViewModel.callGuestLogin(et_test_id.text.toString(),et_name.text.toString(),et_email.text.toString())
    }

    private fun guestLoginResponseHandling(tests: Tests?) {

        Log.e(TAG, "statusCode " + tests?.statusCode)

        Log.e(TAG, "message " + tests?.message)

        val gson = Gson()
        val json = gson.toJson(tests)

        Log.e(TAG, "onGuestLogin " + json)

        if(tests?.statusCode == 200){
            if (tests?.data?.availableTests?.size!! > 0) {

                saveDataToSharedPref(tests)

                for (i in 0..(tests?.data?.availableTests?.size!! - 1)!!) {

                    val availableTest = tests?.data?.availableTests?.get(i)?.availableTest

                    val pos = i
                    val id: Int? = availableTest?.id
                    val url: String? = availableTest?.url
                    val title: String? = availableTest?.title
                    val scenario: String? = availableTest?.scenario
                    val recording_timeout_minutes: Int? = availableTest?.recording_timeout_minutes
                    val special_qual: String? = availableTest?.special_qual
                    val interface_type: String? = availableTest?.interface_type
                    val is_kind_partial_site: Boolean? = availableTest?.is_kind_partial_site
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
                    val suggestion_response_question_id: Int? =
                        availableTest?.suggestion_response_question_id
                    val max_voting_limit: Int? = availableTest?.max_voting_limit

                    val goodResponsesArrayList: java.util.ArrayList<Tests.GoodResponses>? =
                        availableTest?.goodResponsesArrayList
                    val badResponsesArrayList: java.util.ArrayList<Tests.BadResponses>? =
                        availableTest?.badResponsesArrayList
                    val suggestionResponsesArrayList: java.util.ArrayList<Tests.SuggestionResponses>? =
                        availableTest?.suggestionResponsesArrayList


                    if (uxCrowdSurvey.equals("[]", ignoreCase = true)) {
                        uxCrowdSurvey = "[]"
                    }

                    val do_impression_test: Boolean? = availableTest?.do_impression_test
                    val seq_task: Boolean? = availableTest?.getopt_for_seq()
                    val task_complete: Boolean? = availableTest?.opt_for_task_completion

                    val screener_test_avaialable: Boolean? = availableTest?.screener_test_available

                    val opt_for_face_recording: Boolean? = availableTest?.opt_for_face_recording
                    val recorder_orientation: String? = availableTest?.recorder_orientation
                    val technical_qualification: String? = availableTest?.technical_qual

                    availableTestModel = AvailableTestModel(
                        id,
                        pos,
                        title,
                        url,
                        tasks,
                        scenario,
                        surveyQuestions,
                        special_qual,
                        interface_type,
                        susQuestion,
                        tester_platform,
                        uxCrowdSurvey,
                        is_kind_partial_site_text,
                        title_with_id,
                        native_app_test,
                        recording_timeout_minutes,
                        seq_task,
                        task_complete,
                        do_impression_test,
                        is_kind_partial_site,
                        screener_test_avaialable,
                        isVoting,
                        response_type_list,
                        good_question,
                        bad_question,
                        suggestion_question,
                        good_response_question_id,
                        bad_response_question_id,
                        suggestion_response_question_id,
                        max_voting_limit,
                        goodResponsesArrayList,
                        badResponsesArrayList,
                        suggestionResponsesArrayList,
                        npsQuestion,
                        sus_scales,
                        opt_for_face_recording,
                        recorder_orientation,
                        technical_qualification
                    )


                }

                if (tests?.statusCode == 200) {

                    moveToNextActivity(availableTestModel)


                } else {
                    showErrorDialog(tests)
                }

            }
        }
        else
        {
            showErrorDialog(tests)
        }


    }

    private fun guestErrorHandling(error : Throwable?){
        var okBtn =  OkAlertDialog.initOkAlert(this)
        OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
    }


    private fun moveToNextActivity(availableTestModel: AvailableTestModel){
        if (SharedPrefHelper(GuestLoginActivity@this).getHelperFlag()) {
            val intent =
                Intent(this@GuestLoginActivity, TabActivity::class.java)
            intent.putExtra("availableTestConstant", availableTestModel)
            startActivity(intent)
            finish()
        } else {
            val intent =
                Intent(this@GuestLoginActivity, GetToKnowTestingActivity::class.java)
            intent.putExtra("availableTestConstant", availableTestModel)
            startActivity(intent)
            finish()
        }
    }


    private fun saveDataToSharedPref(tests: Tests?){
        var sharedPrefHelper = SharedPrefHelper(this)

        sharedPrefHelper.saveUserName(et_name.text.toString())
        sharedPrefHelper.saveEmailId(et_email.text.toString())
        sharedPrefHelper.saveGuestTester(true)
        sharedPrefHelper.saveToken(tests?.data?.token)
        sharedPrefHelper.setIdentityId(tests?.data?.identity_id)
        sharedPrefHelper.setS3Bucket(tests?.data?.s3_bucket_name)
        sharedPrefHelper.saveAvaliableTestId(tests?.data?.availableTests!![0]?.availableTest?.id.toString())
    }



    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

        dismissProgressDialog()

        dismissErrorDialog()
    }

    override fun onStop() {
        super.onStop()

        dismissProgressDialog()

        dismissErrorDialog()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()

        dismissProgressDialog()

        dismissErrorDialog()
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

    private fun showErrorDialog(guestLoginResponseModel: Tests?){

        var okBtn =  OkAlertDialog.initOkAlert(this)

        if(guestLoginResponseModel == null){

            OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        }
        else
        {
            OkAlertDialog.showOkAlert(guestLoginResponseModel.message)
        }

        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }

    }


}
