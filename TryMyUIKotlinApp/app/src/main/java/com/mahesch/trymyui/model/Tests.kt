package com.seattleapplab.trymyui.models

import android.os.Parcel
import android.widget.TextView
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Tests {
    var message: String? = null
    @SerializedName("status_code")
    var statusCode = 0
    var data: Data? = null
    var error: Throwable? = null

    /* this MyTests is use for customer*/
    class MyTests {
        @SerializedName("my_test")
        var myTest: MyTest? = null
            private set

        fun setMyTest(mTest: MyTests?) {
            myTest = myTest
        }

        inner class MyTest {
            var id = 0
            var title: String? = null
            var url: String? = null
            var scenario: String? = null
            var recording_timeout_minutes = 0
            var special_qual: String? = null
            var interface_type: String? = null
            var isIs_kind_partial_site = false
                private set
            var is_kind_partial_site_text: String? = null
            var opt_for_task_completion = false
            private var opt_for_seq = false
            var do_impression_test = false
            var tester_platform: String? = null
            var identity_id: String? = null
            var s3_bucket_name: String? = null
            var title_with_id: String? = null
            var native_app_url: String? = null
            var technical_qual: String? = null
            var isScreener_test_avaialable = false

            var isOpt_for_face_recording = false

            var recorder_orientation: String? = null
            @SerializedName("survey_questions")
            var surveyQuestions: List<SurveyQuestions>? = null
            @SerializedName("sus_questions")
            var sus_questions: List<SusQuestions>? = null

            @SerializedName("nps_question")
            @Expose
            var npsQuestion: ArrayList<NpsQuestion>? = null

            @SerializedName("sus_scales")
            @Expose
            var susScales: SusScales? = null
            var tasks: List<Tasks>? = null
            var uxCrowdSurveys: List<UXCrowdSurvey>? = null

            fun setopt_for_seq(seq: Boolean) {
                opt_for_seq = seq
            }

            fun getopt_for_seq(): Boolean {
                return opt_for_seq
            }

            fun setIs_kind_partial_site(is_kind_partial_site: Boolean) {
                isIs_kind_partial_site = is_kind_partial_site
            }

        }
    }

    /* this available tests are use for tester data*/
    class AvailableTests {
        @SerializedName("available_test")
        var availableTest: AvailableTest? = null
            private set

        fun setmAvailableTest(mAvailableTest: AvailableTest?) {
            availableTest = mAvailableTest
        }

        inner class AvailableTest {
            var id = 0
            var title: String? = null
            var url: String? = null
            var scenario: String? = null
            var recording_timeout_minutes = 0
            var special_qual: String? = null
            var interface_type: String? = null
            var isIs_kind_partial_site = false
                private set
            var is_kind_partial_site_text: String? = null
            var opt_for_task_completion = false
            private var opt_for_seq = false
            var do_impression_test = false
            var tester_platform: String? = null
            var title_with_id: String? = null
            var native_app_url: String? = null
            var isScreener_test_avaialble = false

            var isOpt_for_face_recording = false

            var recorder_orientation: String? = null
            var technical_qual: String? = null
            @SerializedName("survey_questions")
            var surveyQuestions: List<SurveyQuestions>? = null
            @SerializedName("sus_questions")
            var sus_questions: List<SusQuestions>? = null
            var tasks: List<Tasks>? = null
            var uxCrowdSurveys: List<UXCrowdSurvey>? = null
            @SerializedName("voting")
            @Expose
            var isVoting = false
            @SerializedName("good_question")
            @Expose
            var good_question: String? = null
            @SerializedName("bad_question")
            @Expose
            var bad_question: String? = null
            @SerializedName("suggestion_question")
            @Expose
            var suggestion_question: String? = null
            @SerializedName("good_response_question_id")
            @Expose
            var good_response_question_id = 0
            @SerializedName("bad_response_question_id")
            @Expose
            var bad_response_question_id = 0
            @SerializedName("suggestion_response_question_id")
            @Expose
            var suggestion_response_question_id = 0
            @SerializedName("max_voting_limit")
            @Expose
            var max_voting_limit = 0
            @SerializedName("response_type")
            @Expose
            var response_type: ArrayList<String>? = null
            @SerializedName("good_responses")
            @Expose
            var goodResponsesArrayList: ArrayList<GoodResponses>? = null
            @SerializedName("bad_responses")
            @Expose
            var badResponsesArrayList: ArrayList<BadResponses>? = null
            @SerializedName("suggestion_responses")
            @Expose
            var suggestionResponsesArrayList: ArrayList<SuggestionResponses>? =
                null
            @SerializedName("nps_question")
            @Expose
            var npsQuestion_list: ArrayList<NpsQuestion>? = null

            @SerializedName("sus_scales")
            @Expose
            var susScales: SusScales? = null

            fun setopt_for_seq(seq: Boolean) {
                opt_for_seq = seq
            }

            fun getopt_for_seq(): Boolean {
                return opt_for_seq
            }

            fun setIs_kind_partial_site(is_kind_partial_site: Boolean) {
                isIs_kind_partial_site = is_kind_partial_site
            }

        }
    }

    /* use for view tester performed test*/
    class PerformedTests {
        @SerializedName("performed_test")
        var performedTest: PerformedTest? = null
        var rating: Rating? = null

        inner class Rating {
            var id = 0
            var score = 0
            var comment: String? = null

        }

        inner class PerformedTest {
            var rating: Rating? = null
            var id = 0
            var status: String? = null
            var date: String? = null
            var test: String? = null
            var url: String? = null
            @SerializedName("video_url")
            var videoUrl: String? = null
            var scenario: String? = null
            var title_with_id: String? = null

        }
    }

    /* this MyTests is use for pending test that tester not submitted any written summary or sus or ux crowd */
    class PendingTests {
        @SerializedName("pending_test")
        var pending_tests: PendingTest? = null
            private set

        fun setPending_test(mTest: PendingTest?) {
            pending_tests = mTest
        }

        inner class PendingTest {
            var id = 0
            var title: String? = null
            var url: String? = null
            var scenario: String? = null
            var title_with_id: String? = null
            var interface_type: String? = null
            @SerializedName("survey_questions")
            var surveyQuestions: List<SurveyQuestions>? = null
            @SerializedName("sus_questions")
            var sus_questions: List<SusQuestions>? = null
            var tasks: List<Tasks>? = null
            var uxCrowdSurveys: List<UXCrowdSurvey>? = null
            @SerializedName("nps_question")
            @Expose
            var npsQuestion: ArrayList<NpsQuestion>? = null

            @SerializedName("sus_scales")
            @Expose
            var susScales: SusScales? = null
            @SerializedName("voting")
            @Expose
            var isVoting = false
            @SerializedName("good_question")
            @Expose
            var good_question: String? = null
            @SerializedName("bad_question")
            @Expose
            var bad_question: String? = null
            @SerializedName("suggestion_question")
            @Expose
            var suggestion_question: String? = null
            @SerializedName("good_response_question_id")
            @Expose
            var good_response_question_id = 0
            @SerializedName("bad_response_question_id")
            @Expose
            var bad_response_question_id = 0
            @SerializedName("suggestion_response_question_id")
            @Expose
            var suggestion_response_question_id = 0
            @SerializedName("max_voting_limit")
            @Expose
            var max_voting_limit = 0
            @SerializedName("response_type")
            @Expose
            var response_type: ArrayList<String>? = null

            @SerializedName("good_responses")
            @Expose
            var goodResponsesArrayList: ArrayList<GoodResponses>? = null
            @SerializedName("bad_responses")
            @Expose
            var badResponsesArrayList: ArrayList<BadResponses>? = null
            @SerializedName("suggestion_responses")
            @Expose
            var suggestionResponsesArrayList: ArrayList<SuggestionResponses>? =
                null

        }
    }

    inner class Data {
        @SerializedName("available_tests")
        var availableTests: List<AvailableTests>? = null
        @SerializedName("performed_tests")
        var performedTests: List<PerformedTests>? = null
        @SerializedName("pending_tests")
        var pendingTests: List<PendingTests>? = null
        var resultPage = 0
        @SerializedName("my_tests")
        var myTests: List<MyTests>? = null
        var identity_id: String? = null
        var s3_bucket_name: String? = null
        @SerializedName("qualification_message")
        var qualification_message: String? = null
        //
        @SerializedName("is_qualified")
        var is_Qulified = false
            private set
        /*this token parameter only need for guest login*/
        var token: String? = null

        fun setPerformedTest(performedTest: List<PerformedTests>?) {
            performedTests = performedTest
        }

        fun setIs_qualified(is_qualified: Boolean) {
            is_Qulified = is_qualified
        }

    }

    inner class SurveyQuestions(`in`: Parcel) {
        var id: Int
        var question: String
        var question_type: String
        @Expose
        @SerializedName("question_info")
        var surveyQuestionsInfo_list: ArrayList<SurveyQuestionsInfo>

        init {
            id = `in`.readInt()
            question = `in`.readString()
            question_type = `in`.readString()
            surveyQuestionsInfo_list =
                `in`.readArrayList(SurveyQuestionsInfo::class.java.classLoader) as ArrayList<SurveyQuestionsInfo>
        }
    }

    inner class SurveyQuestionsInfo {
        var id = 0
        var answer: String? = null
        var use_test_question_id = 0
        var min_value = 0
        var max_value = 0
        var min_label: String? = null
        var max_label: String? = null
        var middle_value = 0
        var response_text: String? = null
        lateinit var selected_option: Array<String>

        var isSelected = false
        var selected_rating_value = 0

    }

    inner class UXCrowdSurvey(`in`: Parcel) {
        var id: Int
        var question: String

        init {
            id = `in`.readInt()
            question = `in`.readString()
        }
    }

    inner class SusQuestions(`in`: Parcel) {
        var id: Int
        var question: String

        init {
            id = `in`.readInt()
            question = `in`.readString()
        }
    }

    inner class NpsQuestion {
        var id = 0
        var question: String? = null
        var min_scale = 0
        var max_scale = 0

        var min_label: String? = null
        var max_label: String? = null

    }

    inner class SusScales {
        var min_scale = 0
        var max_scale = 0
        var isNot_applicable = false

    }

    inner class Tasks(`in`: Parcel) {
        @SerializedName("has_sub_tasks")
        var isHasSubTask: Boolean
        var task: String
        var sub_tasks: List<String>? = null
        var task_id = 0

        var isOpt_for_task_completion = false
        var isOpt_for_seq = false

        init {
            isHasSubTask = `in`.readByte().toInt() != 0
            task = `in`.readString()
        }
    }

    inner class GoodResponses : Serializable {
        @SerializedName("response_id")
        @Expose
        var gr_response_id = 0
        @SerializedName("response_text")
        @Expose
        var gr_response_text: String? = null
        var good_vote_given = false
        var good_vote_value = 0
        var good_comment: String? = null
        @Transient
        var tv_plus: TextView? = null
        @Transient
        var tv_minus: TextView? = null

    }

    inner class BadResponses : Serializable {
        @SerializedName("response_id")
        @Expose
        var br_response_id = 0
        @SerializedName("response_text")
        @Expose
        var br_response_text: String? = null
        var bad_vote_given = false
        var bad_vote_value = 0
        var bad_comment: String? = null
        @Transient
        var tv_plus: TextView? = null
        @Transient
        var tv_minus: TextView? = null

    }

    inner class SuggestionResponses : Serializable {
        @SerializedName("response_id")
        @Expose
        var sr_response_id = 0
        @SerializedName("response_text")
        @Expose
        var sr_response_text: String? = null
        var suggestion_vote_given = false
        var suggestion_vote_value = 0
        var suggestion_comment: String? = null
        @Transient
        var tv_plus: TextView? = null
        @Transient
        var tv_minus: TextView? = null

    }
}