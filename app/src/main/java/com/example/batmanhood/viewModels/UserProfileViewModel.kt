package com.example.batmanhood.viewModels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.*
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.IO.database.FirestoreClass
import com.example.batmanhood.IO.iexApiService
import com.example.batmanhood.activities.MainActivity
import com.example.batmanhood.models.AutofillCompany
import com.example.batmanhood.models.HistoricalPrices
import com.example.batmanhood.models.RealTimeStockQuote
import com.example.batmanhood.models.User
import com.example.batmanhood.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.lang.IllegalArgumentException
import javax.inject.Inject

class UserProfileViewModel(stockAndIndexApiHelper: StockAndIndexApiHelper) : ViewModel() {

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        Log.e("EXCEPTION_HANDLER","Exception Thrown => $exception")
    }
    val viewModelCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var altUser = MutableLiveData<User?>()
    var americanCompaniesList = MutableLiveData<MutableList<AutofillCompany>>()
    var currentUserStockList = MutableLiveData<LinkedHashMap<String,RealTimeStockQuote>>()
    var listOfUserStockHistoricalPrices = MutableLiveData<LinkedHashMap<String,List<HistoricalPrices>>>()

    init {

        viewModelCoroutineScope.launch(coroutineExceptionHandler) {
            val userJob = asyncGetUser(stockAndIndexApiHelper)
            val americanCompaniesJob = asyncGetAllAmericanCompanies(stockAndIndexApiHelper)
            val tempUser = userJob.await()
            altUser.postValue(tempUser)
            Log.e("USER_TAG","User -> ${tempUser}")
            val userAssetsJob = asyncGetUserAssets(stockAndIndexApiHelper, userJob)
            americanCompaniesList.postValue(americanCompaniesJob.await())
            currentUserStockList.postValue(userAssetsJob.await())
            val tempListOfUserStocks = userAssetsJob.await().keys.toList()
            Timber.e("USER STOCK SYMBOL LIST => $tempListOfUserStocks")
            var hashMapOfHistoricalPrices = linkedMapOf<String,List<HistoricalPrices>>()
            val flowListOfHistoricalPrices = FirestoreClass(stockAndIndexApiHelper)
                    .fetchHistoricalDataOfAllUserStocks(tempListOfUserStocks,Constants.ONEDAY)
                    .buffer().onEach {
                        hashMapOfHistoricalPrices.putAll(it)
                    }.collect()
             listOfUserStockHistoricalPrices.postValue(hashMapOfHistoricalPrices)
        }
    }
    //todo:decouple altAmerican and (altUser + altCurrentUserSotckList) execute together cuz of dependency

    fun refreshAutofillCompanyList(){
        //TODO run firestoreClass task

    }

    override fun onCleared(
    ) {
        super.onCleared()
        //Get rid of all subscriptions to avoid memory leaks
    }

    fun asyncGetUser(stockAndIndexApiHelper: StockAndIndexApiHelper) = viewModelCoroutineScope
            .async{
        FirestoreClass(stockAndIndexApiHelper).getCurrentUserCoroutineImplementation()
    }

    fun asyncGetAllAmericanCompanies(stockAndIndexApiHelper: StockAndIndexApiHelper) = viewModelCoroutineScope
            .async {
        FirestoreClass(stockAndIndexApiHelper).retrieveAllUSCompanies()
    }

    fun asyncGetUserAssets(stockAndIndexApiHelper: StockAndIndexApiHelper, deferredUser: Deferred<User?>)
        = viewModelCoroutineScope.async {
        altUser.postValue(deferredUser.await())
        //Log.e("USER_TAG_GET_ASSETS","${altUser.value}")
        FirestoreClass(stockAndIndexApiHelper).fetchUserStocks(altUser)
    }
}