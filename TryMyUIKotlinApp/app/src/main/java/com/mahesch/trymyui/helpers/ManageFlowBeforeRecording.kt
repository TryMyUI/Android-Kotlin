package com.mahesch.trymyui.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mahesch.trymyui.activity.*
import com.mahesch.trymyui.model.AvailableTestModel

class ManageFlowBeforeRecording(availableTestModel: AvailableTestModel?, context: Context) {

    val specialQualification : String? = availableTestModel?.specialQalification
    val technicalQualification: String? = availableTestModel?.technicalQualification
    val screenerEligibility: Boolean? = availableTestModel?.screener_test_available
    val faceRecording: Boolean? = availableTestModel?.opt_for_face_recording

    val isScreenerEligibilityVisited = false
    var context = context
    var availableTestModel = availableTestModel

    private var TAG = ManageFlowBeforeRecording::class.java.simpleName.toUpperCase()

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
            3 -> postScreenerEligibility()
            4 -> postFaceWarn()
            else -> {
                Log.e(TAG,"else running")
                callPerformTestActivity()
            }
        }


    }


    private fun callPerformTestActivity(){

        Log.e(TAG,"model before perform test "+availableTestModel)
        context.startActivity(Intent(context,PerformTestActivity::class.java).putExtra("availableTestConstants",availableTestModel))
    }


    private fun postDashBoard()
    {
        if(specialQualification != null && specialQualification.length >0)
        {
            context.startActivity(Intent(context,SpecialQualificationActivity::class.java).putExtra("availableTestConstants",availableTestModel))
        }
        else {
            postSpecQual()
        }
    }

    private fun postSpecQual(){


        if(technicalQualification != null && technicalQualification.length > 0){
            context.startActivity(Intent(context,TechnicalQualificationActivity::class.java).putExtra("availableTestConstants",availableTestModel))
        }
        else {
            postTechQual()
        }
    }

    private fun postTechQual(){
        if(screenerEligibility != null && screenerEligibility && !ApplicationClass.isScreenerVisited){
            context.startActivity(Intent(context,ScreenerEligibilityActivity::class.java).putExtra("availableTestConstants",availableTestModel))
        }
        else {
            postScreenerEligibility()
        }
    }


    private fun postFaceWarn(){

        callPerformTestActivity()
    }

    private fun postScreenerEligibility(){

        if(faceRecording != null && faceRecording ){
            context.startActivity(Intent(context,FaceRecordingInfoActivity::class.java).putExtra("availableTestConstants",availableTestModel))
        }else{
            callPerformTestActivity()
        }

    }


    fun manageBackFlow(fromWhichActivity: Int){
        when(fromWhichActivity){

            0 -> onBackSpecial()
            1 -> onBackTech()
            2 -> onBackScreener()
            3 -> onBackFaceRec()
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
        if(specialQualification != null && !(specialQualification.toString().equals("",true)))
        {
            context.startActivity(Intent(context,SpecialQualificationActivity::class.java).putExtra("availableTestConstants",availableTestModel))
            (context as Activity).finish()
        }
        else{
            onBackSpecial()
        }
    }

    private fun onBackScreener(){

    }

    private fun onBackFaceRec(){
        if(technicalQualification != null && !(technicalQualification.toString().equals("",true)))
        {
            context.startActivity(Intent(context,TechnicalQualificationActivity::class.java).putExtra("availableTestConstants",availableTestModel))
            (context as Activity).finish()
        }
        else {
            onBackTech()
        }
    }





}