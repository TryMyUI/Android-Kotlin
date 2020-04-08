package com.mahesch.trymyui.helpers

import android.content.Context
import android.content.SharedPreferences

 class SharedPrefHelper( context: Context){

    private val PREFERENCENAME  = "TRY_MY_UI_PREFERENCE"
    private var sharedPreferences : SharedPreferences = context.getSharedPreferences(PREFERENCENAME,Context.MODE_PRIVATE)
    private val TOKEN = "TOKEN"

    private val USERNAME = "USERNAME"
    private val TEST_ID = "TEST_ID"
    private val EMAIL = "EMAIL"
    private val START_NATIVE_APP_RECORDING = "StartNativeAppRecording"
    private val TestResultId = "TestResultId"
    private val OhTestId = "OhTestId"
    private val AvaliableTestId = "AvaliableTestId"
    private val GCMNotification = "GCMNotification"

    private val HELPERFLAG = "helper_flag"

    private val MyRegisterID = "MyRegisterID"
    private val TESTERTYPE = "TesterType"
    private val UserType = "UserType"

     val UserTypeCustomer = "customer"
     val UserTypeWorker = "worker"
    private var IdentityId = "identity_id"
    private var S3Bucket = "S3Bucket"
    private var helperscreen = "helper_screen"


    fun clearSharedPreference(){
        sharedPreferences.edit().clear().commit()
    }

    fun saveToken(token: String?){
        sharedPreferences.edit().putString(TOKEN,token).commit()
    }

    fun saveUserName(username: String?) {
        sharedPreferences.edit().putString(USERNAME, username).commit()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN, null)
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(USERNAME, null)
    }

    fun saveEmailId(email: String?) {
        sharedPreferences.edit().putString(EMAIL, email).commit()
    }

    fun getEmailId(): String? {
        return sharedPreferences.getString(EMAIL, null)
    }

    fun saveTestId(testId: String?) {
        sharedPreferences.edit().putString(TEST_ID, testId).commit()
    }

    fun getTestId(): String? {
        return sharedPreferences.getString(TEST_ID, null)
    }

    fun saveTestResultId(testResultId: String?) {
        sharedPreferences.edit().putString(TestResultId, testResultId).commit()
    }


    fun getTestResultId(): String? {
        return sharedPreferences.getString(TestResultId, "")
    }

    fun saveOhTestId(ohTestId: String?) {
        sharedPreferences.edit().putString(OhTestId, ohTestId).commit()
    }


    fun getOhTestId(): String? {
        return sharedPreferences.getString(OhTestId, "")
    }


    fun removeTestResultId() {
        sharedPreferences.edit().remove(TestResultId).commit()
    }


    fun saveAvaliableTestId(AvaliableTestId1: String?) {
        sharedPreferences.edit().putString(AvaliableTestId, AvaliableTestId1).commit()
    }


    fun getAvaliableTestId(): String? {
        return sharedPreferences.getString(AvaliableTestId, "")
    }


    fun removeAvaliableTestId() {
        sharedPreferences.edit().remove(AvaliableTestId).commit()
    }


    fun setGCMNotification_TokenSendToServer(GCM_value: Boolean) {
        sharedPreferences.edit().putBoolean(GCMNotification, GCM_value).commit()
    }

    fun getGCMNotification_isTokenSend(): Boolean {
        return sharedPreferences.getBoolean(GCMNotification, false)
    }

    fun getMyRegisterID(): String? {
        return sharedPreferences.getString(MyRegisterID, null)
    }

    fun setMyRegisterID(myGCMNotificationToken: String?) {
        sharedPreferences.edit().putString(MyRegisterID, myGCMNotificationToken).commit()
    }


    fun saveGuestTester(isGuestLogin: Boolean) {
        sharedPreferences.edit().putBoolean(TESTERTYPE, isGuestLogin).commit()
    }

    fun getGuestTester(): Boolean {
        return sharedPreferences.getBoolean(TESTERTYPE, false)
    }


    // default user type is set as worker
    fun getUserType(): String? {
        return sharedPreferences.getString(UserType, UserTypeWorker)
    }

    fun setUserType(userType: String?) {
        sharedPreferences.edit().putString(UserType, userType).commit()
    }

     fun getIdentityId(): String? {
         return sharedPreferences.getString(IdentityId, "")
     }

     fun setIdentityId(identityId: String?) {
         sharedPreferences.edit().putString(IdentityId, identityId).commit()
     }

     fun getS3Bucket(): String? {
         return sharedPreferences.getString(S3Bucket, "")
     }

     fun setS3Bucket(s3Bucket: String?) {
         sharedPreferences.edit().putString(S3Bucket, s3Bucket).commit()
     }


     fun getHelperFlag(): Boolean {
         return sharedPreferences.getBoolean(helperscreen, false)
     }


     fun setHelperFlag(isHelperSeen: Boolean) {
         sharedPreferences.edit().putBoolean(helperscreen, isHelperSeen).commit()
     }





}