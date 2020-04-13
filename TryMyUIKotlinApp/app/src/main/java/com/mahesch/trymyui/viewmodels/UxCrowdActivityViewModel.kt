package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.repository.NpsQuestionRepository
import com.mahesch.trymyui.repository.UxCrowdRepository

class UxCrowdActivityViewModel (application: Application) : AndroidViewModel(application) {

    var uxCrowdRepository : UxCrowdRepository = UxCrowdRepository(application)


    fun submitUxCrowd(jsonRequestString : JsonObject) : MutableLiveData<CommonModel> {
        return uxCrowdRepository.submitUxCrowdMutableData(jsonRequestString)
    }
}