package com.mahesch.trymyui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.mahesch.trymyui.helpers.ApplicationClass

class ConnectivityReceiver : BroadcastReceiver() {

    companion object{
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }


    override fun onReceive(context: Context?, intent: Intent?) {

        val connectivityManager = ApplicationClass.getInstance()?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

       val isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener?.onNetworkConnectionChanged(isConnected)
        }
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }
}


