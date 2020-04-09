package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.repository.WrittenSummaryRepository

class WrittenSummaryActivityViewModel (application: Application) : AndroidViewModel(application) {

    var writtenSummaryRepository = WrittenSummaryRepository(application)

    fun submitWrittenSummaryAnswer(jsonRequestString : JsonObject) : MutableLiveData<CommonModel> {
        return writtenSummaryRepository.submitWrittenSummary(jsonRequestString)
    }

}