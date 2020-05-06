package com.mahesch.trymyui.helpers

import android.app.ProgressDialog
import android.content.Context
import com.mahesch.trymyui.R

class ProgressDialog {



    companion object{
        var progressDialog : ProgressDialog? = null

        fun initializeProgressDialogue(context: Context) : ProgressDialog {

            progressDialog = ProgressDialog(context)
            progressDialog?.setCancelable(false)
            return progressDialog!!

        }

        fun initializeProgressDialogue(context: Context,style: Int): ProgressDialog{
            progressDialog = ProgressDialog(context)
            progressDialog?.setCancelable(false)
            progressDialog?.setProgressStyle(style)
            return progressDialog!!
        }

        fun setProgress(progress: Int){
            progressDialog?.progress = progress
        }

        fun showProgressDialog(){
            if(progressDialog != null)
            {

                progressDialog?.setMessage("Loading...")

                if (!progressDialog?.isShowing!!)
                    progressDialog?.show()
            }

        }

        fun showProgressDialog(message: String){
            if(progressDialog != null)
            {

                progressDialog?.setMessage(message)

                if (!progressDialog?.isShowing!!)
                    progressDialog?.show()
            }

        }

        fun dismissProgressDialog(){


            if(progressDialog != null && progressDialog?.isShowing!!){
                progressDialog?.dismiss()
            }
        }
    }

}