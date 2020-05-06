package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.repository.GuestLoginRepository
import com.seattleapplab.trymyui.models.Tests

class GuestActivityViewModel(application: Application) : AndroidViewModel(application) {

    var guestLoginRepository : GuestLoginRepository? = null
    var mutableLiveData : MutableLiveData<Tests>? = MutableLiveData<Tests>()

    init {
        guestLoginRepository = GuestLoginRepository(application)
    }


    fun callGuestLogin(name: String,email: String,testId: String) {

        mutableLiveData = guestLoginRepository?.guestLoginMutableData(name,email,testId,mutableLiveData)


    }
}