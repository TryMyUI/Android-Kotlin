package com.mahesch.trymyui.retrofitclient

import com.google.gson.JsonObject
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.model.FeedbackModel
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.model.SpecialTechnicalModel
import com.seattleapplab.trymyui.models.ScreenerQuestionModel
import com.seattleapplab.trymyui.models.TestAvailabilityModel
import com.seattleapplab.trymyui.models.Tests
import retrofit2.Call
import retrofit2.http.*

class ApiService{

    companion object{
        val APP_VERSION = "2"

        val DeviceTypeAndroidOrIOS = "android"

        var BASE_URL = "http://beta.trymyui.com/"
        val ASSIST_URL = "/android"
        val VIDEO_URL = "/android_app_demo_video.mp4"
        val Channel_Type_CrashReport = "crash_report"
        val Channel_Type_Feedback = "feedback"
        val App_Type = ""

    }

    interface ApiInterface {

      /*  @FormUrlEncoded
        @POST("/api/v2/tokens")
        fun onLogin(@Field("email") email: String,
                    @Field("plain_password") password: String, callback: Callback<LoginResponseModel> )*/

        @FormUrlEncoded
        @POST("/api/v2/tokens")
        fun onLogin(@Field("email") email: String,
                    @Field("plain_password") password: String) : Call<LoginResponseModel>

        @GET("/api/v2/anonymous/login")
        fun onGuestLogin(@Query("test_id") testId : String,
                         @Query("name") name : String, @Query("email") email: String) : Call<Tests>


        @FormUrlEncoded
        @POST("/api/v2/tokens/reset_password")
        fun resetPassword(@Field("email") email: String?) : Call<FeedbackModel>


        @GET("/api/v2/worker")
        fun getWorkerDashBoardData(
            @Query("at") token: String?, @Query("device") device: String?) : Call<Tests>

        @GET("/api/v2/customer")
        fun getCustomerDashBoardData(
            @Query("at") token: String?,
            @Query("device") device: String?) : Call<Tests>



        @POST("/api/v2/worker/save_net_promoter_score")
        fun submitNpsValue(@Body body:JsonObject) : Call<CommonModel>


        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("/api/v2/enterprise/save_written_summary_responses")
        fun postWrittenSummaryAnswers(@Body body: JsonObject): Call<CommonModel>

        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("/api/v2/enterprise/save_sus_responses")
        fun submitSUSAnswers(@Body body: JsonObject): Call<CommonModel>

        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("/api/v2/enterprise/save_ux_crowd_responses")
        fun postUXCrowdUserAnswers(@Body body: JsonObject): Call<CommonModel>

        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("/api/v2/worker/vote_worker_response")
        fun sendUxCrowdVoteToServer(@Body voting_json: JsonObject): Call<CommonModel>

        @FormUrlEncoded
        @POST("/api/v2/mobile_events/mobile_rating")
        fun submitRatingForGuest(
            @Field("email") email: String?,
            @Field("rating") rating: Int
        ): Call<CommonModel>

        @FormUrlEncoded
        @POST("/api/v2/mobile_events/mobile_rating")
        fun submitRatingForWorker(
            @Field("at") token: String?,
            @Field("rating") rating: Int
        ): Call<CommonModel>

        // TODO : mobile_update_required force update check from service
        @GET("/api/v2/mobile_update_required")
        fun mobileUpdateRequired(
            @Query("version_number") versionNumber: String,
            @Query("device_type") deviceType: String) : Call<LoginResponseModel>


        /*API for GCM Register token for server */ // TODO : save Push notification register id
        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("/api/v2/worker/device")
        fun GCMRegisterToken(@Body body: JsonObject): Call<okhttp3.Response>


        //GUEST
        @FormUrlEncoded
        @POST("/api/v2/feedback")
        fun postFeedBackGuest(
            @Field("name") name: String?,
            @Field("email") email: String?,
            @Field("channel") channel: String,
            @Field("text") text: String?,
            @Field("subject") subject: String?,
            @Field("device") device: String):
                Call<CommonModel>


        //WORKER
        @FormUrlEncoded
        @POST("/api/v2/feedback")
        fun postFeedBackWorker(
            @Field("at") accessToken: String,
            @Field("channel") channel: String,
            @Field("text") text: String,
            @Field("subject") subject: String,
            @Field("device") device: String):
                Call<CommonModel>


        @GET("/api/v2/mobile_events/check_use_test_available")
        fun checkUseTestAvailabilityForGuest(
            @Query("email") email: String?,
            @Query("use_test_id") use_test_id: Int?) : Call<TestAvailabilityModel>

        @GET("/api/v2/mobile_events/check_use_test_available")
        fun checkUseTestAvailabilityForWorker(
            @Query("at") token: String?,
            @Query("use_test_id") use_test_id: Int?) : Call<TestAvailabilityModel>

        @FormUrlEncoded
        @POST("/api/v2/mobile_events/no_special_qualification")
        fun post_special_criteria_response(
            @Field("at") accessToken: String,
            @Field("test_id") useTestId: String) : Call<SpecialTechnicalModel>

        @FormUrlEncoded
        @POST("/api/v2/mobile_events/no_technical_qualification")
        fun post_technical_criteria_response(
            @Field("at") accessToken: String,
            @Field("test_id") useTestId: String) : Call<SpecialTechnicalModel>

        @GET("/api/v2/mobile_events/get_screener_question")
        fun getScreenerQuestionForWorker(@Query("at") at: String?, @Query("use_test_id") use_test_id: Int)
                :Call<ScreenerQuestionModel>


        @GET("/api/v2/mobile_events/get_screener_question")
        fun getScreenerQuestionForGuest(@Query("email") at: String?, @Query("name") name: String?,
            @Query("use_test_id") use_test_id: Int) : Call<ScreenerQuestionModel>


        @FormUrlEncoded
        @POST("/api/v2/mobile_events/check_screening_eligibility")
        fun checkScreeningEligibiltyForWorker(
            @Field("at") at: String?,
            @Field("use_test_id") use_test_id: Int,
            @Field("sq_id") sq_id: Int,
            @Field("screener_option_ids") screener_option_ids: String?
        ): Call<ScreenerQuestionModel>


        @FormUrlEncoded
        @POST("/api/v2/mobile_events/check_screening_eligibility")
        fun checkScreeningEligibiltyForGuest(
            @Field("email") email: String?,
            @Field("name") name: String?,
            @Field("use_test_id") use_test_id: Int,
            @Field("sq_id") sq_id: Int,
            @Field("screener_option_ids") screener_option_ids: String?
        ): Call<ScreenerQuestionModel>

        // TODO : terminate
        @FormUrlEncoded
        @POST("/api/v2/mobile_events/terminate")
        fun terminate(
            @Field("at") token: String,
            @Field("test_result_id") test_result_id: String) : Call<CommonModel>


        // TODO : guest login start recording
        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("/api/v2/mobile_events/start_recording_anonymous")
        fun checkTestAvailabilityForGuest(@Body body: JsonObject): Call<JsonObject>

        // TODO : start_recording
        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("/api/v2/mobile_events/start_recording")
        fun checkTestAvailabilityForWorker(@Body body: JsonObject): Call<JsonObject>

        // TODO : save task timing
        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("/api/v2/mobile_events/save_task_timings")
        fun saveTaskTime(@Body body: JsonObject): Call<JsonObject>

        // TODO : feedback for carsh repoort
        @FormUrlEncoded
        @POST("/api/v2/feedback")
        fun postCrashReport(
            @Field("channel") channel: String,
            @Field("text") text: String,
            @Field("device") device: String,
            @Field("subject") subject: String):
                Call<CommonModel>


        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("/api/v2/mobile_events/done_recording")
        fun doneRecording(@Body body: JsonObject): Call<JsonObject>






    }

}