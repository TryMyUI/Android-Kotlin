package com.mahesch.trymyui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.model.AvailableTestModel
import kotlinx.android.synthetic.main.get_to_know_testing_activity.*

class GetToKnowTestingActivity : AppCompatActivity() {

    private var availableTestModel: AvailableTestModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_to_know_testing_activity)

        if (intent != null) {
            availableTestModel = intent.getSerializableExtra("availableTestConstant") as? AvailableTestModel
        }

        btn_get_started.setOnClickListener {
            startActivity(
                Intent(
                    this@GetToKnowTestingActivity,
                    HelperActivity1::class.java
                ).putExtra("availableTestConstant", availableTestModel)
            )

            finish()
            overridePendingTransition(R.anim.enter, R.anim.exit)
        }
    }
}
