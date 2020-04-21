package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.repository.GetScreenerQuestionRepository
import com.seattleapplab.trymyui.models.ScreenerQuestionModel

class GetSCreenerQuestionViewModel(application: Application) : AndroidViewModel(application) {

    private var getScreenerQuestionRepo: GetScreenerQuestionRepository = GetScreenerQuestionRepository(application)

    fun getScreenerQuestion(sharedPrefHelper: SharedPrefHelper): MutableLiveData<ScreenerQuestionModel> {
        return getScreenerQuestionRepo?.getScreenerQuestionMutableData(sharedPrefHelper)
    }
}