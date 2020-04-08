package com.mahesch.trymyui.repository

import android.util.Log
import com.google.gson.Gson
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import com.seattleapplab.trymyui.models.TestAvailabilityModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckUseTestAvailabilityRepository(iCheckUseTestAvailability: ICheckUseTestAvailability) {

    private var apiInterface = RetrofitInstance.getService()

    private var testAvailabilityModel : TestAvailabilityModel? = null

    private var TAG = CheckUseTestAvailabilityRepository::class.java.simpleName.toUpperCase()

    private var iCheckUseTestAvailability: ICheckUseTestAvailability = iCheckUseTestAvailability

    fun callGuestCheckUseTestRepository(availableTestModel: AvailableTestModel?,email: String?){

        var call = apiInterface.checkUseTestAvailabilityForGuest(email,availableTestModel?.id)

        call.enqueue(object : Callback<TestAvailabilityModel>{

            override fun onFailure(call: Call<TestAvailabilityModel>, t: Throwable?) {

                testAvailabilityModel = TestAvailabilityModel()
                testAvailabilityModel?.error = t
                iCheckUseTestAvailability.onFailureCheckUseTestAvailability(testAvailabilityModel,availableTestModel)
            }

            override fun onResponse(call: Call<TestAvailabilityModel>, response: Response<TestAvailabilityModel>) {

                    testAvailabilityModel = response.body()
                    iCheckUseTestAvailability.onSuccessCheckUseTestAvailability(testAvailabilityModel,availableTestModel)

            }

        })

    }

    fun callWorkerCheckUseTestRepository(availableTestModel: AvailableTestModel?,token: String?){

        var call = apiInterface.checkUseTestAvailabilityForWorker(token,availableTestModel?.id)

        call.enqueue(object : Callback<TestAvailabilityModel>{

            override fun onFailure(call: Call<TestAvailabilityModel>, t: Throwable) {

                Log.e(TAG,"onFailure callWorkerCheckUseTestRepository")
                Log.e(TAG,"onFailure callWorkerCheckUseTestRepository t "+t.stackTrace)

                testAvailabilityModel = TestAvailabilityModel()
                testAvailabilityModel?.error = t

                iCheckUseTestAvailability.onFailureCheckUseTestAvailability(testAvailabilityModel,availableTestModel)
            }

            override fun onResponse(call: Call<TestAvailabilityModel>, response: Response<TestAvailabilityModel>) {


                Log.e(TAG,"onResponse callWorkerCheckUseTestRepository "+(Gson().toJson(response.body())))

                testAvailabilityModel = response.body()

                iCheckUseTestAvailability.onSuccessCheckUseTestAvailability(testAvailabilityModel,availableTestModel)
            }

        })

    }


    interface ICheckUseTestAvailability{
        fun onSuccessCheckUseTestAvailability(testAvailabilityModel: TestAvailabilityModel?,availableTestModel: AvailableTestModel?)
        fun onFailureCheckUseTestAvailability(testAvailabilityModel: TestAvailabilityModel?,availableTestModel: AvailableTestModel?)
    }
}