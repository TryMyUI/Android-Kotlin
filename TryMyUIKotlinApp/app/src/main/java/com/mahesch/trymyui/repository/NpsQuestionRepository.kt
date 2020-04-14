package com.mahesch.trymyui.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import com.seattleapplab.trymyui.models.Tests
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NpsQuestionRepository(application: Application) {

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getPostApiService()
    private var TAG = NpsQuestionRepository::class.java.simpleName.toUpperCase()

    var mutableLiveData = MutableLiveData<CommonModel>()

    var application: Application? = null


    init {
        this.application = application
    }


    fun npsQuestionSubmitMutableData(npsRequestJsonString : JsonObject) : MutableLiveData<CommonModel>{

        var call = apiInterface.submitNpsValue(npsRequestJsonString)

        call.enqueue(object : Callback<CommonModel>{

            override fun onFailure(call: Call<CommonModel>, t: Throwable) {

                Log.e(TAG, "ONFAILURE")

                var commonModel = CommonModel()
                commonModel.error = t

                Log.e(TAG,"t stackTrace "+t.stackTrace)

                mutableLiveData?.value = commonModel
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {

                Log.e(TAG, "ONRESPONSE")
                Log.e(TAG, "code " + response.code())
                Log.e(TAG, "message " + response.message())

                mutableLiveData?.value = response.body()

            }

        })

        return mutableLiveData
    }


}