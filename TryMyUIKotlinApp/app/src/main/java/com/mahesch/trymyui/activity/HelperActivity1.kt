package com.mahesch.trymyui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.model.AvailableTestModel
import kotlinx.android.synthetic.main.android_onboarding_gesture2.*

class HelperActivity1 : AppCompatActivity() {

    private  var availableTestModel: AvailableTestModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.android_onboarding_gesture2)

        if (intent != null) {
            availableTestModel =
                intent.getSerializableExtra("availableTestConstant") as? AvailableTestModel
        }

        var source_string =
            resources.getString(R.string.android_onboarding_gesture2_desc2)
                .toString() + "<b>" + resources.getString(R.string.android_onboarding_gesture2_desc2_boldpart) + "</b> " + resources.getString(
                R.string.android_onboarding_gesture2_desc2_1
            )
        source_string = source_string.replace("\n", "<br>")
        android_onboarding_gesture2_desc2.setText(Html.fromHtml(source_string))

        btn_got_it.setOnClickListener {
            startActivity(
                Intent(
                    this@HelperActivity1,
                    HelperActivity2::class.java
                ).putExtra("availableTestConstant", availableTestModel)
            )
            finish()
            overridePendingTransition(R.anim.enter, R.anim.exit) }

    }
}