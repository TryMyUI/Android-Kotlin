package com.seattleapplab.trymyui.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TestAvailabilityModel {
    @SerializedName("status_code")
    @Expose
    var status_code: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    var error: Throwable? = null

    inner class Data {
        @SerializedName("test_available")
        @Expose
        var isTest_available = false

    }
}