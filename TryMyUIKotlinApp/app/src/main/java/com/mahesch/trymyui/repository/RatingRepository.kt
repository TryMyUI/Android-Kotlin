package com.mahesch.trymyui.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RatingRepository(application: Application) {

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getPostApiService()
    private var TAG = RatingRepository::class.java.simpleName.toUpperCase()

    var mutableLiveData = MutableLiveData<CommonModel>()

    var application: Application? = null


    init {
        this.application = application
    }

    fun ratingMutableDataForGuest(email : String?,rating : Int) : MutableLiveData<CommonModel>{

        var call = apiInterface.submitRatingForGuest(email,rating)

        call.enqueue(object : Callback<CommonModel>{

            override fun onFailure(call: Call<CommonModel>, t: Throwable) {

                Log.e(TAG, "ONFAILURE")

                mutableLiveData?.value = CommonModel()

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


    fun ratingMutableDataForWorker(token : String?,rating : Int) : MutableLiveData<CommonModel>{
        var call = apiInterface.submitRatingForWorker(token,rating)

        call.enqueue(object : Callback<CommonModel>{

            override fun onFailure(call: Call<CommonModel>, t: Throwable) {

                Log.e(TAG, "ONFAILURE")

                mutableLiveData?.value = CommonModel()
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