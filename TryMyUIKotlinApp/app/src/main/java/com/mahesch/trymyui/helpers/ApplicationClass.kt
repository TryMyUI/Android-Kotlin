package com.mahesch.trymyui.helpers

import android.app.Application
import com.mahesch.trymyui.receivers.ConnectivityReceiver

class ApplicationClass : Application() {



    companion object{

        var selectedBrowserName: String? = null
        private var mInstance: ApplicationClass? = null

        var isScreenerVisited = false

        @Synchronized
        fun getInstance(): ApplicationClass? {
            return mInstance
        }
    }



    override fun onCreate() {
        super.onCreate()

        mInstance =  this
    }


    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }
}