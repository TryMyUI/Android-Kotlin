package com.mahesch.trymyui.viewmodels

import com.mahesch.trymyui.repository.TechnicalQualificationRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.SpecialTechnicalModel

class TechnicalQualificationViewModel(application: Application) : AndroidViewModel(application) {

    var technicalQualificationRepository = TechnicalQualificationRepository(application)


    fun postTechnicalResponse(token: String,testId: String) : MutableLiveData<SpecialTechnicalModel> {
        return technicalQualificationRepository.postTechCriteriaResponseMutableData(token,testId)
    }
}