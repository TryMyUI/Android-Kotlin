package com.seattleapplab.trymyui.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ScreenerQuestionModel {
    @SerializedName("status_code")
    @Expose
    var status_code: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    var error: Throwable? = null

    inner class Data {
        @SerializedName("screener_test_page_heading")
        @Expose
        var screener_test_page_heading: String? = null

        @SerializedName("button_title")
        @Expose
        var button_title: String? = null

        @SerializedName("use_test")
        @Expose
        var useTest: UseTest? = null

        @SerializedName("remaining_and_total_questions_text")
        @Expose
        var remaining_and_total_questions_text: String? = null

        @SerializedName("remaining_questions_count")
        @Expose
        var remaining_questions_count = 0

        @SerializedName("screener_test_completed")
        @Expose
        var isScreener_test_completed = false

    }

    inner class UseTest {
        @SerializedName("id")
        @Expose
        var id = 0

        @SerializedName("screener_questions_count")
        @Expose
        var screener_questions_count = 0

        @SerializedName("screener_question")
        @Expose
        var screenerQuestion: ScreenerQuestion? = null

    }

    inner class ScreenerQuestion {
        @SerializedName("id")
        @Expose
        var id = 0

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("use_test_id")
        @Expose
        var use_test_id = 0

        @SerializedName("ans_type")
        @Expose
        var ans_type: String? = null

        @SerializedName("screening_options_count")
        @Expose
        var screening_options_count = 0

        @SerializedName("screening_options")
        @Expose
        var arrayList_screeningoptions: ArrayList<ScreeningOptions>? = null
        var isAnswered = false

    }

    inner class ScreeningOptions {
        @SerializedName("id")
        @Expose
        var id = 0

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("action")
        @Expose
        var action: String? = null

        @SerializedName("screener_question_id")
        @Expose
        var screener_question_id = 0

        var isSelected = false

    }
}