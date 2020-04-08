import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mahesch.trymyui.model.LoginResponseModel
import com.mahesch.trymyui.repository.LoginRepository

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    var loginRepository : LoginRepository? = null

    init {
        loginRepository = LoginRepository(application)
    }


    fun callLogin(email: String,password: String) : MutableLiveData<LoginResponseModel>?{
        return loginRepository?.loginMutableData(email,password)
    }



}
