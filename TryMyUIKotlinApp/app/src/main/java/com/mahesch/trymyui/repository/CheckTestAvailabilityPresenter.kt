package com.mahesch.trymyui.repository

import com.google.gson.JsonObject
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckTestAvailabilityPresenter(iCheckTestAvailability: ICheckTestAvailability) {

    private var iCheckTestAvailability : ICheckTestAvailability = iCheckTestAvailability
    private var apiInterface =RetrofitInstance.getPostApiService()


    fun checkTestAvailabilityForGuest(jsonObject: JsonObject){

        var call = apiInterface.checkTestAvailabilityForGuest(jsonObject)

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                iCheckTestAvailability.onFailureTestAvailability()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                TODO("Not yet implemented")
                iCheckTestAvailability.onSuccessTestAvailability(response.body()!! as JsonObject)
            }

        })

    }

    fun checkTestAvailabilityForWorker(jsonObject: JsonObject){

        var call = apiInterface.checkTestAvailabilityForWorker(jsonObject)

        call.enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                iCheckTestAvailability.onFailureTestAvailability()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                iCheckTestAvailability.onSuccessTestAvailability(response.body()!!)
            }

        })
    }

    interface ICheckTestAvailability{
        fun onSuccessTestAvailability(jsonObject: JsonObject)
        fun onFailureTestAvailability()
    }
}