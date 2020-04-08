package com.mahesch.trymyui.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback

class LoginRepository(application: Application) {

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getService()

    var mutableLiveData = MutableLiveData<LoginResponseModel>()
    var application: Application? = null


    init {
        this.application = application
    }

    fun loginMutableData(email: String,password: String): MutableLiveData<LoginResponseModel>? {

        var call: Call<LoginResponseModel> = apiInterface.onLogin(email,password)

        call.enqueue(object : Callback<LoginResponseModel> {

            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: retrofit2.Response<LoginResponseModel>
            ) {

                Log.e("LOGIN", "ONRESPONSE")
                Log.e("LOGIN", "code " + response.code())

                mutableLiveData?.value = response.body()

            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                Log.e("LOGIN", "ONFAILURE")

                mutableLiveData.value = LoginResponseModel()

              //  https://medium.com/@sriramr083/error-handling-in-retrofit2-in-mvvm-repository-pattern-a9c13c8f3995
            }

        })

        return mutableLiveData

    }
}