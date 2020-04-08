package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.repository.NpsQuestionRepository

class NpsActivityViewModel(application: Application) : AndroidViewModel(application) {

    var npsQuestionRepository : NpsQuestionRepository = NpsQuestionRepository(application)


    fun submitNPS(jsonRequestString : JsonObject) : MutableLiveData<CommonModel>{
        return npsQuestionRepository.npsQuestionSubmitMutableData(jsonRequestString)
    }


}