package com.mahesch.trymyui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mahesch.trymyui.R
import com.mahesch.trymyui.helpers.ManageFlowBeforeRecording
import com.mahesch.trymyui.model.AvailableTestModel
import kotlinx.android.synthetic.main.face_recording_info_activity.*

class FaceRecordingInfoActivity : AppCompatActivity() {

    private lateinit var availableTestModel: AvailableTestModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_recording_info_activity)

        if(intent != null){
            availableTestModel = intent.extras.getSerializable("availableTestConstants") as AvailableTestModel

            if(availableTestModel == null){
                moveToHome()
            }
        }
        else
        {
            moveToHome()
        }

        btn_face_recording_no.setOnClickListener { onClickNo() }

        btn_face_recording_yes.setOnClickListener { onClickYes() }
    }

    private fun onClickNo(){
            startActivity(Intent(this,TabActivity::class.java))
            finish()
    }

    private fun onClickYes(){
        startActivity(Intent(this,PerformTestActivity::class.java))
        finish()
    }

    private fun moveToHome(){
        startActivity(Intent(this,TabActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        ManageFlowBeforeRecording(availableTestModel,this).manageBackFlow(2)

    }
}
