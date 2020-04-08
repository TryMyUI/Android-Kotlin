package com.mahesch.trymyui.repository


import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.SpecialTechnicalModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TechnicalQualificationRepository(application: Application) {

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getPostApiService()
    private var TAG = SpecialQualificationRepository::class.java.simpleName.toUpperCase()

    var mutableLiveData = MutableLiveData<SpecialTechnicalModel>()

    var application: Application? = null


    init {
        this.application = application
    }

    fun postTechCriteriaResponseMutableData(token: String,testId: String) : MutableLiveData<SpecialTechnicalModel>{

        var call = apiInterface.post_special_criteria_response(token,testId)

        call.enqueue(object : Callback<SpecialTechnicalModel> {

            override fun onFailure(call: Call<SpecialTechnicalModel>, t: Throwable) {

                Log.e(TAG,"onFailure")
                Log.e(TAG,"error "+t.stackTrace)

                var specialTechnicalModel = SpecialTechnicalModel()
                specialTechnicalModel.error = t

                mutableLiveData?.value = specialTechnicalModel

            }

            override fun onResponse(call: Call<SpecialTechnicalModel>, response: Response<SpecialTechnicalModel>) {
                Log.e(TAG, "ONRESPONSE")
                Log.e(TAG, "code " + response.code())
                Log.e(TAG, "message " + response.message())

                mutableLiveData?.value = response.body()
            }

        })

        return mutableLiveData

    }

}