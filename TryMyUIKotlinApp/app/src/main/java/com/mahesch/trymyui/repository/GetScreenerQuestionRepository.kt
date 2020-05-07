package com.mahesch.trymyui.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import com.seattleapplab.trymyui.models.ScreenerQuestionModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetScreenerQuestionRepository(application: Application) {

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getPostApiService()
    private var TAG = GetScreenerQuestionRepository::class.java.simpleName.toUpperCase()

    var mutableLiveData = MutableLiveData<ScreenerQuestionModel>()

    var application: Application? = null


    init {
        this.application = application
    }

    fun getScreenerQuestionMutableData(sharedPrefHelper: SharedPrefHelper): MutableLiveData<ScreenerQuestionModel>{

        if(sharedPrefHelper.getGuestTester()){
            mutableLiveData =   getScreenQuestMutableDataForGuest(sharedPrefHelper)
        }
        else{
            mutableLiveData =   getScreenQuestMutableDataForWorker(sharedPrefHelper)
        }


        return mutableLiveData
    }

    private fun getScreenQuestMutableDataForGuest(sharedPrefHelper: SharedPrefHelper): MutableLiveData<ScreenerQuestionModel>{

        var mutableLiveData = MutableLiveData<ScreenerQuestionModel>()

        var call  = apiInterface.getScreenerQuestionForGuest(sharedPrefHelper.getEmailId(),
            sharedPrefHelper.getUsername(),sharedPrefHelper.getAvaliableTestId()!!.toInt())

        call.enqueue(object : Callback<ScreenerQuestionModel> {
            override fun onFailure(call: Call<ScreenerQuestionModel>, t: Throwable) {
                Log.e(TAG, "ONFAILURE")

                var screenerQuestionModel = ScreenerQuestionModel()
                screenerQuestionModel.error = t

                Log.e(TAG,"t stackTrace "+t.stackTrace)

                mutableLiveData?.value = screenerQuestionModel
            }

            override fun onResponse(
                call: Call<ScreenerQuestionModel>,
                response: Response<ScreenerQuestionModel>) {

                Log.e(TAG, "ONRESPONSE")
                Log.e(TAG, "code " + response.code())
                Log.e(TAG, "message " + response.message())

                mutableLiveData?.value = response.body()


            }
        })

        return mutableLiveData!!
    }

    private fun getScreenQuestMutableDataForWorker(sharedPrefHelper: SharedPrefHelper): MutableLiveData<ScreenerQuestionModel>{

        var mutableLiveData = MutableLiveData<ScreenerQuestionModel>()


        var call  = apiInterface.getScreenerQuestionForWorker(sharedPrefHelper.getToken(),sharedPrefHelper.getAvaliableTestId()!!.toInt())

        call.enqueue(object : Callback<ScreenerQuestionModel> {
            override fun onFailure(call: Call<ScreenerQuestionModel>, t: Throwable) {
                Log.e(TAG, "ONFAILURE")

                var screenerQuestionModel = ScreenerQuestionModel()
                screenerQuestionModel.error = t

                Log.e(TAG,"t stackTrace "+t.stackTrace)

                mutableLiveData?.value = screenerQuestionModel
            }

            override fun onResponse(
                call: Call<ScreenerQuestionModel>,
                response: Response<ScreenerQuestionModel>) {

                Log.e(TAG, "ONRESPONSE")
                Log.e(TAG, "code " + response.code())
                Log.e(TAG, "message " + response.message())

                mutableLiveData?.value = response.body()


            }
        })

        return mutableLiveData!!
    }

}