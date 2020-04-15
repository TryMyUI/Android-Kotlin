package com.mahesch.trymyui.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CheckboxButtonOptionModel
import com.mahesch.trymyui.model.RadioButtonOptionModel
import com.seattleapplab.trymyui.models.ScreenerQuestionModel
import kotlinx.android.synthetic.main.screener_eligibility_acitivy.*
import java.util.*

class ScreenerEligibilityActivity : AppCompatActivity() {

    private val TAG: String = ScreenerEligibilityActivity::class.java.getSimpleName().toUpperCase()

    private lateinit var arrayList_radioButtons: ArrayList<RadioButton>
    private lateinit var arrayList_checkbox: ArrayList<CheckBox>

    private lateinit var radioButtonOptionModelArrayList: ArrayList<RadioButtonOptionModel>
    private lateinit var checkboxButtonOptionModelArrayList: ArrayList<CheckboxButtonOptionModel>

    private val alertDialogBuilder: AlertDialog.Builder? = null
    private val dialog: AlertDialog? = null

    private lateinit var availableTestModel: AvailableTestModel

    private var number_of_question_answered = 0
    private var back_press_count = 0

    private var screenerQuestionModelArrayList = ArrayList<ScreenerQuestionModel>()
    private lateinit var screenerQuestionModel: ScreenerQuestionModel

    private val progressDialog: ProgressDialog? = null

    private var total_number_of_question_count = 0

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

        btn_question_continue.setOnClickListener { onClickQuestionContinue() }
    }

    private fun onClickQuestionContinue(){

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
            showErrorDialog("" + getString(R.string.error), "" + getString(R.string.went_wrong))
        }
    }

    private fun addRadioButton(screeningOptionsArrayList : ArrayList<ScreenerQuestionModel.ScreeningOptions>?){

    }

    private fun addMultiSelectButton(screeningOptionsArrayList: ArrayList<ScreenerQuestionModel.ScreeningOptions>?){

    }
}
