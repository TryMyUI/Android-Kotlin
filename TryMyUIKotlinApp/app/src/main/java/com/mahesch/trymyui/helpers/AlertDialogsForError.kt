package com.mahesch.trymyui.helpers

import android.content.Context

class AlertDialogsForError {

    companion object{

        var alertDialog1 : androidx.appcompat.app.AlertDialog? = null

        fun initDialog(context: Context) : Unit {

            var  alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
            var dialog = alertDialogBuilder.create()

            alertDialog1 = dialog
        }

        fun dismissDialog(context: Context) : Unit{
            if(alertDialog1 != null && alertDialog1!!.isShowing)
                alertDialog1?.dismiss()
        }


        fun showOkDialog(context: Context, message: String?) : Unit {

           if(alertDialog1 != null){
               alertDialog1?.setMessage(message)
               alertDialog1?.show()
           }
        }

        fun showYesNoDialog(context: Context,message: String,buttonText1 : String,buttonText2: String){

        }

    }
}