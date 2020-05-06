package com.mahesch.trymyui.activity

import android.annotation.TargetApi
import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Browser
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.DownloadApkFile
import com.mahesch.trymyui.helpers.ManageFlowAfterTest
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.helpers.YesNoAlertDialog
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.receivers.BrowserIntentReceiver
import com.mahesch.trymyui.services.NativeAppRecordingService
import kotlinx.android.synthetic.main.perform_test_activity.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PerformTestActivity : AppCompatActivity() {

    companion object{
        var app_package_name = ""

    }

    private lateinit var availableTestModel: AvailableTestModel

    private var TAG = PerformTestActivity::class.java.simpleName.toUpperCase()

    private lateinit var manageFlowAfterTest: ManageFlowAfterTest

    private var isServiceStarted = false

    private var chatHeadService: NativeAppRecordingService? = null

    private var bound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perform_test_activity)

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

        Log.e(TAG,"availableTestModel in perform test "+availableTestModel)

        /*  manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this)

          manageFlowAfterTest.moveToWhichActivity(this)*/

        textViewTitle.text = availableTestModel.title?.replace("\\n","\n",true)

        if(availableTestModel.url != null){
            if (!availableTestModel.url?.startsWith("http")!! && availableTestModel.url != "No title set")
                textViewURL.text = "http://$availableTestModel.url?"
            else
                textViewURL.text = availableTestModel.url
        }

        textViewScenario.text = availableTestModel.scenario

        buttonStartTest.setOnClickListener { onClickStartTest() }


    }

    private fun onClickStartTest(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            Log.e(TAG,"chckForpermission "+checkForPermission())

            if(checkForPermission()){
                startTest()
            }else{
                requestMultiplePermissions()
            }
        }
        else{
            //NO PERMISSION REQUIRED
            startTest()
        }
    }

    private fun checkForPermission(): Boolean{

        var listOfPermissions = ArrayList<String>()

        val arrayFromXml  = resources.getStringArray(R.array.permission_list)

        for(i in arrayFromXml.indices){
            listOfPermissions.add(arrayFromXml[i])
        }

        Log.e(TAG,"listof PErmissions "+listOfPermissions)

        for (permission in listOfPermissions) {
            if (ActivityCompat.checkSelfPermission(this@PerformTestActivity, permission) !== PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestMultiplePermissions(){

        var listOfPermissions = ArrayList<String>()

        val arrayFromXml  = resources.getStringArray(R.array.permission_list)

        for(i in arrayFromXml.indices){
            listOfPermissions.add(arrayFromXml[i])
        }

        var remainingPermissions = ArrayList<String>()

        for(permission in listOfPermissions){
            if (ActivityCompat.checkSelfPermission(this@PerformTestActivity, permission) !== PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission)
            }
        }

        Log.e(TAG,"remaining permission size "+remainingPermissions.size)

        ActivityCompat.requestPermissions(this@PerformTestActivity, remainingPermissions.toTypedArray(), 101)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        Log.e(TAG, "requestCode $requestCode")
        Log.e(TAG, "grantResults " + grantResults.size)
        Log.e(TAG, "permissions " + Arrays.toString(permissions))

        var all_grant_result_0 = true

        var request_permission_dialog = Dialog(PerformTestActivity@this)
        request_permission_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        request_permission_dialog.setContentView(R.layout.permission_denied_dialog)

        request_permission_dialog.setCancelable(false)

        val tv_turn_on_in_settings = request_permission_dialog.findViewById<View>(R.id.tv_turn_on_in_settings) as TextView
        val tv_exit = request_permission_dialog.findViewById<View>(R.id.tv_exit) as TextView

        tv_turn_on_in_settings.setOnClickListener {
            request_permission_dialog.dismiss()
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 0)
        }

        tv_exit.setOnClickListener { request_permission_dialog.dismiss() }

        if(requestCode == 101){
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "permissions" + i + " " + permissions[i])
                    all_grant_result_0 = false
                }
            }

            if (all_grant_result_0) startTest() else request_permission_dialog.show()
        }
    }

    private fun startTest(){
        Log.e(TAG,"start test")

        var native_app_test = availableTestModel?.native_app_url

        if(availableTestModel?.interface_type.equals("app",true)){

            // new apk uploading code
            if (native_app_test != null) {
                if (!native_app_test.equals("", ignoreCase = true)) {
                    val file = File(Utils.getNativeAppTestApkPath())
                    Log.e(TAG, "file " + file.absolutePath)
                    if (file.exists()) {

                        //  file.delete();
                        Log.e(TAG, "file exists")
                        val isfileAvailable: Boolean = file.delete()
                        Log.e(TAG, "isfileAvailable $isfileAvailable")
                        Log.e(TAG, "file.exists " + file.exists())
                        app_package_name = Utils.checkAppAlreadyInstall(this)

                        // TODO else other regular url
                        // start app test which apk is provided by url

                        showConfirmationOfApk()
                    } else {
                        Log.e(TAG, "file doesnt exist")
                        showConfirmationOfApk()
                    }
                } else {
                    appSelection()
                }
            } else {
                appSelection()
            }
        }
        else{
            if (native_app_test != null) {
                if (!native_app_test.equals("", ignoreCase = true)) {

                    val file = File(Utils.getNativeAppTestApkPath())

                    if (file.exists()) {
                        app_package_name = Utils.checkAppAlreadyInstall(this)

                        // start app test which apk is provided by url
                        startAppWithApk()
                    } else {
                        showConfirmationOfApk()
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        webSelection()
                    } else {
                        webSelectionForLessThanMarshMellows()
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    webSelection()
                } else {
                    webSelectionForLessThanMarshMellows()
                }
            }
        }

    }

    private fun moveToHome(){
        var intent = Intent(this,TabActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() { moveToHome() }

    override fun onResume() {
        super.onResume()

        var intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        intentFilter.addDataScheme("package")
        registerReceiver(packageReceiver,intentFilter)
    }


    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // handle install event here
            Log.e(TAG+" package receiver ", "app install handling")
            startNativeAppRecording(PerformTestActivity.app_package_name!!, true)
        }
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) { bound = false }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var binder = service as NativeAppRecordingService.LocalBinder
            chatHeadService = binder.getService()
            bound = true
        }
    }

    private fun startNativeAppRecording(appPackageNAme: String,launchApp: Boolean){

        //STOP CURRENT ONGOING SERVICE IF IT IS ON
            if(Utils.isMyServiceRunning(NativeAppRecordingService::class.java,this)){
                    stopService(Intent(this,NativeAppRecordingService::class.java))
            }

            //DELETE PREVIOUS FOLDER AND ITS CONTENT
            Utils.DeleteFolderOfRecording()
            Utils.DeleteFaceRecordingFolder()


            if(launchApp){
                var intent = Intent(this,NativeAppRecordingService::class.java)
                intent.putExtra("availableTestConstants",availableTestModel)
                intent.putExtra("packageName", app_package_name)

                try
                {
                    startService(intent)
                }
                catch (ise : IllegalStateException)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        startForegroundService(intent)
                    }
                }

                bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE)

                unregisterReceiver(packageReceiver)

                val launchIntent = packageManager.getLaunchIntentForPackage(appPackageNAme)

                if(launchIntent != null){
                    launchIntent.addCategory(Intent.CATEGORY_HOME)
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(launchIntent)
                }

                finish()

            }
            else
            {
                var intent = Intent(this,NativeAppRecordingService::class.java)
                intent.putExtra("availableTestConstants",availableTestModel)
                intent.putExtra("packageName", app_package_name)

                try{
                    startService(intent)
                }

                catch (ise : IllegalStateException ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }
                }

                bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE)

                var homeIntent = getIntent()
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(homeIntent)
                finish()
                unregisterReceiver(packageReceiver)
            }




    }

    private fun showConfirmationOfApk(){
        var btn_array = YesNoAlertDialog.initYesNoDialogue(this)

        btn_array!![0].setOnClickListener {

            if(Utils.isInternetAvailable(this)){
                YesNoAlertDialog.dismissYesNoDialogue()
                downloadApk()
            }
            else
            {
                Utils.showInternetCheckToast(this)
            }
        }

        btn_array[1].setOnClickListener { YesNoAlertDialog.dismissYesNoDialogue() }

        YesNoAlertDialog.showYesNoDialogue(title = resources.getString(R.string.download_apk),msg = resources.getString(R.string.download_apk_msg),positiveButtonText = "Yes",negativeButtonText = "No")
    }

    private fun downloadApk(){
        DownloadApkFile(this,availableTestModel?.native_app_url!!).execute()
    }

    @TargetApi(22)
    private fun webSelection(){
        if(!(availableTestModel?.url?.startsWith("http",true)!!) && !(availableTestModel?.url?.startsWith("https",true))!!){
                availableTestModel?.url = "http"+availableTestModel?.url
            }

        if(!isServiceStarted){
            var isService = Utils.isMyServiceRunning(NativeAppRecordingService::class.java,this)

            //STOP IF SERVICE IS ALREADY RUNNING
            if(isService){
                stopService(Intent(this,NativeAppRecordingService::class.java))
            }

            //START A NEW SERVICE AND SEND ALL INFO
            var intent = Intent(this,NativeAppRecordingService::class.java)
            intent.putExtra("availableTestConstants",availableTestModel)
            intent.putExtra("packageName", app_package_name)
            startService(intent)

            bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE)

            openBrowser()
        }

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun openBrowser(){
        var receiver = Intent(this,BrowserIntentReceiver::class.java)

        var pendingIntent = PendingIntent.getBroadcast(this,0,receiver,PendingIntent.FLAG_UPDATE_CURRENT)
        var intentBrowser = Intent(Intent.ACTION_VIEW)
        intentBrowser.setData(Uri.parse(availableTestModel?.url))
        intentBrowser.putExtra(Browser.EXTRA_APPLICATION_ID,this.packageName)
        startActivity(Intent.createChooser(intentBrowser,"Choose browser",pendingIntent.intentSender))
        finish()


    }

    private fun webSelectionForLessThanMarshMellows(){

        if(!(availableTestModel?.url?.startsWith("http",true)!!) && !(availableTestModel?.url?.startsWith("https",true))!!){
            availableTestModel?.url = "http"+availableTestModel?.url
        }

        if (!isServiceStarted) {
            //check service already started
            val isService: Boolean = Utils.isMyServiceRunning(NativeAppRecordingService::class.java,this)

            if (isService)
            {
                val intent = Intent(this@PerformTestActivity, NativeAppRecordingService::class.java)
                stopService(intent)
            }

            //start new service and send all info
            val intent = Intent(this@PerformTestActivity, NativeAppRecordingService::class.java)
            intent.putExtra("availableTestConstants", availableTestModel)

            startService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

            val intentBrowser = Intent(Intent.ACTION_VIEW)
            intentBrowser.data = Uri.parse(availableTestModel?.url)
            intentBrowser.putExtra(
                Browser.EXTRA_APPLICATION_ID,
                this@PerformTestActivity.packageName
            )
            startActivity(Intent.createChooser(intentBrowser, "Choose browser"))
            finish()
        }
    }

    private fun appSelection() {
        val http_text: Int = availableTestModel?.url?.indexOf('/')!!
        val packagename: String = availableTestModel?.url?.substring(http_text + 2)!!
        val intentApp = packageManager.getLaunchIntentForPackage(packagename)

        if (intentApp != null) {
            startNativeAppRecording(packagename, false)
            onGoToAnotherInAppStore(packagename)
        }
        else
        {

            // TODO else other regular url
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                webSelection()
            } else {
                webSelectionForLessThanMarshMellows()
            }
        }
    }

    private fun onGoToAnotherInAppStore(appPackageName: String) {
        var intent: Intent
        try {
            intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse("market://details?id=$appPackageName")
            startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
            startActivity(intent)
        }
    }


    private fun startAppWithApk(){
        var filePath = Utils.getNativeAppTestApkPath()

        var mainFile = File(filePath)

        app_package_name = Utils.checkAppAlreadyInstall(this)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            var apkUri = FileProvider.getUriForFile(this,this.applicationContext.packageName+".provider",mainFile)

            var installIntent = Intent(Intent.ACTION_INSTALL_PACKAGE)
            installIntent.setData(apkUri)
            installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(installIntent)
        } else{
            var apkUri = Uri.fromFile(mainFile)
            var installIntent = Intent(Intent.ACTION_VIEW)
            installIntent.setDataAndType(apkUri,"application/vnd.android.package-archive")
          startActivity(installIntent)
        }
    }






}
