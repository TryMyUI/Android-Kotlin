package com.mahesch.trymyui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.OkAlertDialog
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.services.NativeAppRecordingService

class SplashActivity : AppCompatActivity() {

    private val TAG = SplashActivity::class.java.simpleName.toUpperCase()
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        sharedPrefHelper = SharedPrefHelper(SplashActivity@this)

        init()
    }

    private fun init(){


        if(Utils.isRecordingServiceRunning(NativeAppRecordingService::class.java,SplashActivity@this))
        {
            displayAlertForServiceIsRunning()
        }
        else{
            initSplashActivity()
        }

    }

    private fun displayAlertForServiceIsRunning(){
        var btn = OkAlertDialog.initOkAlert(this)

        OkAlertDialog.showOkAlert(resources.getString(R.string.display_alert_for_service_is_running))

        btn?.setOnClickListener {
            OkAlertDialog.dismissOkAlert()
            finish()
        }
    }

    private fun initSplashActivity(){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val window = window
        window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND)

        callNewActivity()
    }

    private fun callNewActivity(){

        // TODO Auto-generated method stub
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if(!sharedPrefHelper.getGuestTester())
        {
            var token = sharedPrefHelper.getToken()

            if(token != null)
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(sharedPrefHelper.getHelperFlag())
                    {
                        val intent = Intent(this@SplashActivity, TabActivity::class.java)
                        finish()
                        startActivity(intent)
                    }
                    else
                    {
                        val intent = Intent(this@SplashActivity, GetToKnowTestingActivity::class.java)
                        finish()
                        startActivity(intent)
                    }
                }
                else
                {
                    val intent = Intent(this@SplashActivity, GetToKnowTestingActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
            else
            {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            finish()
        }
        else
        {
            sharedPrefHelper.clearSharedPreference()
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
