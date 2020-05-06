package com.mahesch.trymyui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mahesch.trymyui.helpers.SelectedBrowserNameFetch

class BrowserIntentReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val selectedBrowserNameFetch = SelectedBrowserNameFetch(intent!!, context!!)
        selectedBrowserNameFetch.setSelectedBrowserName()
    }
}