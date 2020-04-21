package com.mahesch.trymyui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.viewmodelfactory.NpsViewModelFactory
import com.mahesch.trymyui.viewmodels.NpsActivityViewModel
import com.seattleapplab.trymyui.models.Tests
import com.seattleapplab.trymyui.models.Tests.NpsQuestion
import kotlinx.android.synthetic.main.nps_activity.*

class NpsActivity : AppCompatActivity() {

    private var TAG  = NpsActivity::class.simpleName?.toUpperCase()

    private var selectedValue = -1
    private var  isFinish = false

    private lateinit var npsQuestion : String
    private lateinit var availableTestModel: AvailableTestModel
    private lateinit var npsActivityViewModel: NpsActivityViewModel
    private var npsQuestionList = ArrayList<Tests.NpsQuestion>()
    private lateinit var npsQuestionModel : NpsQuestion
    private lateinit var manageFlowAfterTest :ManageFlowAfterTest

    var radio_button_count = -1

    private var back_alert:android.app.AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nps_activity)

       // var actionBar = actionBar

       // Utils.actionBarSetup(actionBar," NPS Survey")

        if(intent != null){
            npsQuestion = intent.extras.getString("npsQuestion","")
            availableTestModel = intent.extras.getSerializable("availableTestConstants") as AvailableTestModel

            if(npsQuestion == null || availableTestModel == null){
                moveToHome()
            }

            manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this)
        }
        else
        {
            moveToHome()
        }

        if(availableTestModel.ux_crowd_questions.equals("[]",true))
            isFinish = true


        val factory =
            NpsViewModelFactory(
                application
            )

        npsActivityViewModel = ViewModelProvider(this,factory).get(NpsActivityViewModel::class.java)

        npsQuestionList = Gson().fromJson<java.util.ArrayList<NpsQuestion>>(
            npsQuestion,
            object : TypeToken<java.util.ArrayList<NpsQuestion?>?>() {}.type
        )

        npsQuestionModel = npsQuestionList[0]

        button_submit.setOnClickListener { onClickOfNpsSubmit() }

        addDynamicRadioButton()

    }

    private fun addDynamicRadioButton(){

        var minValue = npsQuestionModel.min_scale
        var maxValue = npsQuestionModel.max_scale

        if(maxValue!! <= 5){
            addRadioButton_5()
        }
        else if(maxValue == 6){
           addRadioButtons_stage6()
        }
        else if(maxValue == 7){
            addRadioButtons_stage7()
        }
        else if(maxValue == 8){
            addRadioButtons_stage8()
        }
        else if(maxValue == 9){
            addRadioButtons_stage9()
        }
        else if(maxValue == 10){
            addRadioButton_10()
        }
        else{
            addRadioButton()
        }

    }

    private fun addRadioButton_5(){
        radio_button_count = 0

        val radioGroup1 = RadioGroup(this)
        radioGroup1.orientation = LinearLayout.HORIZONTAL
        radioGroup1.gravity = Gravity.CENTER_HORIZONTAL

        val screensize =Utils.getScreenSizeType(this)
        val top_margin =30
        val right_margin =20;


        for (i in 0..5) {
            val radioButton = RadioButton(this)
            radioButton.text = "" + i
            radioButton.gravity = Gravity.CENTER
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioGroup1.addView(radioButton)
            if (screensize == 0) {
                Log.e(TAG, "General screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 1) //LARGE
            {
                Log.e(TAG, "large screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else if (screensize == 2) //NORMAL
            {
                Log.e(TAG, "normal screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Medium
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 3) //SMALL
            {
                Log.e(TAG, "small screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Small
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 4) //XLARGE
            {
                Log.e(TAG, "xlarge screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else {
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            }
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)

                // radioGroup2.clearCheck();
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    selectedValue = buttonView.text.toString().toInt().toInt()
                    Log.e(TAG, "selectedValue $selectedValue")
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
        }

        ll_nps_view.addView(radioGroup1)


    }



    private fun addRadioButtons_stage6() {
        radio_button_count = 0
        val radioGroup1 = RadioGroup(this)
        radioGroup1.orientation = LinearLayout.HORIZONTAL
        radioGroup1.gravity = Gravity.CENTER_HORIZONTAL
        val radioGroup2 = RadioGroup(this)
        radioGroup2.orientation = LinearLayout.HORIZONTAL
        radioGroup2.gravity = Gravity.CENTER_HORIZONTAL
        val screensize = Utils.getScreenSizeType(this)
        val top_margin = 30
        val right_margin = 20
        for (i in 0..3) {
            val radioButton = RadioButton(this)
            radioButton.text = "" + i
            radioButton.gravity = Gravity.CENTER
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioGroup1.addView(radioButton)
            if (screensize == 0) {
                Log.e(TAG, "General screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 1) //LARGE
            {
                Log.e(TAG, "large screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else if (screensize == 2) //NORMAL
            {
                Log.e(TAG, "normal screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Medium
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 3) //SMALL
            {
                Log.e(TAG, "small screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Small
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 4) //XLARGE
            {
                Log.e(TAG, "xlarge screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else {
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            }
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                radioGroup2.clearCheck()
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    selectedValue = buttonView.text.toString().toInt()
                    Log.e(TAG, "selectedValue $selectedValue")
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
        }
        ll_nps_view.addView(radioGroup1)
        for (i in 4..6) {
            val radioButton = RadioButton(this)
            radioButton.text = "" + i
            radioButton.gravity = Gravity.CENTER
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioGroup2.addView(radioButton)
            if (screensize == 0) {
                Log.e(TAG, "General screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 1) //LARGE
            {
                Log.e(TAG, "large screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else if (screensize == 2) //NORMAL
            {
                Log.e(TAG, "normal screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Medium
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 3) //SMALL
            {
                Log.e(TAG, "small screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Small
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 4) //XLARGE
            {
                Log.e(TAG, "xlarge screen")
                val params = LinearLayout.LayoutParams(
                    80, 80
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else {
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            }
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                radioGroup1.clearCheck()
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    selectedValue = buttonView.text.toString().toInt()
                    Log.e(TAG, "selectedValue $selectedValue")
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
        }
        ll_nps_view.addView(radioGroup2)
    }

    private fun addRadioButtons_stage7() {
        radio_button_count = 0
        val radioGroup1 = RadioGroup(this)
        radioGroup1.orientation = LinearLayout.HORIZONTAL
        radioGroup1.gravity = Gravity.CENTER_HORIZONTAL
        val radioGroup2 = RadioGroup(this)
        radioGroup2.orientation = LinearLayout.HORIZONTAL
        radioGroup2.gravity = Gravity.CENTER_HORIZONTAL
        val screensize = Utils.getScreenSizeType(this)
        val top_margin = 30
        val right_margin = 20
        for (i in 0..3) {
            val radioButton = RadioButton(this)
            radioButton.text = "" + i
            radioButton.gravity = Gravity.CENTER
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioGroup1.addView(radioButton)
            if (screensize == 0) {
                Log.e(TAG, "General screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 1) //LARGE
            {
                Log.e(TAG, "large screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else if (screensize == 2) //NORMAL
            {
                Log.e(TAG, "normal screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Medium
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 3) //SMALL
            {
                Log.e(TAG, "small screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Small
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 4) //XLARGE
            {
                Log.e(TAG, "xlarge screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else {
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            }
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                radioGroup2.clearCheck()
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    selectedValue = buttonView.text.toString().toInt()
                    Log.e(TAG, "selectedValue $selectedValue")
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
        }
        ll_nps_view.addView(radioGroup1)
        for (i in 4..7) {
            val radioButton = RadioButton(this)
            radioButton.text = "" + i
            radioButton.gravity = Gravity.CENTER
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioGroup2.addView(radioButton)
            if (screensize == 0) {
                Log.e(TAG, "General screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 1) //LARGE
            {
                Log.e(TAG, "large screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else if (screensize == 2) //NORMAL
            {
                Log.e(TAG, "normal screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Medium
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 3) //SMALL
            {
                Log.e(TAG, "small screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Small
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 4) //XLARGE
            {
                Log.e(TAG, "xlarge screen")
                val params = LinearLayout.LayoutParams(
                    80, 80
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else {
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            }
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                radioGroup1.clearCheck()
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    selectedValue = buttonView.text.toString().toInt()
                    Log.e(TAG, "selectedValue $selectedValue")
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
        }
        ll_nps_view.addView(radioGroup2)
    }


    private fun addRadioButtons_stage8() {
        radio_button_count = 0
        val radioGroup1 = RadioGroup(this)
        radioGroup1.orientation = LinearLayout.HORIZONTAL
        radioGroup1.gravity = Gravity.CENTER_HORIZONTAL
        for (i in 1..5) {
            val radioButton = RadioButton(this)
            radio_button_count = i
            radioButton.text = "" + radio_button_count
            radioButton.gravity = Gravity.CENTER
            radioButton.height = 80
            radioButton.width = 80
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 20, 10, 0)
            if (radioButton.text.toString().length > 1) {
                radioButton.setPadding(5, 5, 5, 5)
            } else {
                radioButton.setPadding(7, 5, 6, 5)
            }
            radioButton.gravity = Gravity.CENTER
            radioButton.layoutParams = params
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
            radioGroup1.addView(radioButton)
        }
        ll_nps_view.addView(radioGroup1)
        val radioGroup2 = RadioGroup(this)
        radioGroup2.orientation = LinearLayout.HORIZONTAL
        radioGroup2.gravity = Gravity.CENTER_HORIZONTAL
        for (i in 1..3) {
            radio_button_count = 5 + i
            val radioButton = RadioButton(this)
            radioButton.text = "" + radio_button_count
            radioButton.gravity = Gravity.CENTER
            radioButton.height = 80
            radioButton.width = 80
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 20, 10, 0)
            if (radioButton.text.toString().length > 1) {
                radioButton.setPadding(5, 5, 5, 5)
            } else {
                radioButton.setPadding(7, 5, 6, 5)
            }
            radioButton.gravity = Gravity.CENTER
            radioButton.layoutParams = params
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
            radioGroup2.addView(radioButton)
        }
        ll_nps_view.addView(radioGroup2)
    }


    private fun addRadioButtons_stage9() {
        radio_button_count = 0
        val radioGroup1 = RadioGroup(this)
        radioGroup1.orientation = LinearLayout.HORIZONTAL
        radioGroup1.gravity = Gravity.CENTER_HORIZONTAL
        val radioGroup2 = RadioGroup(this)
        radioGroup2.orientation = LinearLayout.HORIZONTAL
        radioGroup2.gravity = Gravity.CENTER_HORIZONTAL
        val screensize = Utils.getScreenSizeType(this)
        val top_margin = 30
        val right_margin = 20
        for (i in 0..5) {
            val radioButton = RadioButton(this)
            radioButton.text = "" + i
            radioButton.gravity = Gravity.CENTER
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioGroup1.addView(radioButton)
            if (screensize == 0) {
                Log.e(TAG, "General screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 1) //LARGE
            {
                Log.e(TAG, "large screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else if (screensize == 2) //NORMAL
            {
                Log.e(TAG, "normal screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Medium
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 3) //SMALL
            {
                Log.e(TAG, "small screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Small
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 4) //XLARGE
            {
                Log.e(TAG, "xlarge screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else {
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            }
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                radioGroup2.clearCheck()
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    selectedValue = buttonView.text.toString().toInt()
                    Log.e(TAG, "selectedValue $selectedValue")
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
        }
        ll_nps_view.addView(radioGroup1)
        for (i in 5..9) {
            val radioButton = RadioButton(this)
            radioButton.text = "" + i
            radioButton.gravity = Gravity.CENTER
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioGroup2.addView(radioButton)
            if (screensize == 0) {
                Log.e(TAG, "General screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 1) //LARGE
            {
                Log.e(TAG, "large screen")
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else if (screensize == 2) //NORMAL
            {
                Log.e(TAG, "normal screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Medium
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 3) //SMALL
            {
                Log.e(TAG, "small screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Small
                )
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 4) //XLARGE
            {
                Log.e(TAG, "xlarge screen")
                val params = LinearLayout.LayoutParams(
                    80, 80
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else {
                val params = LinearLayout.LayoutParams(
                    120, 120
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            }
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                radioGroup1.clearCheck()
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    selectedValue = buttonView.text.toString().toInt()
                    Log.e(TAG, "selectedValue $selectedValue")
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
        }
        ll_nps_view.addView(radioGroup2)
    }

    private fun addRadioButton_10(){
        radio_button_count = 0

        val radioGroup1 = RadioGroup(this)
        radioGroup1.orientation = LinearLayout.HORIZONTAL
        radioGroup1.gravity = Gravity.CENTER_HORIZONTAL

        val radioGroup2 = RadioGroup(this)
        radioGroup2.orientation = LinearLayout.HORIZONTAL
        radioGroup2.gravity = Gravity.CENTER_HORIZONTAL

        val screensize = Utils.getScreenSizeType(this)
        val top_margin = 20
        val right_margin = 10


        for (i in 0..5) {
            val radioButton = RadioButton(this)
            radioButton.text = "" + i
            radioButton.gravity = Gravity.CENTER
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioGroup1.addView(radioButton)
            if (screensize == 0) {
                Log.e(TAG, "General screen")
                val params = LinearLayout.LayoutParams(
                    dpToPx(42), dpToPx(42)
                )
            } else if (screensize == 1) //LARGE
            {
                Log.e(TAG, "large screen")
                val params = LinearLayout.LayoutParams(
                    dpToPx(45), dpToPx(45)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else if (screensize == 2) //NORMAL
            {
                Log.e(TAG, "normal screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Medium
                )
                val params = LinearLayout.LayoutParams(
                    dpToPx(42), dpToPx(42)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 3) //SMALL
            {
                Log.e(TAG, "small screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Small
                )
                val params = LinearLayout.LayoutParams(
                    dpToPx(40), dpToPx(40)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 4) //XLARGE
            {
                Log.e(TAG, "xlarge screen")
                val params = LinearLayout.LayoutParams(
                    dpToPx(80), dpToPx(80)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else {
                val params = LinearLayout.LayoutParams(
                    dpToPx(42), dpToPx(42)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            }
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                radioGroup2.clearCheck()
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    selectedValue = buttonView.text.toString().toInt()
                    Log.e(TAG, "selectedValue $selectedValue")
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
        }

        ll_nps_view.addView(radioGroup1)


        for (i in 6..10) {
            val radioButton = RadioButton(this)
            radioButton.text = "" + i
            radioButton.gravity = Gravity.CENTER
            radioButton.background = resources.getDrawable(R.drawable.radio_button_bg_circle)
            radioButton.buttonDrawable = null
            radioGroup2.addView(radioButton)
            if (screensize == 0) {
                Log.e(TAG, "General screen")
                val params = LinearLayout.LayoutParams(
                    dpToPx(42), dpToPx(42)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 1) //LARGE
            {
                Log.e(TAG, "large screen")
                val params = LinearLayout.LayoutParams(
                    dpToPx(45), dpToPx(45)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else if (screensize == 2) //NORMAL
            {
                Log.e(TAG, "normal screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Medium
                )
                val params = LinearLayout.LayoutParams(
                    dpToPx(42), dpToPx(42)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 3) //SMALL
            {
                Log.e(TAG, "small screen")
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Small
                )
                val params = LinearLayout.LayoutParams(
                    dpToPx(40), dpToPx(40)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            } else if (screensize == 4) //XLARGE
            {
                Log.e(TAG, "xlarge screen")
                val params = LinearLayout.LayoutParams(
                    dpToPx(80), dpToPx(80)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
                radioButton.setTextAppearance(
                    this,
                    android.R.style.TextAppearance_Large
                )
            } else {
                val params = LinearLayout.LayoutParams(
                    dpToPx(42), dpToPx(42)
                )
                params.setMargins(0, top_margin, right_margin, 0)
                radioButton.layoutParams = params
            }
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                radioGroup1.clearCheck()
                if (isChecked) {
                    buttonView.setTextColor(resources.getColor(R.color.white))
                    selectedValue = buttonView.text.toString().toInt()
                    Log.e(TAG, "selectedValue $selectedValue")
                    enableSubmitButton()
                } else {
                    buttonView.setTextColor(resources.getColor(R.color.black))
                }
            }
        }

        ll_nps_view.addView(radioGroup2)
    }

    private fun addRadioButton(){
addRadioButton_10()
    }

    private fun enableSubmitButton() {
        if (selectedValue != -1) {
            button_submit.isEnabled = true
            button_submit.setBackgroundColor(resources.getColor(R.color.light_green))
            button_submit.setTextColor(resources.getColor(R.color.white))
        }
    }

    fun dpToPx(dp: Int): Int {
        val density: Float = this.getResources()
            .getDisplayMetrics().density
        return Math.round(dp.toFloat() * density)
    }

    private fun onClickOfNpsSubmit(){

        if(Utils.isInternetAvailable(this)) {
            ProgressDialog.initializeProgressDialogue(this)
            ProgressDialog.showProgressDialog()
            submitNpsValue(makeJsonDataRequestToSend())
        }
        else {
            Utils.showInternetCheckToast(this)
        }
    }


    private fun submitNpsValue(jsonRequestString : JsonObject){

        Log.e(TAG,"jsonRequestString "+jsonRequestString)

        npsActivityViewModel.submitNPS(jsonRequestString)?.observe(this,object : Observer<CommonModel>{
            override fun onChanged(commonModel: CommonModel) {
                Log.e(TAG,"statuscode "+commonModel.statusCode)
                Log.e(TAG,"message "+commonModel.message)

                ProgressDialog.dismissProgressDialog()

                if(commonModel == null){
                    showErrorDialog(commonModel)
                }
                else
                {
                    if(commonModel.error == null){
                        submitNPSResponseHandling(commonModel)
                    }
                    else
                    {
                        var error: Throwable? = commonModel.error
                        submitNPSErrorHandling(error)
                    }
                }
            }

        })
    }

    private fun makeJsonDataRequestToSend() : JsonObject{


        val joNpsAnswer = JsonObject()
        joNpsAnswer.addProperty(""+npsQuestionModel.id,""+selectedValue)

        val joParent = JsonObject()
        joParent.add("nps_response",joNpsAnswer)
        joParent.addProperty("test_result_id",""+SharedPrefHelper(this).getTestResultId())
        joParent.addProperty("at",""+SharedPrefHelper(this).getToken())

        if(isFinish)
            joParent.addProperty("is_finished",isFinish)
        else
            joParent.addProperty("is_finished",isFinish)

        var jsonParser = JsonParser()

        return jsonParser.parse(joParent.toString()) as JsonObject
    }


    private fun submitNPSResponseHandling(commonModel: CommonModel){
        if(commonModel.statusCode == 200){
            manageFlowAfterTest.isSurveyQuestionsSubmitted = true
            manageFlowAfterTest.isSusQuestionsSubmitted = true
            manageFlowAfterTest.isNpsQuestionSubmitted = true
            manageFlowAfterTest.moveToWhichActivity(this)
        }
        else
        {
            showErrorDialog(commonModel)
        }
    }


    private fun submitNPSErrorHandling(error : Throwable?){
        var okBtn =  OkAlertDialog.initOkAlert(this)
        OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
    }

    private fun showErrorDialog(commonModel: CommonModel){

        var okBtn =  OkAlertDialog.initOkAlert(this)

        if(commonModel == null){

            OkAlertDialog.showOkAlert(this.resources.getString(R.string.something_went_wrong))
        }
        else
        {
            OkAlertDialog.showOkAlert(commonModel.message)
        }

        okBtn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }

    }



    private fun dismissProgressDialog(){
        ProgressDialog.dismissProgressDialog()
    }

    private fun dismissErrorDialog(){
        OkAlertDialog.dismissOkAlert()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()

        dismissProgressDialog()

        dismissErrorDialog()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()

        dismissProgressDialog()

        dismissErrorDialog()
    }

    private fun moveToHome(){
        var intent = Intent(NpsActivity@this,TabActivity::class.java)
        startActivity(intent)
        finish()
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
