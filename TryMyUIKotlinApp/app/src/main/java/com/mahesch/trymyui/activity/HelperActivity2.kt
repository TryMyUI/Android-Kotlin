package com.mahesch.trymyui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.model.AvailableTestModel
import kotlinx.android.synthetic.main.helper_activity2.*

class HelperActivity2 : AppCompatActivity() {

    private  var availableTestModel: AvailableTestModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.helper_activity2)

        if (intent != null) {
            availableTestModel =
                intent.getSerializableExtra("availableTestConstant") as? AvailableTestModel
        }

        btn_next.setOnClickListener {
            startActivity(
                Intent(
                    this@HelperActivity2,
                    HelperActivity3::class.java
                ).putExtra("availableTestConstant", availableTestModel)
            )
            finish()
            overridePendingTransition(R.anim.enter, R.anim.exit)
        }

        var source_string =
            "<b>" + resources.getString(R.string.developer_option_desc2_boldpart)
                .toString() + "</b>" + " " + resources.getString(R.string.developer_option_desc2)
        source_string = source_string.replace("\n", "<br>")
        tv_developer_option_desc2.text = Html.fromHtml(source_string)
    }
}
