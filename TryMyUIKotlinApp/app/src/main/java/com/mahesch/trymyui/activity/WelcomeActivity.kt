package com.mahesch.trymyui.activity

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.ApplicationClass
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.receivers.ConnectivityReceiver
import com.mahesch.trymyui.services.NativeAppRecordingService
//import com.afollestad.materialdialogs.MaterialDialog


class WelcomeActivity : AppCompatActivity() , ConnectivityReceiver.ConnectivityReceiverListener{


    var progressDialogUpdate: AlertDialog? = null
    var progressDialogWebAPI: AlertDialog? = null
    var mHelper: SharedPrefHelper? = null

    private val TAG = WelcomeActivity::class.java.simpleName.toUpperCase()
    private var btn_tmytester: TextView? = null
    private var btn_testwithuniquecode: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO on create layout-sw480dp initize
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        mHelper = SharedPrefHelper(this@WelcomeActivity)
        btn_testwithuniquecode = findViewById<View>(R.id.btn_testwithuniquecode) as TextView
        btn_tmytester = findViewById<View>(R.id.btn_tmy_tester) as TextView
        ApplicationClass.isScreenerVisited = false
        init()

        btn_tmytester!!.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_testwithuniquecode!!.setOnClickListener {
            val intent =
                Intent(this@WelcomeActivity, GuestLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return if (pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) true else false
    }

    fun init() {
        val isMyService = isMyServiceRunning(NativeAppRecordingService::class.java)
        if (isMyService && !isFinishing) {
            DisplayAlertForServiceIsRunning()
        } else {
            // todo initilse splashActivity

            btn_tmytester!!.setOnClickListener {
                val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            btn_testwithuniquecode!!.setOnClickListener {
                val intent =
                    Intent(this@WelcomeActivity, GuestLoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    fun DisplayAlertForServiceIsRunning() {
     /*   val dialogBuilder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(this@WelcomeActivity)
        dialogBuilder.setView(resources.getString(R.string.display_alert_for_service_is_running))
        dialogBuilder.content(resources.getString(R.string.display_alert_for_service_is_running))
        dialogBuilder.positiveText(R.string.ok)
        dialogBuilder.cancelable(false)
        dialogBuilder.positiveColor(resources.getColor(R.color.green))
        dialogBuilder.callback(object : Callback() {
            fun onPositive(dialog: MaterialDialog) {
                if (!isFinishing) {
                    dialog.dismiss()
                }
                finish()
            }

            fun onNegative(dialog: MaterialDialog) {
                if (!isFinishing) {
                    dialog.dismiss()
                }
                finish()
            }
        })
        val materialDialog: MaterialDialog = dialogBuilder.build()
        materialDialog.show()*/
    }





    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }



    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        // TODO register connection status listener
        ApplicationClass.getInstance()?.setConnectivityListener(this@WelcomeActivity)
    }


    override fun onPause() {
        super.onPause()
        val isMyService = isMyServiceRunning(NativeAppRecordingService::class.java)
        if (!isMyService) {
            if (!isFinishing && progressDialogUpdate != null && progressDialogUpdate!!.isShowing) {
                progressDialogUpdate!!.dismiss()
            }
            if (!isFinishing && progressDialogWebAPI != null && progressDialogWebAPI!!.isShowing) {
                progressDialogWebAPI!!.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isFinishing && progressDialogUpdate != null && progressDialogUpdate!!.isShowing) {
            if (!isFinishing && progressDialogUpdate != null && progressDialogUpdate!!.isShowing) {
                progressDialogUpdate!!.dismiss()
            }
            if (!isFinishing && progressDialogWebAPI != null && progressDialogWebAPI!!.isShowing()) {
                progressDialogWebAPI!!.dismiss()
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isFinishing) {
            if (!isConnected) {
                //Utils.InternetConnectionFailCutomAlert(this@WelcomeActivity)
            } else {
                callSelf()
            }
        }
    }

    fun callSelf() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}
