package com.example.batmanhood.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.example.batmanhood.R
import kotlinx.android.synthetic.main.activity_loading_user_assets.*

class LoadingUserAssetsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LOAD_SCREEN","Load screen being rendered")
        setContentView(R.layout.activity_loading_user_assets)

        Handler().postDelayed(Runnable {
            startActivity(Intent(this@LoadingUserAssetsActivity, MainActivity::class.java))
        },2000)
    }
}