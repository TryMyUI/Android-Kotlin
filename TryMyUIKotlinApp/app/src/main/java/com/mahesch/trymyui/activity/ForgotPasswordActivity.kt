package com.mahesch.trymyui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.OkAlertDialog
import com.mahesch.trymyui.helpers.ProgressDialog
import com.mahesch.trymyui.helpers.ShowInternetAlert
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.model.FeedbackModel
import com.mahesch.trymyui.receivers.ConnectivityReceiver
import com.mahesch.trymyui.viewmodelfactory.ForgotPasswordViewModelFactory
import com.mahesch.trymyui.viewmodels.ForgotPasswordActivityViewModel
import kotlinx.android.synthetic.main.forgot_password_activity.*

class ForgotPasswordActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var forgotPasswordActivityViewModel: ForgotPasswordActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_activity)

        et_email.setText("Mahesh@m.com")

        initializeProgressDialog()

        button_resetpassword.setOnClickListener(View.OnClickListener { onClickResetPassword() })

        iv_back.setOnClickListener { onClickBackIcon() }

        val factory =
            ForgotPasswordViewModelFactory(
                application
            )
        forgotPasswordActivityViewModel = ViewModelProvider(this,factory).get(ForgotPasswordActivityViewModel::class.java)

    }

    private fun onClickBackIcon(){
        var intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        //super.onBackPressed()

        onClickBackIcon()

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
                et_email.background = resources.getDrawable(R.drawable.red_round_border_white_fill)
                et_email.setHintTextColor(resources.getColor(R.color._F4523D))

                if (et_email.getText().toString().length > 0) {
                    et_email.setText("")
                    et_email.hint = resources.getString(R.string.invalidemail)
                    et_email.background = resources.getDrawable(R.drawable.red_round_border_white_fill)
                    et_email.setHintTextColor(resources.getColor(R.color._F4523D))
                } else {
                    et_email.hint = "Email"
                    et_email.background = resources.getDrawable(R.drawable.red_round_border_white_fill)
                    et_email.setHintTextColor(resources.getColor(R.color._F4523D))
                }
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

            OkAlertDialog.showOkAlert(this.resources.getString(R.string.went_wrong))
        }
        else
        {
            OkAlertDialog.showOkAlert(feedbackModel.message)
        }

        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }

    }


    fun forgotPasswordErrorHandling(error : Throwable?){
        var okBtn =  OkAlertDialog.initOkAlert(this)
        OkAlertDialog.showOkAlert(this.resources.getString(R.string.went_wrong))
        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
    }


    fun forgotPasswordResponseHandling(feedbackModel: FeedbackModel?){

        if(feedbackModel?.status_code == 200){
            Utils.showToast(this,"Email sent")


            //Utils.showErrorDialog1(ForgotPasswordActivity.this, Utils.TAG_ERROR, "This is temporary dialog");

                val statusCode: String = feedbackModel.status_code.toString()
               var  message = feedbackModel.message
                if (statusCode.equals("200", ignoreCase = true)) {
                    rl_password_sent.visibility = View.VISIBLE
                    findViewById<View>(R.id.tv_lvl_did_forgot_pass).visibility = View.GONE
                    findViewById<View>(R.id.tv_forgetpassworddesc).visibility = View.GONE
                    findViewById<View>(R.id.et_email).visibility = View.GONE
                    findViewById<View>(R.id.button_resetpassword).visibility = View.GONE

                } else {
                    showErrorDialog(feedbackModel)
                }


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
