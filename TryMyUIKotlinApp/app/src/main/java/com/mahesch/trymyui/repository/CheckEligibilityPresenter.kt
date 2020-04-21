package com.mahesch.trymyui.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import com.seattleapplab.trymyui.models.ScreenerQuestionModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CheckEligibilityPresenter(private val context: Context, private val iCheckScreeningEligibility: ICheckScreeningEligibility) {

    private val TAG = CheckEligibilityPresenter::class.java.simpleName.toUpperCase()

    private var mApiInterface_post: ApiService.ApiInterface = RetrofitInstance.getPostApiService()

    private var sharedPrefHelper: SharedPrefHelper = SharedPrefHelper(context)

    fun checkScreeningEligibility(use_test_id: Int, sq_id: Int, screener_question_id: String?)
    {
        if (sharedPrefHelper.getGuestTester())
        {
            val call = mApiInterface_post.checkScreeningEligibiltyForGuest(sharedPrefHelper.getEmailId(), sharedPrefHelper.getUsername(),
                    use_test_id, sq_id, screener_question_id)

            call?.enqueue(object : Callback<ScreenerQuestionModel> {
                override fun onResponse(call: Call<ScreenerQuestionModel>, response: Response<ScreenerQuestionModel>) {
                    try {
                        Log.e(TAG, "onResponse ")
                        val gson = Gson()
                        val json = gson.toJson(response.body())
                        Log.e(TAG, "json $json")
                        val screenerQuestionModel = response.body()

                        if (screenerQuestionModel != null)
                        {
                            iCheckScreeningEligibility.onSuccessCheckScreeningEligibility(screenerQuestionModel)
                        }
                        else
                        {
                            iCheckScreeningEligibility.onErrorCheckScreeningEligibility()
                        }
                    }
                    catch (e: Exception)
                    {
                        iCheckScreeningEligibility.onErrorCheckScreeningEligibility()
                    }
                }

                override fun onFailure(call: Call<ScreenerQuestionModel?>, t: Throwable) {
                    Log.e(TAG, "checkScreeningEligibility error")
                    iCheckScreeningEligibility.onErrorCheckScreeningEligibility()
                }
            })
        }
        else
        {
            val call: Call<ScreenerQuestionModel> = mApiInterface_post.checkScreeningEligibiltyForWorker(
                    SharedPrefHelper(context).getToken(), use_test_id, sq_id, screener_question_id)

            call.enqueue(object : Callback<ScreenerQuestionModel> {
                override fun onResponse(call: Call<ScreenerQuestionModel>, response: Response<ScreenerQuestionModel>) {
                    try {
                        Log.e(TAG, "onResponse ")
                        val gson = Gson()
                        val json = gson.toJson(response.body())
                        Log.e(TAG, "json $json")
                        val screenerQuestionModel = response.body()

                        if (screenerQuestionModel != null) {
                            iCheckScreeningEligibility.onSuccessCheckScreeningEligibility(screenerQuestionModel)
                        }
                        else
                        {
                            iCheckScreeningEligibility.onErrorCheckScreeningEligibility()
                        }
                    }
                    catch (e: Exception)
                    {
                        iCheckScreeningEligibility.onErrorCheckScreeningEligibility()
                    }
                }

                override fun onFailure(
                    call: Call<ScreenerQuestionModel?>,
                    t: Throwable
                ) {
                    Log.e(TAG, "checkScreeningEligibility error")
                    iCheckScreeningEligibility.onErrorCheckScreeningEligibility()
                }
            })
        }
    }

    interface ICheckScreeningEligibility {
        fun onSuccessCheckScreeningEligibility(screenerQuestionModel: ScreenerQuestionModel)
        fun onErrorCheckScreeningEligibility()
    }


}