package com.mahesch.trymyui.adapter

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import at.blogc.android.views.ExpandableTextView
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.TabActivity
import com.mahesch.trymyui.activity.VideoPlayerActivity
import com.mahesch.trymyui.fragment.AvailableTestFragment
import com.mahesch.trymyui.helpers.*
import com.mahesch.trymyui.model.AvailableTestModel
import com.mahesch.trymyui.repository.CheckUseTestAvailabilityRepository
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import com.seattleapplab.trymyui.models.TestAvailabilityModel
import kotlinx.android.synthetic.main.show_gesture_dialog.*


class AvailableTestListAdapter(availableTestModelList: ArrayList<AvailableTestModel>,availableTestFragment: AvailableTestFragment) :
    RecyclerView.Adapter<AvailableTestListAdapter.ViewHolder>(),
    CheckUseTestAvailabilityRepository.ICheckUseTestAvailability{


    private var availableTestFragment: AvailableTestFragment? = null
    private var mSharedPrefHelper: SharedPrefHelper? = null
    private var userType: String? = null
    private var availableTestList: ArrayList<AvailableTestModel>? = null

    private val TAG: String = com.mahesch.trymyui.adapter.AvailableTestListAdapter::class.java.getSimpleName().toUpperCase()

    init
    {
        this.availableTestList = availableTestModelList
        this.availableTestFragment = availableTestFragment

        if(availableTestFragment.activity != null){
            if(!availableTestFragment.activity!!.isFinishing){
                mSharedPrefHelper = SharedPrefHelper(availableTestFragment?.activity!!)
                userType = mSharedPrefHelper?.getUserType()
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var container: ConstraintLayout? = null
        var demo_video_container:ConstraintLayout? = null
        var TestTitle: TextView? = null
        var TestURL:TextView? = null
        var buttonToggle:TextView? = null
        var TestID:TextView? = null
        var expandableTextView: ExpandableTextView? = null
        var button_take_test: AppCompatButton? = null
        var button_watch_video:AppCompatButton? = null


        init {
            TestTitle = view.findViewById<View>(R.id.textViewTitle) as TextView
            expandableTextView =
                view.findViewById<View>(R.id.expandableTextView) as ExpandableTextView
            buttonToggle = view.findViewById<View>(R.id.button_toggle) as TextView
            TestURL = view.findViewById<View>(R.id.textViewURL) as TextView
            TestID = view.findViewById<View>(R.id.textViewTestID) as TextView
            button_take_test =
                view.findViewById<View>(R.id.button_take_test) as AppCompatButton
            container = view.findViewById<View>(R.id.container) as ConstraintLayout
            demo_video_container =
                view.findViewById<View>(R.id.demo_video_container) as ConstraintLayout
            button_watch_video =
                view.findViewById<View>(R.id.button_watch_video) as AppCompatButton

            // set animation duration via code, but preferable in your layout-sw480dp files by using the animation_duration attribute
            expandableTextView!!.setAnimationDuration(1000L)

            // set interpolators for both expanding and collapsing animations
            expandableTextView!!.setInterpolator(OvershootInterpolator())

            // or set them separately
            expandableTextView!!.setExpandInterpolator(OvershootInterpolator())
            expandableTextView!!.setCollapseInterpolator(OvershootInterpolator())
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.available_test_list_row, parent, false)

        return AvailableTestListAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return availableTestList!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        if(availableTestList?.get(i)?.id == 0)
        {
            setVideoContainerVisible(viewHolder)
        }
        else
        {
            setVideoContainerGone(viewHolder)
        }

        commonParametersForGuestWorkerCustomers(viewHolder,availableTestList?.get(i))

        // FOR PENDING TEST (MEANS ONLY WORKER COZ GUEST AND CUSTOMER DOESN'T HAS PENDING STATE)
        if(TabActivity.isPendingTest && userType.equals(mSharedPrefHelper?.UserTypeWorker)){

            displayPendingRow(viewHolder,availableTestList?.get(i))
        }
        else if(mSharedPrefHelper!!.getGuestTester()){
            displayGuestRows(viewHolder,availableTestList?.get(i))
        }
        else if(userType.equals(mSharedPrefHelper?.UserTypeWorker,true)){
            displayWorkerRows(viewHolder,availableTestList?.get(i))
        }
        else //USER IS CUSTOMER
        {
            displayCustomerRows(viewHolder,availableTestList?.get(i))
        }



    }


    interface TakeTestClickedListener{

        fun takeTestClicked(availableTestModel: AvailableTestModel?)
    }


    private fun setVideoContainerVisible(viewHolder: ViewHolder){
        viewHolder.container?.visibility = View.GONE
        viewHolder.demo_video_container?.visibility = View.VISIBLE

        viewHolder.button_watch_video?.setOnClickListener(View.OnClickListener {

            availableTestFragment!!.activity!!.startActivity(Intent(availableTestFragment!!.activity, VideoPlayerActivity::class.java))

            availableTestFragment!!.activity!!.finish()
        })
    }

    private fun setVideoContainerGone(viewHolder: ViewHolder){
        viewHolder.container!!.visibility = View.VISIBLE
        viewHolder.demo_video_container!!.visibility = View.GONE
    }

    private fun commonParametersForGuestWorkerCustomers(viewHolder: ViewHolder,availableTestModel: AvailableTestModel?){

        if (availableTestModel?.interface_type.equals("web",true)) {
            viewHolder.TestTitle!!.text = "Mobile website test"
        } else {
            viewHolder.TestTitle!!.text = "Mobile app test"
        }

        //  viewHolder.TestTitle.setText(availableTestConstants.get(i).getTitle());
        viewHolder.expandableTextView!!.text = Html.fromHtml(availableTestModel?.scenario)

        viewHolder.expandableTextView!!.post { // Use lineCount here
            val lineCount = viewHolder.expandableTextView!!.lineCount
            if (lineCount > 2) {
                Log.e(TAG, "Post")
                viewHolder.buttonToggle!!.visibility = View.VISIBLE
                viewHolder.expandableTextView!!.maxLines = 2
            } else {
                viewHolder.buttonToggle!!.visibility = View.INVISIBLE
            }
        }

        viewHolder.TestURL?.text = ""+availableTestModel?.url

        viewHolder.TestID?.text = "" + availableTestModel?.id
    }

    private fun displayPendingRow(viewHolder: ViewHolder,availableTestModel: AvailableTestModel?){
        viewHolder.button_take_test?.text = "PROCEED TO SURVEY"
        viewHolder.button_take_test?.isClickable = true

        viewHolder.button_take_test!!.setOnClickListener {

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

    private fun displayGuestRows(viewHolder: ViewHolder,availableTestModel: AvailableTestModel?){

        viewHolder.button_take_test?.isClickable = true

        var testerPlatform = availableTestModel?.tester_platform

        viewHolder.button_take_test?.setOnClickListener{

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

        viewHolder.button_take_test?.isClickable = true
        viewHolder.button_take_test!!.setBackgroundResource(R.drawable.rounded_text_box_with_border)

        viewHolder.button_take_test?.setOnClickListener{
            showGestureDialog(availableTestModel)
        }
    }

    private fun displayCustomerRows(viewHolder: ViewHolder,availableTestModel: AvailableTestModel?){

        viewHolder.button_take_test?.isClickable = true

        var testerPlatform = availableTestModel?.tester_platform

        if(Utils.patternMatchForAndroid(testerPlatform)){
            viewHolder.button_take_test!!.setBackgroundResource(R.drawable.rounded_text_box_with_border)
        }else{
            viewHolder.button_take_test!!.setBackgroundResource(R.drawable.rounded_gray_filled)
        }

        viewHolder.button_take_test?.setOnClickListener{

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
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

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
        responseHandlingOfCheckUseTestAvailability(testAvailabilityModel,availableTestModel)
    }

    override fun onFailureCheckUseTestAvailability(testAvailabilityModel: TestAvailabilityModel?,availableTestModel: AvailableTestModel?) {
        responseHandlingOfCheckUseTestAvailability(testAvailabilityModel,availableTestModel)
    }


}