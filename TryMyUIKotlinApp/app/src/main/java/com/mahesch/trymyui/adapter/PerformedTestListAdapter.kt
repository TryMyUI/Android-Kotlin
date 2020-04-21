package com.mahesch.trymyui.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        if (performTestConstants!!.size > 0) {
            val performTestConstant: PerformedTestModel? = performTestConstants[i]
            viewHolder.TestName.setText(performTestConstant?.title_with_id.toString() + "")
            viewHolder.Testurl.setText(performTestConstant?.murl.toString() + "")
            viewHolder.TestID.setText("#" + performTestConstant?.mid.toString() + "")
            if (performTestConstant?.mcomment == null) {
                viewHolder.TestComment.setText("No comment")
            } else {
                viewHolder.TestComment.setText(performTestConstant?.mcomment.toString() + "")
            }
            viewHolder.TestScenario.setText(performTestConstant?.mscenario.toString() + "")
            val start_rate: Int = performTestConstant?.mscore!!.toInt()
            if (start_rate > 0) {
                viewHolder.ratingBar.setVisibility(View.VISIBLE)
                viewHolder.ratingBar.setRating(start_rate.toFloat())
            } else {
                viewHolder.ratingBar.setVisibility(View.VISIBLE)
                viewHolder.ratingBar.setRating(0f)
            }
            var status: String = performTestConstant.mstatus.toUpperCase()
            var rID: Int = R.drawable.pending_icon
            when (status) {
                "ACCEPTED" -> {
                    status = "ACCEPTED"
                    rID = R.drawable.accepted_icon
                    viewHolder.linearLayoutFeedback.setVisibility(View.VISIBLE)
                }
                "PENDING" -> {
                    status = "PENDING"
                    rID = R.drawable.pending_icon
                    viewHolder.linearLayoutFeedback.setVisibility(View.GONE)
                }
                "PAID" -> {
                    status = "PAID"
                    rID = R.drawable.paid_icon
                    viewHolder.linearLayoutFeedback.setVisibility(View.VISIBLE)
                }
                "REJECTED" -> {
                    status = "REJECTED"
                    rID = R.drawable.rejected_icon
                    viewHolder.linearLayoutFeedback.setVisibility(View.VISIBLE)
                }
                "VOIDED" -> {
                    status = "CANCELED"
                    rID = R.drawable.cancel_icon
                    viewHolder.linearLayoutFeedback.setVisibility(View.GONE)
                }
                else -> {
                    status = "UNKNOWN"
                    viewHolder.linearLayoutFeedback.setVisibility(View.GONE)
                }
            }
            val size: Int = dpToPx(30)
            val original = BitmapFactory.decodeResource(activity.getResources(), rID)
            val b = Bitmap.createScaledBitmap(original, size, size, false)
            val d: Drawable = BitmapDrawable(activity.getResources(), b)
            viewHolder.TestStaus.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
            viewHolder.TestStaus.setText(status + "")
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
        var ratingBar: RatingBar
        var TestName: TextView
        var TestID: TextView
        var Testurl: TextView
        var TestScenario: TextView
        var TestStaus: TextView
        var TestComment: TextView
        var linearLayoutFeedback: LinearLayout

        init {
            TestName = view.findViewById<View>(R.id.textViewTitle) as TextView
            TestID = view.findViewById<View>(R.id.textViewTestID) as TextView
            Testurl = view.findViewById<View>(R.id.textviewurl) as TextView
            TestScenario = view.findViewById<View>(R.id.textViewScenario) as TextView
            TestStaus = view.findViewById<View>(R.id.textViewStatus) as TextView
            TestComment = view.findViewById<View>(R.id.good_comment) as TextView
            ratingBar = view.findViewById<View>(R.id.ratingBar) as RatingBar
            linearLayoutFeedback =
                view.findViewById<View>(R.id.linearlayoutfeedback_report) as LinearLayout
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.performed_test_card_row, parent, false)
        return ViewHolder(view)
    }
}