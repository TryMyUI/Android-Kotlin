/*
package com.mahesch.trymyui.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.mahesch.trymyui.helpers.SharedPrefHelper

class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    val TAG = MyFirebaseInstanceIDService::class.java.simpleName.toUpperCase()
    val REGISTRATIONCOMPLETE = "REGISTRATION COMPLETE"
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        sharedPrefHelper = SharedPrefHelper(MyFirebaseInstanceIDService@this)

        var registrationID = FirebaseInstanceId.getInstance().token
        Log.e(TAG,"Firebase Token "+registrationID)
    }
}*/
