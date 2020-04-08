package com.mahesch.trymyui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.receivers.ConnectivityReceiver
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import kotlinx.android.synthetic.main.feedback_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedbackActivity : AppCompatActivity() ,ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var sharedPrefHelper: SharedPrefHelper
    private var  TAG = FeedbackActivity::class.java.simpleName.toUpperCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback_activity)

        ApplicationClass.getInstance()?.setConnectivityListener(this)

        actionBarSetup()

        sharedPrefHelper = SharedPrefHelper(FeedbackActivity@this)

        ProgressDialog.initializeProgressDialogue(FeedbackActivity@this)

        buttonOk.setOnClickListener {
            if(editTextFeedback.text.isNotEmpty()){

                if(Utils.isInternetAvailable(FeedbackActivity@this)){

                    ProgressDialog.showProgressDialog()
                    sendFeedback()}
                else
                    Utils.showInternetCheckToast(FeedbackActivity@this)
            }
            else{
                Utils.showToast(FeedbackActivity@this,"Please enter feedback")
            }
        }
    }

    private fun actionBarSetup(){
       Utils.actionBarSetup(actionBar,"Feedback")
    }


    private fun sendFeedback(){
        var apiInterface = RetrofitInstance.getPostApiService()
        var token =sharedPrefHelper.getToken()
        var username = sharedPrefHelper.getUsername()
        var email = sharedPrefHelper.getEmailId()
        var channelTypeFeedback = ApiService.Channel_Type_Feedback
        var message = editTextFeedback.text.toString()
        var subject = ApiService.App_Type+" Feedback Report @ "+username
        var device = resources.getString(R.string.device)

        if(sharedPrefHelper.getGuestTester()){

                sendFeedbackForGuest(username,email,channelTypeFeedback,message,subject,device,apiInterface)
        }
        else{
            sendFeedbackForWorkerAndCustomer(token,channelTypeFeedback,message,subject,device,apiInterface)
        }
    }


    private fun sendFeedbackForGuest(username: String?,email: String?,channelTypeFeedback: String,message: String?,
                                     subject: String, device: String,apiInterface: ApiService.ApiInterface){

        ProgressDialog.dismissProgressDialog()
       var call =  apiInterface.postFeedBack(username,email,channelTypeFeedback,message,subject,device)

        call.enqueue(object : Callback<CommonModel> {

            override fun onFailure(call: Call<CommonModel>, t: Throwable) {

                Log.e(TAG, "ONFAILURE")

                OkAlertDialog.initOkAlert(this@FeedbackActivity)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
                OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))

            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {

                Log.e(TAG, "ONRESPONSE")
                Log.e(TAG, "code " + response.code())
                Log.e(TAG, "message " + response.message())

                Utils.showToast(this@FeedbackActivity,"Feedback successfully sent")
                startActivity(Intent(this@FeedbackActivity,TabActivity::class.java) )
                finish()


            }

        })

    }


    private fun sendFeedbackForWorkerAndCustomer(token: String?,channelTypeFeedback: String,message: String,
                                                 subject: String, device: String,apiInterface: ApiService.ApiInterface){
        ProgressDialog.dismissProgressDialog()
        var call =  apiInterface.postFeedBack(token,channelTypeFeedback,message,subject,device)

        call.enqueue(object : Callback<CommonModel> {

            override fun onFailure(call: Call<CommonModel>, t: Throwable) {

                Log.e(TAG, "ONFAILURE")

                OkAlertDialog.initOkAlert(this@FeedbackActivity)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
                OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))

            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {

                Log.e(TAG, "ONRESPONSE")
                Log.e(TAG, "code " + response.code())
                Log.e(TAG, "message " + response.message())

                Utils.showToast(this@FeedbackActivity,"Feedback successfully sent")
                startActivity(Intent(this@FeedbackActivity,TabActivity::class.java) )
                finish()


            }

        })
    }



    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        TODO("Not yet implemented")
        Utils.showInternetCheckToast(FeedbackActivity@this)
    }

    override fun onStop() {
        super.onStop()

        ProgressDialog.dismissProgressDialog()
    }

    override fun onDestroy() {
        super.onDestroy()

        ProgressDialog.dismissProgressDialog()
    }


    override fun onBackPressed() {
        super.onBackPressed()

        ProgressDialog.dismissProgressDialog()
    }
}
