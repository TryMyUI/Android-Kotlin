import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.repository.LoginRepository
import com.mahesch.trymyui.retrofitclient.ApiService
import com.mahesch.trymyui.retrofitclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    var loginRepository : LoginRepository? = null
    var mutableLiveData : MutableLiveData<LoginResponseModel>? = null
    var mutableLiveData_dialog : MutableLiveData<LoginResponseModel>? = null

    private var apiInterface: ApiService.ApiInterface = RetrofitInstance.getService()

    init {
        loginRepository = LoginRepository(application)
        mutableLiveData = MutableLiveData<LoginResponseModel>()
        mutableLiveData_dialog = MutableLiveData<LoginResponseModel>()

    }


    fun callLogin(email: String,password: String) {


        //var call = apiInterface.onLogin(email,password)

        loginRepository?.loginMutableData(email,password,object : Callback<LoginResponseModel> {

            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: retrofit2.Response<LoginResponseModel>) {

                Log.e("LOGIN", "ONRESPONSE")
                Log.e("LOGIN", "code " + response.code())


                mutableLiveData?.value = response.body()



            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                Log.e("LOGIN", "ONFAILURE")

                //   mutableLiveData.value = LoginResponseModel()

                //  https://medium.com/@sriramr083/error-handling-in-retrofit2-in-mvvm-repository-pattern-a9c13c8f3995

                var loginResponseModel = LoginResponseModel()
                loginResponseModel.error = t

                mutableLiveData_dialog?.value = loginResponseModel
            }

        })

       /* call?.enqueue(object : Callback<LoginResponseModel> {

            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: retrofit2.Response<LoginResponseModel>) {

                Log.e("LOGIN", "ONRESPONSE")
                Log.e("LOGIN", "code " + response.code())

                mutableLiveData?.value = response.body()




            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                Log.e("LOGIN", "ONFAILURE")

                //   mutableLiveData.value = LoginResponseModel()

                //  https://medium.com/@sriramr083/error-handling-in-retrofit2-in-mvvm-repository-pattern-a9c13c8f3995

                var loginResponseModel = LoginResponseModel()
                loginResponseModel.error = t

                mutableLiveData_dialog?.value = loginResponseModel
            }

        })*/

    }

    override fun onCleared() {
        super.onCleared()
            loginRepository?.dispose()
    }



}
