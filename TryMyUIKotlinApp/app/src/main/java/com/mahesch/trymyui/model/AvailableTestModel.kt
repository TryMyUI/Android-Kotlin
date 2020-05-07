package com.mahesch.trymyui.model

import com.seattleapplab.trymyui.models.Tests.*
import java.io.Serializable
import java.util.*

data class AvailableTestModel(  var id: Int? = 0,
                                var pos : Int?= 0,
                                var title: String? = null,
                              var url: String? = null,
                              var task: String? = null,
                              var scenario: String? = null,
                              var surveyQuestions: String? = null,
                              var specialQalification: String? = "",
                              var interface_type: String? = null,
                              var susQuestion: String? = null,
                              var tester_platform: String? = null,
                              var ux_crowd_questions: String? = null,
                              var is_kind_partial_site_text: String? = null,
                              var title_with_id: String? = null,
                              var native_app_url:String? = null,
                              var recording_timeout_minutes:Int? = 0,
                              var seq_task : Boolean?= false,
                              var task_complete : Boolean?= false,
                              var do_impression_test: Boolean? = false,
                              var is_kind_partial_site : Boolean?= false,
                              var screener_test_available: Boolean? = false,
                              var isVoting: Boolean?= false,
                              var response_type_list: ArrayList<String>? = null,
                              var good_question: String? = null,
                              var bad_question: String? = null,
                              var suggestion_question: String? = null,
                              var good_response_question_id : Int? = 0,
                              var bad_response_question_id : Int?= 0,
                              var suggestion_response_question_id : Int?= 0,
                              var max_voting_limit: Int? = 0,
                              var goodResponsesArrayList: ArrayList<GoodResponses>? = null,
                              var badResponsesArrayList: ArrayList<BadResponses>? = null,
                              var suggestionResponsesArrayList: ArrayList<SuggestionResponses>? = null,
                              var npsQuestion: String? = null,
                              var sus_scales: String? = null,
                              var opt_for_face_recording: Boolean? = false,
                              var recorder_orientation: String? = null,
                              var technicalQualification: String? = null) : Serializable {






}