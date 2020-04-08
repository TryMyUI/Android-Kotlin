package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.repository.SusSubmitRepository

class SusActivityViewModel(application: Application) : AndroidViewModel(application) {

    var susSubmitRepository = SusSubmitRepository(application)


    fun submitSUS(jsonRequestString : JsonObject) : MutableLiveData<CommonModel> {
        return susSubmitRepository.susSubmitMutableData(jsonRequestString)
    }

}