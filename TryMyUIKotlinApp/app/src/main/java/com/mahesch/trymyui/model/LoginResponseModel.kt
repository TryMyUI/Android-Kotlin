package com.mahesch.trymyui.model

import com.google.gson.annotations.SerializedName


class LoginResponseModel {
    @SerializedName("status_code")
    var statusCode = 0
    var message: String? = null
    var data: Data? = null
    var error: Throwable? = null

    class Data {
        var token: String? = null
        var user_type: String? = null
        var update_required: Boolean? = null
        var test_result_id: String? = null
        var oh_test_id: String? = null

    }
}
