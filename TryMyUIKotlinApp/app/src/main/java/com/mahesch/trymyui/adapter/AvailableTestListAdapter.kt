package com.mahesch.trymyui.adapter

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.TabActivity
import com.mahesch.trymyui.activity.VideoPlayerActivity
import com.mahesch.trymyui.fragment.AvailableTestFragment
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.repository.CheckUseTestAvailabilityRepository
import com.mahesch.trymyui.retrofitclient.ApiService
import com.seattleapplab.trymyui.models.TestAvailabilityModel
import kotlinx.android.synthetic.main.show_gesture_dialog.*


class AvailableTestListAdapter(availableTestModelList: ArrayList<AvailableTestModel>,availableTestFragment: AvailableTestFragment) :
    RecyclerView.Adapter<AvailableTestListAdapter.ViewHolder>(),
    CheckUseTestAvailabilityRepository.ICheckUseTestAvailability {


    private var availableTestFragment: AvailableTestFragment? = null
    private var mSharedPrefHelper: SharedPrefHelper? = null
    private var userType: String? = null
    private var availableTestList: ArrayList<AvailableTestModel>? = null

     val TAG: String = AvailableTestListAdapter::class.java.simpleName.toUpperCase()

    init {
        this.availableTestList = availableTestModelList
        this.availableTestFragment = availableTestFragment

        if (availableTestFragment.activity != null) {
            if (!availableTestFragment.activity!!.isFinishing) {
                mSharedPrefHelper = SharedPrefHelper(availableTestFragment?.activity!!)
                userType = mSharedPrefHelper?.getUserType()
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ll_available_test_row: LinearLayout
        var ll_watch_video: LinearLayout
        var rl_testtyp_testid: RelativeLayout
        var tv_test_type: TextView
        var tv_testid: TextView
        var tv_test_title: TextView
        var tv_noofpretest: TextView
        var tv_testid_cust_guest: TextView
        var tv_testduration: TextView
        var btn_taketest: Button
        var btn_watch_video: android.widget.Button


        init {

            Log.e("ViewHolder",""+view)
            ll_available_test_row = view.findViewById<View>(R.id.ll_available_test_row) as LinearLayout
            Log.e("ll_available_test_row ",""+ll_available_test_row)
            ll_watch_video = view.findViewById<View>(R.id.ll_watch_video) as LinearLayout
            rl_testtyp_testid = view.findViewById<View>(R.id.rl_testtyp_testid) as RelativeLayout
            tv_test_type = view.findViewById<View>(R.id.tv_test_type) as TextView
            tv_test_type = view.findViewById<View>(R.id.tv_test_type) as TextView
            tv_testid = view.findViewById<View>(R.id.tv_testid) as TextView
            tv_test_title = view.findViewById<View>(R.id.tv_test_title) as TextView
            tv_noofpretest = view.findViewById<View>(R.id.tv_noofpretest) as TextView
            tv_testid_cust_guest = view.findViewById<View>(R.id.tv_testid_cust_guest) as TextView
            tv_testduration = view.findViewById<View>(R.id.tv_testduration) as TextView
            btn_taketest = view.findViewById<View>(R.id.btn_taketest) as Button
            btn_watch_video = view.findViewById<View>(R.id.btn_watch_video) as Button

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.avaialble_test_row_new, parent, false)

        return AvailableTestListAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return availableTestList!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        var screenerCount = 0

        if (availableTestList?.get(i)?.id == 0) {
            setVideoContainerVisible(viewHolder)
        } else {
            setVideoContainerGone(viewHolder)

            if (!userType.equals(mSharedPrefHelper!!.UserTypeCustomer, ignoreCase = true))
                screenerCount = preTestCountDisplay(viewHolder, availableTestList?.get(i)!!, screenerCount)
            else
                viewHolder.tv_noofpretest!!.visibility = View.GONE


            setButtonTextAndBackground(viewHolder, availableTestList?.get(i)!!, screenerCount, i)

            // FOR PENDING TEST (MEANS ONLY WORKER COZ GUEST AND CUSTOMER DOESN'T HAS PENDING STATE)
            if (TabActivity.isPendingTest && userType.equals(mSharedPrefHelper?.UserTypeWorker))
            {
                displayPendingRow(viewHolder, availableTestList?.get(i))
            }
            else if (mSharedPrefHelper!!.getGuestTester())
            {
                displayGuestRows(viewHolder, availableTestList?.get(i))
            }
            else if (userType.equals(mSharedPrefHelper?.UserTypeWorker, true))
            {

                Log.e(TAG, "model in adapter " + availableTestList?.get(i))
                displayWorkerRows(viewHolder, availableTestList?.get(i))
            }
            else //USER IS CUSTOMER
            {
                displayCustomerRows(viewHolder, availableTestList?.get(i))
            }
        }


    }


    interface TakeTestClickedListener {

        fun takeTestClicked(availableTestModel: AvailableTestModel?)
    }


    private fun setVideoContainerVisible(viewHolder: ViewHolder) {
        viewHolder.ll_available_test_row?.visibility = View.GONE
        viewHolder.ll_watch_video?.visibility = View.VISIBLE

        viewHolder.btn_watch_video?.setOnClickListener(View.OnClickListener {

            availableTestFragment!!.activity!!.startActivity(
                Intent(
                    availableTestFragment!!.activity,
                    VideoPlayerActivity::class.java
                )
            )

            //availableTestFragment!!.activity!!.finish()
        })
    }

    private fun setVideoContainerGone(viewHolder: ViewHolder) {
        viewHolder.ll_available_test_row!!.visibility = View.VISIBLE
        viewHolder.ll_watch_video!!.visibility = View.GONE
    }


    private fun displayPendingRow(viewHolder: ViewHolder, availableTestModel: AvailableTestModel?) {
        viewHolder.btn_taketest?.text = "Resume test"
        viewHolder.btn_taketest?.isClickable = true

        viewHolder.rl_testtyp_testid?.visibility = View.VISIBLE
        viewHolder.tv_testid!!.text = availableTestFragment!!.activity!!!!.resources.getString(R.string.test_id)
                .toString() + "" + availableTestModel?.id

        if (availableTestModel?.interface_type.equals("web", true)) {
            viewHolder.tv_test_type!!.text = availableTestFragment!!.resources.getString(R.string.mobilewebsitetest)
        } else {
            viewHolder.tv_test_type!!.text = availableTestFragment!!.resources.getString(R.string.mobilewebsitetest)
        }

        viewHolder.tv_test_title!!!!.visibility = View.GONE
        viewHolder.tv_testid_cust_guest!!!!.visibility = View.GONE

        viewHolder.btn_taketest!!.setOnClickListener {

            if(availableTestModel?.id == 0){
                //SHOW TOAST
                availableTestFragment?.context?.let { it1 -> Utils.showToast(it1,availableTestFragment!!.getString(R.string.went_wrong)) }
            }
            else
            {
                availableTestFragment?.takeTestClicked(availableTestModel)
            }

        }


    }

    private fun displayGuestRows(viewHolder: AvailableTestListAdapter.ViewHolder, availableTestModel: AvailableTestModel?){

        viewHolder.btn_taketest?.isClickable = true

        var testerPlatform = availableTestModel?.tester_platform

        viewHolder.rl_testtyp_testid?.visibility = View.GONE
        viewHolder.tv_noofpretest?.visibility = (View.GONE)
        viewHolder.tv_test_title?.visibility = (View.VISIBLE)
        viewHolder.tv_test_title?.text = (availableTestModel?.title)
        viewHolder.tv_testid_cust_guest?.visibility = (View.VISIBLE)
        viewHolder.tv_testid_cust_guest?.text = ("Test ID : " + availableTestModel?.id)

        viewHolder.btn_taketest?.setOnClickListener{

            if(Utils.patternMatchForAndroid(testerPlatform))
            {
                //SHOW DIALOGUE FOR GUESTURE INSTRUCTION
                showGestureDialog(availableTestModel)
            }
            else
            {
                showOkAlertForPcMacTestOrIos(testerPlatform)
            }
        }
    }


    private fun displayWorkerRows(viewHolder: ViewHolder,availableTestModel: AvailableTestModel?){

        viewHolder.btn_taketest?.isClickable = true

        viewHolder.rl_testtyp_testid!!.visibility = View.VISIBLE
        viewHolder.tv_testid!!.text = availableTestFragment!!.activity!!.resources.getString(R.string.test_id)
                .toString() + "" + availableTestModel?.id

        if (availableTestModel?.interface_type.equals("web",true))
        {
            viewHolder.tv_test_type!!.text = availableTestFragment!!.resources.getString(R.string.mobilewebsitetest)
        }
        else
        {
            viewHolder.tv_test_type!!.text = availableTestFragment!!.resources.getString(R.string.mobilewebsitetest)
        }
        viewHolder.tv_test_title!!.visibility = View.GONE
        viewHolder.tv_testid_cust_guest!!.visibility = View.GONE

        viewHolder.btn_taketest?.setOnClickListener{
            showGestureDialog(availableTestModel)
        }
    }

    private fun displayCustomerRows(viewHolder: AvailableTestListAdapter.ViewHolder, availableTestModel: AvailableTestModel?){

        viewHolder.btn_taketest?.isClickable = true
        viewHolder.btn_taketest?.text = "Preview test"

        var testerPlatform = availableTestModel?.tester_platform

        viewHolder.rl_testtyp_testid?.visibility = View.GONE
        viewHolder.tv_noofpretest?.visibility = (View.GONE)
        viewHolder.tv_test_title?.visibility = (View.VISIBLE)
        viewHolder.tv_test_title?.text = (availableTestModel?.title)
        viewHolder.tv_testid_cust_guest?.visibility = (View.VISIBLE)
        viewHolder.tv_testid_cust_guest?.text = ("Test ID : " + availableTestModel?.id)

        viewHolder.btn_taketest?.setOnClickListener{

            if(Utils.patternMatchForAndroid(testerPlatform))
            {
                //SHOW DIALOGUE FOR GUESTURE INSTRUCTION
                showGestureDialog(availableTestModel)
            }
            else
            {
                showOkAlertForPcMacTestOrIos(testerPlatform)
            }
        }
    }

    private fun setButtonTextAndBackground(viewHolder: AvailableTestListAdapter.ViewHolder, availableTestConstant: AvailableTestModel,
                                           screenerCount: Int, i: Int) {

        if (TabActivity.isPendingTest)
        {
            viewHolder.btn_taketest?.text = availableTestFragment!!.resources.getString(R.string.resume_test)
            viewHolder.tv_testduration?.text = availableTestFragment!!.resources.getString(R.string.completepostsurvey)
        }
        else
        {
            viewHolder.tv_testduration?.text = availableTestFragment!!.activity!!.resources.getString(R.string.testduration)
                .toString() + " " + availableTestList?.get(i)?.recording_timeout_minutes + " min"

            if (userType.equals(mSharedPrefHelper!!.UserTypeCustomer, ignoreCase = true))
            {
                viewHolder.btn_taketest?.text = availableTestFragment!!.resources.getString(R.string.proceedtosurvey)
            }
            else {
                if (screenerCount > 0) {
                    viewHolder.btn_taketest?.text = availableTestFragment!!.resources.getString(R.string.checkeligibilty)
                }
                else
                {
                    viewHolder.btn_taketest?.text = availableTestFragment!!.resources.getString(R.string.taketest)
                }
            }
        }
    }

    private fun preTestCountDisplay(
        viewHolder: AvailableTestListAdapter.ViewHolder,
        availableTestConstant: AvailableTestModel,
        screenerCount: Int): Int {

        var screenerCount = screenerCount

        if (availableTestConstant.specialQalification != null)
        {
            if (availableTestConstant.specialQalification!!.length > 0)
            {
                screenerCount += 1
            }
        }
        if (availableTestConstant.technicalQualification != null)
        {
            if (availableTestConstant.technicalQualification!!.length > 0)
            {
                screenerCount += 1
            }
        }
        if (availableTestConstant.screener_test_available!!)
        {
            screenerCount += 1
        }
        if (screenerCount > 0)
        {
            viewHolder.tv_noofpretest?.visibility = View.VISIBLE
            viewHolder.tv_noofpretest?.text = availableTestFragment!!.resources.getString(R.string.pretestsurvey)
        }
        else
        {
            viewHolder.tv_noofpretest?.setVisibility(View.GONE)
        }
        return screenerCount
    }

    private fun showOkAlertForPcMacTestOrIos(testerPlatform : String?){

        var btn = OkAlertDialog.initOkAlert(availableTestFragment?.context!!)

        if(testerPlatform.equals("pc_mac",true))
        {
            OkAlertDialog.showOkAlert(availableTestFragment?.context!!.resources.getString(R.string.pcmac_test))
        }
        else
        {
            OkAlertDialog.showOkAlert(availableTestFragment?.context!!.resources.getString(R.string.ios_test))
        }

        btn?.setOnClickListener { OkAlertDialog.dismissOkAlert() }
    }

    private fun showGestureDialog(availableTestModel: AvailableTestModel?){

        var dialog = Dialog(availableTestFragment?.context)
        dialog.setContentView(R.layout.show_gesture_dialog)

        var lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window.attributes)

        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.window.attributes = lp
        dialog.setCancelable(false)
        dialog.show()

        dialog.tv_yes_link.setOnClickListener{
            dialog.dismiss()

            val intentBrowser = Intent(Intent.ACTION_VIEW)
            intentBrowser.data = Uri.parse(ApiService.BASE_URL + ApiService.ASSIST_URL)

            availableTestFragment!!.activity?.startActivity(Intent.createChooser(intentBrowser, "Choose browser"))
        }

        dialog.tv_no_already_enabled.setOnClickListener {
            dialog.dismiss()



            if(!(mSharedPrefHelper?.getUserType().equals(mSharedPrefHelper?.UserTypeCustomer,true))){

                availableTestFragment?.context?.let { it1 -> ProgressDialog.initializeProgressDialogue(it1) }

                ProgressDialog.showProgressDialog()

                callCheckUseTestAvailability(availableTestModel)
            }
            else
            {
                Log.e(TAG,"customer doesn't check for useTestAvailability")
                // availableTestFragment?.temporaryFlow(availableTestModel)
                ManageFlowBeforeRecording(availableTestModel,availableTestFragment?.context!!).moveToWhichActivity(0)
            }




        }

    }


    private fun callCheckUseTestAvailability(availableTestModel: AvailableTestModel?)  {

        var checkUseTestAvailabilityRepository = CheckUseTestAvailabilityRepository(this)

        if(mSharedPrefHelper!!.getGuestTester()){
            checkUseTestAvailabilityRepository.callGuestCheckUseTestRepository(availableTestModel,mSharedPrefHelper?.getEmailId())
        }
        else{
            checkUseTestAvailabilityRepository.callWorkerCheckUseTestRepository(availableTestModel,mSharedPrefHelper?.getToken())
        }



    }


    private fun responseHandlingOfCheckUseTestAvailability(testAvailabilityModel: TestAvailabilityModel?,availableTestModel: AvailableTestModel?){

        ProgressDialog.dismissProgressDialog()
        if(testAvailabilityModel == null){
            //SHOW WENT WRONG DIALOG

            availableTestFragment?.context?.let { OkAlertDialog.initOkAlert(it)?.setOnClickListener { OkAlertDialog.dismissOkAlert() } }
            OkAlertDialog.showOkAlert(availableTestFragment?.activity?.resources?.getString(R.string.went_wrong))
        }
        else
        {
            if(testAvailabilityModel.error == null){
                //CHECK FOR STATUS
                if(testAvailabilityModel?.status_code?.toInt() == 200){
                    if(testAvailabilityModel?.data?.isTest_available!!){
                        availableTestFragment?.takeTestClicked(availableTestModel)
                    }
                    else{
                        availableTestFragment?.context?.let { OkAlertDialog.initOkAlert(it)?.setOnClickListener { OkAlertDialog.dismissOkAlert() } }
                        OkAlertDialog.showOkAlert(testAvailabilityModel?.message)
                    }
                }
                else{
                    availableTestFragment?.context?.let { OkAlertDialog.initOkAlert(it)?.setOnClickListener { OkAlertDialog.dismissOkAlert() } }
                    OkAlertDialog.showOkAlert(testAvailabilityModel?.message)
                }
            }
            else{
                //CHECK FOR ERROR MSG
                availableTestFragment?.context?.let { OkAlertDialog.initOkAlert(it)?.setOnClickListener { OkAlertDialog.dismissOkAlert() } }
                OkAlertDialog.showOkAlert(availableTestFragment?.activity?.resources?.getString(R.string.went_wrong))

            }
        }
    }

    override fun onSuccessCheckUseTestAvailability(testAvailabilityModel: TestAvailabilityModel?,availableTestModel: AvailableTestModel?) {

        Log.e(TAG,"onSuccessCheckUseTestAvailability model "+availableTestModel)
        responseHandlingOfCheckUseTestAvailability(testAvailabilityModel,availableTestModel)
    }

    override fun onFailureCheckUseTestAvailability(testAvailabilityModel: TestAvailabilityModel?,availableTestModel: AvailableTestModel?) {
        responseHandlingOfCheckUseTestAvailability(testAvailabilityModel,availableTestModel)
    }


}