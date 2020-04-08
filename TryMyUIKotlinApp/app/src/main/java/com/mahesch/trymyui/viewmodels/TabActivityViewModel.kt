package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.repository.DashboardRepository
import com.mahesch.trymyui.repository.MobileUpdateRequiredRepository
import com.seattleapplab.trymyui.models.Tests

class TabActivityViewModel(application : Application) : AndroidViewModel(application) {

    private var dashboardRepository : DashboardRepository = DashboardRepository(application)
    private var mobileUpdateRequiredRepository: MobileUpdateRequiredRepository = MobileUpdateRequiredRepository(application)

    fun callWorkerDashBoardData(token: String?, device: String) : MutableLiveData<Tests>?{
        return dashboardRepository?.workerDashBoardMutableData(token, device)
    }

    fun callCustomerDashBoardData(token: String?,device: String) : MutableLiveData<Tests>?{
        return dashboardRepository?.customerDashBoardMutableData(token,device)
    }

    fun callMobileUpdateRequired(version: String,device: String) : MutableLiveData<LoginResponseModel>{
        return mobileUpdateRequiredRepository.mobileUpdateRequiredMutableData(version,device)
    }

}