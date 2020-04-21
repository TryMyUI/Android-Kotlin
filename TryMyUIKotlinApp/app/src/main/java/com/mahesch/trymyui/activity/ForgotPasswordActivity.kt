package com.mahesch.trymyui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.OkAlertDialog
import com.mahesch.trymyui.helpers.ProgressDialog
import com.mahesch.trymyui.helpers.ShowInternetAlert
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.model.FeedbackModel
import com.mahesch.trymyui.viewmodels.ForgotPasswordActivityViewModel
import com.mahesch.trymyui.receivers.ConnectivityReceiver
import com.mahesch.trymyui.viewmodelfactory.ForgotPasswordViewModelFactory
import kotlinx.android.synthetic.main.forgot_password_activity.*

class ForgotPasswordActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var forgotPasswordActivityViewModel: ForgotPasswordActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_activity)

        et_email.setText("Mahesh@m.com")

        initializeProgressDialog()

        btn_reset_password.setOnClickListener(View.OnClickListener { onClickResetPassword() })

        tv_return_to_sign_in.setOnClickListener { View.OnClickListener { onClickReturnToSignIn() } }

        val factory =
            ForgotPasswordViewModelFactory(
                application
            )
        forgotPasswordActivityViewModel = ViewModelProvider(this,factory).get(ForgotPasswordActivityViewModel::class.java)

    }

    override fun onBackPressed() {
        super.onBackPressed()

        dismissProgressDialog()

        dismissErrorDialog()
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

    private fun onClickResetPassword() {

        if(Utils.isInternetAvailable(this)){
            if(Utils.isValidEmail(et_email.text.toString())) {
                showProgressDialog()
                resetPassword()
            }
            else {
                et_email.error = "Email"
            }
        }
        else
        {
            Utils.showInternetCheckToast(this)
        }

    }


    private fun onClickReturnToSignIn(){
        onBackPressed()
    }

    private fun initializeProgressDialog(){
        ProgressDialog.initializeProgressDialogue(this)
    }


    private fun resetPassword(){
        forgotPasswordActivityViewModel.callForgotPassword(et_email.text.toString())?.observe(this,object : Observer<FeedbackModel>{
            override fun onChanged(feedbackModel: FeedbackModel?) {
                Log.e("FORGOTPASSWORD","FORGOTPASSWORDCALLED")

                dismissProgressDialog()

                if(feedbackModel == null){
                    showErrorDialog(feedbackModel)
                }
                else
                {
                    if(feedbackModel.error == null){
                        forgotPasswordResponseHandling(feedbackModel)
                    }
                    else{
                        var error: Throwable? = feedbackModel.error
                        forgotPasswordErrorHandling(error)
                    }
                }

            }

        })
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

    private fun showErrorDialog(feedbackModel: FeedbackModel?){

        var okBtn =  OkAlertDialog.initOkAlert(this)

        if(feedbackModel == null){

            OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        }
        else
        {
            OkAlertDialog.showOkAlert(feedbackModel.message)
        }

        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }

    }


    fun forgotPasswordErrorHandling(error : Throwable?){
        var okBtn =  OkAlertDialog.initOkAlert(this)
        OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
    }


    fun forgotPasswordResponseHandling(feedbackModel: FeedbackModel?){

        if(feedbackModel?.status_code == 200){
            Utils.showToast(this,"Email sent")
        }
        else{
            showErrorDialog(feedbackModel)
        }
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isFinishing)
            if (!isConnected)
                ShowInternetAlert.showInternetAlert(this@ForgotPasswordActivity)
    }


}
