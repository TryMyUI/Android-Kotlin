package com.mahesch.trymyui.activity

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.ManageFlowAfterTest
import com.mahesch.trymyui.helpers.ProgressDialog
import com.mahesch.trymyui.helpers.YesNoAlertDialog
import com.mahesch.trymyui.model.AvailableTestModel
import kotlinx.android.synthetic.main.perform_test_activity.*
import java.util.*
import kotlin.collections.ArrayList

class PerformTestActivity : AppCompatActivity() {

    private lateinit var availableTestModel: AvailableTestModel

    private var TAG = PerformTestActivity::class.java.simpleName.toUpperCase()

    private lateinit var manageFlowAfterTest: ManageFlowAfterTest

    private var isServiceStarted = false

    private var app_package_name: String? = null

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

        if(availableTestModel.url?.isEmpty()!!){
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
            startNativeAppRecording(app_package_name, true)
        }
    }


    private fun startNativeAppRecording(appPackageNAme: String,bol: Boolean){

    }

    private fun showConfirmationOfApk(){
            var btn_array = YesNoAlertDialog.initYesNoDialogue(this)

            btn_array!![0].setOnClickListener { downloadApk() }

            btn_array[1].setOnClickListener { YesNoAlertDialog.dismissYesNoDialogue() }

            YesNoAlertDialog.showYesNoDialogue(title = resources.getString(R.string.download_apk),msg = resources.getString(R.string.download_apk_msg),positiveButtonText = "Yes",negativeButtonText = "No")
    }

    private fun downloadApk(){

    }

   inner  class DownloadFileInAsync : AsyncTask<Void,String,String>(){

        override fun onPreExecute() {
            super.onPreExecute()

            ProgressDialog.initializeProgressDialogue(this@PerformTestActivity)
            ProgressDialog.showProgressDialog()

        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
        }

        override fun doInBackground(vararg params: Void?): String {
            TODO("Not yet implemented")
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

        override fun onCancelled() {
            super.onCancelled()
        }
    }



}
