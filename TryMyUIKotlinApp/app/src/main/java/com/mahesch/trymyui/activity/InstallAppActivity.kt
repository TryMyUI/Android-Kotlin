package com.mahesch.trymyui.activity

import android.annotation.TargetApi
import android.app.*
import android.content.*
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Browser
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.ProgressDialog.Companion.progressDialog
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.receivers.BrowserIntentReceiver
import com.mahesch.trymyui.services.NativeAppRecordingService
import kotlinx.android.synthetic.main.install_app_activity.*
import java.io.*
import java.net.URL

class InstallAppActivity : AppCompatActivity() {


    private var popupWindow: PopupWindow? = null
    private var show_confirmation_dialog: Dialog? = null
    private var app_package_name: String? = null


   var TAG = InstallAppActivity::class.java.simpleName.toUpperCase()
    private var sharedPrefHelper: SharedPrefHelper? = null

    private var availableTestConstant: AvailableTestModel? = null
    private var progressDialog: ProgressDialog? = null

    private var chatHeadService: NativeAppRecordingService? = null
    private var bound = false
    

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder: NativeAppRecordingService.LocalBinder = service as NativeAppRecordingService.LocalBinder
            chatHeadService = binder.getService()
            bound = true
            //            chatHeadService.minimize();
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.install_app_activity)

        sharedPrefHelper = SharedPrefHelper(this)
        availableTestConstant =
            intent.getSerializableExtra("availableTestConstants") as AvailableTestModel
        if (availableTestConstant == null) {
            Toast.makeText(this, resources.getString(R.string.went_wrong), Toast.LENGTH_LONG)
                .show()
            moveToDashBoard()
        }
        progressDialog = ProgressDialog(this)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Please wait...")
        btn_install_app.setOnClickListener(View.OnClickListener { downloadApk() })
        ll_three_dots.setOnClickListener(View.OnClickListener { v -> showMenuPopUp(v) })
        tv_leave_test.setOnClickListener(View.OnClickListener { showConfirmation() })
    }

    private fun downloadApk() {
        if (availableTestConstant?.interface_type.equals("app",true)) {
            Log.e(TAG, "Native app test " + availableTestConstant?.native_app_url)
            if (availableTestConstant?.native_app_url != null) {
                if (!availableTestConstant?.native_app_url.equals("")) {
                    val file = File(
                        Utils.getNativeAppTestApkPath()
                    )
                    Log.e(TAG, "file " + file.absolutePath)
                    if (file.exists()) {

                        //  file.delete();
                        Log.e(TAG, "file exists")
                        val isfileAvailable: Boolean = file.delete()
                        Log.e(TAG, "isfileAvailable $isfileAvailable")
                        Log.e(TAG, "file.exists " + file.exists())
                        app_package_name = CheckAppAreadyInstall()

                        // TODO else other regular url
                        // start app test which apk is provided by url

                        //   StartAppWithAPK();
                        //  ShowConfirmationOfAPK();
                        DownloadFileAsync(availableTestConstant?.native_app_url!!).execute()
                    } else {
                        Log.e(TAG, "file doesnt exist")
                        //                        ShowConfirmationOfAPK();
                        DownloadFileAsync(availableTestConstant?.native_app_url!!).execute()
                    }
                } else {
                    AppSelection()
                }
            } else {
                AppSelection()
            }
        } else {
            if (availableTestConstant?.native_app_url != null) {
                if (!availableTestConstant?.native_app_url.equals("")) {
                    val file = File(
                        Utils.getNativeAppTestApkPath())
                    if (file.exists()) {
                        app_package_name = CheckAppAreadyInstall()

                        // TODO else other regular url
                        // start app test which apk is provided by url
                        StartAppWithAPK()
                    } else {
                        //  ShowConfirmationOfAPK();
                        DownloadFileAsync(availableTestConstant?.native_app_url!!).execute()
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        WebSelection()
                    } else {
                        WebSelectionForLessthanMarshmellow()
                    }
                }
            }
        }
    }

    private fun moveToDashBoard() {
        val intent = Intent(this, TabActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showMenuPopUp(view1: View) {
        val density = resources.displayMetrics.density
        popupWindow =
            PopupWindow(view1, density.toInt() * 240, WindowManager.LayoutParams.WRAP_CONTENT, true)
        val view: View
        val layoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(R.layout.pop_up_menu, null)
        popupWindow!!.isFocusable = true
        popupWindow!!.contentView = view
        popupWindow!!.showAtLocation(
            view1,
            Gravity.TOP or Gravity.RIGHT,
            0,
            rl_testdetails.getHeight() + 42
        )
        val tv_feedback =
            view.findViewById<View>(R.id.tv_menu_feedback) as TextView
        val tv_logout =
            view.findViewById<View>(R.id.tv_menu_logout) as TextView
        val tv_menu_leavetest =
            view.findViewById<View>(R.id.tv_menu_leavetest) as TextView
        val tv_menu_reportissue =
            view.findViewById<View>(R.id.tv_menu_reportissue) as TextView
        tv_feedback.visibility = View.GONE
        val vw_sendfeedback =
            view.findViewById(R.id.vw_sendfeedback) as View
        vw_sendfeedback.visibility = View.GONE
        tv_menu_reportissue.setOnClickListener {
            popupWindow!!.dismiss()
            val intentFeedback =
                Intent(this@InstallAppActivity, FeedbackActivity::class.java)
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            finish()
        }
        tv_logout.setOnClickListener {
            popupWindow!!.dismiss()
            val intentFeedback =
                Intent(this@InstallAppActivity, WelcomeActivity::class.java)
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            finish()
        }
        tv_menu_leavetest.setOnClickListener {
            popupWindow!!.dismiss()
            showConfirmation()
        }
    }

    fun showConfirmation() {
        val dialogBuilder =
            AlertDialog.Builder(this, R.style.AppTheme_MaterialDialogTheme)
        dialogBuilder.setTitle("Are you sure you want to leave this test?")
        dialogBuilder.setMessage("This will end your test and you will be redirected to your TryMyUI dashboard")
        dialogBuilder.setNegativeButton(
            R.string.cancel
        ) { dialog, which -> dialog.dismiss() }
        dialogBuilder.setPositiveButton(
            R.string.yes
        ) { dialog, which ->
            if (SharedPrefHelper(this@InstallAppActivity).getGuestTester()) {
                val intent =
                    Intent(this@InstallAppActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@InstallAppActivity, TabActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        show_confirmation_dialog = dialogBuilder.create()
        val dialogWindow = show_confirmation_dialog?.window
        val dialogWindowAttributes = dialogWindow?.attributes

// Set fixed width (280dp) and WRAP_CONTENT height
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogWindowAttributes)
        lp.width =
            WindowManager.LayoutParams.MATCH_PARENT //(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, getResources().getDisplayMetrics());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow?.attributes = lp
        show_confirmation_dialog?.show()
    }

    fun ShowConfirmationOfAPK() {
//        int http_text = url.lastIndexOf('/');
//        TestAppAPKFile = url.substring(http_text + 1);
        val dialogBuilder =
            AlertDialog.Builder(this, R.style.AppTheme_MaterialDialogTheme)
        dialogBuilder.setTitle("Download apk")
        dialogBuilder.setMessage("You will be downloading client's native application." + "Once download completes, install apk on to your device.")
        dialogBuilder.setPositiveButton(
            R.string.yes
        ) { dialog, which ->
            dialog.dismiss()
            DownloadFileAsync(availableTestConstant?.native_app_url!!).execute()
        }
        if (!isFinishing) {
            dialogBuilder.show()
        }
    }

    fun onGoToAnotherInAppStore(appPackageName: String) {
        var intent: Intent
        try {
            intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse("market://details?id=$appPackageName")
            startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
            Log.e(TAG, "ActivityNotFoundException onGoToAnotherInAppStore")
            intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
            startActivity(intent)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    /* Native app recording service started */
    @TargetApi(26)
    fun StartNativeAppRecording(
        packagename: String?,
        LaunchApp: Boolean
    ) {

        //start new service and send all info

        if(!NativeAppRecordingService.isServiceStarted){



            //check service already started
            val isService =
                isMyServiceRunning(NativeAppRecordingService::class.java)

            // stop if already service running
            if (isService) {
                val intent =
                    Intent(this@InstallAppActivity, NativeAppRecordingService::class.java)
                stopService(intent)
            }

            // delete all previous files
            Utils.DeleteFolderOfRecording()
            Utils.DeleteFaceRecordingFolder()

            if (LaunchApp) {
                val intent =
                    Intent(this@InstallAppActivity, NativeAppRecordingService::class.java)
                intent.putExtra("packagename", packagename)
                intent.putExtra("availableTestConstants", availableTestConstant)
                try {
                    /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }*/
                    startService(intent)
                } catch (ise: IllegalStateException) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    }
                }
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
                unregisterReceiver(PackageReceiver)
                val pm = packageManager
                val launchIntent = pm.getLaunchIntentForPackage(app_package_name)
                if (launchIntent != null) {
                    Log.e(TAG, "launch intent")
                    launchIntent.addCategory(Intent.CATEGORY_HOME)
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(launchIntent)
                }
                finish()
            } else {
                val intent =
                    Intent(this@InstallAppActivity, NativeAppRecordingService::class.java)
                intent.putExtra("availableTestConstant", availableTestConstant)
                intent.putExtra("packagename", packagename)
                try {
                    /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }*/
                    startService(intent)
                } catch (ise: IllegalStateException) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    }
                }
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

                // move app to background as just like (press home button)
                val homeIntent = getIntent()
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(homeIntent)
                finish()
                unregisterReceiver(PackageReceiver)
            }
        }
    }


    fun CheckAppAreadyInstall(): String? {
        val pm = packageManager
        val fullPath: String = Utils.getNativeAppTestApkPath() //Environment.getExternalStorageDirectory() + "/" + apkName;
        val info = pm.getPackageArchiveInfo(fullPath, 0)
        var packageName = ""
        packageName = if (info == null) "" else info.packageName
        Log.e(TAG, "packageName $packageName")
        return packageName
    }


    /* this method use to start app using apk name*/
    fun StartAppWithAPK() {
        val f_path: String = Utils.getNativeAppTestApkPath()
        val MainFile = File(f_path)
        Log.e(TAG, "StartAppWithAPK MainFile $MainFile")
        app_package_name = CheckAppAreadyInstall()
        Log.e(TAG, "app_package_name $app_package_name")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(
                this@InstallAppActivity,
                this@InstallAppActivity.applicationContext.packageName + ".provider",
                MainFile
            )
            val install = Intent(Intent.ACTION_INSTALL_PACKAGE)
            install.data = apkUri
            install.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            this@InstallAppActivity.startActivity(install)
            Log.e(TAG, "apkUri $apkUri")
        } else {
            val apkUri = Uri.fromFile(MainFile)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            startActivity(intent)
            Log.e(TAG, "version less than N")
        }
    }

     inner class DownloadFileAsync(apkUrl: String) :
        AsyncTask<Void?, String?, String?>() {

         private var apkUrl = apkUrl
         private var TAG = "DOWNLOADAPKFILE"

        override fun onPreExecute() {
            super.onPreExecute()
                progressDialog = ProgressDialog(this@InstallAppActivity)
                progressDialog?.setMessage("Downloading file...")
                progressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                progressDialog?.setCancelable(false)
                progressDialog?.show()

        }

         override fun onProgressUpdate(vararg progress: String?) {
             progressDialog?.progress = progress[0]?.toInt()!!
         }

        override fun onPostExecute(unused: String?) {
            if (!isFinishing()) {
                progressDialog?.dismiss()
                //  Toast.makeText(getApplicationContext(), "File store in your sdcard as " + TestAppAPKFile +"  "+unused, Toast.LENGTH_LONG).show();

                // start app test which apk is provided by url
                StartAppWithAPK()
            }
        }

         override fun doInBackground(vararg params: Void?): String? {
             var count: Int
             try {

//                String native_app_testing_url="http://oh-staging.s3.amazonaws.com/concept_mapping/27310/Amazon%20Shopping_in.amazon.mShop.android.shopping.apk?AWSAccessKeyId=AKIAIMDGE3CO2J2OYM6A&Expires=1522653452&Signature=nMQ9lutmtjkuGaI88uQ%2FuvFVANE%3D";
//                String native_app_testing_url="http://oh-staging.s3.amazonaws.com/concept_mapping/27310/sample-debug.apk?AWSAccessKeyId=AKIAIMDGE3CO2J2OYM6A&Expires=1522679910&Signature=nKZF3aVlg8Wzwe%2FaASmBrnU7QXU%3D";
                 val url = URL(apkUrl)

                 /*here apk file name ll save same as added in url example xyz.apk*/
                 val outputfile: String = Utils.getNativeAppTestApkPath()

                 //  String outputfile = "content://com.seattleapplab.trymyui.provider/external_files/TryMyUIAppTestAPK/TryMyUITest.apk";
                 Log.e(TAG, "outputfile $outputfile")
                 val urlConnection = url.openConnection()
                 Log.e(TAG, "urlConnection $urlConnection")
                 val lenghtOfFile = urlConnection.contentLength
                 Log.e(TAG, "lenghtOfFile $lenghtOfFile")
                 val input: InputStream = BufferedInputStream(url.openStream())
                 Log.e(TAG, "input $input")
                 val output: OutputStream = FileOutputStream(outputfile)
                 Log.e(TAG, "output $output")
                 val data = ByteArray(1024)
                 Log.e(TAG, "data[] $data")
                 var total: Long = 0
                 while (input.read(data).also { count = it } != -1) {
                     total += count.toLong()
                     publishProgress("" + (total * 100 / lenghtOfFile).toInt())
                     output.write(data, 0, count)
                     Log.e(TAG, "total $total")
                 }
                 output.flush()
                 output.close()
                 input.close()
             } catch (e: Exception) {
                 Log.e(TAG, "Exeption in doInBackground $e")
             }
             return null
         }
     }

    private val PackageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // handle install event here
            Log.d("heelllooooo", "appp installl handling")
            StartNativeAppRecording(app_package_name, true)
        }
    }

    @TargetApi(22)
    private fun WebSelection() {
        /*
         // final webview recording
        */
        if (!availableTestConstant?.url?.startsWith("http")!! && !availableTestConstant?.url?.startsWith("https")!!
        ) {
            availableTestConstant?.url = ("http" + availableTestConstant?.url)
        }

        if (!NativeAppRecordingService.isServiceStarted) {
            //check service already started
            val isService =
                isMyServiceRunning(NativeAppRecordingService::class.java)

            // stop if already service running
            if (isService) {
                val intent =
                    Intent(this@InstallAppActivity, NativeAppRecordingService::class.java)
                stopService(intent)
            }

            //start new service and send all info
            val intent =
                Intent(this@InstallAppActivity, NativeAppRecordingService::class.java)
            intent.putExtra("availableTestConstant", availableTestConstant)

            /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               startForegroundService(intent);
            } else {
                startService(intent);
            }*/startService(intent)
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

            /* open default browser*/
            val receiver = Intent(
                this@InstallAppActivity,
                BrowserIntentReceiver::class.java
            )
            val pendingIntent = PendingIntent.getBroadcast(
                this@InstallAppActivity,
                0,
                receiver,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val intentBrowser = Intent(Intent.ACTION_VIEW)
            intentBrowser.data = Uri.parse(availableTestConstant?.url)
            //  intentBrowser.setData(Uri.parse("https://www.dream11.com/"));
            intentBrowser.putExtra(
                Browser.EXTRA_APPLICATION_ID,
                this@InstallAppActivity.packageName
            )
            startActivity(
                Intent.createChooser(
                    intentBrowser,
                    "Choose browser",
                    pendingIntent.intentSender
                )
            )
            finish()
        }


        /* APK and navtive app recording with parameters*/
    }


    private fun WebSelectionForLessthanMarshmellow() {
        if (!availableTestConstant?.url?.startsWith("http")!! &&
            (!availableTestConstant?.url?.startsWith("https")!!)) {
            availableTestConstant?.url = ("http" + availableTestConstant?.url)
        }


        if (!NativeAppRecordingService.isServiceStarted) {
            //check service already started
            val isService =
                isMyServiceRunning(NativeAppRecordingService::class.java)

            // stop if already service running
            if (isService) {
                val intent =
                    Intent(this@InstallAppActivity, NativeAppRecordingService::class.java)
                stopService(intent)
            }

            //start new service and send all info
            val intent =
                Intent(this@InstallAppActivity, NativeAppRecordingService::class.java)
            intent.putExtra("availableTestConstant", availableTestConstant)
            startService(intent)
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            val intentBrowser = Intent(Intent.ACTION_VIEW)
            intentBrowser.data = Uri.parse(availableTestConstant?.url)
            intentBrowser.putExtra(
                Browser.EXTRA_APPLICATION_ID,
                this@InstallAppActivity.packageName
            )
            startActivity(Intent.createChooser(intentBrowser, "Choose browser"))
            finish()
        }
    }

    fun AppSelection() {
        Log.e(TAG, "App selection")
        val http_text: Int? = availableTestConstant?.url?.indexOf('/')
        Log.e(TAG, "url " + availableTestConstant?.url)
        val packagename: String? = http_text?.plus(2)?.let {
            availableTestConstant?.url?.substring(
                it
            )
        }

        //String packagename = "";
        Log.e(TAG, "package name $packagename")
        val intentApp = packageManager.getLaunchIntentForPackage(packagename)
        Log.e(TAG, "intent app $intentApp")
        if (intentApp != null) {
            StartNativeAppRecording(packagename, false)
            onGoToAnotherInAppStore(packagename!!)
        } else {

            // TODO else other regular url
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                WebSelection()
            } else {
                WebSelectionForLessthanMarshmellow()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        filter.addDataScheme("package")
        registerReceiver(PackageReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        if (progressDialog!!.isShowing) progressDialog!!.dismiss()
        if (popupWindow != null) {
            if (popupWindow!!.isShowing) {
                popupWindow!!.dismiss()
            }
        }
        if (show_confirmation_dialog != null) {
            if (show_confirmation_dialog!!.isShowing) {
                show_confirmation_dialog!!.dismiss()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog!!.isShowing) progressDialog!!.dismiss()
        if (popupWindow != null) {
            if (popupWindow!!.isShowing) {
                popupWindow!!.dismiss()
            }
        }
        if (show_confirmation_dialog != null) {
            if (show_confirmation_dialog!!.isShowing) {
                show_confirmation_dialog!!.dismiss()
            }
        }
    }
}
