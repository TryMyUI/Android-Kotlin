package com.mahesch.trymyui.helpers

import android.content.Context
import com.mahesch.trymyui.R

class ShowInternetAlert {

    companion object{

        fun showInternetAlert(context: Context){
            var btn = OkAlertDialog.initOkAlert(context)
            OkAlertDialog.showOkAlert(context.resources.getString(R.string.internet_check))
            btn?.setOnClickListener({OkAlertDialog.dismissOkAlert()})
        }

    }


}