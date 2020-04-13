package com.mahesch.trymyui.adapter

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.size
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.SusQuestionActivity
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.model.AvailableTestModel
import com.seattleapplab.trymyui.models.Tests
import com.seattleapplab.trymyui.models.Tests.SusScales
import java.util.*
import kotlin.collections.HashMap

class SusAdapter(susQuestionActivity: SusQuestionActivity,susTestsList: ArrayList<Tests.SusQuestions>,availableTestModel: AvailableTestModel) : BaseAdapter() {

     var checkvalues: HashMap<String, String> = HashMap()
    private var context: Context = susQuestionActivity.baseContext
    private var susTestList: ArrayList<Tests.SusQuestions> = susTestsList
    private var sharedPrefHelper: SharedPrefHelper = SharedPrefHelper(context)
    private var availableTestModel: AvailableTestModel = availableTestModel
    private var mOnDataChangeListener: OnDataChangeListener? = null
    private var susQuestionActivity = susQuestionActivity


    private val TAG: String = SusAdapter::class.java.simpleName.toUpperCase()
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {


        var holder: Holder? = null
        var rowView = convertView

        if(rowView == null){
            var rowView = inflater.inflate(R.layout.sus_list_row,null)

            holder = SusAdapter(susQuestionActivity,susTestList,availableTestModel).Holder()

            holder?.tv = (rowView?.findViewById<View>(R.id.textview_sus_question) as TextView)
            holder?.llRatingLabels = (rowView?.findViewById<View>(R.id.ll_rating_labels) as LinearLayout)
            holder?.radioGroup = (rowView?.findViewById<View>(R.id.radio_group) as RadioGroup)

            holder?.tv?.text = Html.fromHtml("<b>"+"Q "+(position+1)+": </b> "+susTestList[position].question)

            Log.e(TAG,"id "+susTestList[position].id)
            Log.e(TAG,"question "+susTestList[position].question)

            val susScales: SusScales = Gson().fromJson(availableTestModel.sus_scales, object : TypeToken<SusScales?>() {}.type)

            if(susScales.isNot_applicable){
                if (holder != null) {
                    displayViewForNotApplicable(susScales,holder,context,susTestList[position])
                }
            }
            else{
                if (holder != null) {
                    displayViewForApplicable(susScales,holder,context,susTestList[position])
                }
            }

            rowView.tag = holder
        }
        else
        {
            holder = rowView.tag as Holder
        }

        holder?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->

            var radioButton  = group.findViewById<View>(checkedId) as RadioButton

            if(null != radioButton &&  checkedId > -1){
                Toast.makeText(context, ""+radioButton.getText()+" pos"+susTestList[position].id, Toast.LENGTH_SHORT).show();

                Log.e(TAG,"checkedID "+checkedId)

                if(radioButton.text.toString().equals("NA",true)){
                    checkvalues.put(susTestList[position].id.toString(),"0")
                }
                else{
                    checkvalues.put(susTestList[position].id.toString(),radioButton.text.toString())
                }
                if(mOnDataChangeListener != null){
                    mOnDataChangeListener?.onDataChanged(checkvalues.size)
                }

            }
        }

        return rowView
    }


    private fun displayViewForApplicable(susScales: SusScales,holder: Holder,context: Context,susQuestions: Tests.SusQuestions){

        for (i in susScales.min_scale..susScales.max_scale) {
            var radioButton = RadioButton(context)

            Log.e(TAG,"selected vale "+ checkvalues[susQuestions.id.toString()])

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            )
            params.setMargins(0, 0, 10, 0)
            params.weight = 1.0f
            if (radioButton.text.toString().length > 1) {
                radioButton.setPadding(5, 5, 5, 5)
            } else {
                radioButton.setPadding(7, 5, 6, 5)
            }

         //   radioButton.isChecked = checkvalues[susQuestions.id.toString()]?.toInt() == i

          /*  if(radioButton.isChecked)
                radioButton.setTextColor(context.resources.getColor(R.color.white))
            else
                radioButton.setTextColor(context.resources.getColor(R.color.black))*/

            radioButton.gravity = Gravity.CENTER
            radioButton.layoutParams = params
            radioButton.text = "" + i
            radioButton.background = context.resources.getDrawable(R.drawable.radio_button_bg)
            radioButton.buttonDrawable = null

            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)

                if (isChecked) {
                    buttonView.setTextColor(context.resources.getColor(R.color.white))
                } else {
                    buttonView.setTextColor(context.resources.getColor(R.color.black))
                }
            }

            holder?.radioGroup.addView(radioButton)

        }
    }

    private fun displayViewForNotApplicable(susScales: SusScales,holder: Holder,context: Context,susQuestions: Tests.SusQuestions){
        for (i in susScales.min_scale .. susScales.max_scale+1) {

            val radioButton = RadioButton(context)

            holder.radioGroup.addView(radioButton)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            )

            params.setMargins(0, 0, 10, 0)

            params.weight = 1.0f

            if (i == susScales.max_scale + 1) {
                params.setMargins(30, 0, 10, 0)
            } else {
                params.setMargins(0, 0, 10, 0)
            }

            if (radioButton.text.toString().length > 1) {
                radioButton.setPadding(5, 5, 5, 5)
            } else {
                radioButton.setPadding(7, 5, 6, 5)
            }

         //   radioButton.isChecked = checkvalues[susQuestions.id.toString()]?.toInt() == i

           /* if(radioButton.isChecked)
                radioButton.setTextColor(context.resources.getColor(R.color.white))
            else
                radioButton.setTextColor(context.resources.getColor(R.color.black))*/

            radioButton.gravity = Gravity.CENTER

            radioButton.layoutParams = params

            val ll_rating_labels_params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            ll_rating_labels_params.setMargins(0, 0, 175, 0)
            holder.llRatingLabels.setLayoutParams(ll_rating_labels_params)
            if (i == susScales.max_scale + 1) {
                radioButton.text = "NA"
            } else {
                radioButton.text = "" + i
            }
            radioButton.background = context.resources.getDrawable(R.drawable.radio_button_bg)
            radioButton.buttonDrawable = null
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e("isChecked ", "" + isChecked)
                if (isChecked) {
                    buttonView.setTextColor(context.resources.getColor(R.color.white))
                } else {
                    buttonView.setTextColor(context.resources.getColor(R.color.black))
                }
            }
        }
    }

    override fun getItem(position: Int): Any {
        return susTestList
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return susTestList.size
    }

    inner class Holder{
             lateinit var tv: TextView
             lateinit var radioGroup: RadioGroup
             lateinit var llRatingLabels: LinearLayout
    }

    interface OnDataChangeListener{
        fun onDataChanged(size: Int)
    }

     fun setonDataChangesListener(onDataChangeListener: OnDataChangeListener){
            this.mOnDataChangeListener = onDataChangeListener
    }
}