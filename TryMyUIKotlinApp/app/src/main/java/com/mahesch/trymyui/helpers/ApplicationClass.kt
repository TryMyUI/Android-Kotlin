package com.mahesch.trymyui.helpers

import android.app.Application
import com.mahesch.trymyui.receivers.ConnectivityReceiver

class ApplicationClass : Application() {

    companion object{

        private val mInstance: ApplicationClass? = null

        @Synchronized
        fun getInstance(): ApplicationClass? {
            return mInstance
        }
    }



    override fun onCreate() {
        super.onCreate()
    }


    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }
}