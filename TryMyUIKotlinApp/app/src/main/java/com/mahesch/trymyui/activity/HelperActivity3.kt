package com.mahesch.trymyui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.mahesch.trymyui.R
import com.mahesch.trymyui.model.AvailableTestModel
import kotlinx.android.synthetic.main.helper_activity3.*

class HelperActivity3 : AppCompatActivity() {

    private var availableTestModel: AvailableTestModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.helper_activity3)

        if (intent != null) {
            availableTestModel =
                intent.getSerializableExtra("availableTestConstant") as? AvailableTestModel
        }

        btn_next.setOnClickListener {
            startActivity(
                Intent(
                    this@HelperActivity3,
                    HelperActivity4::class.java
                ).putExtra("availableTestConstant", availableTestModel)
            )
            finish()
            finish()
            overridePendingTransition(R.anim.enter, R.anim.exit) }

        var source_string = resources.getString(R.string.developer_option_desc4)
            .toString() + "<b>" + resources.getString(R.string.developer_option_desc4_boldpart) + "</b>" + " " + resources.getString(
            R.string.developer_option_desc4_1
        )
        source_string = source_string.replace("\n", "<br>")
        tv_developer_option_desc2.text = Html.fromHtml(source_string)
    }
}
