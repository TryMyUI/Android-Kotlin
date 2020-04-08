package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.FeedbackModel
import com.mahesch.trymyui.repository.ForgotPasswordRepository

class ForgotPasswordActivityViewModel(application: Application) : AndroidViewModel(application) {

    var forgotPasswordRepository : ForgotPasswordRepository? = null

    init {
        forgotPasswordRepository = ForgotPasswordRepository(application)
    }

    fun callForgotPassword(email: String): MutableLiveData<FeedbackModel>?{
        return forgotPasswordRepository?.forgotPasswordMutableData(email)
    }
}