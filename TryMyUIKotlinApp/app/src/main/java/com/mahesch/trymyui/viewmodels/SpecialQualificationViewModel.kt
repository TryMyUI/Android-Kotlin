package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.SpecialTechnicalModel
import com.mahesch.trymyui.repository.SpecialQualificationRepository

class SpecialQualificationViewModel(application: Application) : AndroidViewModel(application) {

   var specialQualificationRepository = SpecialQualificationRepository(application)


    fun postSpecialResponse(token: String,testId: String) : MutableLiveData<SpecialTechnicalModel> {
        return specialQualificationRepository.postSpecCriteriaResponseMutableData(token,testId)
    }
}