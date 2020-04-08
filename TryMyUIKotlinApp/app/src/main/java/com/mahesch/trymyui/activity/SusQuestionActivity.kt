package com.mahesch.trymyui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.ManageFlowAfterTest
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.viewmodels.SusActivityViewModel
import com.mahesch.trymyui.viewmodelfactory.SusActivityViewModelFactory
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sus_question_activity)

        if(intent != null){
            susQuestion = intent.extras.getString("susQuestion","")
            availableTestModel = intent.extras.getSerializable("availableTestConstants") as AvailableTestModel

            if(susQuestion == null || availableTestModel == null){
                Utils.moveToHome(this)
            }

            manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this)
        }
        else
        {
            Utils.moveToHome(this)
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

        button_submit.setOnClickListener { onClickOfSusSubmit() }
    }


    private fun onClickOfSusSubmit(){

    }
}
