package com.mahesch.trymyui.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.blogc.android.views.ExpandableTextView
import com.mahesch.trymyui.R
import com.mahesch.trymyui.model.PerformedTestModel
import java.util.*

class PerformedTestListAdapter(activity: FragmentActivity,
                               performTestConstant: ArrayList<PerformedTestModel>,
                               recyclerView: RecyclerView) :
    RecyclerView.Adapter<PerformedTestListAdapter.ViewHolder>() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    val VIEW_TYPE_ITEM = 0
    val VIEW_TYPE_LOADING = 1
    var mOnLoadMoreListener: OnLoadMoreListener? = null
    private lateinit var activity: FragmentActivity
    private lateinit var performTestConstants: ArrayList<PerformedTestModel>
    var isLoading = false
    val visibleThreshold = 1
    var lastVisibleItem = 0
    private  var totalItemCount:Int = 0

   init {
        this.performTestConstants = performTestConstant
        this.activity = activity
        linearLayoutManager = recyclerView.getLayoutManager() as LinearLayoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = linearLayoutManager.getItemCount()
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (!isLoading && totalItemCount == lastVisibleItem + visibleThreshold) {
                    mOnLoadMoreListener?.onLoadMore()
                    isLoading = true
                }
            }
        })


    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener?) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        if (performTestConstants.size > 0) {
            val performTestConstant: PerformedTestModel = performTestConstants[i]
            viewHolder.tv_testid!!.text = activity.resources.getString(R.string.test_id)
                .toString() + "" + performTestConstant.mid
            if (performTestConstant.interface_type.equals("web",true)) {
                viewHolder.tv_test_type!!.text =
                    activity.resources.getString(R.string.mobilewebsitetest)
            } else {
                viewHolder.tv_test_type!!.text =
                    activity.resources.getString(R.string.mobileapptest)
            }


            // viewHolder.tv_test_scenario.setText(Html.fromHtml(activity.getResources().getString(R.string.lorem_ipsum)));
            viewHolder.tv_test_scenario!!.text = Html.fromHtml(
                performTestConstants[i].mscenario
            )
            // set animation duration via code, but preferable in your layout-sw480dp files by using the animation_duration attribute
            viewHolder.tv_test_scenario!!.setAnimationDuration(1000L)

            // set interpolators for both expanding and collapsing animations
            viewHolder.tv_test_scenario!!.setInterpolator(OvershootInterpolator())

            // or set them separately
            viewHolder.tv_test_scenario!!.expandInterpolator = OvershootInterpolator()
            viewHolder.tv_test_scenario!!.collapseInterpolator = OvershootInterpolator()
            viewHolder.tv_test_scenario!!.post { // Use lineCount here
                val lineCount = viewHolder.tv_test_scenario!!.lineCount
                if (lineCount > 2) {
                    viewHolder.tv_expandable_toggle!!.visibility = View.VISIBLE
                    viewHolder.tv_test_scenario!!.maxLines = 2
                } else {
                    viewHolder.tv_expandable_toggle!!.visibility = View.INVISIBLE
                }
            }
            viewHolder.tv_expandable_toggle!!.setOnClickListener {
                if (viewHolder.tv_test_scenario!!.isExpanded) {
                    viewHolder.tv_test_scenario!!.collapse()
                    viewHolder.tv_expandable_toggle!!.text = "Show more"
                } else {
                    viewHolder.tv_test_scenario!!.expand()
                    viewHolder.tv_expandable_toggle!!.text = "View less"
                }
            }
            var status: String = performTestConstant.mstatus.toUpperCase()
            var rID = R.drawable.pending_icon
            when (status) {
                "ACCEPTED" -> {
                    status = "ACCEPTED"
                    rID = R.drawable.accepted_icon
                    viewHolder.iv_test_status!!.setImageDrawable(activity.getDrawable(R.drawable.ic_accepted))
                    viewHolder.tv_test_status!!.text =
                        activity.resources.getString(R.string.accpeted)
                }
                "PENDING" -> {
                    status = "PENDING"
                    rID = R.drawable.pending_icon
                    viewHolder.iv_test_status!!.setImageDrawable(activity.getDrawable(R.drawable.ic_pending))
                    viewHolder.tv_test_status!!.text =
                        activity.resources.getString(R.string.pending)
                }
                "PAID" -> {
                    status = "PAID"
                    rID = R.drawable.paid_icon
                    viewHolder.iv_test_status!!.setImageDrawable(activity.getDrawable(R.drawable.ic_accepted))
                    viewHolder.tv_test_status!!.text = activity.resources.getString(R.string.paid)
                }
                "REJECTED" -> {
                    status = "REJECTED"
                    rID = R.drawable.rejected_icon
                    viewHolder.iv_test_status!!.setImageDrawable(activity.getDrawable(R.drawable.ic_rejected))
                    viewHolder.tv_test_status!!.text =
                        activity.resources.getString(R.string.rejected)
                }
            }
            val size = dpToPx(30)
            val original = BitmapFactory.decodeResource(activity.resources, rID)
            val b = Bitmap.createScaledBitmap(original, size, size, false)
            val d: Drawable = BitmapDrawable(activity.resources, b)
        } else {
            //TODO no test to display
        }
    }

    fun dpToPx(dp: Int): Int {
        val displayMetrics: DisplayMetrics = activity.getResources().getDisplayMetrics()
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }


    override fun getItemViewType(position: Int): Int {
        return if (performTestConstants!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }


    override fun getItemCount(): Int {
        return performTestConstants?.size ?: 0
    }

    fun setLoaded() {
        isLoading = false
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rl_testtyp_testid: RelativeLayout? = null
        var tv_test_type: TextView? = null
        var tv_testid: TextView? = null
        var tv_test_scenario: ExpandableTextView? = null
        var iv_test_status: ImageView? = null
        var tv_test_status: TextView? = null
        var tv_expandable_toggle: TextView? = null

        init {
            rl_testtyp_testid = view.findViewById<View>(R.id.rl_testtyp_testid) as RelativeLayout
            tv_test_type = view.findViewById<View>(R.id.tv_test_type) as TextView
            tv_testid = view.findViewById<View>(R.id.tv_testid) as TextView
            tv_test_scenario = view.findViewById<View>(R.id.tv_test_scenario) as ExpandableTextView
            iv_test_status = view.findViewById<View>(R.id.iv_test_status) as ImageView
            tv_test_status = view.findViewById<View>(R.id.tv_test_status) as TextView
            tv_expandable_toggle = view.findViewById<View>(R.id.tv_expandable_toggle) as TextView
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.completed_tests_row, parent, false)
        return ViewHolder(view)
    }
}