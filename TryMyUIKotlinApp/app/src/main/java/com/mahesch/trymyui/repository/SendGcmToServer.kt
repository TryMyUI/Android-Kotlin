package com.mahesch.trymyui.repository

import android.util.Log
import com.google.gson.JsonObject
import com.mahesch.trymyui.retrofitclient.ApiService
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback

//It doesn't has any UI Impact so no need for LiveData process
class SendGcmToServer(apiInterface: ApiService.ApiInterface) {

    var apiInterface = apiInterface
    var TAG = SendGcmToServer::class.java.simpleName.toUpperCase()

    fun callGcmRegistrationToken(jsonReq: JsonObject){

       var call =  apiInterface.GCMRegisterToken(jsonReq)

        call.enqueue(object : Callback<Response>{
            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.e(TAG,"onFialure callGcmRegistrationToken")
            }

            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                Log.e(TAG,"onResponse callGcmRegistrationToken")
            }

        })


    }



}