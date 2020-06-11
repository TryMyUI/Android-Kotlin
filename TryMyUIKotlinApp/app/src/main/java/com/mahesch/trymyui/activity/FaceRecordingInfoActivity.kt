package com.mahesch.trymyui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.ManageFlowBeforeRecording
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.helpers.YesNoAlertDialog
import com.mahesch.trymyui.model.AvailableTestModel
import kotlinx.android.synthetic.main.face_recording_info_activity.*

class FaceRecordingInfoActivity : AppCompatActivity() {

    private lateinit var availableTestModel: AvailableTestModel
    private var popupWindow: PopupWindow? = null
    private var show_confirmation_dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_recording_info_activity)

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


        rg_face_rec_permission.setOnCheckedChangeListener { group, checkedId ->
            btn_face_rec_cont.setBackgroundResource(R.drawable.trymyuitester_btn_bg)
            btn_face_rec_cont.isClickable = true
        }

        btn_face_rec_cont.setOnClickListener {
            if (rg_face_rec_permission.checkedRadioButtonId == R.id.rb_yes) {
                ManageFlowBeforeRecording(availableTestModel, this@FaceRecordingInfoActivity).
                moveToWhichActivity(4)
                finish()
            } else {
                moveToHome()
            }
        }

        ll_three_dots.setOnClickListener { v -> showMenuPopUp(v) }
    }

    private fun onClickNo(){
        startActivity(Intent(this,TabActivity::class.java))
        finish()
    }

    private fun onClickYes(){
        ManageFlowBeforeRecording(availableTestModel,FaceRecordingInfoActivity@this).moveToWhichActivity(4)
        finish()
    }

    private fun moveToHome(){
        startActivity(Intent(this,TabActivity::class.java))
        finish()
    }

    override fun onBackPressed() {

        if(availableTestModel?.screener_test_available!!){
            showBackWarning()
        }
        else
        {
            ManageFlowBeforeRecording(availableTestModel,this).manageBackFlow(3)
            finish()
        }

    }

    private fun showBackWarning(){


        var btn_array = YesNoAlertDialog.initYesNoDialogue(this)

        var btn_yes = btn_array!![0]
        var btn_no = btn_array!![1]

        YesNoAlertDialog.showYesNoDialogue("",resources.getString(R.string.screener_back_warning),"Yes","No")

        btn_no.setOnClickListener { YesNoAlertDialog.dismissYesNoDialogue() }

        btn_yes.setOnClickListener { YesNoAlertDialog.dismissYesNoDialogue()
            moveToHome()}

    }


    private fun showMenuPopUp(view1: View) {
        val density = resources.displayMetrics.density
        popupWindow =
            PopupWindow(view1, density.toInt() * 240, WindowManager.LayoutParams.WRAP_CONTENT, true)
        val view: View
        val layoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(R.layout.pop_up_menu, null)
        popupWindow?.isFocusable = true
        popupWindow?.contentView = view
        popupWindow?.showAtLocation(
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
            popupWindow?.dismiss()
            val intentFeedback =
                Intent(this@FaceRecordingInfoActivity, FeedbackActivity::class.java)
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            this@FaceRecordingInfoActivity.finish()
        }
        tv_logout.setOnClickListener {
            popupWindow?.dismiss()
            val intentFeedback =
                Intent(this@FaceRecordingInfoActivity, WelcomeActivity::class.java)
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            this@FaceRecordingInfoActivity.finish()
        }
        tv_menu_leavetest.setOnClickListener {
            popupWindow?.dismiss()
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
        ) { dialog, which ->
            dialogBuilder.setPositiveButton(
                R.string.yes
            ) { dialog, which ->
                if (SharedPrefHelper(this@FaceRecordingInfoActivity).getGuestTester()) {
                    val intent =
                        Intent(this@FaceRecordingInfoActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    this@FaceRecordingInfoActivity.finish()
                } else {
                    val intent =
                        Intent(this@FaceRecordingInfoActivity, TabActivity::class.java)
                    startActivity(intent)
                    this@FaceRecordingInfoActivity.finish()
                }
            }
        }
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
}
