package com.mahesch.trymyui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.mahesch.trymyui.R
import com.mahesch.trymyui.adapter.SusAdapter
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.viewmodelfactory.SusActivityViewModelFactory
import com.mahesch.trymyui.viewmodels.SusActivityViewModel
import com.seattleapplab.trymyui.models.Tests
import kotlinx.android.synthetic.main.sus_question_activity.*

class SusQuestionActivity : AppCompatActivity() {

    private var TAG  = SusQuestionActivity::class.simpleName?.toUpperCase()

    private var  isFinish = false

    private lateinit var susQuestion : String
    private lateinit var availableTestModel: AvailableTestModel
    private lateinit var susActivityViewModel: SusActivityViewModel
    private var susQuestionList = ArrayList<Tests.SusQuestions>()
    private lateinit var susQuestionModel : Tests.SusQuestions
    private lateinit var manageFlowAfterTest : ManageFlowAfterTest
    private lateinit var susAdapter: SusAdapter
    private lateinit var sharedPrefHelper: SharedPrefHelper

    private var back_alert:android.app.AlertDialog? = null


    private var totalDataSize = 0
    private var size = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sus_question_activity)

        if(intent != null){
            susQuestion = intent.extras.getString("susQuestions","")
            availableTestModel = intent.extras.getSerializable("availableTestConstants") as AvailableTestModel

            if(susQuestion == null || availableTestModel == null){
                moveToHome()
            }

            manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this)
        }
        else
        {
            moveToHome()
        }

        if(availableTestModel.ux_crowd_questions.equals("[]",true) && availableTestModel.npsQuestion.equals("[]",true))
            isFinish = true


        val factory =
            SusActivityViewModelFactory(
                application
            )


        susActivityViewModel = ViewModelProvider(this,factory).get(SusActivityViewModel::class.java)

        susQuestionList = Gson().fromJson<java.util.ArrayList<Tests.SusQuestions>>(
            susQuestion,
            object : TypeToken<java.util.ArrayList<Tests.SusQuestions?>?>() {}.type
        )

        susQuestionModel = susQuestionList[0]

        sharedPrefHelper = SharedPrefHelper(this)

        button_submit.setOnClickListener { onClickOfSusSubmit() }

        susQuestionList = makeTaskFromJson(susQuestion)
        totalDataSize = susQuestionList.size

        if(totalDataSize != 0){
            susAdapter = SusAdapter(this,susQuestionList,availableTestModel)
            listView_sus.adapter = susAdapter
        }

        button_submit.setBackgroundColor(resources.getColor(R.color.darker_grey))
        button_submit.isEnabled = false

        susAdapter.setonDataChangesListener(object : SusAdapter.OnDataChangeListener {
            override fun onDataChanged(size: Int) {
                if (size == totalDataSize) {
                    button_submit.setBackgroundColor(resources.getColor(R.color.green_light))
                    button_submit.isEnabled = true
                } else {
                    button_submit.setBackgroundColor(resources.getColor(R.color.darker_grey))
                    button_submit.isEnabled = false
                }
            }
        })
    }

    private fun makeTaskFromJson(susQuestionString: String): ArrayList<Tests.SusQuestions>{

        return Gson().fromJson(susQuestionString,object: TypeToken<ArrayList<Tests.SusQuestions>>(){}.type)
    }


    private fun onClickOfSusSubmit(){
        size =  susAdapter.checkvalues.size

        if(size == totalDataSize){
            if(Utils.isInternetAvailable(this)){

                ProgressDialog.initializeProgressDialogue(this)
                ProgressDialog.showProgressDialog()
                susTestSubmit(makeJsonObjectOfValues())
            }
            else{
                Utils.showInternetCheckToast(this)
            }
        }
    }

    private fun makeJsonObjectOfValues(): JsonObject{
        var jsonObjectCheckValues = JsonObject()
        var joParent = JsonObject()
        for (key in susAdapter.checkvalues.keys){
            jsonObjectCheckValues.addProperty(key,susAdapter.checkvalues.get(key))
        }

        joParent.addProperty("at",sharedPrefHelper.getToken())
        joParent.addProperty("test_result_id",sharedPrefHelper.getTestResultId())
        joParent.add("answers",jsonObjectCheckValues)
        joParent.addProperty("isFinished",isFinish)

        return JsonParser().parse(joParent.toString()) as JsonObject


    }

    private fun showWentWrongDialog(){
        OkAlertDialog.initOkAlert(this@SusQuestionActivity)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))
    }

    private fun showErrorDialog(commonModel: CommonModel){
        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(commonModel.message)
    }

    private fun susTestSubmit(jsonObjectRequestString : JsonObject){

        susActivityViewModel.submitSUS(jsonObjectRequestString)?.observe(this,object : Observer<CommonModel>{
            override fun onChanged(commonModel: CommonModel) {

                ProgressDialog.dismissProgressDialog()

                if(commonModel == null){
                    showWentWrongDialog()
                }
                else{
                    if(commonModel.error == null){
                        if(commonModel.statusCode == 200){
                            var manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this@SusQuestionActivity)
                            manageFlowAfterTest.isSurveyQuestionsSubmitted = true
                            manageFlowAfterTest.isSusQuestionsSubmitted = true
                            manageFlowAfterTest.moveToWhichActivity(this@SusQuestionActivity)

                        }
                        else{
                            showErrorDialog(commonModel)
                        }
                    }
                    else{
                        showWentWrongDialog()
                    }
                }
            }

        })
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        showBackWarning()
    }

    private fun showBackWarning() {
        val builder =
            AlertDialog.Builder(this, R.style.AppTheme_MaterialDialogTheme)
        builder.setMessage("Are you sure you want leave this test?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                dialog.dismiss()
                moveToHome()
            }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.dismiss() }
        back_alert = builder.create()
        back_alert?.show()
    }

    private fun moveToHome(){
        var intent = Intent(SusQuestionActivity@this,TabActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()

        OkAlertDialog.dismissOkAlert()
        ProgressDialog.dismissProgressDialog()
    }

    override fun onDestroy() {
        super.onDestroy()

        OkAlertDialog.dismissOkAlert()
        ProgressDialog.dismissProgressDialog()
    }
}
