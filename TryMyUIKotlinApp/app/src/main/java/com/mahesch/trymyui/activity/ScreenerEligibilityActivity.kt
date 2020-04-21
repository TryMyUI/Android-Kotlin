package com.mahesch.trymyui.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CheckboxButtonOptionModel
import com.mahesch.trymyui.model.RadioButtonOptionModel
import com.mahesch.trymyui.repository.CheckEligibilityPresenter
import com.mahesch.trymyui.viewmodelfactory.GetScreenerQuestionViewModelFactory
import com.mahesch.trymyui.viewmodels.GetSCreenerQuestionViewModel
import com.seattleapplab.trymyui.models.ScreenerQuestionModel
import kotlinx.android.synthetic.main.screener_eligibility_acitivy.*
import java.util.*

class ScreenerEligibilityActivity : AppCompatActivity(),CheckEligibilityPresenter.ICheckScreeningEligibility {

    private val TAG: String = ScreenerEligibilityActivity::class.java.getSimpleName().toUpperCase()

    private lateinit var arrayList_radioButtons: ArrayList<RadioButton>
    private lateinit var arrayList_checkbox: ArrayList<CheckBox>

    private lateinit var radioButtonOptionModelArrayList: ArrayList<RadioButtonOptionModel>
    private lateinit var checkboxButtonOptionModelArrayList: ArrayList<CheckboxButtonOptionModel>

    private lateinit var availableTestModel: AvailableTestModel

    private var number_of_question_answered = 0
    private var back_press_count = 0

    private var screenerQuestionModelArrayList = ArrayList<ScreenerQuestionModel>()
    private lateinit var screenerQuestionModel: ScreenerQuestionModel

    private var total_number_of_question_count = 0

    private lateinit var getSCreenerQuestionViewModel: GetSCreenerQuestionViewModel

    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screener_eligibility_acitivy)

        if(intent != null){
            availableTestModel = intent.extras.getSerializable("availableTestConstants") as AvailableTestModel

            if(availableTestModel == null){
                moveToHome()
            }
        }
        else
        {
            moveToHome()
        }

        tv_desc.text = availableTestModel.scenario
        tv_desc.visibility = View.GONE

        val factory = GetScreenerQuestionViewModelFactory(application)

        getSCreenerQuestionViewModel =  ViewModelProvider(this,factory).get(GetSCreenerQuestionViewModel::class.java)

        sharedPrefHelper = SharedPrefHelper(this)

        if(Utils.isInternetAvailable(this))
        getFirstQuestion()
        else Utils.showInternetCheckToast(this)

        btn_question_continue.setOnClickListener { onClickQuestionContinue() }
    }

    private fun getFirstQuestion(){
        getSCreenerQuestionViewModel.getScreenerQuestion(sharedPrefHelper)?.observe(this,object : Observer<ScreenerQuestionModel>{
            override fun onChanged(screenerQuestionModel: ScreenerQuestionModel) {

                ProgressDialog.dismissProgressDialog()

                    if(screenerQuestionModel == null){
                    showWentWrongDialog()
                    }
                else{
                        if(screenerQuestionModel.error == null){
                            if(screenerQuestionModel.status_code?.toInt() == 200){

                                total_number_of_question_count = screenerQuestionModel.data!!.useTest!!.screener_questions_count

                                handleView(screenerQuestionModel)

                            }else{
                                showErrorDialog(screenerQuestionModel)
                            }
                        }
                        else{
                                showWentWrongDialog()
                        }
                    }
            }

        })
    }

    private fun showErrorDialog(screenerQuestionModel: ScreenerQuestionModel){
        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(screenerQuestionModel.message)
    }



    private fun onClickQuestionContinue() {
        if (back_press_count > 0)
        {
            back_press_count = back_press_count - 1

            Log.e(TAG, "show already answered")

            showPreviouslyAnsweredQuestion(screenerQuestionModelArrayList.size - 1 - back_press_count)

        }
        else if (back_press_count == 0)
        {
            Log.e(TAG, "server call")
            var isAnyOptionSelected = false

            if (screenerQuestionModel.data != null)
            {
                if (screenerQuestionModel.data!!.useTest != null)
                {
                    val use_test_id = screenerQuestionModel.data!!.useTest!!.id
                    val sq_id = screenerQuestionModel.data!!.useTest!!.screenerQuestion!!.id
                    var screener_question_id = ""

                    if (screenerQuestionModel.data!!.useTest!!.screenerQuestion!!.ans_type.equals("single_select", ignoreCase = true))
                    {
                        for (j in radioButtonOptionModelArrayList.indices)
                        {
                            val radioButtonOptionModel =
                                radioButtonOptionModelArrayList[j]

                            if (radioButtonOptionModel.isSelected)
                            {
                                isAnyOptionSelected = true
                                screener_question_id = "" + radioButtonOptionModel.id
                            }
                        }
                    }
                    else
                    {
                        for (j in checkboxButtonOptionModelArrayList.indices)
                        {
                            val checkboxButtonOptionModel =
                                checkboxButtonOptionModelArrayList[j]
                            if (checkboxButtonOptionModel.isSelected)
                            {
                                isAnyOptionSelected = true
                                if (j == 0) {
                                    screener_question_id = "" + checkboxButtonOptionModel.id
                                } else {
                                    screener_question_id += "," + checkboxButtonOptionModel.id
                                }
                            }
                        }
                    }
                    if (isAnyOptionSelected)
                    {
                        if(Utils.isInternetAvailable(this)){
                            ProgressDialog.initializeProgressDialogue(this)
                            ProgressDialog.showProgressDialog()
                            val checkEligibilityPresenter = CheckEligibilityPresenter(this, this)
                            checkEligibilityPresenter.checkScreeningEligibility(use_test_id, sq_id, screener_question_id)
                        }
                        else{
                            Utils.showInternetCheckToast(this)
                        }

                    }
                    else
                    {
                        Utils.showToast(this,"Please select atleast one option above")
                    }
                }
            }
        }
    }

    private fun showPreviouslyAnsweredQuestion(index_of_question_to_show: Int) {

        try
        {
            val screenerQuestionModel = screenerQuestionModelArrayList[index_of_question_to_show]

            btn_question_continue.text = screenerQuestionModel.data!!.button_title
            tv_question_title.text = screenerQuestionModel.data!!.useTest!!.screenerQuestion!!.title

            tv_no_of_q_attempted.text = "(" + (index_of_question_to_show + 1) + " of " + total_number_of_question_count + ")"

            ll_for_dynamic_rows.removeAllViews()

            val data = screenerQuestionModel.data
            val useTest = data!!.useTest
            val screenerQuestion = useTest!!.screenerQuestion
            val isAnswered = screenerQuestion!!.isAnswered
            val ans_type = screenerQuestion.ans_type

            val colorStateList = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_enabled)),
                intArrayOf(resources.getColor(R.color.d3d3d3),
                    resources.getColor(R.color.d3d3d3)))

            val colorStateList_selected = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_enabled)),
                intArrayOf(resources.getColor(R.color.d3d3d3), resources.getColor(R.color.d3d3d3)))

            if (ans_type.equals("single_select", ignoreCase = true))
            {
                //show radio options
                for (row in 0..0)
                {
                    val ll = RadioGroup(this)

                    ll.orientation = LinearLayout.VERTICAL

                    for (i in screenerQuestion.arrayList_screeningoptions!!.indices)
                    {
                        val screeningOptions = screenerQuestion.arrayList_screeningoptions!![i]

                        val rdbtn = RadioButton(this)
                        rdbtn.id = screeningOptions.id
                        rdbtn.text = screeningOptions.name
                        rdbtn.isChecked = screeningOptions.isSelected

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(10, 20, 10, 20)

                        rdbtn.layoutParams = params

                        if (isAnswered) {
                            rdbtn.buttonTintList = colorStateList_selected
                            rdbtn.isClickable = false
                        }
                        else {
                            rdbtn.isClickable = true
                            rdbtn.buttonTintList = colorStateList
                        }

                        ll.addView(rdbtn)
                    }

                    ll.setOnCheckedChangeListener { group, checkedId ->

                        Log.e(TAG, "check id $checkedId")

                        for (i in radioButtonOptionModelArrayList.indices)
                        {
                            val radioButtonOptionModel = radioButtonOptionModelArrayList[i]

                            val screeningOptions = screenerQuestion.arrayList_screeningoptions!![i]

                            if (radioButtonOptionModel.id === checkedId) {
                                radioButtonOptionModel.isSelected = true
                                screeningOptions.isSelected = true
                            } else {
                                radioButtonOptionModel.isSelected = false
                                screeningOptions.isSelected = false
                            }
                        }
                    }
                    ll_for_dynamic_rows.addView(ll)
                }
            }
            else
            {
                //show multiselect options
                //  addMultiSelectButton(screenerQuestion.getArrayList_screeningoptions());
                ll_for_dynamic_rows.removeAllViews()

                val colorStateList_multiselect = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_enabled)),
                    intArrayOf(
                        resources.getColor(R.color.orange),
                        resources.getColor(R.color.green)))

                val colorStateList_multiselect_selected = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_enabled)),
                    intArrayOf(
                        resources.getColor(R.color.d3d3d3),
                        resources.getColor(R.color.d3d3d3)))

                for (i in screenerQuestion.arrayList_screeningoptions!!.indices)
                {
                    val screeningOptions = screenerQuestion.arrayList_screeningoptions!![i]

                    val checkBox = CheckBox(this)
                    checkBox.id = screeningOptions.id
                    checkBox.text = screeningOptions.name
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)

                    params.setMargins(10, 20, 10, 20)
                    checkBox.layoutParams = params
                    checkBox.isChecked = screeningOptions.isSelected

                    if (isAnswered) {
                        checkBox.buttonTintList = colorStateList_multiselect_selected
                        checkBox.isClickable = false
                    } else {
                        checkBox.buttonTintList = colorStateList_multiselect
                        checkBox.isClickable = true
                    }

                    ll_for_dynamic_rows.addView(checkBox)

                    checkBox.setOnCheckedChangeListener { buttonView, isChecked ->

                        Log.e(TAG, "buttonview id " + buttonView.id)
                        Log.e(TAG, "checkbox isChecked $isChecked")

                        for (i in checkboxButtonOptionModelArrayList.indices)
                        {
                            val checkboxButtonOptionModel = checkboxButtonOptionModelArrayList[i]

                            if (buttonView.id == checkboxButtonOptionModel.id)
                            {
                                if (isChecked)
                                {
                                    checkboxButtonOptionModel.isSelected = true
                                    screeningOptions.isSelected = true
                                }
                                else
                                {
                                    checkboxButtonOptionModel.isSelected = false
                                    screeningOptions.isSelected = false
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun moveToHome(){
        var intent = Intent(this,TabActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun handleView(screenerQuestionModel: ScreenerQuestionModel) {
        try {
            btn_question_continue.text = screenerQuestionModel.data!!.button_title

            this.screenerQuestionModel = screenerQuestionModel

            val data = screenerQuestionModel.data

            if (data != null) {
                screenerQuestionModelArrayList.add(screenerQuestionModel)
                Log.e(TAG, "screenerQuestionModelArrayList size " + screenerQuestionModelArrayList.size)
            }

            val useTest = data!!.useTest
            val screenerQuestion = useTest!!.screenerQuestion

            tv_no_of_q_attempted.text = "(" + screenerQuestionModelArrayList.size + " of " + total_number_of_question_count + ")"

            val ans_type = screenerQuestion!!.ans_type

            tv_question_title.text = screenerQuestion.title

            textViewTitle.text = data.screener_test_page_heading

            Log.e(TAG, "ans_type $ans_type")

            if (ans_type.equals("single_select", ignoreCase = true)) {
                //show radio options
                addRadioButton(screenerQuestion.arrayList_screeningoptions)
            }
            else
            {
                //show multiselect options
                addMultiSelectButton(screenerQuestion.arrayList_screeningoptions)
            }
        }
        catch (e: Exception)
        {
            showWentWrongDialog()
        }
    }

    private fun addRadioButton(screeningOptionsArrayList : ArrayList<ScreenerQuestionModel.ScreeningOptions>?){
        if (screeningOptionsArrayList!!.size > 0) {

            arrayList_radioButtons = ArrayList()

            radioButtonOptionModelArrayList = ArrayList()

            val colorStateList = ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_enabled)),
                intArrayOf(resources.getColor(R.color.orange), resources.getColor(R.color.green)))

            ll_for_dynamic_rows.removeAllViews()

            for (row in 0..0)
            {
                val ll = RadioGroup(this)
                ll.orientation = LinearLayout.VERTICAL

                for (i in screeningOptionsArrayList!!.indices)
                {
                    val screeningOptions = screeningOptionsArrayList!![i]
                    val rdbtn = RadioButton(this)
                    rdbtn.id = screeningOptions.id
                    rdbtn.text = screeningOptions.name

                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                    params.setMargins(10, 20, 10, 20)
                    rdbtn.layoutParams = params
                    rdbtn.buttonTintList = colorStateList
                    ll.addView(rdbtn)

                    arrayList_radioButtons.add(rdbtn)

                    radioButtonOptionModelArrayList.add(RadioButtonOptionModel(rdbtn, screeningOptions.name!!, screeningOptions.id, false))
                }

                ll.setOnCheckedChangeListener { group, checkedId ->

                    Log.e(TAG, "check id $checkedId")

                    for (i in radioButtonOptionModelArrayList.indices)
                    {
                        val radioButtonOptionModel =
                            radioButtonOptionModelArrayList[i]
                        val screeningOptions =
                            screeningOptionsArrayList!![i]
                        if (radioButtonOptionModel.id === checkedId)
                        {
                            radioButtonOptionModel.isSelected = true
                            screeningOptions.isSelected = true
                        }
                        else
                        {
                            radioButtonOptionModel.isSelected = false
                            screeningOptions.isSelected = false
                        }
                    }
                }
                ll_for_dynamic_rows.addView(ll)
            }
        }
    }

    private fun addMultiSelectButton(screeningOptionsArrayList: ArrayList<ScreenerQuestionModel.ScreeningOptions>?){
        if (screeningOptionsArrayList!!.size > 0)
        {
            arrayList_checkbox = ArrayList()
            checkboxButtonOptionModelArrayList = ArrayList()
            ll_for_dynamic_rows.removeAllViews()

            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_enabled)),
                intArrayOf(
                    resources.getColor(R.color.orange),
                    resources.getColor(R.color.green)))

            for (i in screeningOptionsArrayList!!.indices)
            {
                val screeningOptions = screeningOptionsArrayList!![i]

                val checkBox = CheckBox(this)
                checkBox.id = screeningOptions.id
                checkBox.text = screeningOptions.name

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                params.setMargins(10, 20, 10, 20)
                checkBox.layoutParams = params
                checkBox.buttonTintList = colorStateList

                ll_for_dynamic_rows.addView(checkBox)

                arrayList_checkbox.add(checkBox)

                checkboxButtonOptionModelArrayList.add(CheckboxButtonOptionModel(
                        checkBox,
                        checkBox.text.toString(),
                        checkBox.id,
                        false))

                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    Log.e(TAG, "buttonview id " + buttonView.id)
                    Log.e(TAG, "checkbox isChecked $isChecked")

                    for (i in checkboxButtonOptionModelArrayList.indices)
                    {
                        val checkboxButtonOptionModel =
                            checkboxButtonOptionModelArrayList[i]

                        if (buttonView.id == checkboxButtonOptionModel.id)
                        {
                            if (isChecked)
                            {
                                checkboxButtonOptionModel.isSelected = true
                                screeningOptions.isSelected = true
                            }
                            else
                            {
                                checkboxButtonOptionModel.isSelected = false
                                screeningOptions.isSelected = false
                            }
                        }
                    }
                }
            }
        }

    }

    private fun showWentWrongDialog(){
        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
        OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))
    }

    override fun onBackPressed() {

        //  super.onBackPressed();
        if (back_press_count == screenerQuestionModelArrayList.size - 1) {
            showBackWarning()

        } else {
            //show Previous question
            back_press_count = back_press_count + 1
            Log.e(TAG, "show previous question")
            showPreviouslyAnsweredQuestion(screenerQuestionModelArrayList.size - 1 - back_press_count)
        }
    }

    private fun showBackWarning(){


        var btn_array = YesNoAlertDialog.initYesNoDialogue(this)

        var btn_yes = btn_array!![0]
        var btn_no = btn_array!![1]

        YesNoAlertDialog.showYesNoDialogue("",resources.getString(R.string.screener_back_warning),"Yes","No")

        btn_no.setOnClickListener {YesNoAlertDialog.dismissYesNoDialogue() }

        btn_yes.setOnClickListener { YesNoAlertDialog.dismissYesNoDialogue() }

    }

    override fun onStop() {
        super.onStop()

        dismissAllDialogIfOpen()
    }

    override fun onDestroy() {
        super.onDestroy()

        dismissAllDialogIfOpen()

    }

    override fun onSuccessCheckScreeningEligibility(screenerQuestionModel: ScreenerQuestionModel) {

        ProgressDialog.dismissProgressDialog()

        if (screenerQuestionModel != null) {

            if (screenerQuestionModel.status_code.equals("200", ignoreCase = true))
            {
                if (screenerQuestionModel.data!!.useTest != null)
                {
                    if (screenerQuestionModelArrayList.size > 0)
                    {
                        val screenerQuestionModel_answered =
                            screenerQuestionModelArrayList[screenerQuestionModelArrayList.size - 1]

                        screenerQuestionModel_answered.data!!.useTest!!.screenerQuestion!!.isAnswered =
                            true

                        number_of_question_answered = number_of_question_answered + 1

                        tv_no_of_q_attempted.text =
                            "(" + screenerQuestionModelArrayList.size + " of " + total_number_of_question_count + ")"
                    }
                    handleView(screenerQuestionModel)
                }
                else
                {
                    Log.e(TAG, "use test is null check for message")

                    if (screenerQuestionModel.data!!.isScreener_test_completed) {
                        val intent = Intent(this, PerformTestActivity::class.java)

                        intent.putExtra("availableTestConstants", availableTestModel)
                        intent.putExtra("backfromScreener", "yes")
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        showErrorDialog(screenerQuestionModel)
                    }
                }
            }
            else
            {
                showErrorDialog(screenerQuestionModel)
            }

        }
        else
        {
            showWentWrongDialog()
        }
    }

    override fun onErrorCheckScreeningEligibility() {
        ProgressDialog.dismissProgressDialog()
showWentWrongDialog()
    }

    private fun dismissAllDialogIfOpen(){

        ProgressDialog.dismissProgressDialog()


        OkAlertDialog.dismissOkAlert()


        YesNoAlertDialog.dismissYesNoDialogue()
    }
}
