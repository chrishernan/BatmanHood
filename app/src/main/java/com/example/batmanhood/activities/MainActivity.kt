package com.example.batmanhood.activities

import android.os.Bundle
import android.util.Log

import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.R
import com.example.batmanhood.IO.database.FirestoreClass
import com.example.batmanhood.adapters.AssetRecyclerViewAdapter
import com.example.batmanhood.fragments.ErrorFragment
import com.example.batmanhood.fragments.LoadingScreenFragment
import com.example.batmanhood.models.RealTimeStockQuote
import com.example.batmanhood.models.User
import com.example.batmanhood.utils.UserProfileViewModelFactory
import com.example.batmanhood.viewModels.EmptyUserProfileViewModel
import com.example.batmanhood.viewModels.UserProfileViewModel
import com.google.api.Distribution
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_loading_screen.*
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var stockAndIndexApiHelper: StockAndIndexApiHelper
    lateinit var viewModel : UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        val userProfileModelFactory = UserProfileViewModelFactory(
                this@MainActivity,
                stockAndIndexApiHelper)
        */

        supportFragmentManager.commit {
            replace<LoadingScreenFragment>(R.id.main_activity_fragment_container_view)
            setReorderingAllowed(true)
        }

        try {
            val userProfileModelFactory = UserProfileViewModelFactory(stockAndIndexApiHelper)
            viewModel = ViewModelProvider(this,userProfileModelFactory).get(UserProfileViewModel::class.java)
        } catch(e : Exception) {
            Log.e("FACTORY_EXCEPTION",e.toString())
            supportFragmentManager.commit {
                replace<ErrorFragment>(R.id.main_activity_fragment_container_view)
                setReorderingAllowed(true)
            }
        }
        /* --> used to troubleshoot inner view model exceptions
        try {
            val userProfileModelFactory = UserProfileViewModelFactory(
                    this@MainActivity,
                    stockAndIndexApiHelper)
        } catch(e : Exception) {
            Log.d("FACTORY_EXCEPTION",e.cause.toString())
        }*/


        observeUserProfileModel()
        initListeners()

        //Recycler View variables
       /* var listOfAssets : Array<String> = arrayOf("First", "Second","Third","Fourth")
        val assetRecyclerViewAdapter = viewModel.getCurrentUserStockList.value?.let {
            AssetRecyclerViewAdapter(
                it
            )
        }*/

    }

    private fun initListeners() {
        //TODO basically any listeners that are needed by main activity for buttons and stuff.

    }

    private fun observeUserProfileModel() {

        viewModel.altUser.observe(this, Observer {
            //todo POSSIBLE SHOW USERNAME AT THE TOP, OR MAYBE DO NOTHING
        })
        viewModel.americanCompaniesList.observe(this, Observer {
            //TODO -> when this gets updated or w/e we can let our app know we can autofill now.
           loadingAssetsTextViewCompanies.text = it.size.toString()
        })
        viewModel.currentUserStockList.observe(this, Observer {
            //TODO render or rerender stocks list
            loadingAssetsTextViewAssets.text = it.size.toString()
            //val rerenderingAssetRealTimeStockQuote = viewModel.altCurrentUserStockList.value?.let { it1 -> AssetRecyclerViewAdapter(it1) }
            //TODO recyclerView.adapter = rerenderingAssetRealTimeStockQuote
            //TODO recyclerView.layoutManager = LinearLayoutManager(this)
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

    fun populateAutofillOptions() {

    }

}