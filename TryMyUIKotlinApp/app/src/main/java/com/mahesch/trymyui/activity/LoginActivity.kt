package com.mahesch.trymyui.activity

import LoginActivityViewModel
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
//import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.repository.SendGcmToServer
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import com.mahesch.trymyui.viewmodelfactory.LoginViewModelFactory
import kotlinx.android.synthetic.main.login_activiy.*


class LoginActivity : AppCompatActivity(){

    private val TAG: String? = LoginActivity::class.simpleName?.toUpperCase()

    private lateinit var loginActivityViewModel: LoginActivityViewModel

    private lateinit var sharedPrefHelper: SharedPrefHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activiy)

        editTextUsername.setText("Expert@trymyui.com")
        editTextPassword.setText("trymyui123")

        btn_login.setOnClickListener { btnSignInOnClickEvent() }

        textview_forget.setOnClickListener { tvForgotPasswordOnClickEvent() }

        guest_testing_btn.setOnClickListener { tvGuestLoginOnClickEvent() }

        sharedPrefHelper = SharedPrefHelper(this)
        sharedPrefHelper.clearSharedPreference()
        TabActivity.isPendingTest = false

        initializeProgressDialog()

        val factory =
            LoginViewModelFactory(
                application
            )

        loginActivityViewModel = ViewModelProvider(this,factory).get(LoginActivityViewModel ::class.java)

        setObserver()

    }

    private fun initializeProgressDialog(){
        ProgressDialog.initializeProgressDialogue(this)
    }

    private fun setObserver(){
        loginActivityViewModel.mutableLiveData?.observe(this, object : Observer<LoginResponseModel>{
            override fun onChanged(loginResponseModel: LoginResponseModel?) {
                Log.e(TAG,"seObserver onChaned")

                dismissProgressDialog()

                if(loginResponseModel == null){
                    //SHOW SOMETHING WENT WRONG DIALOG
                    showErrorDialog(loginResponseModel!!)
                }
                else{
                    if(loginResponseModel.error == null){
                        Log.e("LOGINACTIVITY","CHECK LOGIN CALLED")
                        callLoginResponseHandling(loginResponseModel)
                    }
                    else{
                        var error: Throwable? = loginResponseModel.error
                        callLoginErrorHandling(error)
                    }
                }
            }

        })

        loginActivityViewModel.mutableLiveData_dialog?.observe(this,object : Observer<LoginResponseModel>{
            override fun onChanged(t: LoginResponseModel?) {
                showErrorDialog(t!!)
            }

        })
    }



    private fun login(){

       /* loginActivityViewModel.callLogin(et_email_username.text.toString(),et_password.text.toString())?.observe(this,object : Observer<LoginResponseModel>{
            override fun onChanged(loginResponseModel: LoginResponseModel) {

                dismissProgressDialog()

                if(loginResponseModel == null){
                    //SHOW SOMETHING WENT WRONG DIALOG
                    showErrorDialog(loginResponseModel)
                }
                else{
                    if(loginResponseModel.error == null){
                        Log.e("LOGINACTIVITY","CHECK LOGIN CALLED")
                        callLoginResponseHandling(loginResponseModel)
                    }
                    else{
                        var error: Throwable? = loginResponseModel.error
                        callLoginErrorHandling(error)
                    }
                }
            }
        })*/

        loginActivityViewModel.callLogin(editTextUsername.text.toString(),editTextPassword.text.toString())
    }

    private fun btnSignInOnClickEvent() {
        if(Utils.isInternetAvailable(this)) {

            val isValidEmail = Utils.isValidEmail(editTextUsername.text.toString())
            val isValidPassword = Utils.isValidPassword(editTextPassword.text.toString())

            if(isValidEmail && isValidPassword){

                showProgressDialog()
                login()
            }
            else{
                if(!isValidEmail){
                    Utils.showToast(this,this.resources.getString(R.string.invalidemail))
                }

                if(!isValidPassword){
                    Utils.showToast(this,this.resources.getString(R.string.invalidpass))
                }
            }

        } else {
            //PROMPT DIALOGUE
            Utils.showInternetCheckToast(this)
        }

        Log.e(TAG,"btn_login clicked")
    }

    private fun tvForgotPasswordOnClickEvent() {
        Log.e(TAG,"textview_forget clicked")

        var intent = Intent(this,ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun tvGuestLoginOnClickEvent() {
        Log.e(TAG,"guest_testing_btn clicked")

        var intent = Intent(this,GuestLoginActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        var intent = Intent(this,WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

        dismissErrorDialog()

        dismissProgressDialog()


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


    private fun callLoginResponseHandling(loginResponseModel: LoginResponseModel){

        Log.e(TAG,"message "+loginResponseModel.message)

        if(loginResponseModel.statusCode == 200){

            if(loginResponseModel.data?.token != null){

                var sharedPrefHelper = SharedPrefHelper(this)

                sharedPrefHelper.saveToken(loginResponseModel.data?.token.toString())

                var userType = loginResponseModel.data?.user_type

                sharedPrefHelper.setUserType(userType)

                sharedPrefHelper.saveUserName(editTextUsername.text.toString())

              /*  if(userType.equals("worker",true))
                    getGcmToken()*/


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                      moveToNextActivity(sharedPrefHelper)

                }
                else
                {
                    moveToTabActivity()
                }

            }
            else
            {
                showErrorDialog(loginResponseModel)
            }
        }
        else
        {
            showErrorDialog(loginResponseModel)
        }
    }


    private fun callLoginErrorHandling(error : Throwable?){
        var okBtn =  OkAlertDialog.initOkAlert(this)
        OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
    }


    private fun showErrorDialog(loginResponseModel: LoginResponseModel){

        var okBtn =  OkAlertDialog.initOkAlert(this)

        if(loginResponseModel == null){

            OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        }
        else
        {
            OkAlertDialog.showOkAlert(loginResponseModel.message)
        }

        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }

    }

    private fun showProgressDialog(){
        ProgressDialog.showProgressDialog()
    }

    private fun dismissProgressDialog(){
        ProgressDialog.dismissProgressDialog()
    }

    private fun dismissErrorDialog(){
        OkAlertDialog.dismissOkAlert()

        YesNoAlertDialog.dismissYesNoDialogue()
    }


    /*private fun getGcmToken(){
        if(Utils.checkGooglePlayServices(LoginActivity@this)){
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                if (!it.isSuccessful){
                    Log.w(TAG, "getInstanceId failed", it.getException())
                    return@addOnCompleteListener
                }

                var fcm_token = it.result?.token

                if(fcm_token != null){
                    sharedPrefHelper.setMyRegisterID(fcm_token)

                    if(Utils.isInternetAvailable(this))
                        sendGcmToServer()
                }

            }
        }

    }*/


    private fun sendGcmToServer(){

        var apiService =  RetrofitInstance.getPostApiService()

        var token = sharedPrefHelper.getToken()
        var registerId = sharedPrefHelper.getMyRegisterID()
        var phoneType = "android phone"
        val deviceUniqId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        var jsonObject = JsonObject()
        jsonObject.addProperty("token",token)
        jsonObject.addProperty("device_uniq_id",deviceUniqId)
        jsonObject.addProperty("device_type",phoneType)
        jsonObject.addProperty("registration_id",registerId)


        var sendGcmToServer = SendGcmToServer(apiService)
        sendGcmToServer.callGcmRegistrationToken(JsonParser().parse(jsonObject.toString()) as JsonObject)
    }

    private fun moveToTabActivity(){
        Utils.moveToHome(this)
    }

    private fun moveToNextActivity(sharedPrefHelper: SharedPrefHelper){

        if(sharedPrefHelper.getHelperFlag())
            Utils.moveToHome(this)
        else {
        //    Utils.moveToNextActivityAndFinishCurrent(this,GetToKnowTestingActivity::class.java as Class<AppCompatActivity>)
            var intent = Intent(this,GetToKnowTestingActivity::class.java)
            startActivity(intent)
            finish()
        }


    }






}
