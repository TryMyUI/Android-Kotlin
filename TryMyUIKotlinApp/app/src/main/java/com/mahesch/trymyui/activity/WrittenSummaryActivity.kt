package com.mahesch.trymyui.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.viewmodelfactory.WrittenSummaryViewModelFactory
import com.mahesch.trymyui.viewmodels.WrittenSummaryActivityViewModel
import com.seattleapplab.trymyui.models.Tests
import com.seattleapplab.trymyui.models.Tests.SurveyQuestions
import com.seattleapplab.trymyui.models.Tests.SurveyQuestionsInfo
import kotlinx.android.synthetic.main.written_summary_acivity.*
import java.lang.Error

class WrittenSummaryActivity : AppCompatActivity(),View.OnClickListener {

    private var surveyQuestions : String? = null
    private var TAG = WrittenSummaryActivity::class.java.simpleName.toUpperCase()
    private var availableTestModel: AvailableTestModel? = null
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private var back_alert:android.app.AlertDialog? = null

    private var surveyQuestionList = ArrayList<Tests.SurveyQuestions>()
    private var questionIndex = 0
    private var backpress = 0
    private var value = 0
    private var isFinished = false
    private var seekValueMapping = HashMap<Int,Int>()

    private val FREERESPONSE = "free_response"
    private val SINGLESELECT = "single_select"
    private val MULTISELECT = "multi_select"
    private val SLIDERRATING = "slider_rating"

    private lateinit var writtenSummaryActivityViewModel: WrittenSummaryActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.written_summary_acivity)

        calculateHeight()

        sharedPrefHelper = SharedPrefHelper(this)

        ProgressDialog.initializeProgressDialogue(this)
        if(intent != null){
            availableTestModel = intent.extras.getSerializable("availableTestConstants")  as? AvailableTestModel
            surveyQuestions = intent.extras.getString("surveyQuestions","")
        }
        else
        {
            moveToHome()
        }

        if(availableTestModel?.ux_crowd_questions.equals("[]",true)
            && availableTestModel?.susQuestion.equals("[]",true)
            && availableTestModel?.npsQuestion.equals("[]",true))
            isFinished = true

        addJsonStringToList()

        mapSeekValue()

        btn_ws_back.setOnClickListener(this)
        btn_ws_next.setOnClickListener(this)

        showQuestions(surveyQuestionList[0])

        setIndicator(0)

        val factory =
            WrittenSummaryViewModelFactory(
                application
            )

        writtenSummaryActivityViewModel = ViewModelProvider(this,factory).get(WrittenSummaryActivityViewModel::class.java)

    }


    private fun addJsonStringToList(){
        surveyQuestionList = Gson().fromJson<java.util.ArrayList<SurveyQuestions>>(
            surveyQuestions,
            object : TypeToken<java.util.ArrayList<SurveyQuestions?>?>() {}.type
        )
    }


    private fun mapSeekValue(){
        for (i in surveyQuestionList.indices) {
            val surveyQuestions: SurveyQuestions = surveyQuestionList.get(i)
            if (surveyQuestions.question_type.equals(SLIDERRATING, ignoreCase = true)) {
                seekValueMapping.put(surveyQuestions.id, 0)
            }
        }
    }

    private fun showQuestions(surveyQuestions: SurveyQuestions){

        tv_ws_question.text = "Q. ${surveyQuestions.question}"

        when(surveyQuestions.question_type.toString()){
            FREERESPONSE -> displayFreeResponse(surveyQuestions)
            SINGLESELECT -> displaySingleSelect(surveyQuestions)
            MULTISELECT -> displayMultiSelect(surveyQuestions)
            SLIDERRATING -> displaySliderRating(surveyQuestions)
            else -> {
                displayFreeResponse(surveyQuestions)
            }
        }
    }

    private fun displayFreeResponse(surveyQuestions: SurveyQuestions){
        ll_free_response.visibility = View.VISIBLE
        ll_single_select.visibility = View.GONE
        ll_multi_select.visibility = View.GONE
        ll_silder_rating.visibility = View.GONE

        if(surveyQuestions.surveyQuestionsInfo_list != null){
            if(surveyQuestions.surveyQuestionsInfo_list.size > 0){
                et_response.setText(surveyQuestions.surveyQuestionsInfo_list.get(0).response_text)
            }
        }
    }

    private fun displaySingleSelect(surveyQuestions: SurveyQuestions){
        ll_free_response.visibility = View.GONE
        ll_single_select.visibility = View.VISIBLE
        ll_multi_select.visibility = View.GONE
        ll_silder_rating.visibility = View.GONE

        showSingleSelectType(surveyQuestions)
    }

    private fun showSingleSelectType(surveyQuestions: SurveyQuestions){

        var surveyQuestionsInfo_list = surveyQuestions.surveyQuestionsInfo_list

        val colorStateList = ColorStateList(arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled)), intArrayOf(
                resources.getColor(R.color.orange),
                resources.getColor(R.color.green)))

        ll_single_select.removeAllViews()

        val rg_single_select = RadioGroup(this)
        rg_single_select.orientation = LinearLayout.VERTICAL


        for (i in surveyQuestionsInfo_list.indices) {
            val surveyQuestionsInfo: SurveyQuestionsInfo = surveyQuestionsInfo_list.get(i)
            val rb_single_select = RadioButton(this)
            rb_single_select.id = surveyQuestionsInfo.id
            rb_single_select.text = surveyQuestionsInfo.answer
            rb_single_select.gravity = Gravity.TOP
            rb_single_select.setLineSpacing(2.0f, 1.0f)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 20, 20, 20)
            if (Utils.getScreenSizeType(this) === 1) {
                //LARGE
                rb_single_select.setPadding(0, 6, 0, 0)
            } else if (Utils.getScreenSizeType(this) === 2) {
                //NORMAL
                rb_single_select.setPadding(0, 14, 0, 0)
            } else if (Utils.getScreenSizeType(this) === 3) {
                //SMALL
                rb_single_select.setPadding(0, 15, 0, 0)
            } else if (Utils.getScreenSizeType(this) === 4) {
                //XLARGE
                rb_single_select.setPadding(0, 4, 0, 0)
            } else if (Utils.getScreenSizeType(this) === 0) {
                //DEFAULT
                rb_single_select.setPadding(0, 14, 0, 0)
            }
            rb_single_select.layoutParams = params
            rb_single_select.isChecked = surveyQuestionsInfo.isSelected
            rb_single_select.buttonTintList = colorStateList
            rg_single_select.addView(rb_single_select)
        }

        rg_single_select.setOnCheckedChangeListener { group, checkedId ->
            for (i in surveyQuestionsInfo_list.indices) {
                val surveyQuestionsInfo: SurveyQuestionsInfo = surveyQuestionsInfo_list.get(i)
                surveyQuestionsInfo.isSelected = surveyQuestionsInfo.id == checkedId
            }
        }

        ll_single_select.addView(rg_single_select)
    }

    private fun displayMultiSelect(surveyQuestions: SurveyQuestions){
        ll_free_response.visibility = View.GONE
        ll_single_select.visibility = View.GONE
        ll_multi_select.visibility = View.VISIBLE
        ll_silder_rating.visibility = View.GONE

        showMultiSelectType(surveyQuestions)
    }

    private fun showMultiSelectType(surveyQuestions: SurveyQuestions){
        var surveyQuestionsInfo_list = surveyQuestions.surveyQuestionsInfo_list

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled)
            ),
            intArrayOf(
                resources.getColor(R.color.orange),
                resources.getColor(R.color.green)
            )
        )

        ll_multi_select.removeAllViews()

        for (i in surveyQuestionsInfo_list.indices) {
            val surveyQuestionsInfo = surveyQuestionsInfo_list[i]
            val checkBox = CheckBox(this)
            checkBox.id = surveyQuestionsInfo.id
            checkBox.text = surveyQuestionsInfo.answer
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 20, 20, 20)
            if (Utils.getScreenSizeType(this) === 1) {
                //LARGE
                checkBox.setPadding(0, 6, 0, 0)
            } else if (Utils.getScreenSizeType(this) === 2) {
                //NORMAL
                checkBox.setPadding(0, 14, 0, 0)
            } else if (Utils.getScreenSizeType(this) === 3) {
                //SMALL
                checkBox.setPadding(0, 15, 0, 0)
            } else if (Utils.getScreenSizeType(this) === 4) {
                //XLARGE
                checkBox.setPadding(0, 4, 0, 0)
            } else if (Utils.getScreenSizeType(this) === 0) {
                //DEFAULT
                checkBox.setPadding(0, 14, 0, 0)
            }
            checkBox.layoutParams = params
            checkBox.isChecked = surveyQuestionsInfo.isSelected
            checkBox.buttonTintList = colorStateList
            checkBox.gravity = Gravity.TOP
            checkBox.setLineSpacing(2.0f, 1.0f)
            ll_multi_select.addView(checkBox)
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                for (i in surveyQuestionsInfo_list.indices) {
                    val surveyQuestionsInfo =
                        surveyQuestionsInfo_list[i]
                    Log.e(TAG, "button view id " + buttonView.id)
                    Log.e(TAG, "chck id " + surveyQuestionsInfo.id)
                    if (buttonView.id == surveyQuestionsInfo.id) {
                        surveyQuestionsInfo.isSelected = isChecked
                    }
                }
            }
        }

    }

    private fun displaySliderRating(surveyQuestions: SurveyQuestions){
        ll_free_response.visibility = View.GONE
        ll_single_select.visibility = View.GONE
        ll_multi_select.visibility = View.GONE
        ll_silder_rating.visibility = View.VISIBLE

        showSliderType(surveyQuestions)
    }

    private fun showSliderType(surveyQuestions: SurveyQuestions){
        var surveyQuestionsInfo_list = surveyQuestions.surveyQuestionsInfo_list
        ll_add_slider.removeAllViews()
        val surveyQuestionsInfo = surveyQuestionsInfo_list[0]

        surveyQuestionsInfo.max_value = surveyQuestionsInfo.max_value
        surveyQuestionsInfo.min_value = surveyQuestionsInfo.min_value

        tv_max_label.text = surveyQuestionsInfo.max_label
        tv_min_label.text = surveyQuestionsInfo.min_label

        val step = 1

        tv_min_value.text = "" + surveyQuestionsInfo.min_value
        tv_max_value.text = "" + surveyQuestionsInfo.max_value

        val seekBar = layoutInflater.inflate(R.layout.custom_seekbar, null) as SeekBar

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        seekBar.max = (surveyQuestionsInfo.max_value - surveyQuestionsInfo.min_value) / step
        seekBar.progressDrawable = resources.getDrawable(R.drawable.seekbar_line)
        seekBar.layoutParams = params

        if (surveyQuestionsInfo.selected_rating_value != 0) {
            GetProgressValue(seekBar, surveyQuestionsInfo)
            Log.e(TAG, "seekbar prog not zero " + seekBar.progress)
        } else {
            seekBar.progress = 0
            Log.e(TAG, "seekbar prog 0")
            GetProgressValue(seekBar, surveyQuestionsInfo)
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                Log.e("MAHESH", "OnProgress called $progress")
                value = surveyQuestionsInfo.min_value + progress * step
                Log.e("MAHESH", "max " + surveyQuestionsInfo.max_value)
                Log.e("MAHESH", "min " + surveyQuestionsInfo.min_value)
                Log.e("MAHESH", "Value $value")
                surveyQuestionsInfo.selected_rating_value = value
                GetProgressValue(seekBar, surveyQuestionsInfo)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        ll_add_slider.addView(seekBar)
    }

    private fun GetProgressValue(
        seekBar: SeekBar,
        surveyQuestionsInfo: SurveyQuestionsInfo
    ) {
        if (surveyQuestionsInfo.selected_rating_value > 0) {
            seekBar.progressDrawable
                .setColorFilter(resources.getColor(R.color.orange), PorterDuff.Mode.MULTIPLY)
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .height(resources.getDimension(R.dimen.my_seekbar_height).toInt())
                .width(resources.getDimension(R.dimen.my_seekbar_width).toInt())
                .withBorder(resources.getDimension(R.dimen.my_seekbar_border).toInt())
                .fontSize(resources.getDimension(R.dimen.my_seekbar_fontsize).toInt())
                .endConfig()
                .buildRound(
                    surveyQuestionsInfo.selected_rating_value.toString(),
                    resources.getColor(R.color.green_light)
                )
            seekBar.thumb = drawable
            seekBar.progress =
                surveyQuestionsInfo.selected_rating_value - surveyQuestionsInfo.min_value
        } else {
            seekBar.progressDrawable.setColorFilter(
                resources.getColor(R.color.light_grey),
                PorterDuff.Mode.MULTIPLY
            )
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .height(resources.getDimension(R.dimen.my_seekbar_height).toInt())
                .width(resources.getDimension(R.dimen.my_seekbar_width).toInt())
                .withBorder(resources.getDimension(R.dimen.my_seekbar_border).toInt())
                .fontSize(resources.getDimension(R.dimen.my_seekbar_fontsize).toInt())
                .endConfig()
                .buildRound("", resources.getColor(R.color.light_grey))
            seekBar.thumb = drawable
            seekBar.progress = surveyQuestionsInfo.middle_value - surveyQuestionsInfo.min_value
        }
    }

    private fun moveToHome(){
        var intent = Intent(this,TabActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun calculateHeight(){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val density = resources.displayMetrics.density

        var baseheight = 0.0f

        if (Utils.getScreenSizeType(this) === 1) {
            //LARGE
            baseheight = 110.0f * density
            Log.e(TAG, "base height large $baseheight")
        } else if (Utils.getScreenSizeType(this) === 2) {
            //NORMAL
            baseheight = 118.0f * density
            Log.e(TAG, "base height normal $baseheight")
        } else if (Utils.getScreenSizeType(this) === 3) {
            //SMALL
            baseheight = 125.0f * density
            Log.e(TAG, "base height small $baseheight")
        } else if (Utils.getScreenSizeType(this) === 4) {
            //XLARGE
            baseheight = 97.0f * density
            Log.e(TAG, "base height xlarge $baseheight")
        } else if (Utils.getScreenSizeType(this) === 0) {
            baseheight = 118.0f * density
            Log.e(TAG, "base height default $baseheight")
        }


        val real_height: Int =
            displayMetrics.heightPixels - Math.round(baseheight) - getNavigationBarHeight()

        val layout_description = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            Math.round(real_height.toFloat())
        )

        rl_3.setLayoutParams(layout_description)

    }


    private fun getNavigationBarHeight() : Int{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val usableHeight = metrics.heightPixels
            windowManager.defaultDisplay.getRealMetrics(metrics)
            val realHeight = metrics.heightPixels
            val density = resources.displayMetrics.density
            return if (realHeight > usableHeight) Math.round((realHeight - usableHeight) / density) else 0
        }
        return 0
    }

    override fun onClick(v: View?) {

        when(v?.id){

            R.id.btn_ws_back -> onBackButtonPressed()
                R.id.btn_ws_next -> onNextButtonPressed()
        }

    }

    private fun  onBackButtonPressed(){
        if (backpress != 0) {
            Log.e(TAG, "SHOW PREVIOUS QUESTION")
            backpress = backpress - 1
            btn_ws_next.text = "Next"
            showQuestions(surveyQuestionList.get(backpress))
            setIndicator(backpress)
        } else {
            onBackPressed()
        }
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

    private fun onNextButtonPressed(){
        if (backpress == surveyQuestionList.size - 2) {
            btn_ws_next.text = "Submit"
        }

        if (backpress != surveyQuestionList.size - 1) {

            val surveyQuestions: SurveyQuestions = surveyQuestionList.get(backpress)

            if (surveyQuestions.question_type.equals(FREERESPONSE, ignoreCase = true)) {
                if (et_response.text.toString().length > 0) {
                    val surveyQuestionsInfo = Tests().SurveyQuestionsInfo()
                    Log.e(
                        TAG,
                        "et_response.getText().toString() " + et_response.text.toString()
                    )
                    surveyQuestionsInfo.response_text = et_response.text.toString()
                    val surveyQuestionsInfoArrayList =
                        java.util.ArrayList<SurveyQuestionsInfo>()
                    surveyQuestionsInfoArrayList.add(surveyQuestionsInfo)
                    surveyQuestions.surveyQuestionsInfo_list = surveyQuestionsInfoArrayList
                    Log.e(TAG, "valid")
                    et_response.setText("")
                    showQuestions(surveyQuestionList.get(backpress + 1))
                    backpress = backpress + 1
                    setIndicator(backpress)
                } else {
                    Log.e(TAG, "not valid")
                    Toast.makeText(
                        WrittenSummaryWithChoicesActivity@this,
                        "" + resources.getString(R.string.free_response_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (surveyQuestions.question_type.equals(SINGLESELECT, ignoreCase = true)) {
                var isAtleastOneSelected = false
                if (surveyQuestions.surveyQuestionsInfo_list.size > 0) {
                    for (i in surveyQuestions.surveyQuestionsInfo_list.indices) {
                        val surveyQuestionsInfo =
                            surveyQuestions.surveyQuestionsInfo_list[i]
                        if (surveyQuestionsInfo.isSelected) {
                            Log.e(TAG, "break")
                            isAtleastOneSelected = true
                            break
                        } else {
                            Log.e(TAG, "no single selected")
                        }
                    }
                }
                if (isAtleastOneSelected) {
                    showQuestions(surveyQuestionList.get(backpress + 1))
                    backpress = backpress + 1
                    setIndicator(backpress)
                } else {
                    Toast.makeText(
                        WrittenSummaryWithChoicesActivity@this,
                        "" + resources.getString(R.string.radio_button_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (surveyQuestions.question_type.equals(MULTISELECT, ignoreCase = true)) {
                var isAtleastOneSelected = false
                if (surveyQuestions.surveyQuestionsInfo_list.size > 0) {
                    for (i in surveyQuestions.surveyQuestionsInfo_list.indices) {
                        val surveyQuestionsInfo =
                            surveyQuestions.surveyQuestionsInfo_list[i]
                        if (surveyQuestionsInfo.isSelected) {
                            Log.e(TAG, "break")
                            isAtleastOneSelected = true
                            break
                        } else {
                            Log.e(TAG, "no single selected")
                        }
                    }
                }
                if (isAtleastOneSelected) {
                    showQuestions(surveyQuestionList.get(backpress + 1))
                    backpress = backpress + 1
                    setIndicator(backpress)
                } else {
                    Toast.makeText(
                        WrittenSummaryWithChoicesActivity@this,
                        "" + resources.getString(R.string.multiple_choice_button_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (surveyQuestions.question_type.equals(SLIDERRATING, ignoreCase = true)) {
                if (surveyQuestions.surveyQuestionsInfo_list.size > 0) {
                    if (surveyQuestions.surveyQuestionsInfo_list[0].selected_rating_value != 0) {
                        Log.e(TAG, "Rating given")
                        showQuestions(surveyQuestionList.get(backpress + 1))
                        backpress = backpress + 1
                        setIndicator(backpress)
                    } else {
                        Log.e(TAG, "rating not given")
                        Toast.makeText(
                            WrittenSummaryWithChoicesActivity@this,
                            "" + resources.getString(R.string.rating_validation),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        else{
            Log.e(TAG, "SUBMIT RESPONSE")

            val surveyQuestions: SurveyQuestions = surveyQuestionList.get(backpress)

            if (surveyQuestions.question_type.equals(FREERESPONSE, ignoreCase = true)) {
                if (et_response.text.toString().length > 0) {
                    val surveyQuestionsInfo = Tests().SurveyQuestionsInfo()
                    Log.e(
                        TAG,
                        "et_response.getText().toString() " + et_response.text.toString()
                    )
                    surveyQuestionsInfo.response_text = et_response.text.toString()
                    val surveyQuestionsInfoArrayList =
                        java.util.ArrayList<SurveyQuestionsInfo>()
                    surveyQuestionsInfoArrayList.add(surveyQuestionsInfo)
                    surveyQuestions.surveyQuestionsInfo_list = surveyQuestionsInfoArrayList
                    makeJsonRequestOfAllAnswers()
                    backpress = backpress + 1
                    Log.e(TAG, "valid")
                } else {
                    Log.e(TAG, "not valid")
                    Toast.makeText(
                        WrittenSummaryWithChoicesActivity@this,
                        "Please enter comment",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (surveyQuestions.question_type.equals(SINGLESELECT, ignoreCase = true)) {
                var isAtleastOneSelected = false
                if (surveyQuestions.surveyQuestionsInfo_list.size > 0) {
                    for (i in surveyQuestions.surveyQuestionsInfo_list.indices) {
                        val surveyQuestionsInfo =
                            surveyQuestions.surveyQuestionsInfo_list[i]
                        if (surveyQuestionsInfo.isSelected) {
                            Log.e(TAG, "break")
                            isAtleastOneSelected = true
                            break
                        } else {
                            Log.e(TAG, "no single selected")
                        }
                    }
                }
                if (isAtleastOneSelected) {
                    makeJsonRequestOfAllAnswers()
                    backpress = backpress + 1
                } else {
                    Toast.makeText(
                        WrittenSummaryWithChoicesActivity@this,
                        "" + resources.getString(R.string.radio_button_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (surveyQuestions.question_type.equals(MULTISELECT, ignoreCase = true)) {
                var isAtleastOneSelected = false
                if (surveyQuestions.surveyQuestionsInfo_list.size > 0) {
                    for (i in surveyQuestions.surveyQuestionsInfo_list.indices) {
                        val surveyQuestionsInfo =
                            surveyQuestions.surveyQuestionsInfo_list[i]
                        if (surveyQuestionsInfo.isSelected) {
                            Log.e(TAG, "break")
                            isAtleastOneSelected = true
                            break
                        } else {
                            Log.e(TAG, "no single selected")
                        }
                    }
                }
                if (isAtleastOneSelected) {
                    makeJsonRequestOfAllAnswers()
                    backpress = backpress + 1
                } else {
                    Toast.makeText(
                        WrittenSummaryWithChoicesActivity@this,
                        "" + resources.getString(R.string.multiple_choice_button_validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (surveyQuestions.question_type.equals(SLIDERRATING, ignoreCase = true)) {
                if (surveyQuestions.surveyQuestionsInfo_list.size > 0) {
                    if (surveyQuestions.surveyQuestionsInfo_list[0].selected_rating_value != 0) {
                        Log.e(TAG, "Rating given")
                        makeJsonRequestOfAllAnswers()
                        backpress = backpress + 1
                    } else {
                        Log.e(TAG, "rating not given")
                        Toast.makeText(
                            WrittenSummaryWithChoicesActivity@this,
                            "" + resources.getString(R.string.rating_validation),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }


    private fun makeJsonRequestOfAllAnswers() {
        ProgressDialog.initializeProgressDialogue(this)

        var jo_whole_postResponse = JsonObject()

        jo_whole_postResponse.addProperty("at", "" + sharedPrefHelper.getToken())
        jo_whole_postResponse.addProperty("test_result_id", "" + sharedPrefHelper.getTestResultId())
        jo_whole_postResponse.addProperty("is_finished", isFinished)

        val jsonObject_answers = JsonObject()

        for (surveyQuestions in surveyQuestionList) {
            val question_type = surveyQuestions.question_type
            if (question_type.equals(SLIDERRATING, ignoreCase = true)) {
                jsonObject_answers.addProperty(
                    "" + surveyQuestions.id,
                    "" + surveyQuestions.surveyQuestionsInfo_list[0].selected_rating_value
                )
            } else if (question_type.equals(FREERESPONSE, ignoreCase = true)) {
                jsonObject_answers.addProperty(
                    "" + surveyQuestions.id,
                    "" + surveyQuestions.surveyQuestionsInfo_list[0].response_text
                )
            } else if (question_type.equals(SINGLESELECT, ignoreCase = true)) {
                for (i in surveyQuestions.surveyQuestionsInfo_list.indices) {
                    val surveyQuestionsInfo =
                        surveyQuestions.surveyQuestionsInfo_list[i]
                    if (surveyQuestionsInfo.isSelected) {
                        jsonObject_answers.addProperty(
                            "" + surveyQuestions.id,
                            "" + surveyQuestionsInfo.id
                        )
                    }
                }
            } else if (question_type.equals(MULTISELECT, ignoreCase = true)) {
                val multiselected_id_list =
                    java.util.ArrayList<String>()
                val jsonArray = JsonArray()
                for (i in surveyQuestions.surveyQuestionsInfo_list.indices) {
                    val surveyQuestionsInfo =
                        surveyQuestions.surveyQuestionsInfo_list[i]
                    if (surveyQuestionsInfo.isSelected) {
                        multiselected_id_list.add("" + surveyQuestionsInfo.id)
                        jsonArray.add("" + surveyQuestionsInfo.id)
                    }
                }
                jsonObject_answers.add("" + surveyQuestions.id, jsonArray)
            }
        }

        jo_whole_postResponse.add("answers", jsonObject_answers)

         var jsonParser = JsonParser()

        if(Utils.isInternetAvailable(this))
            submitWrittenSummary(jsonParser.parse(jo_whole_postResponse.toString()) as JsonObject)
        else
            Utils.showInternetCheckToast(this)


    }



    private fun setIndicator(index: Int) {
        tv_current_question.text = "(" + (index + 1) + " / " + surveyQuestionList.size + ")"
    }


    private fun submitWrittenSummary(jo_whole_postResponse : JsonObject){

        ProgressDialog.initializeProgressDialogue(this)
        ProgressDialog.showProgressDialog()

        writtenSummaryActivityViewModel.submitWrittenSummaryAnswer(jo_whole_postResponse)?.observe(this,object : Observer<CommonModel>{
            override fun onChanged(commonModel: CommonModel) {

                ProgressDialog.dismissProgressDialog()

                if(commonModel == null){
                    showErrorDialog(commonModel)
                }
                else
                {
                    if(commonModel.error == null){
                        submitWrittenSummaryResponseHandling(commonModel)
                    }
                    else
                    {
                        showErrorDialog(commonModel)
                    }
                }
            }

        })
    }

    private fun showErrorDialog(commonModel: CommonModel){

        OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
    }

    private fun submitWrittenSummaryResponseHandling(commonModel: CommonModel){

        if (commonModel == null){
            showErrorDialog(commonModel)
        }
        else
        {
            if(commonModel.statusCode == 200){
               Utils.showToast(this,"Written Summary submitted successfully")

                Log.e(TAG,"availableTestModel "+availableTestModel)
                Log.e(TAG,"availableTestModel survey "+availableTestModel?.surveyQuestions)
                Log.e(TAG,"availableTestModel sus "+availableTestModel?.susQuestion)
                Log.e(TAG,"availableTestModel ux "+availableTestModel?.ux_crowd_questions)
                Log.e(TAG,"availableTestModel nps "+availableTestModel?.npsQuestion)

                var manageFlowAfterTest = ManageFlowAfterTest(availableTestModel,this)
                manageFlowAfterTest.isSurveyQuestionsSubmitted = true
                manageFlowAfterTest.moveToWhichActivity(this)
            }
            else
            {
                OkAlertDialog.initOkAlert(this)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
                OkAlertDialog.showOkAlert(commonModel.message)

            }
        }
    }


    override fun onStop() {
        super.onStop()

        ProgressDialog.dismissProgressDialog()

        OkAlertDialog.initOkAlert(this)
        OkAlertDialog.dismissOkAlert()

        dismissBackAlert()

    }

    private fun dismissBackAlert(){
        if(back_alert != null){
            if(back_alert!!.isShowing)
                back_alert!!.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        ProgressDialog.dismissProgressDialog()
        OkAlertDialog.initOkAlert(this)
        OkAlertDialog.dismissOkAlert()

        dismissBackAlert()
    }

    override fun onPause() {
        super.onPause()

        ProgressDialog.dismissProgressDialog()
        OkAlertDialog.initOkAlert(this)
        OkAlertDialog.dismissOkAlert()

        dismissBackAlert()
    }




}
