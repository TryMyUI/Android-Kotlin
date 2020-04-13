package com.mahesch.trymyui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.*
import com.mahesch.trymyui.activity.TabActivity.Companion.isQualified
import com.mahesch.trymyui.activity.TabActivity.Companion.qualification_message
import com.mahesch.trymyui.adapter.AvailableTestListAdapter
import com.mahesch.trymyui.helpers.ManageFlowBeforeRecording
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.helpers.Utils
import com.mahesch.trymyui.model.AvailableTestModel

/**
 * A simple [Fragment] subclass.
 */
class AvailableTestFragment(activity: Activity,availableTestList: ArrayList<AvailableTestModel>) : Fragment(),AvailableTestListAdapter.TakeTestClickedListener {

    private var TAG = AvailableTestFragment::class.java.simpleName

    private lateinit var sharedPrefHelper: SharedPrefHelper
    private var activity: Activity = activity
    private lateinit var recyclerViewAdapter : RecyclerView.Adapter<AvailableTestListAdapter.ViewHolder>

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var rlWithRefreshButton: RelativeLayout
    private lateinit var ivRefreshButton: ImageView
    private lateinit var tvNoTestMessage: TextView

    private var availableTestList = availableTestList

        init {
            sharedPrefHelper = SharedPrefHelper(activity)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.available_test_fragment, container, false)

        initViews(view)

        return view
    }

    private fun initViews(view: View){

        recyclerView = view.findViewById<RecyclerView>(R.id.card_recycler_view)
        recyclerView.setHasFixedSize(true)

        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        rlWithRefreshButton = view.findViewById<RelativeLayout>(R.id.relativelayout_with_refresh_button)
        ivRefreshButton = view.findViewById<ImageView>(R.id.refresh_button)
        tvNoTestMessage = view.findViewById<TextView>(R.id.textview_no_test_message)
        tvNoTestMessage.text = resources.getString(R.string.no_tests_available_message)

        view.findViewById<TextView>(R.id.tv_lbl_how_test).setOnClickListener { startActivity(Intent(activity,VideoPlayerActivity::class.java)) }

        var layoutManager = LinearLayoutManager(activity.applicationContext)

        recyclerView.layoutManager = layoutManager

        swipeRefreshLayout.setOnRefreshListener {  }

        if(availableTestList != null && availableTestList.size>0){

            rlWithRefreshButton.visibility = View.GONE
            swipeRefreshLayout.visibility = View.VISIBLE

            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                            android.R.color.holo_green_light,
                            android.R.color.holo_orange_light,
                            android.R.color.holo_red_light)

            recyclerViewAdapter = AvailableTestListAdapter(availableTestList,this)
            recyclerView.adapter = recyclerViewAdapter

            recyclerView.isClickable = false


        }
        else{
            rlWithRefreshButton.visibility = View.VISIBLE
            swipeRefreshLayout.visibility = View.GONE

            if(!isQualified){
                tvNoTestMessage.text = qualification_message
            }

            ivRefreshButton.setOnClickListener {
                if(sharedPrefHelper.getGuestTester()){
                    swipeRefreshLayout.isRefreshing = false
                }else{

                    if(sharedPrefHelper.getUserType().equals(sharedPrefHelper.UserTypeWorker)){
                        //GET WORKER DATA
                    }
                    else{
                        //GET CUSTOMER DATA
                    }
                }

            }
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun takeTestClicked(availableTestModel: AvailableTestModel?) {

        Log.e(TAG,"takeTestClicked")

       // ManageFlowBeforeRecording(availableTestModel,activity).moveToWhichActivity(0)
        temporaryFlow(availableTestModel)

    }

    private fun moveToNextActivityAndFinishCurrent(){
        var intent = Intent(activity, PerformTestActivity::class.java)
        startActivity(intent)
        activity.finish()
    }

     fun temporaryFlow(availableTestModel: AvailableTestModel?){

        Log.e(TAG, "availableTestModel $availableTestModel")

        Log.e(TAG,"title "+availableTestModel?.title)

        SharedPrefHelper(activity).saveTestResultId("309459")

        var intent = Intent(activity,SusQuestionActivity::class.java)
        intent.putExtra("susQuestion",availableTestModel?.susQuestion)
        intent.putExtra("availableTestConstants",availableTestModel)
        startActivity(intent)
        activity.finish()
    }

}
