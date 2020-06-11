package com.mahesch.trymyui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.ApplicationClass
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CheckboxButtonOptionModel
import com.mahesch.trymyui.model.RadioButtonOptionModel
import com.mahesch.trymyui.repository.CheckEligibilityPresenter
import com.mahesch.trymyui.repository.ScreenerQuestionPresenter
import com.seattleapplab.trymyui.models.ScreenerQuestionModel
import com.seattleapplab.trymyui.models.ScreenerQuestionModel.*
import kotlinx.android.synthetic.main.screener_eligibility_acitivy.*
import java.util.*

class ScreenerEligibilityActivity : AppCompatActivity(),
    CheckEligibilityPresenter.ICheckScreeningEligibility,
ScreenerQuestionPresenter.IScreenerQuestionList{

    var scrollYAxis = 180

    private var arrayList_radioButtons: ArrayList<RadioButton>? = null
    private var arrayList_checkbox: ArrayList<CheckBox>? = null

    private var radioButtonOptionModelArrayList: ArrayList<RadioButtonOptionModel>? = null
    private var checkboxButtonOptionModelArrayList: ArrayList<CheckboxButtonOptionModel>? =
        null

    private var alertDialogBuilder: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null

    private var availableTestConstant: AvailableTestModel? = null

    private var number_of_question_answered = 0

    private val screenerQuestionModelArrayList =
        ArrayList<ScreenerQuestionModel>()

    private var progressDialog: ProgressDialog? = null

    private var total_number_of_question_count = 0

    private val TAG =
        ScreenerEligibilityActivity::class.java.simpleName.toUpperCase()

    private var screenerQuestionModel_parent: ScreenerQuestionModel? = null

    var wrong_answer_dialog: Dialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screener_eligibility_acitivy)

        availableTestConstant = intent.getSerializableExtra("availableTestConstants") as AvailableTestModel

        if (availableTestConstant == null)
        {
            Toast.makeText(this@ScreenerEligibilityActivity,
                resources.getString(R.string.went_wrong), Toast.LENGTH_LONG).show()
            moveToDashBoardAndFinishThis()
        }
        progressDialog = ProgressDialog(this@ScreenerEligibilityActivity)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.show()
        val screenerQuestionPresenter =
            ScreenerQuestionPresenter(this@ScreenerEligibilityActivity, this)
        screenerQuestionPresenter.getScreenerQuestion()
        btn_continue.setOnClickListener {
            val intent =
                Intent(this@ScreenerEligibilityActivity, PerformTestActivity::class.java)
            intent.putExtra("availableTestConstants", availableTestConstant)
            intent.putExtra("backfromScreener", "yes")
            startActivity(intent)
            finish()
        }
        ll_three_dots!!.setOnClickListener { v -> showMenuPopUp(v) }
    }


    private fun addQuestionsRows(
        screenerQuestionModel: ScreenerQuestionModel,
        isWrong: Boolean
    ) {
        ll_questionlist.removeAllViews()
        val data =
            screenerQuestionModel.data
        if (data != null) {
            screenerQuestionModelArrayList.add(screenerQuestionModel)
            Log.e(
                TAG,
                "screenerQuestionModelArrayList size " + screenerQuestionModelArrayList.size
            )
        }
        var useTest: UseTest? = null
        var screenerQuestion: ScreenerQuestion? = null
        var ans_type: String? = null
        if (!isWrong) {
            useTest = data!!.useTest
            screenerQuestion = useTest!!.screenerQuestion
            ans_type = screenerQuestion!!.ans_type
        }
        for (i in 0 until total_number_of_question_count) {
            val layoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View =
                layoutInflater.inflate(R.layout.screener_question_row, null)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(60, 35, 60, 35)
            view.layoutParams = params
            val ll_question_options =
                view.findViewById<LinearLayout>(R.id.ll_question_options)
            val tv_question = view.findViewById<TextView>(R.id.tv_question)
            val rl_collapsedquestion =
                view.findViewById<RelativeLayout>(R.id.rl_collapsedquestion)
            val iv_question_status =
                view.findViewById<ImageView>(R.id.iv_question_status)
            val tv_question_collapsed =
                view.findViewById<TextView>(R.id.tv_question_collapsed)
            if (i == number_of_question_answered) {
                ll_question_options.visibility = View.VISIBLE
                rl_collapsedquestion.visibility = View.GONE
            } else {
                ll_question_options.visibility = View.GONE
                rl_collapsedquestion.visibility = View.VISIBLE
                tv_question_collapsed.text = "Question " + (i + 1) + "."
            }
            val ll_dyanamic_screener_options =
                view.findViewById<LinearLayout>(R.id.ll_dyanamic_screener_options)
            val btn_confirm =
                view.findViewById<Button>(R.id.btn_confirm)
            btn_confirm.isEnabled = false
            if (screenerQuestion != null) {
                val questionText = screenerQuestion.title
                tv_question.text = Html.fromHtml("<b>Q. </b>$questionText")
                if (ans_type.equals("single_select", ignoreCase = true)) {
                    addRadioButton(
                        screenerQuestion.arrayList_screeningoptions,
                        ll_dyanamic_screener_options,
                        btn_confirm
                    )
                } else {
                    addMultiSelect(
                        screenerQuestion.arrayList_screeningoptions,
                        ll_dyanamic_screener_options,
                        btn_confirm
                    )
                }
            }
            btn_confirm.setOnClickListener {
                Log.e(TAG, "Button clicked")
                onConfirmClicked()
            }
            if (i > number_of_question_answered) {
                iv_question_status.setImageDrawable(resources.getDrawable(R.drawable.ic_vector_lock))
            } else {
                iv_question_status.setImageDrawable(resources.getDrawable(R.drawable.ic_accepted))
            }
            var isWrongFound = false
            if (screenerQuestion == null) {
                iv_question_status.setImageDrawable(resources.getDrawable(R.drawable.ic_rejected))
                isWrongFound = true
            }
            ll_questionlist.addView(view, i)

            /*  if(isWrongFound && i==number_of_question_answered)
                break;*/
        }
    }


    private fun addQuestionRowsForWrong(screenerQuestionModel: ScreenerQuestionModel) {
        ll_questionlist.removeAllViews()
        val data =
            screenerQuestionModel.data
        if (data != null) {
            screenerQuestionModelArrayList.add(screenerQuestionModel)
            Log.e(
                TAG,
                "screenerQuestionModelArrayList size " + screenerQuestionModelArrayList.size
            )
        }
        for (i in 0 until total_number_of_question_count) {
            val layoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View =
                layoutInflater.inflate(R.layout.screener_question_row, null)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 20, 20, 20)
            view.layoutParams = params
            val ll_question_options =
                view.findViewById<LinearLayout>(R.id.ll_question_options)
            val tv_question = view.findViewById<TextView>(R.id.tv_question)
            val rl_collapsedquestion =
                view.findViewById<RelativeLayout>(R.id.rl_collapsedquestion)
            val iv_question_status =
                view.findViewById<ImageView>(R.id.iv_question_status)
            val tv_question_collapsed =
                view.findViewById<TextView>(R.id.tv_question_collapsed)
            ll_question_options.visibility = View.GONE
            rl_collapsedquestion.visibility = View.VISIBLE
            tv_question_collapsed.text = "Question " + (i + 1) + "."
            if (i == number_of_question_answered) {
                iv_question_status.setImageDrawable(resources.getDrawable(R.drawable.ic_rejected))
            } else if (i > number_of_question_answered) {
                iv_question_status.setImageDrawable(resources.getDrawable(R.drawable.ic_vector_lock))
            } else {
                iv_question_status.setImageDrawable(resources.getDrawable(R.drawable.ic_accepted))
            }
            ll_questionlist.addView(view, i)
        }
    }

    private fun onConfirmClicked() {
        Log.e(TAG, "server call")
        var isAnyOptionSelected = false
        if (screenerQuestionModel_parent != null && screenerQuestionModel_parent!!.data != null) {
            if (screenerQuestionModel_parent!!.data!!.useTest != null) {
                val use_test_id = screenerQuestionModel_parent!!.data!!.useTest!!.id
                val sq_id = screenerQuestionModel_parent!!.data!!.useTest!!.screenerQuestion!!.id
                var screener_question_id = ""
                if (screenerQuestionModel_parent!!.data!!.useTest!!.screenerQuestion!!.ans_type.equals(
                        "single_select",
                        ignoreCase = true
                    )
                ) {
                    for (j in radioButtonOptionModelArrayList!!.indices) {
                        val radioButtonOptionModel =
                            radioButtonOptionModelArrayList!![j]
                        if (radioButtonOptionModel.isSelected) {
                            isAnyOptionSelected = true
                            screener_question_id = "" + radioButtonOptionModel.id
                        }
                    }
                } else {
                    for (j in checkboxButtonOptionModelArrayList!!.indices) {
                        val checkboxButtonOptionModel =
                            checkboxButtonOptionModelArrayList!![j]
                        if (checkboxButtonOptionModel.isSelected) {
                            isAnyOptionSelected = true
                            if (j == 0) {
                                screener_question_id = "" + checkboxButtonOptionModel.id
                            } else {
                                screener_question_id += "," + checkboxButtonOptionModel.id
                            }
                        }
                    }
                }
                if (isAnyOptionSelected) {
                    progressDialog!!.show()
                    val checkEligibilityPresenter =
                        CheckEligibilityPresenter(this@ScreenerEligibilityActivity, this)
                    checkEligibilityPresenter.checkScreeningEligibility(
                        use_test_id,
                        sq_id,
                        screener_question_id
                    )
                } else {
                    Toast.makeText(
                        this@ScreenerEligibilityActivity,
                        "Please select atleast one option above",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            if (progressDialog != null && progressDialog!!.isShowing) progressDialog!!.dismiss()
            showErrorDialog("" + getString(R.string.error), "" + getString(R.string.went_wrong))
        }
    }

    private fun addRadioButton(
        screeningOptionsArrayList: ArrayList<ScreeningOptions>?,
        ll_dyanamic_screener_options: LinearLayout,
        btn_confirm: Button
    ) {
        if (screeningOptionsArrayList!!.size > 0) {
            arrayList_radioButtons = ArrayList()
            radioButtonOptionModelArrayList = ArrayList()

            // final ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{-android.R.attr.state_checked},new int[]{android.R.attr.state_enabled}},new int[]{getResources().getColor(R.color.orange),getResources().getColor(R.color.green)});
            ll_dyanamic_screener_options.removeAllViews()
            for (row in 0..0) {
                val ll = RadioGroup(this)
                ll.orientation = LinearLayout.VERTICAL
                for (i in screeningOptionsArrayList.indices) {
                    val screeningOptions = screeningOptionsArrayList[i]
                    val rdbtn = RadioButton(this)
                    rdbtn.id = screeningOptions.id
                    rdbtn.text = screeningOptions.name
                    rdbtn.buttonDrawable = resources.getDrawable(R.drawable.radio_selector)
                    rdbtn.setPadding(25, 0, 0, 0)
                    rdbtn.setTextColor(resources.getColor(R.color.dark_slate_gray))
                    rdbtn.textSize = 16f
                    rdbtn.gravity = Gravity.CENTER_VERTICAL
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(10, 20, 10, 20)
                    rdbtn.layoutParams = params

                    // rdbtn.setButtonTintList(colorStateList);
                    ll.addView(rdbtn)
                    arrayList_radioButtons!!.add(rdbtn)
                    radioButtonOptionModelArrayList!!.add(
                        RadioButtonOptionModel(
                            rdbtn,
                            screeningOptions.name!!,
                            screeningOptions.id,
                            false
                        )
                    )
                }
                ll.setOnCheckedChangeListener { group, checkedId ->
                    Log.e(TAG, "check id $checkedId")
                    btn_confirm.isEnabled = true
                    btn_confirm.setBackgroundDrawable(resources.getDrawable(R.drawable.trymyuitester_btn_bg))
                    btn_confirm.setTextColor(resources.getColor(R.color.white))
                    for (i in radioButtonOptionModelArrayList!!.indices) {
                        val radioButtonOptionModel =
                            radioButtonOptionModelArrayList!![i]
                        val screeningOptions =
                            screeningOptionsArrayList[i]
                        if (radioButtonOptionModel.id === checkedId) {
                            radioButtonOptionModel.isSelected = true
                            screeningOptions.isSelected = true
                        } else {
                            radioButtonOptionModel.isSelected = false
                            screeningOptions.isSelected = false
                        }
                    }
                }
                ll_dyanamic_screener_options.addView(ll)
            }
        }
    }

    private fun addMultiSelect(
        screeningOptionsArrayList: ArrayList<ScreeningOptions>?,
        ll_dyanamic_screener_options: LinearLayout,
        btn_confirm: Button
    ) {
        if (screeningOptionsArrayList!!.size > 0) {
            arrayList_checkbox = ArrayList()
            checkboxButtonOptionModelArrayList = ArrayList()
            ll_dyanamic_screener_options.removeAllViews()
            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_enabled)
                ),
                intArrayOf(
                    resources.getColor(R.color.d3d3d3),
                    resources.getColor(R.color._4A90E2)
                )
            )
            for (i in screeningOptionsArrayList.indices) {
                val screeningOptions = screeningOptionsArrayList[i]
                val checkBox = CheckBox(this)
                checkBox.id = screeningOptions.id
                checkBox.text = screeningOptions.name
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 20, 10, 0)
                checkBox.layoutParams = params
                checkBox.setPadding(25, 0, 0, 0)
                checkBox.textSize = 16f
                checkBox.setTextColor(resources.getColor(R.color.dark_slate_gray))
                checkBox.buttonTintList = colorStateList
                ll_dyanamic_screener_options.addView(checkBox)
                arrayList_checkbox!!.add(checkBox)
                checkboxButtonOptionModelArrayList!!.add(
                    CheckboxButtonOptionModel(
                        checkBox,
                        checkBox.text.toString(),
                        checkBox.id,
                        false
                    )
                )
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    Log.e(TAG, "buttonview id " + buttonView.id)
                    Log.e(TAG, "checkbox isChecked $isChecked")
                    btn_confirm.isEnabled = true
                    btn_confirm.setBackgroundDrawable(resources.getDrawable(R.drawable.trymyuitester_btn_bg))
                    btn_confirm.setTextColor(resources.getColor(R.color.white))
                    for (i in checkboxButtonOptionModelArrayList!!.indices) {
                        val checkboxButtonOptionModel =
                            checkboxButtonOptionModelArrayList!![i]
                        if (buttonView.id == checkboxButtonOptionModel.id) {
                            if (isChecked) {
                                checkboxButtonOptionModel.isSelected = true
                                screeningOptions.isSelected = true
                            } else {
                                checkboxButtonOptionModel.isSelected = false
                                screeningOptions.isSelected = false
                            }
                        }
                    }
                }
            }
        }
    }



    override fun onSuccessGetScreenerQuestion(screenerQuestionModel: ScreenerQuestionModel?) {
        progressDialog!!.dismiss()
        screenerQuestionModel_parent = screenerQuestionModel
        if (screenerQuestionModel != null) {
            if (screenerQuestionModel.status_code.equals("200", ignoreCase = true)) {
                total_number_of_question_count =
                    screenerQuestionModel.data!!.useTest!!.screener_questions_count
                Log.e(
                    TAG,
                    "total_number_of_question_count $total_number_of_question_count"
                )

                //  handleView(screenerQuestionModel);
                addQuestionsRows(screenerQuestionModel, false)
            } else {
                showErrorDialog(
                    "" + getString(R.string.information),
                    "" + screenerQuestionModel.message
                )
            }
        } else {
            showErrorDialog("" + getString(R.string.error), "" + getString(R.string.went_wrong))
        }
    }

    override fun onErrorGetScreenerQuestion() {
        Log.e(TAG, "onErrorGetScreenerQuestion")
        showErrorDialog("Alert", resources.getString(R.string.went_wrong))
    }

    fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isFinishing) {
            if (!isConnected) {
                Utils.isInternetAvailable(this@ScreenerEligibilityActivity)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
        if (progressDialog!!.isShowing) progressDialog!!.dismiss()
    }

    override fun onStop() {
        super.onStop()
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
        if (progressDialog!!.isShowing) progressDialog!!.dismiss()
        if (wrong_answer_dialog != null) {
            if (wrong_answer_dialog!!.isShowing) wrong_answer_dialog!!.dismiss()
        }
        if (popupWindow != null) {
            if (popupWindow!!.isShowing) {
                popupWindow!!.dismiss()
            }
        }
        if (show_confirmation_dialog != null) {
            if (show_confirmation_dialog!!.isShowing) {
                show_confirmation_dialog!!.dismiss()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
        if (progressDialog!!.isShowing) progressDialog!!.dismiss()
        if (wrong_answer_dialog != null) {
            if (wrong_answer_dialog!!.isShowing) wrong_answer_dialog!!.dismiss()
        }
        if (popupWindow != null) {
            if (popupWindow!!.isShowing) {
                popupWindow!!.dismiss()
            }
        }
        if (show_confirmation_dialog != null) {
            if (show_confirmation_dialog!!.isShowing) {
                show_confirmation_dialog!!.dismiss()
            }
        }
    }


    private fun moveToDashBoardAndFinishThis() {
        if (SharedPrefHelper(this).getGuestTester()) {
            val intent =
                Intent(this@ScreenerEligibilityActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@ScreenerEligibilityActivity, TabActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        alertDialogBuilder = AlertDialog.Builder(
            ContextThemeWrapper(
                this@ScreenerEligibilityActivity,
                R.style.AppTheme_MaterialDialogTheme
            )
        )
        alertDialogBuilder!!.setTitle(title)
        alertDialogBuilder!!.setCancelable(false)
        alertDialogBuilder!!.setMessage(message)
        alertDialogBuilder!!.setPositiveButton(
            "OK"
        ) { dialog, which ->
            dialog.dismiss()
            if (SharedPrefHelper(this@ScreenerEligibilityActivity).getGuestTester()) {
                SharedPrefHelper(this@ScreenerEligibilityActivity).clearSharedPreference()
                Toast.makeText(
                    this@ScreenerEligibilityActivity,
                    getString(R.string.logout_successful),
                    Toast.LENGTH_LONG
                ).show()
                val intent =
                    Intent(this@ScreenerEligibilityActivity, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                moveToDashBoardAndFinishThis()
            }
        }
        dialog = alertDialogBuilder!!.create()
        if (!(dialog?.isShowing)!!) {
            dialog?.show()
        }
    }

    override fun onBackPressed() {
        //  super.onBackPressed();
        moveToDashBoardAndFinishThis()
    }


    private fun showWrongAnswerDialog(wrongAnswerMessage: String?) {
        wrong_answer_dialog = Dialog(this)
        wrong_answer_dialog?.setContentView(R.layout.screener_wrong_answer_dialog)
        wrong_answer_dialog!!.show()
        wrong_answer_dialog!!.setCancelable(false)
        val tv_wrong_answer_message =
            wrong_answer_dialog!!.findViewById<View>(R.id.tv_wrong_answer_message) as TextView
        val tv_back_dashboard =
            wrong_answer_dialog!!.findViewById<View>(R.id.tv_back_dashboard) as TextView
        tv_wrong_answer_message.text = wrongAnswerMessage
        if (SharedPrefHelper(this).getGuestTester()) {
            tv_wrong_answer_message.text = resources.getString(R.string.guest_screener_fail)
            tv_back_dashboard.text = resources.getString(R.string.leavetest)
        }
        tv_back_dashboard.setOnClickListener {
            moveToDashBoardAndFinishThis()
            wrong_answer_dialog!!.dismiss()
        }
    }


    private var popupWindow: PopupWindow? = null
    private var show_confirmation_dialog: Dialog? = null


    private fun showMenuPopUp(view1: View) {
        val density = resources.displayMetrics.density
        popupWindow =
            PopupWindow(view1, density.toInt() * 240, WindowManager.LayoutParams.WRAP_CONTENT, true)
        val view: View
        val layoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(R.layout.pop_up_menu, null)
        popupWindow!!.isFocusable = true
        popupWindow!!.contentView = view
        popupWindow!!.showAtLocation(
            view1,
            Gravity.TOP or Gravity.RIGHT,
            0,
            rl_testdetails!!.height + 42
        )
        val tv_feedback =
            view.findViewById<View>(R.id.tv_menu_feedback) as TextView
        val tv_logout =
            view.findViewById<View>(R.id.tv_menu_logout) as TextView
        val tv_menu_leavetest =
            view.findViewById<View>(R.id.tv_menu_leavetest) as TextView
        val tv_menu_reportissue =
            view.findViewById<View>(R.id.tv_menu_reportissue) as TextView
        tv_feedback.visibility = View.GONE
        val vw_sendfeedback =
            view.findViewById(R.id.vw_sendfeedback) as View
        vw_sendfeedback.visibility = View.GONE
        tv_menu_reportissue.setOnClickListener {
            popupWindow!!.dismiss()
            val intentFeedback = Intent(
                this@ScreenerEligibilityActivity,
                FeedbackActivity::class.java
            )
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            finish()
        }
        tv_logout.setOnClickListener {
            popupWindow!!.dismiss()
            val intentFeedback =
                Intent(this@ScreenerEligibilityActivity, WelcomeActivity::class.java)
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            finish()
        }
        tv_menu_leavetest.setOnClickListener {
            popupWindow!!.dismiss()
            showConfirmation()
        }
    }

    fun showConfirmation() {
        val dialogBuilder =
            AlertDialog.Builder(this, R.style.AppTheme_MaterialDialogTheme)
        dialogBuilder.setTitle("Are you sure you want to leave this test?")
        dialogBuilder.setMessage("This will end your test and you will be redirected to your TryMyUI dashboard")
        dialogBuilder.setNegativeButton(
            R.string.cancel
        ) { dialog, which -> dialog.dismiss() }
        dialogBuilder.setPositiveButton(
            R.string.yes
        ) { dialog, which -> moveToDashBoardAndFinishThis() }
        show_confirmation_dialog = dialogBuilder.create()
        val dialogWindow = show_confirmation_dialog?.window
        val dialogWindowAttributes = dialogWindow?.attributes

// Set fixed width (280dp) and WRAP_CONTENT height
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogWindowAttributes)
        lp.width =
            WindowManager.LayoutParams.MATCH_PARENT //(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, getResources().getDisplayMetrics());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow?.attributes = lp
        show_confirmation_dialog?.show()
    }

    override fun onSuccessCheckScreeningEligibility(screenerQuestionModel: ScreenerQuestionModel) {
        Log.e(TAG, "onSuccessCheckScreeningEligibilty")
        val gson = Gson()
        val json = gson.toJson(screenerQuestionModel)
        Log.e(TAG, "onSuccessCheckScreeningEligibilty $json")
        progressDialog!!.dismiss()
        scrollYAxis = scrollYAxis + 80
        parent_scrollview.smoothScrollBy(0, scrollYAxis)
        Log.e(TAG, "scrollYAxis $scrollYAxis")
        if (screenerQuestionModel != null) {
            if (screenerQuestionModel.status_code.equals("200", ignoreCase = true)) {
                screenerQuestionModel_parent = screenerQuestionModel
                if (screenerQuestionModel.data!!.useTest != null) {
                    if (screenerQuestionModelArrayList.size > 0) {
                        val screenerQuestionModel_answered =
                            screenerQuestionModelArrayList[screenerQuestionModelArrayList.size - 1]
                        screenerQuestionModel_answered.data!!.useTest!!.screenerQuestion!!.isAnswered =
                            true
                        number_of_question_answered = number_of_question_answered + 1
                    }
                    addQuestionsRows(screenerQuestionModel, false)
                } else {
                    Log.e(TAG, "use test is null check for message")
                    if (screenerQuestionModel.data!!.isScreener_test_completed) {
                        ApplicationClass.isScreenerVisited = true

                        /* new ManageFlowBeforeRecording(availableTestConstant,ScreenerEligibilityActivity.this)
                                .moveToWhichActivity(3);*/btn_continue.isEnabled = true
                        btn_continue.setTextColor(resources.getColor(R.color.white))
                        btn_continue.background =
                            resources.getDrawable(R.drawable.trymyuitester_btn_bg)
                        if (screenerQuestionModelArrayList.size > 0) {
                            val screenerQuestionModel_answered =
                                screenerQuestionModelArrayList[screenerQuestionModelArrayList.size - 1]
                            screenerQuestionModel_answered.data!!.useTest!!.screenerQuestion!!.isAnswered =
                                true
                            number_of_question_answered = number_of_question_answered + 1
                        }
                        addQuestionRowsForWrong(screenerQuestionModel)

                        /* if(number_of_question_answered == total_number_of_question_count){
                            btn_continue.setEnabled(true);
                            btn_continue.setTextColor(getResources().getColor(R.color.white));
                            btn_continue.setBackground(getResources().getDrawable(R.drawable.trymyuitester_btn_bg));
                        }*/
                    } else {
                        if (screenerQuestionModelArrayList.size > 0) {
                            val screenerQuestionModel_answered =
                                screenerQuestionModelArrayList[screenerQuestionModelArrayList.size - 1]
                            // screenerQuestionModel_answered.getData().getUseTest().getScreenerQuestion().setAnswered(true);
                            number_of_question_answered = number_of_question_answered + 1
                        }
                        Log.e(TAG, "show screener message")
                        showErrorDialog(
                            "" + getString(R.string.information),
                            "" + screenerQuestionModel.message
                        )
                    }
                }
            } else {
                addQuestionRowsForWrong(screenerQuestionModel)

                //WRONG ANSWERED
                //showErrorDialog(""+getString(R.string.information),""+screenerQuestionModel.getMessage());
                showWrongAnswerDialog(screenerQuestionModel.message)
            }
        } else {
            showErrorDialog("" + getString(R.string.error), "" + getString(R.string.went_wrong))
        }
    }

    override fun onErrorCheckScreeningEligibility() {
        showErrorDialog("Alert", resources.getString(R.string.went_wrong))
    }


}
