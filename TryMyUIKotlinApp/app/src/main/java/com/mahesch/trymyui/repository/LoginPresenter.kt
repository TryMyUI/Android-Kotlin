package com.mahesch.trymyui.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback


class LoginPresenter(private var password: String,private var email: String,private var iLoginPresenter: ILoginPresenter) {

    var apiInterface: ApiService.ApiInterface = RetrofitInstance.getService()

    var mutableLiveData : MutableLiveData<LoginResponseModel>? = null



    fun login(): Unit{


        var call : Call<LoginResponseModel> = apiInterface.onLogin(email,password)

        call.enqueue(object : Callback<LoginResponseModel>{

            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: retrofit2.Response<LoginResponseModel>
            ) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                Log.e("LOGIN","ONRESPONSE")
                Log.e("LOGIN","code "+response.code())
                Log.e("LOGIN","message "+response.message())

                var loginResponseModel  = response.body()

                if (loginResponseModel != null) {
                    iLoginPresenter.onSuccessLoginPresenter(loginResponseModel)
                }
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
   //             TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.e("LOGIN","ONFAILURE")
            }
        })



    }

    fun loginMutableData(): MutableLiveData<LoginResponseModel>? {


        var call: Call<LoginResponseModel> = apiInterface.onLogin(email, password)

        call.enqueue(object : Callback<LoginResponseModel> {

            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: retrofit2.Response<LoginResponseModel>
            ) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                Log.e("LOGIN", "ONRESPONSE")
                Log.e("LOGIN", "code " + response.code())
                Log.e("LOGIN", "message " + response.message())

                var loginResponseModel = response.body()

                if (loginResponseModel != null) {
                    iLoginPresenter.onSuccessLoginPresenter(loginResponseModel)
                }

                mutableLiveData?.value = response.body()


            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                //             TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.e("LOGIN", "ONFAILURE")
            }

        })

        return mutableLiveData


    }

    interface ILoginPresenter{
        fun onSuccessLoginPresenter(loginResponseModel: LoginResponseModel)
        fun onErrorLoginPresenter(loginResponseModel: LoginResponseModel)
    }
}