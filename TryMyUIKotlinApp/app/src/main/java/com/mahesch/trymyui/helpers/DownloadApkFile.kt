package com.mahesch.trymyui.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.PerformTestActivity
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadApkFile(context: Context,apkUrl: String) : AsyncTask<Void,String,String>(){

    private var context: Context = context
    private var apkUrl = apkUrl
    private var TAG = "DOWNLOADAPKFILE"

    override fun onPreExecute() {
        super.onPreExecute()

  //      ProgressDialog.initializeProgressDialogue(context,android.app.ProgressDialog.STYLE_HORIZONTAL)
  //      ProgressDialog.showProgressDialog(context.resources.getString(R.string.downloading_apk_msg))

    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)

   //     ProgressDialog.setProgress(values[0]?.toInt()!!)

    }

    override fun doInBackground(vararg params: Void?): String? {

        try{
            var count  = 0

            var url = URL(apkUrl)

            var outputFile = Utils.getNativeAppTestApkPath()

            var urlConnection = url.openConnection()

            var lengthOfFile = urlConnection.contentLength
            Log.e(TAG, "lengthOfFile $lengthOfFile")

            var inputStream = BufferedInputStream(url.openStream())

            var outputStream = FileOutputStream(outputFile)

            var dataByte = ByteArray(1024)

            var total: Long = 0

            Log.e(TAG,"count $count")

            count = inputStream.read(dataByte)

                while ( count != -1)
                {
                    total += count

                    publishProgress( ((total * 100 / lengthOfFile).toString()))

                    outputStream.write(dataByte,0,count)

                    count = inputStream.read(dataByte)

               //     Log.e(TAG,"total $total")
                }

            Log.e(TAG,"count in end $count")

            outputStream.flush()
            outputStream.close()
            inputStream.close()


        }
        catch (e: Exception){
            ProgressDialog.dismissProgressDialog()
            Log.e(TAG,"doinBackground exception $e")
        }
        return ""

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
       // ProgressDialog.dismissProgressDialog()
        startAppWithApk()
    }

    override fun onCancelled() {
        super.onCancelled()
    }

    private fun startAppWithApk(){
        var filePath = Utils.getNativeAppTestApkPath()

        var mainFile = File(filePath)
        Log.e(TAG,"mainFile "+mainFile)

        PerformTestActivity.app_package_name = Utils.checkAppAlreadyInstall(context)
        Log.e(TAG,"PerformTestActivity.app_package_name "+PerformTestActivity.app_package_name)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            Log.e(TAG,"provider "+context.applicationContext.packageName)
            var apkUri = FileProvider.getUriForFile(context,context.applicationContext.packageName+".provider",mainFile)

            Log.e(TAG,"apkUri "+apkUri)

            var installIntent = Intent(Intent.ACTION_INSTALL_PACKAGE)
            installIntent.data = apkUri
            installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(installIntent)
        } else{
            var apkUri = Uri.fromFile(mainFile)
            var installIntent = Intent(Intent.ACTION_VIEW)
            installIntent.setDataAndType(apkUri,"application/vnd.android.package-archive")
            context.startActivity(installIntent)
        }
    }

}