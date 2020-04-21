package com.mahesch.trymyui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.viewmodelfactory.UxCrowdViewModelFactory
import com.mahesch.trymyui.viewmodels.UxCrowdActivityViewModel
import com.seattleapplab.trymyui.models.Tests
import kotlinx.android.synthetic.main.ux_crowd_acitivity_activity.*

class UxCrowdActivity : AppCompatActivity() {

    private var TAG  = UxCrowdActivity::class.simpleName?.toUpperCase()
    private var  isFinish = false
    private lateinit var uxCrowdStringList : ArrayList<String>
    private lateinit var uxCrowdSurveyQuestion : String
    private lateinit var availableTestModel: AvailableTestModel
    private lateinit var uxCrowdActivityViewModel: UxCrowdActivityViewModel
    private var uxCrowdList = ArrayList<Tests.UXCrowdSurvey>()
    private lateinit var uxCrowdSurveyModel : Tests.UXCrowdSurvey
    private lateinit var manageFlowAfterTest : ManageFlowAfterTest
    private var questionIndex = 0

    private lateinit var questAnsMap : HashMap<Int,List<String>>
    private lateinit var id : ArrayList<Int>
    private lateinit var question: ArrayList<String>
    private lateinit var sharedPrefHelper: SharedPrefHelper

    private var back_alert : AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ux_crowd_acitivity_activity)

        if(intent != null){
            uxCrowdSurveyQuestion = intent.extras.getString("uxCrowdSurvey","")
            availableTestModel = intent.extras.getSerializable("availableTestConstants") as AvailableTestModel

            if(uxCrowdSurveyQuestion == null || availableTestModel == null){
                moveToHome()
            }
        }
        else
        {
            moveToHome()
        }

        manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this)

        sharedPrefHelper = SharedPrefHelper(this)

        val factory =
            UxCrowdViewModelFactory(
                application
            )

        uxCrowdActivityViewModel = ViewModelProvider(this,factory).get(UxCrowdActivityViewModel::class.java)

        parseSurveyQuestion(uxCrowdSurveyQuestion)

        textViewQuestion.text = question[questionIndex]

        setIndicators(questionIndex)

        setBackground()

        buttonNext.setOnClickListener { onNextClick() }

        buttonBack.setOnClickListener { onBackClick() }
    }


    private fun onBackClick(){
        uxCrowdStringList = java.util.ArrayList<String>()
        uxCrowdStringList.add(editTextAnswerOne.text.toString())
        uxCrowdStringList.add(editTextAnswerTwo.text.toString())


        if (questionIndex > 0) {
            questAnsMap.put(id[questionIndex], uxCrowdStringList)
            questionIndex--
            if (questAnsMap.containsKey(id[questionIndex])) {
                editTextAnswerOne.setText(questAnsMap.get(id[questionIndex])?.get(0).toString())
                editTextAnswerTwo.setText(questAnsMap.get(id[questionIndex])?.get(1).toString())
            } else {
                editTextAnswerOne.setText("")
                editTextAnswerTwo.setText("")
            }
            textViewQuestion.invalidate()
            textViewQuestion.text = ""
            textViewQuestion.text = "" + question[questionIndex]
            setIndicators(questionIndex)
        }
        if (questionIndex == question.size - 1) buttonNext.visibility =
            View.INVISIBLE else buttonNext.visibility =
            View.VISIBLE
        buttonNext.setImageDrawable(resources.getDrawable(R.drawable.ic_keyboard_arrow_right_white))

        if (questionIndex == 0) buttonBack.visibility = View.INVISIBLE else buttonBack.visibility =
            View.VISIBLE


        setBackground()
    }

    private fun onNextClick(){

        if((editTextAnswerOne.text.isEmpty()) or (editTextAnswerTwo.text.isEmpty())){
            Utils.showToast(this,"Please enter comments")
        }
        else{
            uxCrowdStringList = ArrayList<String>()
            uxCrowdStringList.add(editTextAnswerOne.text.toString())
            uxCrowdStringList.add(editTextAnswerTwo.text.toString())

            if(questionIndex == 2)
            {
                questAnsMap[id[questionIndex]] = uxCrowdStringList

                setIndicators(questionIndex)

                if(Utils.isInternetAvailable(this)){

                    ProgressDialog.initializeProgressDialogue(this)
                    ProgressDialog.showProgressDialog()
                    submitUxCrowdSurvey(makeJsonObjectForSubmitRequest(questAnsMap))

                }
                else{
                    Utils.showInternetCheckToast(this)
                }
            }
            else
            {
                if (questionIndex < question.size)
                {
                    questAnsMap.put(id[questionIndex], uxCrowdStringList)
                    questionIndex++
                    setIndicators(questionIndex)
                    textViewQuestion.invalidate()
                    textViewQuestion.text = ""
                    textViewQuestion.text = "" + question[questionIndex]

                    if (questAnsMap.containsKey(id[questionIndex]))
                    {
                        editTextAnswerOne.setText(questAnsMap[id[questionIndex]]?.get(0).toString())
                        editTextAnswerTwo.setText(questAnsMap[id[questionIndex]]?.get(1).toString())
                    }
                    else
                    {
                        editTextAnswerOne.setText("")
                        editTextAnswerTwo.setText("")
                    }
                }

                editTextAnswerOne.requestFocus()
            }

            if (questionIndex == question.size - 1)
            {
                buttonNext.visibility = View.INVISIBLE
            }


            if (questionIndex == 0)
            {
                setIndicators(questionIndex)
                buttonBack.visibility = View.INVISIBLE
            }
            else
            {
                setIndicators(questionIndex)
                buttonBack.visibility = View.VISIBLE
            }
        }

        setBackground()
    }

    private fun showWentWrongDialog(){
        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))
    }

    private fun showErrorDialog(commonModel: CommonModel){
        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(commonModel.message)
    }


    private fun submitUxCrowdSurvey(jsonObject: JsonObject){

        uxCrowdActivityViewModel.submitUxCrowd(jsonObject).observe(this,
            Observer<CommonModel> { commonModel ->

                ProgressDialog.dismissProgressDialog()

                if(commonModel == null){
                    showWentWrongDialog()
                } else{
                    if(commonModel.error == null){
                        if(commonModel.statusCode == 200){
                            var manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this@UxCrowdActivity)
                            manageFlowAfterTest.isSurveyQuestionsSubmitted = true
                            manageFlowAfterTest.isSusQuestionsSubmitted = true
                            manageFlowAfterTest.isNpsQuestionSubmitted = true
                            manageFlowAfterTest.isUXCrowdSurveySubmitted = true
                            manageFlowAfterTest.moveToWhichActivity(this@UxCrowdActivity)
                        } else{
                            showErrorDialog(commonModel)
                        }
                    } else{
                        showWentWrongDialog()
                    }
                }
            })
    }


    private fun makeJsonObjectForSubmitRequest(quesAnsMap: HashMap<Int,List<String>>) : JsonObject{

            var jsonObject = JsonObject()

            var jsonAnswer  = JsonObject()

            for(i in 0 until 3){
                var jsonArray = JsonArray()
                jsonArray.add(questAnsMap[id[i]]?.get(0))
                jsonArray.add(questAnsMap[id[i]]?.get(1))

                jsonObject.add(id[i]?.toString(),jsonArray)

            }

            isFinish = true

                jsonAnswer.addProperty("at",sharedPrefHelper.getToken())
                jsonAnswer.addProperty("test_result_id",sharedPrefHelper.getTestResultId())
                jsonAnswer.addProperty("is_finished",isFinish)
                jsonAnswer.add("answers",jsonObject)

        return JsonParser().parse(jsonAnswer.toString()) as JsonObject
    }



    private fun setBackground(){

        when(questionIndex){
            0 -> goodQuestionBg()
            1 -> badQuestionBg()
            2 -> suggestionQuestionBg()
        }
    }

    private fun goodQuestionBg(){
        uxcrowd_main_layout.background = resources.getDrawable(R.drawable.shadow_layout_green)
        uxcrowd_image.setImageDrawable(getDrawable(R.drawable.ic_thumb_up_white))
        bottom_layout.setBackgroundColor(resources.getColor(R.color.green))
        linearlayout_main.setBackgroundColor(resources.getColor(R.color.green))
    }

    private fun badQuestionBg(){
        uxcrowd_main_layout.background = resources.getDrawable(R.drawable.shadow_layout_red)
        uxcrowd_image.setImageDrawable(getDrawable(R.drawable.ic_thumb_down_white))
        bottom_layout.setBackgroundColor(resources.getColor(R.color.red))
        linearlayout_main.setBackgroundColor(resources.getColor(R.color.red))
    }

    private fun suggestionQuestionBg(){
        uxcrowd_main_layout.background = resources.getDrawable(R.drawable.shadow_layout_blue)
        uxcrowd_image.setImageDrawable(getDrawable(R.drawable.ic_idea_white))
        bottom_layout.setBackgroundColor(resources.getColor(R.color.background_blue))
        linearlayout_main.setBackgroundColor(resources.getColor(R.color.background_blue))
        buttonNext.visibility = View.VISIBLE
        buttonNext.setImageDrawable(resources.getDrawable(R.drawable.ic_check_white))
    }


    private fun moveToHome(){
        var intent = Intent(UxCrowdActivity@this,TabActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()

        ProgressDialog.dismissProgressDialog()
        OkAlertDialog.initOkAlert(this)
        OkAlertDialog.dismissOkAlert()
    }

    override fun onDestroy() {
        super.onDestroy()

        ProgressDialog.dismissProgressDialog()
        OkAlertDialog.initOkAlert(this)
        OkAlertDialog.dismissOkAlert()
    }

    private fun setIndicators(index: Int){
        for(i in 0 until question.size){
            if(i == index){
                var imageView = findViewById<ImageView>(index)
                imageView.setImageResource(R.drawable.black_circle_img)
            }
            else{
                var imageView = findViewById<ImageView>(i)
                imageView.setImageResource(R.drawable.white_circle_img)
            }
        }
    }

    private fun createCircleView(size: Int){
        var linearLayout = findViewById<LinearLayout>(R.id.linearLayoutCircleView)
        linearLayout.weightSum = size.toFloat()

        for(i in 0 until size){
            var params = LinearLayout.LayoutParams(0,25)
            params.weight = 1.0f

            val image = ImageView(this)
            image.maxHeight = 25
            image.maxWidth = 0

            if (i == questionIndex) {
                image.setImageResource(R.drawable.black_circle_img)
            } else {
                image.setImageResource(R.drawable.white_circle_img)
            }
            image.layoutParams = params

            image.id = i

            linearLayout.addView(image)
        }
    }

    private fun parseSurveyQuestion(uxCrowdQuestionJson: String){

        var root = JsonParser().parse(uxCrowdQuestionJson) as JsonArray

        var size = root.size()
        questAnsMap = HashMap(size)
        id = ArrayList(size)
        question = ArrayList(size)

        createCircleView(size)

        for(i in 0 until size){
            val temp = root[i].asJsonObject

            id.add(temp.get("id").asInt)

            question.add(temp.get("question").asString)
        }



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
}
