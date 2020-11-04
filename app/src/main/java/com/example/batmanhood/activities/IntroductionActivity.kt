package com.example.batmanhood.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.batmanhood.R
import kotlinx.android.synthetic.main.activity_introduction.*
import kotlinx.android.synthetic.main.activity_splash.*

class IntroductionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        val typeface: Typeface =
            Typeface.createFromAsset(assets, "HOMOARAK.TTF")
        textView_app_name_intro.typeface = typeface

        button_sign_in_intro.setOnClickListener {

            // Launch the sign in screen.
            startActivity(Intent(this@IntroductionActivity, SignInActivity::class.java))
        }

        button_register_intro.setOnClickListener {

            // Launch the sign up screen.
            startActivity(Intent(this@IntroductionActivity, RegisterActivity::class.java))
        }
    }

}