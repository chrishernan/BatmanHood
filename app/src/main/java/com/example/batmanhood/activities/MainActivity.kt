package com.example.batmanhood.activities

import android.os.Bundle
import android.util.Log
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.R
import com.example.batmanhood.IO.database.FirestoreClass
import com.example.batmanhood.IO.iexApiService
import com.example.batmanhood.models.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject lateinit var stockAndIndexApiHelper: StockAndIndexApiHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirestoreClass(stockAndIndexApiHelper).loadUserData(this@MainActivity)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

    private fun configureUserPreferences() {
        //TODO get user preferences from database

    }

    fun configureUserAssetList(user: User){
        //TODO make a call to firebase database and get asset list.
        Log.d("USER_RECEIVED", "User: ${user}")
        mainActivity_title.text = user.toString()

        for(crypto in user.crypto_list) {

        }

        for(index in user.index_list){

        }

    }

    fun fetchStockPrice(){

    }

    fun fetchStockHistoryForTheDay(){

    }

    fun populateAssetList(){

    }




}