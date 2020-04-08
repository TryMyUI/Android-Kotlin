package com.mahesch.trymyui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.SharedPrefHelper
import com.mahesch.trymyui.model.AvailableTestModel
import kotlinx.android.synthetic.main.helper_activity4.*

class HelperActivity4 : AppCompatActivity() {

    private lateinit var sharedPrefHelper: SharedPrefHelper
    private  var availableTestModel: AvailableTestModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.helper_activity4)


        sharedPrefHelper = SharedPrefHelper(HelperActivity4@this)

        if (intent != null) {
            availableTestModel =
                intent.getSerializableExtra("availableTestConstant") as? AvailableTestModel
        }


        btn_finish.setOnClickListener {
            sharedPrefHelper.setHelperFlag(true)


            // TODO move to next activity
            val intent: Intent = Intent(
                this@HelperActivity4,
                TabActivity::class.java
            ).putExtra("availableTestConstant", availableTestModel)
            startActivity(intent)
            finish()

//                startActivity(new Intent(HelperActivity4.this,SplashActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit)
        }


    }
}
