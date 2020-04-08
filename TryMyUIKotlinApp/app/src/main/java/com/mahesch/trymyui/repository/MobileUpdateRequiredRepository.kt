package com.mahesch.trymyui.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileUpdateRequiredRepository(application: Application) {

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getPostApiService()
    private var TAG = MobileUpdateRequiredRepository::class.java.simpleName.toUpperCase()

    var mutableLiveData = MutableLiveData<LoginResponseModel>()

    var application: Application? = null


    init {
        this.application = application
    }

    fun mobileUpdateRequiredMutableData(version: String,deviceType: String) : MutableLiveData<LoginResponseModel>{

        var call = apiInterface.mobileUpdateRequired(version,deviceType)

        call.enqueue(object : Callback<LoginResponseModel> {

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                Log.e(TAG, "ONFAILURE")

                mutableLiveData?.value = LoginResponseModel()
            }

            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {

                Log.e(TAG, "ONRESPONSE")
                Log.e(TAG, "code " + response.code())
                Log.e(TAG, "message " + response.message())

                mutableLiveData?.value = response.body()
            }
        })
        return mutableLiveData
    }


}