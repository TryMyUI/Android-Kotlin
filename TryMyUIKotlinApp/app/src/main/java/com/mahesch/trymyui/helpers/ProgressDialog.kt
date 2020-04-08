package com.mahesch.trymyui.helpers

import android.app.ProgressDialog
import android.content.Context
import com.mahesch.trymyui.R

class ProgressDialog {



    companion object{
        var progressDialog : ProgressDialog? = null

        fun initializeProgressDialogue(context: Context) : ProgressDialog {

            progressDialog = ProgressDialog(context)
            progressDialog?.setMessage(context.resources.getString(R.string.loading_progress_dialog))
            progressDialog?.setCancelable(false)

            return progressDialog!!

        }

        fun showProgressDialog(){
            if(progressDialog != null)
            {
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