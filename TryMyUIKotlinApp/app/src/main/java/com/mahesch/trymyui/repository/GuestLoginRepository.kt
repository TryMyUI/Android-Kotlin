package com.mahesch.trymyui.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import com.seattleapplab.trymyui.models.Tests
import retrofit2.Call
import retrofit2.Callback

class GuestLoginRepository(application: Application) {

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getService()

    var mutableLiveData = MutableLiveData<Tests>()
    var application: Application? = null

    init {
        this.application = application
    }


    fun guestLoginMutableData(testId: String,name: String,email: String) : MutableLiveData<Tests>{

        var call: Call<Tests> = apiInterface.onGuestLogin(testId,name,email)

        call.enqueue(object : Callback<Tests> {

            override fun onResponse(
                call: Call<Tests>,
                response: retrofit2.Response<Tests>
            ) {

                Log.e("GUESTLOGIN", "ONRESPONSE")
                Log.e("GUESTLOGIN", "code " + response.code())
                Log.e("GUESTLOGIN", "message " + response.message())

                mutableLiveData?.value = response.body()
            }

            override fun onFailure(call: Call<Tests>, t: Throwable) {
                Log.e("GUESTLOGIN", "ONFAILURE")

                mutableLiveData.value = Tests()

            }

        })

        return mutableLiveData
    }

}