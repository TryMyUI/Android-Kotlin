package com.mahesch.trymyui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.TabActivity.Companion.isPendingTest
import com.mahesch.trymyui.adapter.PerformedTestListAdapter
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.model.PerformedTestModel

/**
 * A simple [Fragment] subclass.
 */
class PerformedTestFragment(activity: Activity, performedTestList: ArrayList<PerformedTestModel>,performedTestPages: Int) : Fragment() {

    private var sharedPrefHelper : SharedPrefHelper = SharedPrefHelper(activity)

    private lateinit var recyclerView : RecyclerView
    private lateinit var performedTestListAdapter : PerformedTestListAdapter

    private var page = 0
    private var performedTestPages = 0
    private lateinit var llLoadingScreen : LinearLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tvNoTestMessage: TextView

    private var performedTestList = performedTestList

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        var  view : View? = null

        if(performedTestList != null && performedTestList.size > 0){
            view = inflater.inflate(R.layout.performed_test_fragment,container,false)
        }
        else{
            if(activity != null){
                view = LayoutInflater.from(activity)?.inflate(R.layout.no_test_to_display,container,false)!!
            }
            else
            {
                view = inflater.inflate(R.layout.no_test_to_display,container,false)
            }
        }

        if(view != null){
            initViews(view)
        }

        return view
    }


    private fun initViews(view: View){

            if(performedTestList != null){
                if(performedTestList.size >0){

                    recyclerView = view.findViewById<RecyclerView>(R.id.card_recycler_view)
                    llLoadingScreen = view.findViewById<LinearLayout>(R.id.linearlayout_loading_screen)
                    swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

                    swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                                    android.R.color.holo_green_light,
                                    android.R.color.holo_orange_light,
                                    android.R.color.holo_red_light)

                    swipeRefreshLayout.setOnRefreshListener {
                        //GET DATA FOR WEB SERVICE
                    }

                    var layoutManager = LinearLayoutManager(activity)
                    recyclerView.layoutManager = layoutManager

/*
                    performedTestListAdapter = PerformedTestListAdapter(activity,performedTestList,recyclerView)

                    recyclerView.adapter =
*/

                }
                else{
                   var textViewNoTestMessage =
                        view.findViewById<View>(R.id.textview_no_test_message) as TextView
                    if (isPendingTest) {
                        textViewNoTestMessage.setText(
                            activity!!.resources
                                .getString(R.string.no_test_performed_toshow_in_pending_test)
                        )
                    } else {
                        textViewNoTestMessage.setText(
                            activity!!.resources
                                .getString(R.string.no_tests_completed_message)
                        )
                    }
                }
            }

    }



}
