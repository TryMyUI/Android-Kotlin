package com.mahesch.trymyui.helpers

import android.content.Context
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.mahesch.trymyui.R

class OkAlertDialog {

    companion object{

         var alertDialogBuilder1 : AlertDialog.Builder? = null
        var alertDialog1 : AlertDialog? = null
        var context : Context? = null

        fun initOkAlert(context: Context) : Button?{

            val alertDialogBuilder = AlertDialog.Builder(context)

            alertDialogBuilder1 = alertDialogBuilder

            alertDialog1 = alertDialogBuilder1?.create()

            alertDialog1?.setContentView(R.layout.ok_alert_window)

            alertDialog1?.setCancelable(true)

            return alertDialog1?.findViewById(R.id.btn_ok_alert)
        }

        fun showOkAlert(msg: String?){

            alertDialog1?.setMessage(msg)
            alertDialog1?.show()
        }

        fun dismissOkAlert(){
            if(alertDialog1 != null){
                if(alertDialog1!!.isShowing){
                   alertDialog1?.dismiss()
                }
            }
        }
    }
}