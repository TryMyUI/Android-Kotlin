package com.mahesch.trymyui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.SpecialTechnicalModel
import com.mahesch.trymyui.viewmodelfactory.TechnicalQualificationViewModelFactory
import com.mahesch.trymyui.viewmodels.TechnicalQualificationViewModel
import kotlinx.android.synthetic.main.technical_qualification_activity.*

class TechnicalQualificationActivity : AppCompatActivity() {

    private var TAG  = TechnicalQualificationActivity::class.simpleName?.toUpperCase()
    private lateinit var  techQualViewModel : TechnicalQualificationViewModel
    private lateinit var availableTestModel: AvailableTestModel

    private var popupWindow: PopupWindow? = null
    private var show_confirmation_dialog: Dialog? = null
    private var progressDialog: android.app.ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.technical_qualification_activity)

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

        val factory =  TechnicalQualificationViewModelFactory(application)

        progressDialog = android.app.ProgressDialog(this)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Please wait...")

        techQualViewModel = ViewModelProvider(this,factory).get(TechnicalQualificationViewModel::class.java)

        tv_tech_qual.text = Html.fromHtml(availableTestModel.technicalQualification)

        rg_tech_qual.setOnCheckedChangeListener { group, checkedId ->
            btn_tech_cont.setBackgroundResource(R.drawable.trymyuitester_btn_bg)
            btn_tech_cont.setTextColor(resources.getColor(R.color.white))
            btn_tech_cont.isClickable = true
        }

        btn_tech_cont.setOnClickListener {
            if (rg_tech_qual.checkedRadioButtonId == R.id.rb_yes) {
                ManageFlowBeforeRecording(availableTestModel, this@TechnicalQualificationActivity)
                    .moveToWhichActivity(2)
                finish()
            } else {
                if (Utils.isInternetAvailable(this@TechnicalQualificationActivity)) {
                    progressDialog?.show()
                    postTechCriteriaResponse()
                } else {
                    Utils.showInternetCheckToast(TechnicalQualificationActivity@this)
                }
            }
        }

        ll_three_dots.setOnClickListener { v -> showMenuPopUp(v) }


    }


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
            rl_testdetails.height + 42
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
                this@TechnicalQualificationActivity,
                FeedbackActivity::class.java
            )
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            finish()
        }
        tv_logout.setOnClickListener {
            popupWindow!!.dismiss()
            val intentFeedback = Intent(
                this@TechnicalQualificationActivity,
                WelcomeActivity::class.java
            )
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            finish()
        }
        tv_menu_leavetest.setOnClickListener {
            popupWindow!!.dismiss()
            showConfirmation()
        }
    }

    private fun showConfirmation() {
        val dialogBuilder =
            AlertDialog.Builder(this, R.style.AppTheme_MaterialDialogTheme)
        dialogBuilder.setTitle("Are you sure you want to leave this test?")
        dialogBuilder.setMessage("This will end your test and you will be redirected to your TryMyUI dashboard")
        dialogBuilder.setNegativeButton(
            R.string.cancel
        ) { dialog, which -> dialog.dismiss() }
        dialogBuilder.setPositiveButton(
            R.string.yes
        ) { dialog, which -> moveToHome() }
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

    private fun moveToHome(){
        var intent = Intent(TechnicalQualificationActivity@this,TabActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun postTechCriteriaResponse(){
        ProgressDialog.initializeProgressDialogue(this)
        ProgressDialog.showProgressDialog()

        SharedPrefHelper(TechnicalQualificationActivity@this).getToken()?.let {
            techQualViewModel.postTechnicalResponse(it,availableTestModel.id.toString())?.observe(this,
                Observer<SpecialTechnicalModel> { moveToHome()
                ProgressDialog.dismissProgressDialog()
                })
        }
    }

    override fun onStop() {
        super.onStop()
        OkAlertDialog.dismissOkAlert()
        ProgressDialog.dismissProgressDialog()
        if (progressDialog!!.isShowing) progressDialog!!.dismiss()

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
        OkAlertDialog.dismissOkAlert()
        ProgressDialog.dismissProgressDialog()

        if (progressDialog!!.isShowing) progressDialog!!.dismiss()

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

    override fun onBackPressed() {
        //super.onBackPressed()

        ManageFlowBeforeRecording(availableTestModel,this).manageBackFlow(1)
    }
}
