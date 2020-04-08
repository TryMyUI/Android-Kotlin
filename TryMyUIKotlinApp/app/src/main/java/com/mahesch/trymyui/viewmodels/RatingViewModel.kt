package com.mahesch.trymyui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.CommonModel
import com.mahesch.trymyui.repository.RatingRepository

class RatingViewModel(application: Application) : AndroidViewModel(application) {

    var ratingRepository : RatingRepository = RatingRepository(application)




    fun submitRateForWorker(token : String?,rating : Int) : MutableLiveData<CommonModel> {
        return ratingRepository.ratingMutableDataForWorker(token,rating)
    }

    fun submitRateForGuest(email:  String?,rating: Int) : MutableLiveData<CommonModel>{
        return ratingRepository.ratingMutableDataForGuest(email,rating)
    }


}