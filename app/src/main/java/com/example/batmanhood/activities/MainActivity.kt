package com.example.batmanhood.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels

import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.*
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.R
import com.example.batmanhood.fragments.ErrorFragment
import com.example.batmanhood.fragments.LoadingScreenFragment
import com.example.batmanhood.fragments.UserAssetsFragment
import com.example.batmanhood.models.RealTimeStockQuote
import com.example.batmanhood.models.User
import com.example.batmanhood.utils.UserProfileViewModelFactory
import com.example.batmanhood.viewModels.UserProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var stockAndIndexApiHelper: StockAndIndexApiHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace<LoadingScreenFragment>(R.id.main_activity_fragment_container_view)
            setReorderingAllowed(true)
        }

        try {
            //val userProfileModelFactory = UserProfileViewModelFactory(stockAndIndexApiHelper)
            val viewModel : UserProfileViewModel by viewModels { UserProfileViewModelFactory(
                stockAndIndexApiHelper
            )}
            observeUserProfileModel(viewModel)
            initListeners()
            //viewModel = ViewModelProvider(this,userProfileModelFactory).get(UserProfileViewModel::class.java)
        } catch(e : Exception) {
            Log.e("FACTORY_EXCEPTION",e.toString())
            supportFragmentManager.commit {
                replace<ErrorFragment>(R.id.main_activity_fragment_container_view)
                setReorderingAllowed(true)
            }
        }

    }

    private fun initListeners() {
        //TODO basically any listeners that are needed by main activity for buttons and stuff.

    }

    private fun observeUserProfileModel(viewModel: UserProfileViewModel) {

        viewModel.altUser.observe(this, Observer {
            //todo POSSIBLE SHOW USERNAME AT THE TOP, OR MAYBE DO NOTHING
        })
        viewModel.americanCompaniesList.observe(this, Observer {
            //TODO -> when this gets updated or w/e we can let our app know we can autofill now.
           //populate autucomplete textview
            populateAutofillOptions(viewModel.americanCompaniesList)
           // var a : ArrayAdapter<String> = ArrayAdapter<String>(this,1)
        })
        viewModel.currentUserStockList.observe(this, Observer {
            //render main ui fragment

            supportFragmentManager.commit {
                replace<UserAssetsFragment>(R.id.main_activity_fragment_container_view)
                setReorderingAllowed(true)
            }
        })
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }


    private fun configureUserPreferences() {
        //TODO get user preferences from database

    }

    fun configureUserAssetList(user_stocks : MutableList<MutableLiveData<RealTimeStockQuote>>, user : User){
        for (s in user_stocks) {
            Log.d("STOCK_MAIN_ACTIVITY", s.value?.companyName.toString())
        }
    }

    fun fetchStockPrice(){

    }



    fun fetchStockHistoryForTheDay(){

    }

    fun populateAssetList(companyMap : HashMap<String,String>){

    }

    fun populateAutofillOptions(americanCompaniesList: MutableLiveData<HashMap<String, String>>) {

    }

}