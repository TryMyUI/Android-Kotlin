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
import com.mahesch.trymyui.viewmodelfactory.SpecialQualificationViewModelFactory
import com.mahesch.trymyui.viewmodels.SpecialQualificationViewModel
import kotlinx.android.synthetic.main.special_qualification_activity.*

class SpecialQualificationActivity : AppCompatActivity() {

    private var TAG  = SpecialQualificationActivity::class.simpleName?.toUpperCase()

    private lateinit var  specQualViewModel : SpecialQualificationViewModel
    private lateinit var availableTestModel: AvailableTestModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.special_qualification_activity)

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

      //  ManageFlowBeforeRecording(availableTestModel,SpecialQualificationActivity@this).isSpecialQualificationVisited = true

        val factory =  SpecialQualificationViewModelFactory(application)

        specQualViewModel = ViewModelProvider(this,factory).get(SpecialQualificationViewModel::class.java)

        tv_spec_qual_data.text = availableTestModel.specialQalification

        btn_spec_qual_yes.setOnClickListener { onClickYes() }

        btn_spec_qual_no.setOnClickListener { onClickNo() }
    }

    private fun onClickYes(){
            ManageFlowBeforeRecording(availableTestModel,SpecialQualificationActivity@this).moveToWhichActivity(1)
    }

    private fun onClickNo(){

        if(Utils.isInternetAvailable(SpecialQualificationActivity@this))
        postSpecCriteriaResponse()
        else
            Utils.showInternetCheckToast(SpecialQualificationActivity@this)
    }

    private fun moveToHome(){
        var intent = Intent(SpecialQualificationActivity@this,TabActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun postSpecCriteriaResponse(){

        ProgressDialog.initializeProgressDialogue(this)
        ProgressDialog.showProgressDialog()

        SharedPrefHelper(SpecialQualificationActivity@this).getToken()?.let {
            specQualViewModel.postSpecialResponse(it,availableTestModel.id.toString())?.observe(this,
                Observer<SpecialTechnicalModel> {
                    moveToHome()
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

    override fun onBackPressed() {
        //super.onBackPressed()
        ManageFlowBeforeRecording(availableTestModel,this).manageBackFlow(0)
    }
}
