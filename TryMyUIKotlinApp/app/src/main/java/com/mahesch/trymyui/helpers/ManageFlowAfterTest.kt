package com.mahesch.trymyui.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.mahesch.trymyui.model.AvailableTestModel

class ManageFlowAfterTest(availableTestModel: AvailableTestModel,context: Context) {

    var surveyQuestions: String? = null
    var susQuestions: String? = null
    var npsQuestion: String? = null
    var uxCrowdSurvey: String? = null

    var isSurveyQuestionsSubmitted : Boolean = false
    var  isSusQuestionsSubmitted : Boolean = false
    var isNpsQuestionSubmitted : Boolean = false
    var isUXCrowdSurveySubmitted : Boolean = false

    var availableTestModel : AvailableTestModel
    var context : Context

    init {
        this.availableTestModel = availableTestModel
        this.context = context
    }

    fun setSurveyQuestion(surveyQuestions: String?){
        this.surveyQuestions = surveyQuestions
    }

    fun setSusQuestion(susQuestions: String?){
        this.susQuestions = susQuestions
    }

    fun setNpsQuestions(npsQuestion: String?){
        this.npsQuestion = npsQuestion
    }

    fun setUxCrowdSurveys(uxCrowdSurvey: String?){
        this.uxCrowdSurvey = uxCrowdSurvey
    }

    fun getSurveyQuestion() : String?{
        return surveyQuestions
    }

    fun getSusQuestion(): String?{
        return susQuestions
    }

    fun getNpsQuestions(): String?{
        return npsQuestion
    }

    fun getUxCrowdSurveys(): String?{
        return uxCrowdSurvey
    }

    fun setSurveyQuestionsSubmittedStatus(isSurveyQuestionsSubmitted : Boolean){
        this.isSurveyQuestionsSubmitted = isSurveyQuestionsSubmitted
    }

    fun setSusQuestionsSubmittedStatus(isSusQuestionsSubmitted : Boolean){
        this.isSusQuestionsSubmitted  = isSusQuestionsSubmitted
    }

    fun setNpsQuestionSubmittedStatus(isNpsQuestionSubmitted : Boolean){
        this.isNpsQuestionSubmitted = isNpsQuestionSubmitted
    }

    fun setUXCrowdSurveySubmittedStatus( isUXCrowdSurveySubmitted : Boolean){
        this.isUXCrowdSurveySubmitted = isUXCrowdSurveySubmitted
    }

    fun getSurveyQuestionsSubmittedStatus(): Boolean{
        return isSurveyQuestionsSubmitted
    }

    fun getSusQuestionsSubmittedStatus(): Boolean{
        return isSusQuestionsSubmitted
    }

    fun getNpsQuestionSubmittedStatus(): Boolean{
        return isNpsQuestionSubmitted
    }

    fun getUXCrowdSurveySubmittedStatus(): Boolean{
        return isUXCrowdSurveySubmitted
    }


    fun moveToWhichActivity(context: Context) {

        var sharedPrefHelper = SharedPrefHelper(context)

        if(surveyQuestions != null && !surveyQuestions.equals("[]",true) && !isSurveyQuestionsSubmitted){
            var intent = Intent()
            intent.putExtra("surveyQuestions", surveyQuestions)
            otherIntentParams(intent,sharedPrefHelper,context)
        }
        else if(susQuestions != null && !susQuestions.equals("[]",true) && !isSusQuestionsSubmitted){
            var intent = Intent()

            intent.putExtra("susQuestions", susQuestions)
            otherIntentParams(intent,sharedPrefHelper,context)

        }
        else if(npsQuestion != null && !npsQuestion.equals("[]",true) && !isNpsQuestionSubmitted){
            var intent = Intent()
            intent.putExtra("npsQuestion", npsQuestion)
            otherIntentParams(intent,sharedPrefHelper,context)
        }
        else if(uxCrowdSurvey != null && !uxCrowdSurvey.equals("[]",true) && !isUXCrowdSurveySubmitted){
            var intent = Intent()
            intent.putExtra("uxCrowdSurvey", uxCrowdSurvey)
            otherIntentParams(intent,sharedPrefHelper,context)
        }
        else {
           //SHOW THUMBS UP DIALOG
        }
    }

    private fun otherIntentParams(intent: Intent, sharedPrefHelper: SharedPrefHelper, context: Context){
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("availableTestConstant", availableTestModel)
        intent.putExtra("post_id", sharedPrefHelper.getTestResultId())
        context.startActivity(intent)
    }

}