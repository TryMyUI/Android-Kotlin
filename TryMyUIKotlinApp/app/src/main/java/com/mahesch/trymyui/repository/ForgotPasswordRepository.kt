package com.mahesch.trymyui.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.FeedbackModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordRepository(application: Application) {


    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getService()

    var mutableLiveData = MutableLiveData<FeedbackModel>()
    var application: Application? = null

    init {
        this.application = application
    }

    fun forgotPasswordMutableData(email: String) : MutableLiveData<FeedbackModel>?{

        var call : Call<FeedbackModel> = apiInterface.resetPassword(email)

        call.enqueue(object : Callback<FeedbackModel>{

            override fun onResponse(call: Call<FeedbackModel>, response: Response<FeedbackModel>) {

                Log.e("FORGORTPASSWORD", "ONRESPONSE")
                Log.e("FORGORTPASSWORD", "code " + response.code())
                Log.e("FORGORTPASSWORD", "message " + response.message())

                mutableLiveData?.value = response.body()

            }

            override fun onFailure(call: Call<FeedbackModel>, t: Throwable) {
                Log.e("FORGORTPASSWORD", "ONFAILURE")
            }

        })

        return mutableLiveData
    }

}