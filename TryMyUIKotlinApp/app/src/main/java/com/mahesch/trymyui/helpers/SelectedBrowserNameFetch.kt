package com.mahesch.trymyui.helpers

import android.content.Context
import android.content.Intent
import android.util.Log
import com.mahesch.trymyui.R
import org.jsoup.helper.StringUtil
import java.util.*
import java.util.regex.Pattern

class SelectedBrowserNameFetch(intent: Intent,context: Context) {

    private var intent: Intent? = intent
    private var context: Context? = context

    fun setSelectedBrowserName(){
        var selectedAppPackage = intent!!.extras[Intent.EXTRA_CHOSEN_COMPONENT].toString()
        Log.e("IntentReceiver", "selectedAppPackage $selectedAppPackage")

        val requiredString = selectedAppPackage.substring(selectedAppPackage.indexOf(".") + 1,
            selectedAppPackage.indexOf("/"))
        Log.e("IntentReceiver", requiredString)

        selectedAppPackage = requiredString

        var browser_list = context!!.resources.getStringArray(R.array.browser_list)

        var browser_list_string = ArrayList<String>()

        for (i in browser_list.indices) {
            browser_list_string.add(browser_list[i])
        }

        val patternString = "\\b(" + StringUtil.join(browser_list_string, "|").toString() + ")\\b"
        val pattern = Pattern.compile(patternString)
        val matcher = pattern.matcher(selectedAppPackage.toLowerCase())
        Log.e("IntentReceiver", "Pattern string $patternString")

        if (matcher.find()) {
            Log.e("IntentReceiver", "Found")
            Log.e("IntentReceiver", matcher.group(0))
            ApplicationClass.selectedBrowserName = matcher.group(0)
        } else {
            Log.e("IntentReceiver", "No match found")
            selectedAppPackage = selectedAppPackage.replace(".", " ")
            ApplicationClass.selectedBrowserName = selectedAppPackage
        }
    }

}