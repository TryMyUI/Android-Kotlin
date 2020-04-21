package com.mahesch.trymyui.helpers

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.mahesch.trymyui.R
import com.mahesch.trymyui.activity.TabActivity

class ThumbsUpDialog() {

    companion object{

        private lateinit var context : Context
        private var dialog : Dialog? = null
        private var tv_ok: TextView? = null

        fun initThumbsUpDialogue(context: Context) {

            this.context = context
            dialog = Dialog(context)
            dialog?.setContentView(R.layout.thumbs_up)
            dialog?.setCancelable(false)

            tv_ok = dialog?.findViewById<TextView>(R.id.tv_ok)

        }

        fun showThumbsUpDialogue(){

            if(!(context as Activity).isFinishing)
                dialog?.show()

            tv_ok?.setOnClickListener { dismissThumbsUpDialogue() }

        }

        private fun dismissThumbsUpDialogue(){

            if(dialog != null)
                dialog?.dismiss()

            var intent = Intent(context,TabActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()

        }

    }

}