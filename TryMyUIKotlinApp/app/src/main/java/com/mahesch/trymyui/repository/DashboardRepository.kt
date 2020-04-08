package com.mahesch.trymyui.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import com.seattleapplab.trymyui.models.Tests
import retrofit2.Call
import retrofit2.Callback

class DashboardRepository(application: Application) {

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getService()
    var mutableLiveData = MutableLiveData<Tests>()
    var application: Application? = null

    private val TAG: String? = DashboardRepository::class.simpleName?.toUpperCase()

    init {
        this.application = application
    }

    fun workerDashBoardMutableData(token: String?, device: String) : MutableLiveData<Tests>{

        var call: Call<Tests> = apiInterface.getWorkerDashBoardData(token,device)

        call.enqueue(object : Callback<Tests> {

            override fun onResponse(
                call: Call<Tests>,
                response: retrofit2.Response<Tests>
            ) {

                Log.e(TAG, "ONRESPONSE")
                Log.e(TAG, "code " + response.code())
                Log.e(TAG, "message " + response.message())

                mutableLiveData?.value = response.body()
            }

            override fun onFailure(call: Call<Tests>, t: Throwable) {
                Log.e(TAG, "ONFAILURE")

                mutableLiveData.value = Tests()

            }

        })

        return mutableLiveData
    }


    fun customerDashBoardMutableData(token: String?,device: String) : MutableLiveData<Tests>{
        var call: Call<Tests> = apiInterface.getCustomerDashBoardData(token,device)

        call.enqueue(object : Callback<Tests> {

            override fun onResponse(
                call: Call<Tests>,
                response: retrofit2.Response<Tests>
            ) {

                Log.e(TAG, "ONRESPONSE")
                Log.e(TAG, "code " + response.code())
                Log.e(TAG, "message " + response.message())

                mutableLiveData?.value = response.body()
            }

            override fun onFailure(call: Call<Tests>, t: Throwable) {
                Log.e(TAG, "ONFAILURE")

                mutableLiveData.value = Tests()

            }

        })

        return mutableLiveData
    }
}