package com.mahesch.trymyui.helpers

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.mahesch.trymyui.R

class YesNoAlertDialog {

    companion object{

        private lateinit var context : Context
        private lateinit var btn_yes: Button
        private lateinit var btn_no: Button
        private lateinit var tv_alert_msg : TextView
        private var dialog : Dialog? = null

        fun initYesNoDialogue(context: Context) : Array<Button>?{

            this.context = context
            dialog = Dialog(context)
            dialog?.setContentView(R.layout.yes_no_alertwindow)
            dialog?.setCancelable(false)


            btn_yes = dialog?.findViewById<Button>(R.id.btn_yes)!!
            btn_no = dialog?.findViewById<Button>(R.id.btn_no)!!
            tv_alert_msg = dialog?.findViewById<TextView>(R.id.tv_alert_message)!!

            return arrayOf(btn_yes!!,btn_no!!)


        }

        fun showYesNoDialogue(title: String,msg: String,positiveButtonText: String,negativeButtonText: String){

            dialog?.setTitle(title)
            tv_alert_msg.text = msg

            btn_yes.text = positiveButtonText
            btn_no.text = negativeButtonText

            if(!(context as Activity).isFinishing)
                dialog?.show()

        }

        fun dismissYesNoDialogue(){

            if(dialog != null)
            dialog?.dismiss()

        }

    }


}