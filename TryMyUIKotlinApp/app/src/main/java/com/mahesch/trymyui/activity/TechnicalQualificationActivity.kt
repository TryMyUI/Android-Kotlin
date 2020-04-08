package com.mahesch.trymyui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.SpecialTechnicalModel
import com.mahesch.trymyui.viewmodelfactory.TechnicalQualificationViewModelFactory
import com.mahesch.trymyui.viewmodels.TechnicalQualificationViewModel
import kotlinx.android.synthetic.main.technical_qualification_activity.*

class TechnicalQualificationActivity : AppCompatActivity() {

    private var TAG  = TechnicalQualificationActivity::class.simpleName?.toUpperCase()
    private lateinit var  techQualViewModel : TechnicalQualificationViewModel
    private lateinit var availableTestModel: AvailableTestModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.technical_qualification_activity)

        if(intent != null){
            availableTestModel = intent.extras.getSerializable("availableTestConstants") as AvailableTestModel

            if(availableTestModel == null){
               moveToHome()
            }
        }
        else
        {
            moveToHome()
        }

        ManageFlowBeforeRecording(availableTestModel,TechnicalQualificationActivity@this).isTechnicalQualificationVisited = true


        val factory =  TechnicalQualificationViewModelFactory(application)

        techQualViewModel = ViewModelProvider(this,factory).get(TechnicalQualificationViewModel::class.java)

        tv_tech_qual_data.text = availableTestModel.technicalQualification

        btn_tech_qual_yes.setOnClickListener { onClickYes() }

        btn_tech_qual_no.setOnClickListener { onClickNo() }
    }


    private fun onClickYes(){
        ManageFlowBeforeRecording(availableTestModel,TechnicalQualificationActivity@this).moveToWhichActivity(2)
    }

    private fun onClickNo(){
        if(Utils.isInternetAvailable(TechnicalQualificationActivity@this))
            postTechCriteriaResponse()
        else
            Utils.showInternetCheckToast(TechnicalQualificationActivity@this)
    }

    private fun moveToHome(){
        var intent = Intent(TechnicalQualificationActivity@this,TabActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun postTechCriteriaResponse(){
        ProgressDialog.initializeProgressDialogue(this)
        ProgressDialog.showProgressDialog()

        SharedPrefHelper(TechnicalQualificationActivity@this).getToken()?.let {
            techQualViewModel.postTechnicalResponse(it,availableTestModel.id.toString())?.observe(this,
                Observer<SpecialTechnicalModel> { moveToHome()
                ProgressDialog.dismissProgressDialog()
                })
        }
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
