package com.mahesch.trymyui.helpers

import android.content.Context
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import com.mahesch.trymyui.R

class YesNoAlertDialog {

    private fun xyz(context: Context){

        var alertDialogBuilder =  AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme_MaterialDialogTheme))
        alertDialogBuilder.setTitle("Alert")
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage(context.resources.getString(R.string.no_special_qualification))

        alertDialogBuilder.setPositiveButton(
            "Yes") { dialog, which ->
            dialog.cancel()
            }

    }
}