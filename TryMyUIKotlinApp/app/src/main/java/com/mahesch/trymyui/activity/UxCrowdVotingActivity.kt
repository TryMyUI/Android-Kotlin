package com.mahesch.trymyui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.viewmodelfactory.UxCrowdBadModelFactory
import com.mahesch.trymyui.viewmodelfactory.UxCrowdGoodModelFactory
import com.mahesch.trymyui.viewmodelfactory.UxCrowdSuggestionModelFactory
import com.mahesch.trymyui.viewmodelfactory.UxCrowdViewModelFactory
import com.mahesch.trymyui.viewmodels.UxCrowdBadVotingViewModel
import com.mahesch.trymyui.viewmodels.UxCrowdGoodVotingViewModel
import com.mahesch.trymyui.viewmodels.UxCrowdSuggestionVotingViewModel
import com.seattleapplab.trymyui.models.Tests.*
import kotlinx.android.synthetic.main.ux_crowd_voting_activity.*
import java.util.*

class UxCrowdVotingActivity : AppCompatActivity() {

    private lateinit var availableTestModel: AvailableTestModel
    private val TAG = UxCrowdVotingActivity::class.java.simpleName.toUpperCase()

    private var question_number = 0
    private var no_of_questions_attempted = 0

    var key_for_response = ArrayList<String>()
    var tv_response_vote_arraylist = ArrayList<TextView>()

    var remaining_votes = 3
    var back_press_count = 0

    private var isGoodQuestionEditable = true
    private var isBadQuestionEditable = true
    private val isSuggestionQuestionEditable = true

    private val good_question_page = 0
    private val bad_question_page = 1
    private val suggestion_question_page = 2

    private var currentPage = -1

    private val et_comments_list = ArrayList<EditText>()
    private lateinit var uxCrowdActivityViewModel_good: UxCrowdGoodVotingViewModel
    private lateinit var uxCrowdActivityViewModel_bad: UxCrowdBadVotingViewModel
    private lateinit var uxCrowdActivityViewModel_suggestion: UxCrowdSuggestionVotingViewModel

    private var back_alert: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ux_crowd_voting_activity)

        if(intent != null){
            availableTestModel = intent.extras.getSerializable("availableTestConstants") as AvailableTestModel

            Log.e(TAG,"isVoting "+availableTestModel.isVoting)

            if(availableTestModel == null){
                moveToHome()
            }
        }
        else
        {
            moveToHome()
        }

        addResponseTypeToList()

        btn_reset.setOnClickListener { onResetButton() }

        btn_next.setOnClickListener { onNextButton() }

        showFirstQuestion(key_for_response[0])

        val goodfactory =
            UxCrowdGoodModelFactory(
                application
            )

        val badFactory =
            UxCrowdBadModelFactory(
                application
            )

        val suggestionFactory =
            UxCrowdSuggestionModelFactory(
                application
            )

        uxCrowdActivityViewModel_good = ViewModelProvider(this,goodfactory).get(UxCrowdGoodVotingViewModel::class.java)
        uxCrowdActivityViewModel_bad = ViewModelProvider(this,badFactory).get(UxCrowdBadVotingViewModel::class.java)
        uxCrowdActivityViewModel_suggestion = ViewModelProvider(this,suggestionFactory).get(UxCrowdSuggestionVotingViewModel::class.java)


    }

    private fun addResponseTypeToList(){
        for(key in availableTestModel?.response_type_list!!){
            key_for_response.add(key)
        }
    }

    private fun onResetButton(){
            resetAllVotesForCurrentPage()
    }


    private fun onNextButton(){
        if (remaining_votes > 0) {
            Toast.makeText(
                this@UxCrowdVotingActivity,
                "" + resources.getString(R.string.please_vote),
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (back_press_count == no_of_questions_attempted) {
                Log.e(TAG, "server call")
               if(Utils.isInternetAvailable(this)){

                   sendVoteToServer()
               }
                else{
                   Utils.showInternetCheckToast(this)
               }

            } else {
                Log.e(TAG, "No server call show question")
                back_press_count = back_press_count + 1
                showUxQuestion()
            }
        }
    }

    private fun sendVoteToServer(){
        if(back_press_count == 0)
        {
            ProgressDialog.initializeProgressDialogue(this)
            ProgressDialog.showProgressDialog()
           sendGoodQuestionVote()
        }
        else if(back_press_count == 1)
        {
            ProgressDialog.initializeProgressDialogue(this)
            ProgressDialog.showProgressDialog()
            sendBadQuestionVote()
        }
        else if(back_press_count == 2)
        {
            ProgressDialog.initializeProgressDialogue(this)
            ProgressDialog.showProgressDialog()
            sendSuggestionQuestionVote()
        }
    }

    private fun updateManageFlowAfterTest(){
        var manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this@UxCrowdVotingActivity)
        manageFlowAfterTest.isSurveyQuestionsSubmitted = true
        manageFlowAfterTest.isSusQuestionsSubmitted = true
        manageFlowAfterTest.isNpsQuestionSubmitted = true
        manageFlowAfterTest.isUXCrowdSurveySubmitted = true
    }

    private fun sendGoodQuestionVote(){
        uxCrowdActivityViewModel_good.submitUxCrowdGoodVoting(getGoodQuestionData()).observe(this, object : Observer<CommonModel>{
            override fun onChanged(commonModel: CommonModel) {
                    ProgressDialog.dismissProgressDialog()
                if(commonModel == null){
                    showWentWrongDialog()
                } else{
                    if(commonModel.error == null){
                        if(commonModel.statusCode == 200){


                           updateGoodVoteGiven()

                            refreshView()


                        } else{
                            showErrorDialog(commonModel)
                        }
                    } else{
                        showWentWrongDialog()
                    }
                }


            }

        })
    }

    private fun sendBadQuestionVote(){
        uxCrowdActivityViewModel_bad.submitUxCrowdBadVoting(getBadQuestionData()).observe(this, object : Observer<CommonModel>{
            override fun onChanged(commonModel: CommonModel) {

                ProgressDialog.dismissProgressDialog()

                if(commonModel == null){
                    showWentWrongDialog()
                } else{
                    if(commonModel.error == null){
                        if(commonModel.statusCode == 200){


                            updateBadVoteGiven()

                            refreshView()


                        } else{
                            showErrorDialog(commonModel)
                        }
                    } else{
                        showWentWrongDialog()
                    }
                }



            }

        })
    }

    private fun sendSuggestionQuestionVote(){
        uxCrowdActivityViewModel_suggestion.submitUxCrowdSuggestionVoting(getSuggestionQuestionData()).observe(this, object : Observer<CommonModel>{
            override fun onChanged(commonModel: CommonModel) {
                ProgressDialog.dismissProgressDialog()
                if(commonModel == null){
                    showWentWrongDialog()
                } else{
                    if(commonModel.error == null){
                        if(commonModel.statusCode == 200){

                            updateManageFlowAfterTest()

                            updateSuggestionVoteGiven()



                        } else{
                            showErrorDialog(commonModel)
                        }
                    } else{
                        showWentWrongDialog()
                    }
                }



            }

        })
    }

    private fun updateGoodVoteGiven(){
        isGoodQuestionEditable = false

        for (goodResponses in availableTestModel.goodResponsesArrayList!!) {
            goodResponses.good_vote_given = true
        }
    }

    private fun updateBadVoteGiven(){
        isGoodQuestionEditable = false
        isBadQuestionEditable = false

        for (goodResponses in availableTestModel.goodResponsesArrayList!!) {
            goodResponses.good_vote_given = true
        }

        for (badResponses in availableTestModel.badResponsesArrayList!!) {
            badResponses.bad_vote_given = true
        }
    }

    private fun refreshView(){
        nestedScrollView.fullScroll(ScrollView.FOCUS_UP)
        appbarlayout.setExpanded(true, true)

        no_of_questions_attempted = no_of_questions_attempted + 1
        back_press_count = back_press_count + 1

        showUxQuestion()
    }

    private fun updateSuggestionVoteGiven(){
        isGoodQuestionEditable = false
        isBadQuestionEditable = false

        for (goodResponses in availableTestModel.goodResponsesArrayList!!) {
            goodResponses.good_vote_given = true
        }

        for (badResponses in availableTestModel.badResponsesArrayList!!) {
            badResponses.bad_vote_given = true
        }


        for (suggestionResponses in availableTestModel.suggestionResponsesArrayList!!) {
            suggestionResponses.suggestion_vote_given = true
        }
    }



    private fun resetAllVotesForCurrentPage() {
        remaining_votes = 3
        tv_vote_remaining_count.text = "" + remaining_votes
        for (tv in tv_response_vote_arraylist) {
            tv.text = "0"
        }
        val question_tpe = key_for_response[back_press_count]
        when (question_tpe) {
            "good_responses" -> resetAllGoodcolors()
            "bad_responses" -> resetAllBadcolors()
            "suggestion_responses" -> resetAllSuggestioncolors()
        }
    }

    private fun showFirstQuestion(questionTypeString : String){
        when(questionTypeString){
            "good_responses" -> showGoodQuestion()
            "bad_responses" -> showBadQuestion()
            "suggestion_responses" -> showSuggestionQuestion()
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun moveToHome(){
        var intent = Intent(UxCrowdVotingActivity@this,TabActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showGoodQuestion(){
        tv_ux_question.text = availableTestModel.good_question

        question_number = question_number +1

        remaining_votes = 3

        tv_response_vote_arraylist.clear()

        ll_add_vote_comment_view.removeAllViews()

        tv_vote_remaining_count.text = "" + remaining_votes

        val goodResponsesArrayList: ArrayList<GoodResponses>? = availableTestModel?.goodResponsesArrayList

        et_comments_list.clear()

        tv_vote_verbiage.visibility = View.VISIBLE
        rl_vote_remaining.visibility = View.VISIBLE

        for (i in goodResponsesArrayList!!.indices) {
            val goodResponses = goodResponsesArrayList!![i]
            val vote_comment_view: View =
                layoutInflater.inflate(R.layout.vote_comment_layout, null)
            val tv_response_text =
                vote_comment_view.findViewById<View>(R.id.tv_response_text) as TextView
            val tv_vote_value =
                vote_comment_view.findViewById<View>(R.id.tv_vote_value) as TextView
            tv_response_vote_arraylist.add(tv_vote_value)
            val tv_plus_vote =
                vote_comment_view.findViewById<View>(R.id.tv_plus_vote) as TextView
            val tv_minus_vote =
                vote_comment_view.findViewById<View>(R.id.tv_minus_vote) as TextView
            val tv_lbl_comment =
                vote_comment_view.findViewById<View>(R.id.tv_lbl_comment) as TextView
            val et_comment =
                vote_comment_view.findViewById<View>(R.id.et_comment) as EditText
            val tv_cancel_comment =
                vote_comment_view.findViewById<View>(R.id.tv_cancel_comment) as TextView
            val tv_save_comment =
                vote_comment_view.findViewById<View>(R.id.tv_save_comment) as TextView
            tv_cancel_comment.text = Html.fromHtml(
                "<u>" + resources.getString(R.string.cancel_comment).toString() + "</u>"
            )
            tv_save_comment.text = Html.fromHtml(
                "<u>" + resources.getString(R.string.save_comment).toString() + "</u>"
            )
            et_comments_list.add(et_comment)
            goodResponses.tv_plus = tv_plus_vote
            goodResponses.tv_minus = tv_minus_vote
            tv_vote_value.text = "" + goodResponses.good_vote_value
            if (goodResponses.good_comment != null && goodResponses.good_comment!!.length > 0) {
                et_comment.visibility = View.VISIBLE
                et_comment.setText(goodResponses.good_comment)
            }
            tv_lbl_comment.setOnClickListener {
                et_comment.visibility = View.VISIBLE
                tv_cancel_comment.visibility = View.VISIBLE
                if (et_comment.text.toString().length > 0) {
                    tv_cancel_comment.text = Html.fromHtml("<u>" + "Delete" + "</u>")
                } else {
                    tv_cancel_comment.text = Html.fromHtml("<u>" + "Cancel" + "</u>")
                }
                tv_save_comment.visibility = View.VISIBLE
            }
            tv_cancel_comment.setOnClickListener {
                tv_lbl_comment.text = Html.fromHtml(
                    "<u>" + resources.getString(R.string.add_a_comment)
                        .toString() + "</u>"
                )
                et_comment.setText("")
                et_comment.visibility = View.GONE
                tv_cancel_comment.visibility = View.GONE
                tv_save_comment.visibility = View.GONE
                hideKeyboard(tv_cancel_comment)
            }
            tv_save_comment.setOnClickListener {
                et_comment.visibility = View.GONE
                tv_lbl_comment.text = Html.fromHtml("<u>" + "View/edit comment" + "</u>")
                tv_cancel_comment.visibility = View.GONE
                tv_save_comment.visibility = View.GONE
                hideKeyboard(tv_save_comment)
            }
            tv_response_text.text = goodResponses.gr_response_text
            ll_add_vote_comment_view.addView(vote_comment_view)
            tv_plus_vote.setOnClickListener {
                if (remaining_votes > 0) {
                    remaining_votes = remaining_votes - 1
                    Log.e(TAG, "remaining  votes $remaining_votes")
                    tv_vote_remaining_count.text = "" + remaining_votes
                    if (tv_vote_value.text.toString().equals("0", ignoreCase = true)) {
                        tv_vote_value.text = "1"
                        goodResponses.good_vote_value = 1
                    } else if (tv_vote_value.text.toString()
                            .equals("1", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "2"
                        goodResponses.good_vote_value = 2
                    } else if (tv_vote_value.text.toString()
                            .equals("2", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "3"
                        goodResponses.good_vote_value = 3
                    }
                    colorValidationForGoodResponses(goodResponsesArrayList)
                }
            }
            tv_minus_vote.setOnClickListener {
                if (!tv_vote_value.text.toString().equals("0", ignoreCase = true)) {
                    remaining_votes = remaining_votes + 1
                    Log.e(TAG, "remaining  votes $remaining_votes")
                    tv_vote_remaining_count.text = "" + remaining_votes
                    if (tv_vote_value.text.toString().equals("1", ignoreCase = true)) {
                        tv_vote_value.text = "0"
                        goodResponses.good_vote_value = 0
                    } else if (tv_vote_value.text.toString()
                            .equals("2", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "1"
                        goodResponses.good_vote_value = 1
                    } else if (tv_vote_value.text.toString()
                            .equals("3", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "2"
                        goodResponses.good_vote_value = 2
                    }
                } else {
                    Log.e(TAG, "else - ")
                }
                colorValidationForGoodResponses(goodResponsesArrayList)
            }
            et_comment.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {
                    goodResponses.good_comment = et_comment.text.toString()
                }
            })
            et_comment.setOnTouchListener { view, event ->
                if (view.id == R.id.et_comment) {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> view.parent
                            .requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }
            if (!isGoodQuestionEditable) {
                tv_plus_vote.isClickable = false
                tv_minus_vote.isClickable = false
                btn_reset.isClickable = false
                tv_lbl_comment.isClickable = false
                tv_plus_vote.setBackgroundColor(resources.getColor(R.color.light_grey))
                tv_minus_vote.setBackgroundColor(resources.getColor(R.color.light_grey))
                tv_vote_disabled_msg.visibility = View.VISIBLE
                remaining_votes = 0
                tv_vote_remaining_count.text = "" + remaining_votes
                tv_lbl_comment.visibility = View.INVISIBLE
                tv_vote_verbiage.visibility = View.GONE
                rl_vote_remaining.visibility = View.VISIBLE
                if (goodResponses.good_comment != null && goodResponses.good_comment!!.length > 0) {
                    et_comment.visibility = View.VISIBLE
                    et_comment.setText(goodResponses.good_comment)
                    et_comment.isFocusable = false
                }
            } else {
                tv_vote_disabled_msg.visibility = View.GONE
            }
        }

    }

    private fun showBadQuestion() {
        tv_ux_question.text = (availableTestModel.bad_question)
        question_number = question_number + 1
        remaining_votes = 3
        tv_response_vote_arraylist.clear()
        ll_add_vote_comment_view.removeAllViews()
        tv_vote_remaining_count.text = "" + remaining_votes
        val badResponsesArrayList =
            availableTestModel.badResponsesArrayList
        et_comments_list.clear()
        tv_vote_verbiage.visibility = View.VISIBLE
        rl_vote_remaining.visibility = View.VISIBLE
        for (badResponses in badResponsesArrayList!!) {
            val vote_comment_view =
                layoutInflater.inflate(R.layout.vote_comment_layout, null)
            val tv_response_text =
                vote_comment_view.findViewById<View>(R.id.tv_response_text) as TextView
            val tv_vote_value =
                vote_comment_view.findViewById<View>(R.id.tv_vote_value) as TextView
            tv_response_vote_arraylist.add(tv_vote_value)
            val tv_plus_vote =
                vote_comment_view.findViewById<View>(R.id.tv_plus_vote) as TextView
            val tv_minus_vote =
                vote_comment_view.findViewById<View>(R.id.tv_minus_vote) as TextView
            val tv_lbl_comment =
                vote_comment_view.findViewById<View>(R.id.tv_lbl_comment) as TextView
            val et_comment =
                vote_comment_view.findViewById<View>(R.id.et_comment) as EditText
            val tv_cancel_comment =
                vote_comment_view.findViewById<View>(R.id.tv_cancel_comment) as TextView
            val tv_save_comment =
                vote_comment_view.findViewById<View>(R.id.tv_save_comment) as TextView
            tv_cancel_comment.text = Html.fromHtml(
                "<u>" + resources.getString(R.string.cancel_comment).toString() + "</u>"
            )
            tv_save_comment.text = Html.fromHtml(
                "<u>" + resources.getString(R.string.save_comment).toString() + "</u>"
            )
            et_comments_list.add(et_comment)
            badResponses.tv_plus = tv_plus_vote
            badResponses.tv_minus = tv_minus_vote
            tv_vote_value.text = "" + badResponses.bad_vote_value
            val vote_count = tv_vote_remaining_count.text.toString().toInt()
            remaining_votes = vote_count - tv_vote_value.text.toString().toInt()
            tv_vote_remaining_count.text = "" + remaining_votes
            tv_lbl_comment.setOnClickListener {
                et_comment.visibility = View.VISIBLE
                tv_cancel_comment.visibility = View.VISIBLE
                tv_save_comment.visibility = View.VISIBLE
                if (et_comment.text.toString().length > 0) {
                    tv_cancel_comment.text = Html.fromHtml("<u>" + "Delete" + "</u>")
                } else {
                    tv_cancel_comment.text = Html.fromHtml("<u>" + "Cancel" + "</u>")
                }
            }
            tv_cancel_comment.setOnClickListener {
                tv_lbl_comment.text = Html.fromHtml(
                    "<u>" + resources.getString(R.string.add_a_comment)
                        .toString() + "</u>"
                )
                et_comment.setText("")
                et_comment.visibility = View.GONE
                tv_cancel_comment.visibility = View.GONE
                tv_save_comment.visibility = View.GONE
                hideKeyboard(tv_cancel_comment)
            }
            tv_save_comment.setOnClickListener {
                et_comment.visibility = View.GONE
                tv_lbl_comment.text = Html.fromHtml("<u>" + "View/edit comment" + "</u>")
                tv_cancel_comment.visibility = View.GONE
                tv_save_comment.visibility = View.GONE
                hideKeyboard(tv_save_comment)
            }
            tv_response_text.text = badResponses.br_response_text
            ll_add_vote_comment_view.addView(vote_comment_view)
            tv_plus_vote.setOnClickListener {
                if (remaining_votes > 0) {
                    remaining_votes = remaining_votes - 1
                    Log.e(TAG, "remaining  votes $remaining_votes")
                    tv_vote_remaining_count.text = "" + remaining_votes
                    if (tv_vote_value.text.toString().equals("0", ignoreCase = true)) {
                        tv_vote_value.text = "1"
                        badResponses.bad_vote_value = 1
                    } else if (tv_vote_value.text.toString()
                            .equals("1", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "2"
                        badResponses.bad_vote_value = 2
                    } else if (tv_vote_value.text.toString()
                            .equals("2", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "3"
                        badResponses.bad_vote_value = 3
                    }
                } else {
                    Log.e(TAG, "else + ")
                }
                colorValidationForBadResponses(badResponsesArrayList)
            }
            tv_minus_vote.setOnClickListener {
                if (!tv_vote_value.text.toString().equals("0", ignoreCase = true)) {
                    remaining_votes = remaining_votes + 1
                    Log.e(TAG, "remaining  votes $remaining_votes")
                    tv_vote_remaining_count.text = "" + remaining_votes
                    if (tv_vote_value.text.toString().equals("1", ignoreCase = true)) {
                        tv_vote_value.text = "0"
                        badResponses.bad_vote_value = 0
                    } else if (tv_vote_value.text.toString()
                            .equals("2", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "1"
                        badResponses.bad_vote_value = 1
                    } else if (tv_vote_value.text.toString()
                            .equals("3", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "2"
                        badResponses.bad_vote_value = 2
                    }
                } else {
                    Log.e(TAG, "else - ")
                }
                colorValidationForBadResponses(badResponsesArrayList)
            }
            et_comment.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {
                    badResponses.bad_comment = et_comment.text.toString()
                }
            })
            et_comment.setOnTouchListener { view, event ->
                if (view.id == R.id.et_comment) {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> view.parent
                            .requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }
            if (!isBadQuestionEditable) {
                tv_plus_vote.isClickable = false
                tv_minus_vote.isClickable = false
                btn_reset.isClickable = false
                tv_lbl_comment.isClickable = false
                tv_plus_vote.setBackgroundColor(resources.getColor(R.color.light_grey))
                tv_minus_vote.setBackgroundColor(resources.getColor(R.color.light_grey))
                tv_vote_disabled_msg.visibility = View.VISIBLE
                tv_lbl_comment.visibility = View.INVISIBLE
                remaining_votes = 0
                tv_vote_remaining_count.text = "" + remaining_votes
                tv_vote_verbiage.visibility = View.GONE
                rl_vote_remaining.visibility = View.VISIBLE
                if (badResponses.bad_comment != null && badResponses.bad_comment!!.length > 0) {
                    et_comment.visibility = View.VISIBLE
                    et_comment.setText(badResponses.bad_comment)
                    et_comment.isFocusable = false
                }
            } else {
                tv_vote_disabled_msg.visibility = View.GONE
                btn_reset.isClickable = true
                tv_vote_verbiage.visibility = View.VISIBLE
                rl_vote_remaining.visibility = View.VISIBLE
            }
        }
        if (isBadQuestionEditable) colorValidationForBadResponses(badResponsesArrayList)
    }


    private fun showSuggestionQuestion() {
        tv_ux_question.text = (availableTestModel.suggestion_question)
        question_number = question_number + 1
        remaining_votes = 3
        tv_response_vote_arraylist.clear()
        ll_add_vote_comment_view.removeAllViews()
        tv_vote_remaining_count.text = "" + remaining_votes
        val suggestionResponsesArrayList =
            availableTestModel.suggestionResponsesArrayList
        tv_vote_disabled_msg.visibility = View.GONE
        btn_reset.isClickable = true
        tv_vote_verbiage.visibility = View.VISIBLE
        rl_vote_remaining.visibility = View.VISIBLE
        for (suggestionResponses in suggestionResponsesArrayList!!) {
            val vote_comment_view =
                layoutInflater.inflate(R.layout.vote_comment_layout, null)
            val tv_response_text =
                vote_comment_view.findViewById<View>(R.id.tv_response_text) as TextView
            val tv_vote_value =
                vote_comment_view.findViewById<View>(R.id.tv_vote_value) as TextView
            tv_response_vote_arraylist.add(tv_vote_value)
            val tv_plus_vote =
                vote_comment_view.findViewById<View>(R.id.tv_plus_vote) as TextView
            val tv_minus_vote =
                vote_comment_view.findViewById<View>(R.id.tv_minus_vote) as TextView
            val tv_lbl_comment =
                vote_comment_view.findViewById<View>(R.id.tv_lbl_comment) as TextView
            val et_comment =
                vote_comment_view.findViewById<View>(R.id.et_comment) as EditText
            et_comment.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            val tv_cancel_comment =
                vote_comment_view.findViewById<View>(R.id.tv_cancel_comment) as TextView
            val tv_save_comment =
                vote_comment_view.findViewById<View>(R.id.tv_save_comment) as TextView
            tv_cancel_comment.text = Html.fromHtml(
                "<u>" + resources.getString(R.string.cancel_comment).toString() + "</u>"
            )
            tv_save_comment.text = Html.fromHtml(
                "<u>" + resources.getString(R.string.save_comment).toString() + "</u>"
            )
            et_comments_list.add(et_comment)
            suggestionResponses.tv_plus = tv_plus_vote
            suggestionResponses.tv_minus = tv_minus_vote
            tv_vote_value.text = "" + suggestionResponses.suggestion_vote_value
            val vote_count = tv_vote_remaining_count.text.toString().toInt()
            remaining_votes = vote_count - tv_vote_value.text.toString().toInt()
            tv_vote_remaining_count.text = "" + remaining_votes
            if (suggestionResponses.suggestion_comment != null && suggestionResponses.suggestion_comment!!.length > 0) {
                et_comment.visibility = View.VISIBLE
                et_comment.setText(suggestionResponses.suggestion_comment)
                et_comment.isFocusable = false
            }
            tv_lbl_comment.setOnClickListener {
                et_comment.visibility = View.VISIBLE
                tv_cancel_comment.visibility = View.VISIBLE
                tv_save_comment.visibility = View.VISIBLE
                if (et_comment.text.toString().length > 0) {
                    tv_cancel_comment.text = Html.fromHtml("<u>" + "Delete" + "</u>")
                } else {
                    tv_cancel_comment.text = Html.fromHtml("<u>" + "Cancel" + "</u>")
                }
            }
            tv_cancel_comment.setOnClickListener {
                tv_lbl_comment.text = Html.fromHtml(
                    "<u>" + resources.getString(R.string.add_a_comment)
                        .toString() + "</u>"
                )
                et_comment.setText("")
                et_comment.visibility = View.GONE
                tv_cancel_comment.visibility = View.GONE
                tv_save_comment.visibility = View.GONE
                hideKeyboard(tv_cancel_comment)
            }
            tv_save_comment.setOnClickListener {
                et_comment.visibility = View.GONE
                tv_lbl_comment.text = Html.fromHtml("<u>" + "View/edit comment" + "</u>")
                tv_cancel_comment.visibility = View.GONE
                tv_save_comment.visibility = View.GONE
                hideKeyboard(tv_save_comment)
            }
            tv_response_text.text = suggestionResponses.sr_response_text
            ll_add_vote_comment_view.addView(vote_comment_view)
            tv_plus_vote.setOnClickListener {
                if (remaining_votes > 0) {
                    remaining_votes = remaining_votes - 1
                    Log.e(TAG, "remaining  votes $remaining_votes")
                    tv_vote_remaining_count.text = "" + remaining_votes
                    if (tv_vote_value.text.toString().equals("0", ignoreCase = true)) {
                        tv_vote_value.text = "1"
                        suggestionResponses.suggestion_vote_value = 1
                    } else if (tv_vote_value.text.toString()
                            .equals("1", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "2"
                        suggestionResponses.suggestion_vote_value = 2
                    } else if (tv_vote_value.text.toString()
                            .equals("2", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "3"
                        suggestionResponses.suggestion_vote_value = 3
                    }
                    colorValidationForSuggestionResponses(suggestionResponsesArrayList)
                } else {
                    Log.e(TAG, "else + ")
                }
            }
            tv_minus_vote.setOnClickListener {
                if (!tv_vote_value.text.toString().equals("0", ignoreCase = true)) {
                    remaining_votes = remaining_votes + 1
                    Log.e(TAG, "remaining  votes $remaining_votes")
                    tv_vote_remaining_count.text = "" + remaining_votes
                    if (tv_vote_value.text.toString().equals("1", ignoreCase = true)) {
                        tv_vote_value.text = "0"
                        suggestionResponses.suggestion_vote_value = 0
                    } else if (tv_vote_value.text.toString()
                            .equals("2", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "1"
                        suggestionResponses.suggestion_vote_value = 1
                    } else if (tv_vote_value.text.toString()
                            .equals("3", ignoreCase = true)
                    ) {
                        tv_vote_value.text = "2"
                        suggestionResponses.suggestion_vote_value = 2
                    }
                } else {
                    Log.e(TAG, "else - ")
                }
                colorValidationForSuggestionResponses(suggestionResponsesArrayList)
            }
            et_comment.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    Log.e(TAG, "beforeTextChanged")
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    Log.e(TAG, "onTextChanged")
                }

                override fun afterTextChanged(s: Editable) {
                    suggestionResponses.suggestion_comment = et_comment.text.toString()
                    Log.e(TAG, "afterTextChanged")
                }
            })
            et_comment.setOnTouchListener { view, event ->
                if (view.id == R.id.et_comment) {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> view.parent
                            .requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }
        }
        if (isSuggestionQuestionEditable) {
            colorValidationForSuggestionResponses(suggestionResponsesArrayList)
        }
    }

    private fun hideKeyboard(view: View){
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.e(TAG, "event $event")
        return super.onTouchEvent(event)
    }


    private fun colorValidationForGoodResponses(goodResponsesArrayList: ArrayList<GoodResponses>) {
        for (i in goodResponsesArrayList.indices) {
            val goodResponses = goodResponsesArrayList[i]

            //   Log.e(TAG, "goodResponses vote value "+goodResponses.getGood_vote_value()+" "+i);
            if (goodResponses.good_vote_value == 0) {
                when (remaining_votes) {
                    0 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                    1 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                    2 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                    3 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                }
            } else if (goodResponses.good_vote_value == 1) {
                when (remaining_votes) {
                    0 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                    1 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                    2 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                }
            } else if (goodResponses.good_vote_value == 2) {
                when (remaining_votes) {
                    0 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                    1 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                }
            } else if (goodResponses.good_vote_value == 3) {
                when (remaining_votes) {
                    0 -> {
                        goodResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        goodResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                }
            }
        }
    }

    private fun colorValidationForBadResponses(badResponsesArrayList: ArrayList<BadResponses>) {
        for (i in badResponsesArrayList.indices) {
            val badResponses = badResponsesArrayList[i]
            if (badResponses.bad_vote_value == 0) {
                when (remaining_votes) {
                    0 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                    1 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                    2 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                    3 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                }
            } else if (badResponses.bad_vote_value == 1) {
                when (remaining_votes) {
                    0 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                    1 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                    2 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                }
            } else if (badResponses.bad_vote_value == 2) {
                when (remaining_votes) {
                    0 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                    1 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                }
            } else if (badResponses.bad_vote_value == 3) {
                when (remaining_votes) {
                    0 -> {
                        badResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        badResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                }
            }
        }
    }

    private fun colorValidationForSuggestionResponses(suggestionResponsesArrayList: ArrayList<SuggestionResponses>) {
        for (i in suggestionResponsesArrayList.indices) {
            val suggestionResponses = suggestionResponsesArrayList[i]
            if (suggestionResponses.suggestion_vote_value == 0) {
                when (remaining_votes) {
                    0 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                    1 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                    2 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                    3 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                    }
                }
            } else if (suggestionResponses.suggestion_vote_value == 1) {
                when (remaining_votes) {
                    0 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                    1 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                    2 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                }
            } else if (suggestionResponses.suggestion_vote_value == 2) {
                when (remaining_votes) {
                    0 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                    1 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.green))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                }
            } else if (suggestionResponses.suggestion_vote_value == 3) {
                when (remaining_votes) {
                    0 -> {
                        suggestionResponses.tv_plus!!.setBackgroundColor(resources.getColor(R.color.edittext_background))
                        suggestionResponses.tv_minus!!.setBackgroundColor(resources.getColor(R.color.orange))
                    }
                }
            }
        }
    }

    private fun resetAllGoodcolors() {
        val goodResponsesArrayList =
            availableTestModel.goodResponsesArrayList
        if (goodResponsesArrayList != null) {
            for (goodResponses in goodResponsesArrayList) {
                goodResponses.good_vote_value = 0
                goodResponses.good_vote_given = false
            }
        }
        if (et_comments_list != null) {
            if (et_comments_list.size > 0) {
                for (i in et_comments_list.indices) {
                    val editText = et_comments_list[i]
                    editText.setText("")
                }
            }
        }
        if (goodResponsesArrayList != null) {
            colorValidationForGoodResponses(goodResponsesArrayList)
        }
    }

    private fun resetAllBadcolors() {
        val badResponsesArrayList =
            availableTestModel.badResponsesArrayList
        if (badResponsesArrayList != null) {
            for (badResponses in badResponsesArrayList) {
                badResponses.bad_vote_value = 0
                badResponses.bad_vote_given = false
            }
        }
        if (et_comments_list != null) {
            if (et_comments_list.size > 0) {
                for (i in et_comments_list.indices) {
                    val editText = et_comments_list[i]
                    editText.setText("")
                }
            }
        }
        if (badResponsesArrayList != null) {
            colorValidationForBadResponses(badResponsesArrayList)
        }
    }

    private fun resetAllSuggestioncolors() {
        val suggestionResponsesArrayList  =
            availableTestModel.suggestionResponsesArrayList
        if (suggestionResponsesArrayList != null) {
            for (suggestionResponses in suggestionResponsesArrayList) {
                suggestionResponses.suggestion_vote_value = 0
                suggestionResponses.suggestion_vote_given = false
            }
        }
        if (et_comments_list != null) {
            if (et_comments_list.size > 0) {
                for (i in et_comments_list.indices) {
                    val editText = et_comments_list[i]
                    editText.setText("")
                }
            }
        }
        colorValidationForSuggestionResponses(suggestionResponsesArrayList!!)
    }

    private fun getGoodQuestionData(): JsonObject {
        val goodResponsesArrayList = availableTestModel.goodResponsesArrayList

        val ux_testing_question_id = "" + availableTestModel.good_response_question_id


        val jsonObject = JsonObject()

        jsonObject.addProperty(
            "test_result_id",
            "" + SharedPrefHelper(this@UxCrowdVotingActivity).getTestResultId()
        )

        if (SharedPrefHelper(this@UxCrowdVotingActivity)?.getGuestTester()) {
            jsonObject.addProperty(
                "email",
                SharedPrefHelper(this@UxCrowdVotingActivity).getEmailId()
            )
        }
        else {
            jsonObject.addProperty(
                "email",
                SharedPrefHelper(this@UxCrowdVotingActivity).getUsername()
            )
        }

        val json_data = JsonObject()

        for (goodResponses in goodResponsesArrayList!!) {
            val jsonObject_response_id = JsonObject()
            jsonObject_response_id.addProperty("score", goodResponses.good_vote_value)
            Log.e(TAG, "comment " + goodResponses.good_comment)

          jsonObject_response_id.addProperty("comment", "" + goodResponses.good_comment)
            jsonObject_response_id.addProperty("ux_testing_question_id", ux_testing_question_id)
            json_data.add("" + goodResponses.gr_response_id, jsonObject_response_id)

        }
        jsonObject.add("data", json_data)
        Log.e(TAG, "jsonObject good question $jsonObject")

        return JsonParser().parse(jsonObject.toString()) as JsonObject
    }

    private fun getBadQuestionData(): JsonObject {
        val badResponsesArrayList =
            availableTestModel.badResponsesArrayList
        val ux_testing_question_id = "" + availableTestModel.bad_response_question_id
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "test_result_id",
            "" + SharedPrefHelper(this@UxCrowdVotingActivity).getTestResultId()
        )
        if (SharedPrefHelper(this@UxCrowdVotingActivity).getGuestTester()) {
            jsonObject.addProperty(
                "email",
                SharedPrefHelper(this@UxCrowdVotingActivity).getEmailId()
            )
        } else {
            jsonObject.addProperty(
                "email",
                SharedPrefHelper(this@UxCrowdVotingActivity).getUsername()
            )
        }
        val json_data = JsonObject()
        for (badResponses in badResponsesArrayList!!) {
            val jsonObject_response_id = JsonObject()
            jsonObject_response_id.addProperty("score", badResponses.bad_vote_value)
            if (badResponses.bad_comment != null) {
                if (badResponses.bad_comment!!.length > 0) {
                    jsonObject_response_id.addProperty("comment", "" + badResponses.bad_comment)
                }
            }
            jsonObject_response_id.addProperty("ux_testing_question_id", ux_testing_question_id)
            json_data.add("" + badResponses.br_response_id, jsonObject_response_id)
        }
        jsonObject.add("data", json_data)
         return JsonParser().parse(jsonObject.toString()) as JsonObject
    }

    private fun getSuggestionQuestionData(): JsonObject {
        val suggestionResponsesArrayList =
            availableTestModel.suggestionResponsesArrayList
        val ux_testing_question_id =
            "" + availableTestModel.suggestion_response_question_id
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "test_result_id",
            "" + SharedPrefHelper(this@UxCrowdVotingActivity).getTestResultId()
        )
        if (SharedPrefHelper(this@UxCrowdVotingActivity).getGuestTester()) {
            jsonObject.addProperty(
                "email",
                SharedPrefHelper(this@UxCrowdVotingActivity).getEmailId()
            )
        } else {
            jsonObject.addProperty(
                "email",
                SharedPrefHelper(this@UxCrowdVotingActivity).getUsername()
            )
        }
        jsonObject.addProperty("is_finished", true)
        val json_data = JsonObject()
        for (suggestionResponses in suggestionResponsesArrayList!!) {
            val jsonObject_response_id = JsonObject()
            jsonObject_response_id.addProperty("score", suggestionResponses.suggestion_vote_value)

          jsonObject_response_id.addProperty(
                "comment",
                "" + suggestionResponses.suggestion_comment
            )
            jsonObject_response_id.addProperty("ux_testing_question_id", ux_testing_question_id)
            json_data.add("" + suggestionResponses.sr_response_id, jsonObject_response_id)
        }
        jsonObject.add("data", json_data)
        Log.e(TAG, "jsonObject suggestion question $jsonObject")
        return return JsonParser().parse(jsonObject.toString()) as JsonObject
    }


    private fun showUxQuestion() {
        var question_tpe: String? = null
        Log.e(TAG, "back_press_count $back_press_count")
        Log.e(TAG, "no_of_questions_attempted $no_of_questions_attempted")
        nestedScrollView.fullScroll(ScrollView.FOCUS_UP)
        appbarlayout.setExpanded(true, true)
        question_tpe = key_for_response[back_press_count]
        when (question_tpe) {
            "good_responses" -> showGoodQuestion()
            "bad_responses" -> {
                isGoodQuestionEditable = false
                showBadQuestion()
            }
            "suggestion_responses" -> {
                isGoodQuestionEditable = false
                isBadQuestionEditable = false
                showSuggestionQuestion()
            }
        }
    }

    private fun showWentWrongDialog(){
        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))
    }

    private fun showErrorDialog(commonModel: CommonModel){
        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(commonModel.message)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        showBackWarning()
    }

    private fun showBackWarning() {
        val builder =
            AlertDialog.Builder(this, R.style.AppTheme_MaterialDialogTheme)
        builder.setMessage("Are you sure you want leave this test?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                dialog.dismiss()
                moveToHome()
            }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.dismiss() }
        back_alert = builder.create()
        back_alert?.show()
    }
}
