package com.mahesch.trymyui.model

import com.google.gson.annotations.SerializedName

class CommonModel {

    @SerializedName("status_code")
     var statusCode = 0
     var message: String? = null
     var data: String? = null
    var error: Throwable? = null



}