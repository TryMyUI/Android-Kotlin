package com.mahesch.trymyui.helpers

import android.app.ActionBar
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.TabActivity
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class Utils {


    companion object {

        private val TAG = Utils::class.simpleName?.toUpperCase()

        fun isInternetAvailable(context: Context): Boolean{

            val isConnected: Boolean

            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            Log.e(TAG,"connectivityManager "+connectivityManager)

            val networkInfo: NetworkInfo = connectivityManager.activeNetworkInfo

            Log.e(TAG,"activeNetworkInfo "+connectivityManager.activeNetworkInfo)

            if(networkInfo != null){
                isConnected = networkInfo.isConnected
            }
            else{
                isConnected = false
            }


            return isConnected
        }


        fun isValidEmail(email: String): Boolean{

            var isValid: Boolean = false

            isValid = if(email == null || email.trim().isEmpty()) {
                false
            } else {
                Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", email)
            }

            return isValid
        }

        fun isValidPassword(password: String): Boolean{

            return password != null && password.trim().length>3
        }


        fun isValidName(name: String): Boolean{

            return !(name == null || name.trim().isEmpty())
        }

        fun isValidTestId(test_id : String): Boolean {
            return !(test_id == null || test_id.trim().isEmpty())
        }

        fun showInternetCheckToast(context: Context){
            Toast.makeText(context,context.resources.getString(R.string.internet_check), Toast.LENGTH_SHORT).show()
        }

        fun showToast(context: Context,message: String){
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }

        fun moveToHome(activity: AppCompatActivity){
            var intent = Intent(activity, TabActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }

        fun moveToNextActivityAndFinishCurrent(currentActivity: AppCompatActivity,class_ : Class<AppCompatActivity>){
            var intent = Intent(currentActivity, class_::class.java)
            currentActivity.startActivity(intent)
            currentActivity.finish()
        }

        fun moveToNextActivityAndFinishCurrentActivity(context: Context,class_: Class<AppCompatActivity>){
            var intent = Intent(context, class_::class.java)
            context.startActivity(intent)
            (context as AppCompatActivity).finish()
        }

        fun moveToNextActivityAndFinishCurrentFragment(fragmentActivity: FragmentActivity,class_ : Class<AppCompatActivity>){
            var intent = Intent(fragmentActivity, class_::class.java)
            fragmentActivity.startActivity(intent)
            fragmentActivity.finish()
        }

        fun moveToNextActivityAndKeepCurrent(currentActivity: AppCompatActivity,class_ : Class<AppCompatActivity>){
            var intent = Intent(currentActivity, class_::class.java)
            currentActivity.startActivity(intent)
        }

        fun patternMatchForAndroid(str : String?) : Boolean{

            var isForAndroid = false

            var tokens = ArrayList<String>()

            var st = StringTokenizer(str)

            //SPLIT BY SPACE
            while (st.hasMoreElements()){
                tokens.add(st.nextElement().toString())
            }

            var android = "Android"
            var mobile = "mobile"

            for (i in 0 until tokens.size){
                if(tokens.get(i).contains(android,true)){
                    isForAndroid = true
                    break
                }else if(tokens.get(i).contains(mobile,true)){
                    isForAndroid = true
                }
            }

            return isForAndroid
        }


        fun isRecordingServiceRunning(serviceClass: Class<*>?,context: Context): Boolean {

            var activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            for(service in activityManager.getRunningServices(Integer.MAX_VALUE)){
                if (serviceClass!!.name == service.service.className) {
                    return true
                }
            }
            return false
        }


        fun checkGooglePlayServices(context: Context) : Boolean{

            var apiAvailability = GoogleApiAvailability.getInstance()
            var resultCode = apiAvailability.isGooglePlayServicesAvailable(context)

            if(resultCode != ConnectionResult.SUCCESS){
                    if(apiAvailability.isUserResolvableError(resultCode)){
                        apiAvailability.getErrorDialog(context as Activity,resultCode,9000).show()
                    } else { Log.i("GCM check play service", "This device is not supported.") }
                return false
            }
            return true
        }


         fun actionBarSetup(actionbar: ActionBar,actionbarTitle: String){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                actionbar?.title = "   $actionbarTitle"
                actionbar?.setHomeButtonEnabled(true)
                actionbar?.setDisplayHomeAsUpEnabled(true)
                actionbar?.show()
            }
        }


        fun getScreenSizeType(context: Context): Int {
            val screenSize = context.resources.configuration.screenLayout and
                    Configuration.SCREENLAYOUT_SIZE_MASK
            val screen_size_value: Int
            screen_size_value = when (screenSize) {
                Configuration.SCREENLAYOUT_SIZE_LARGE -> {
                    Log.e(TAG, "screenSize large $screenSize")
                    1
                }
                Configuration.SCREENLAYOUT_SIZE_NORMAL -> {
                    Log.e(TAG, "screenSize normal $screenSize")
                    2
                }
                Configuration.SCREENLAYOUT_SIZE_SMALL -> {
                    Log.e(TAG, "screenSize small $screenSize")
                    3
                }
                Configuration.SCREENLAYOUT_SIZE_XLARGE -> {
                    Log.e(TAG, "scrrensize xlarge $screenSize")
                    4
                }
                else -> {
                    Log.e(TAG, "screenSize $screenSize")
                    0
                }
            }
            return screen_size_value
        }

    }





}

