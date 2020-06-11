package com.mahesch.trymyui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.ManageFlowBeforeRecording
import com.mahesch.trymyui.helpers.OkAlertDialog
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.model.FeedbackModel
import com.mahesch.trymyui.repository.NdaAgreementPresenter
import kotlinx.android.synthetic.main.activity_nda.*
import java.io.File

class NdaActivity : AppCompatActivity() ,NdaAgreementPresenter.INdaAgreementCallback{

    private val TAG: String = NdaActivity::class.java.getSimpleName().toUpperCase()

    private var ndaAgreementPresenter: NdaAgreementPresenter? = null
    private var sharedPrefHelper: SharedPrefHelper? = null

    private var availableTestConstant: AvailableTestModel? = null

    private var progressDialog: ProgressDialog? = null

    private var popupWindow: PopupWindow? = null
    private var show_confirmation_dialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nda)

        availableTestConstant = intent.getSerializableExtra("availableTestConstants") as AvailableTestModel

        if (availableTestConstant == null) {
            Toast.makeText(
                this@NdaActivity,
                resources.getString(R.string.went_wrong),
                Toast.LENGTH_LONG
            ).show()
            moveToDashBoardAndFinishThis()
        }
        progressDialog = ProgressDialog(this)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Please wait...")

        PRDownloader.initialize(applicationContext)
        val str: String? = Utils.getRootDirPath(this)
        Log.e(TAG, str)

        ll_three_dots.setOnClickListener(View.OnClickListener { v -> showMenuPopUp(v) })

        ndaAgreementPresenter = NdaAgreementPresenter(this, this)
        sharedPrefHelper = SharedPrefHelper(this)

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
        cb_nda.setButtonTintList(colorStateList)
        cb_nda.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btn_begin_test.setEnabled(true)
                btn_begin_test.setBackgroundDrawable(resources.getDrawable(R.drawable.trymyuitester_btn_bg))
                btn_begin_test.setTextColor(resources.getColor(R.color.white))
            } else {
                btn_begin_test.setEnabled(false)
                btn_begin_test.setBackgroundDrawable(resources.getDrawable(R.drawable.diable_button_bg))
                btn_begin_test.setTextColor(resources.getColor(R.color._AAAAAA))
            }
        })
        downloadPDFFromUrl()
        btn_begin_test.setOnClickListener(View.OnClickListener {
            if (sharedPrefHelper?.getUserType().equals("customer",true)) {
                ManageFlowBeforeRecording(
                    availableTestConstant,
                    this@NdaActivity).moveToWhichActivity(6)
            } else {
                if (Utils.isInternetAvailable(this@NdaActivity)) {
                    progressDialog!!.show()
                    if (sharedPrefHelper?.getGuestTester()!!) {
                        ndaAgreementPresenter?.submitNdaForGuest(
                            sharedPrefHelper?.getEmailId()!!,
                            sharedPrefHelper?.getAvaliableTestId()!!,
                            cb_nda.isChecked()
                        )
                    } else {
                        ndaAgreementPresenter?.submitNdaForWorker(
                            sharedPrefHelper?.getToken(),
                            sharedPrefHelper?.getAvaliableTestId(),
                            cb_nda.isChecked()
                        )
                    }
                } else {
                    Toast.makeText(
                        this@NdaActivity,
                        "Please check your internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun moveToDashBoardAndFinishThis() {
        val intent = Intent(this, TabActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun downloadPDFFromUrl() {
        PRDownloader.download(
                availableTestConstant?.nda_agreement_url,
                Utils.getRootDirPath(this), "myfile.pdf"
            )
            .build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Toast.makeText(this@NdaActivity, "downloadComplete", Toast.LENGTH_LONG)
                        .show()
                    val downloadFile =
                        File(Utils.getRootDirPath(this@NdaActivity), "myfile.pdf")
                    showPDFFromFile(downloadFile)
                }

                override fun onError(error: Error) {
                    Toast.makeText(
                        this@NdaActivity,
                        "Error in downloading file : $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun showPDFFromFile(downloadFile: File) {
        pdf_view.fromFile(downloadFile)
            .password(null)
            .defaultPage(0)
            .onPageError(OnPageErrorListener { page, t -> Log.e(TAG, "onPageError $page") })
            .load()
    }


    override fun onSuccessNdaAgreementCallback(feedback: FeedbackModel) {
        progressDialog!!.dismiss()
        if (feedback != null) {
            val statusCode: String = feedback.status_code.toString()
            Log.e(TAG, "statusCode $statusCode")
            if (statusCode.equals("200", ignoreCase = true)) {
                //MOVEFORWARD
                ManageFlowBeforeRecording(
                    availableTestConstant,
                    this@NdaActivity
                )
                    .moveToWhichActivity(5)
                finish()
            } else {
                OkAlertDialog.initOkAlert(
                    this@NdaActivity)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }

                OkAlertDialog.showOkAlert(feedback.message)
            }
        }
    }

    override fun onErrorNdaAgreementCallback() {
        progressDialog!!.dismiss()
        OkAlertDialog.initOkAlert(
            this@NdaActivity)?.setOnClickListener { OkAlertDialog.dismissOkAlert() }

        OkAlertDialog.showOkAlert(resources.getString(R.string.went_wrong))
    }

    override fun onStop() {
        super.onStop()
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
            rl_testdetails.getHeight() + 42
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
            val intentFeedback =
                Intent(this@NdaActivity, FeedbackActivity::class.java)
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            this@NdaActivity.finish()
        }
        tv_logout.setOnClickListener {
            popupWindow!!.dismiss()
            val intentFeedback =
                Intent(this@NdaActivity, WelcomeActivity::class.java)
            intentFeedback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentFeedback)
            this@NdaActivity.finish()
            //    onDestroy();
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
        ) { dialog, which ->
            dialogBuilder.setPositiveButton(
                R.string.yes
            ) { dialog, which ->
                if (SharedPrefHelper(this@NdaActivity).getGuestTester()) {
                    val intent =
                        Intent(this@NdaActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    this@NdaActivity.finish()
                } else {
                    val intent =
                        Intent(this@NdaActivity, TabActivity::class.java)
                    startActivity(intent)
                    this@NdaActivity.finish()
                }
            }
        }
        show_confirmation_dialog = dialogBuilder.create()
        val dialogWindow = show_confirmation_dialog?.getWindow()
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
