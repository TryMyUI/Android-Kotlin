package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.repository.NpsQuestionRepository
import com.mahesch.trymyui.repository.UxCrowdRepository
import com.mahesch.trymyui.repository.UxCrowdVotingRepository

class UxCrowdBadVotingViewModel (application: Application) : AndroidViewModel(application) {

    private var uxCrowdVotingRepository : UxCrowdVotingRepository = UxCrowdVotingRepository(application)

    fun submitUxCrowdBadVoting(jsonRequestString : JsonObject) : MutableLiveData<CommonModel> {
        return uxCrowdVotingRepository.submitUxCrowdBadVotingMutableData(jsonRequestString)
    }

}