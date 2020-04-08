package com.mahesch.trymyui.model

class SpecialTechnicalModel {

    var status_code = 0
    var message: String? = null
    var data: Data? = null

    var error: Throwable? = null

    class Data{
        private var test_result_id : String? = null
    }

}