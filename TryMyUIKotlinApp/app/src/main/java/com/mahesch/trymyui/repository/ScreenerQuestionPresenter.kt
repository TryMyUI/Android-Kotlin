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

class ScreenerQuestionPresenter {

    private var context: Context? = null
    private var mApiInterface: ApiService.ApiInterface? = null
    private val TAG = ScreenerQuestionPresenter::class.java.simpleName.toUpperCase()
    private var iScreenerQuestionList: IScreenerQuestionList? = null

    private val guestTesterType = false
    private var sharedPrefHelper: SharedPrefHelper? = null

    constructor (context: Context?, iScreenerQuestionList: IScreenerQuestionList?) {
        this.context = context
        this.iScreenerQuestionList = iScreenerQuestionList
        mApiInterface = RetrofitInstance.getService()
        sharedPrefHelper = SharedPrefHelper(context!!)
    }

    fun getScreenerQuestion()
    {
        Log.e(TAG, "clciedk test id " + SharedPrefHelper(context!!).getAvaliableTestId())
        Log.e(TAG, "" + SharedPrefHelper(context!!).getEmailId())
        Log.e(TAG, "" + SharedPrefHelper(context!!).getUsername())

        if (sharedPrefHelper?.getGuestTester()!!)
        {
            var call = mApiInterface?.getScreenerQuestionForGuest(
                sharedPrefHelper?.getEmailId(),
                sharedPrefHelper?.getUsername(),
                SharedPrefHelper(context!!).getAvaliableTestId()?.toInt()!!)

            call?.enqueue(object : Callback<ScreenerQuestionModel>{
                override fun onFailure(call: Call<ScreenerQuestionModel>, t: Throwable) {
                    iScreenerQuestionList!!.onErrorGetScreenerQuestion()
                }

                override fun onResponse(call: Call<ScreenerQuestionModel>, response: Response<ScreenerQuestionModel>) {
                    try
                    {
                        Log.e(TAG, "getScreenerQuestion success $response")

                        var screenerQuestionModel  = response.body()

                        if (screenerQuestionModel != null)
                        {
                            val gson = Gson()
                            val json = gson.toJson(screenerQuestionModel)
                            Log.e(TAG, "json $json")
                            iScreenerQuestionList!!.onSuccessGetScreenerQuestion(
                                screenerQuestionModel
                            )
                        } else {
                            iScreenerQuestionList!!.onErrorGetScreenerQuestion()
                        }
                    } catch (e: Exception) {
                        iScreenerQuestionList!!.onErrorGetScreenerQuestion()
                    }
                }

            })


        }
        else
        {
          var call =   mApiInterface?.getScreenerQuestionForWorker(
                sharedPrefHelper?.getToken(),
                SharedPrefHelper(context!!).getAvaliableTestId()?.toInt()!!)

            call?.enqueue(object : Callback<ScreenerQuestionModel>{
                override fun onFailure(call: Call<ScreenerQuestionModel>, t: Throwable) {
                    iScreenerQuestionList!!.onErrorGetScreenerQuestion()
                }

                override fun onResponse(
                    call: Call<ScreenerQuestionModel>,
                    response: Response<ScreenerQuestionModel>) {
                   var screenerQuestionModel = response.body()

                    try {
                        Log.e(
                            TAG,
                            "getScreenerQuestion success $screenerQuestionModel"
                        )
                        if (screenerQuestionModel != null) {
                            val gson = Gson()
                            val json = gson.toJson(screenerQuestionModel)
                            Log.e(TAG, "json $json")
                            iScreenerQuestionList!!.onSuccessGetScreenerQuestion(
                                screenerQuestionModel
                            )
                        } else {
                            iScreenerQuestionList!!.onErrorGetScreenerQuestion()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "getScreenerQuestion exception $e")
                        iScreenerQuestionList!!.onErrorGetScreenerQuestion()
                    }
                }

            })


        }
    }


    interface IScreenerQuestionList {
        fun onSuccessGetScreenerQuestion(screenerQuestionModel: ScreenerQuestionModel?)
        fun onErrorGetScreenerQuestion()
    }

}