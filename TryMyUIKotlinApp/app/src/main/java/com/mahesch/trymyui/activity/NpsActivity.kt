package com.mahesch.trymyui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.viewmodels.NpsActivityViewModel
import com.mahesch.trymyui.viewmodelfactory.NpsViewModelFactory
import com.seattleapplab.trymyui.models.Tests
import com.seattleapplab.trymyui.models.Tests.NpsQuestion
import kotlinx.android.synthetic.main.nps_activity.*

class NpsActivity : AppCompatActivity() {

    private var TAG  = NpsActivity::class.simpleName?.toUpperCase()

    private var selectedValue = 1
    private var  isFinish = false

    private lateinit var npsQuestion : String
    private lateinit var availableTestModel: AvailableTestModel
    private lateinit var npsActivityViewModel: NpsActivityViewModel
    private var npsQuestionList = ArrayList<Tests.NpsQuestion>()
    private lateinit var npsQuestionModel : NpsQuestion
    private lateinit var manageFlowAfterTest :ManageFlowAfterTest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nps_activity)

        Utils.actionBarSetup(actionBar," NPS Survey")

        if(intent != null){
            npsQuestion = intent.extras.getString("npsQuestion","")
            availableTestModel = intent.extras.getSerializable("availableTestConstants") as AvailableTestModel

            if(npsQuestion == null || availableTestModel == null){
                Utils.moveToHome(this)
            }

            manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this)
        }
        else
        {
            Utils.moveToHome(this)
        }

        if(availableTestModel.ux_crowd_questions.equals("[]",true))
            isFinish = true


        val factory =
            NpsViewModelFactory(
                application
            )

        npsActivityViewModel = ViewModelProvider(this,factory).get(NpsActivityViewModel::class.java)

        npsQuestionList = Gson().fromJson<java.util.ArrayList<NpsQuestion>>(
            npsQuestion,
            object : TypeToken<java.util.ArrayList<NpsQuestion?>?>() {}.type
        )

        npsQuestionModel = npsQuestionList[0]

        button_submit.setOnClickListener { onClickOfNpsSubmit() }

    }


    private fun onClickOfNpsSubmit(){

        if(Utils.isInternetAvailable(this@NpsActivity))
            submitNpsValue(makeJsonDataRequestToSend())
        else
            Utils.showInternetCheckToast(this@NpsActivity)
    }


    private fun submitNpsValue(jsonRequestString : JsonObject){

        Log.e(TAG,"jsonRequestString "+jsonRequestString)

        npsActivityViewModel.submitNPS(jsonRequestString)?.observe(this,object : Observer<CommonModel>{
            override fun onChanged(commonModel: CommonModel) {
                Log.e(TAG,"statuscode "+commonModel.statusCode)
                Log.e(TAG,"message "+commonModel.message)

                if(commonModel == null){
                    showErrorDialog(commonModel)
                }
                else
                {
                    if(commonModel.error == null){
                        submitNPSResponseHandling(commonModel)
                    }
                    else
                    {
                        var error: Throwable? = commonModel.error
                        submitNPSErrorHandling(error)
                    }
                }
            }

        })
    }

    private fun makeJsonDataRequestToSend() : JsonObject{


        val joNpsAnswer = JsonObject()
        joNpsAnswer.addProperty(""+npsQuestionModel.id,""+selectedValue)

        val joParent = JsonObject()
        joParent.add("nps_response",joNpsAnswer)
        joParent.addProperty("test_result_id",""+SharedPrefHelper(this).getTestResultId())
        joParent.addProperty("at",""+SharedPrefHelper(this).getToken())

        if(isFinish)
            joParent.addProperty("is_finished",isFinish)
        else
            joParent.addProperty("is_finished",isFinish)

        var jsonParser = JsonParser()

        return jsonParser.parse(joParent.toString()) as JsonObject
    }


    private fun submitNPSResponseHandling(commonModel: CommonModel){
        if(commonModel.statusCode == 200){
            //MANAGE FLOW
            manageFlowAfterTest.isNpsQuestionSubmitted = true
            manageFlowAfterTest.moveToWhichActivity(this)
        }
        else
        {
            showErrorDialog(commonModel)
        }
    }


    private fun submitNPSErrorHandling(error : Throwable?){
        var okBtn =  OkAlertDialog.initOkAlert(this)
        OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
    }

    private fun showErrorDialog(commonModel: CommonModel){

        var okBtn =  OkAlertDialog.initOkAlert(this)

        if(commonModel == null){

            OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        }
        else
        {
            OkAlertDialog.showOkAlert(commonModel.message)
        }

        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }

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

}
