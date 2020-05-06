package com.mahesch.trymyui.helpers

import android.content.Context
import android.content.Intent
import com.mahesch.trymyui.activity.LoginActivity
import com.mahesch.trymyui.activity.TabActivity
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TerminateTestPresenter(iTerminateTest: ITerminateTest) {


    private var iTerminateTest = iTerminateTest

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getService()

    fun terminate(context: Context,sharedPrefHelper: SharedPrefHelper){
        var  call = apiInterface.terminate(sharedPrefHelper.getToken()!!,sharedPrefHelper.getTestResultId()!!)

        call.enqueue(object : Callback<CommonModel>{
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {


                iTerminateTest.onFailureTerminateTest()
                moveToDashboard(context,sharedPrefHelper)

            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                moveToDashboard(context,sharedPrefHelper)

                iTerminateTest.onSuccessTerminateTest()
                moveToDashboard(context,sharedPrefHelper)
            }

        })
    }

    private fun moveToDashboard(context: Context,sharedPrefHelper: SharedPrefHelper){

        if (sharedPrefHelper.getGuestTester()) {

            //Logout move to login
            SharedPrefHelper(context).clearSharedPreference()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } else {

            // move to dashboard
            val intent = Intent(context, TabActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    interface ITerminateTest{
        fun onSuccessTerminateTest()
        fun onFailureTerminateTest()
    }

}