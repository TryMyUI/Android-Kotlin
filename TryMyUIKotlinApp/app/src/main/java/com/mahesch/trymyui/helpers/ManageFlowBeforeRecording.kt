package com.mahesch.trymyui.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.mahesch.trymyui.activity.*
import com.mahesch.trymyui.model.AvailableTestModel

class ManageFlowBeforeRecording(availableTestModel: AvailableTestModel?, context: Context) {

    val specialQualification : String? = availableTestModel?.specialQalification
    val technicalQualification: String? = availableTestModel?.technicalQualification
    val screenerEligibility: Boolean? = availableTestModel?.screener_test_available
    val faceRecording: Boolean? = availableTestModel?.opt_for_face_recording

    var isSpecialQualificationVisited = false
    var isTechnicalQualificationVisited = false
    val isScreenerEligibilityVisited = false
    val isFaceRecordingVisited = false
    var context = context
    var availableTestModel = availableTestModel

    //TAB = 0
    //SPECQUAL = 1
    //TECHQUAL = 2
    //FACEQUAL = 3
    //SCREENER = 4

    fun moveToWhichActivity(fromWhichActivity: Int){

        when(fromWhichActivity){
            0 -> postDashBoard()
            1 -> postSpecQual()
            2 -> postTechQual()
            3 -> postFaceWarn()
            4 -> postScreenEligibility()
            else -> {
                callPerformTestActivity()
            }
        }


    }


    private fun callPerformTestActivity(){
        context.startActivity(Intent(context,PerformTestActivity::class.java).putExtra("availableTestConstants",availableTestModel))
    }


    private fun postDashBoard()
    {
        if(specialQualification != null && specialQualification.length > 0 && !isSpecialQualificationVisited)
        {
            context.startActivity(Intent(context,SpecialQualificationActivity::class.java).putExtra("availableTestConstants",availableTestModel))
        }
        else {
            postSpecQual()
        }
    }

    private fun postSpecQual(){


        if(technicalQualification != null && technicalQualification.length > 0 && !isTechnicalQualificationVisited){
            context.startActivity(Intent(context,TechnicalQualificationActivity::class.java).putExtra("availableTestConstants",availableTestModel))
        }
        else {
            postTechQual()
        }
    }

    private fun postTechQual(){
        if(faceRecording != null && faceRecording && !isFaceRecordingVisited){
            context.startActivity(Intent(context,FaceRecordingInfoActivity::class.java).putExtra("availableTestConstants",availableTestModel))
        }
        else {
            postFaceWarn()
        }
    }

    private fun postFaceWarn(){
        if(screenerEligibility != null && screenerEligibility && !isScreenerEligibilityVisited){
            context.startActivity(Intent(context,ScreenerEligibilityActivity::class.java).putExtra("availableTestConstants",availableTestModel))
        }
        else
        {
            callPerformTestActivity()
        }
    }

    private fun postScreenEligibility(){

        callPerformTestActivity()
    }


    fun manageBackFlow(fromWhichActivity: Int){
            when(fromWhichActivity){

                0 -> onBackSpecial()
                1 -> onBackTech()
                2 -> onBackFaceRec()
                else -> {
                    onBackSpecial()
                }
            }
    }

    private fun onBackSpecial(){
        context.startActivity(Intent(context,TabActivity::class.java))
        (context as Activity).finish()

    }

    private fun onBackTech(){
        if(specialQualification != null && specialQualification.length.toString().isNotEmpty())
        {
            context.startActivity(Intent(context,SpecialQualificationActivity::class.java).putExtra("availableTestConstants",availableTestModel))
            (context as Activity).finish()
        }
        else{
            onBackSpecial()
        }
    }

    private fun onBackFaceRec(){
        if(technicalQualification != null && technicalQualification.length.toString().isNotEmpty())
        {
            context.startActivity(Intent(context,TechnicalQualificationActivity::class.java).putExtra("availableTestConstants",availableTestModel))
            (context as Activity).finish()
        }
        else {
            onBackTech()
        }
    }





}