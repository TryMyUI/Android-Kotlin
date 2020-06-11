package com.mahesch.trymyui.repository

import android.content.Context
import android.util.Log
import com.mahesch.trymyui.model.FeedbackModel
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NdaAgreementPresenter(context: Context,iNdaAgreementCallback: INdaAgreementCallback?) {

    private var context: Context? = null
    private var mApiInterface: ApiService.ApiInterface? = null

    private val TAG = NdaAgreementPresenter::class.java.simpleName.toUpperCase()

    private var iNdaAgreementCallback: INdaAgreementCallback? = null

   init {
        this.context = context
        this.iNdaAgreementCallback = iNdaAgreementCallback
        mApiInterface = RetrofitInstance.getPostApiService()
    }

    fun submitNdaForGuest(
        email: String,
        testId: String,
        ndaResponse: Boolean
    ) {
        Log.e(TAG, "email $email")
        Log.e(TAG, "testid $testId")
        Log.e(TAG, "ndaresponse $ndaResponse")
        var call = mApiInterface?.submitNdaResponseForGuest(email, testId, ndaResponse)

                call?.enqueue(object : Callback<FeedbackModel>{
                        override fun onFailure(call: Call<FeedbackModel>, t: Throwable) {
                            iNdaAgreementCallback!!.onErrorNdaAgreementCallback()
                        }

                        override fun onResponse(
                            call: Call<FeedbackModel>,
                            response: Response<FeedbackModel>
                        ) {
                            iNdaAgreementCallback!!.onSuccessNdaAgreementCallback(response.body()!!)
                        }

                    })

    }

    fun submitNdaForWorker(
        token: String?,
        testId: String?,
        ndaResponse: Boolean
    ) {
        var call = mApiInterface?.submitNdaResponseForWorker(token, testId, ndaResponse)
            call?.enqueue(object : Callback<FeedbackModel> {
                override fun onResponse(
                    call: Call<FeedbackModel>,
                    response: Response<FeedbackModel>
                ) {
                    iNdaAgreementCallback!!.onSuccessNdaAgreementCallback(response.body()!!)
                }

                override fun onFailure(
                    call: Call<FeedbackModel>,
                    t: Throwable
                ) {
                    iNdaAgreementCallback!!.onErrorNdaAgreementCallback()
                }
            })
    }


    interface INdaAgreementCallback {
        fun onSuccessNdaAgreementCallback(feedback: FeedbackModel)
        fun onErrorNdaAgreementCallback()
    }
}