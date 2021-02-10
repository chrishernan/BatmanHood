package com.example.batmanhood.viewModels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.*
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.IO.database.FirestoreClass
import com.example.batmanhood.IO.iexApiService
import com.example.batmanhood.activities.MainActivity
import com.example.batmanhood.models.AutofillCompany
import com.example.batmanhood.models.RealTimeStockQuote
import com.example.batmanhood.models.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import javax.inject.Inject

class UserProfileViewModel(stockAndIndexApiHelper: StockAndIndexApiHelper) : ViewModel() {

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        Log.e("EXCEPTION_HANDLER","Exception Thrown => $exception")
    }
    val viewModelCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var altUser = MutableLiveData<User?>()
    var americanCompaniesList = MutableLiveData<HashMap<String,String>>()
    var currentUserStockList = MutableLiveData<LinkedHashMap<String,String>>()

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

/*
    private var _americanCompaniesList = MutableLiveData<HashMap<String,String>>()
    private  var _currentUserStockList = MutableLiveData<LinkedHashMap<String,String>>()
    private  var _user : User = User()

    init{
        _user = FirestoreClass(stockAndIndexApiHelper).getCurrentUser()
        Log.d("STOCK_VIEW_MODEL", _user.name + " " + _user.email)
        _americanCompaniesList = FirestoreClass(stockAndIndexApiHelper).retrieveAllUSCompanies()
        _currentUserStockList = FirestoreClass(stockAndIndexApiHelper).fetchUserStocks(_user)
    }

    val autofillUSCompanyList : MutableLiveData<HashMap<String,String>>
        get() = _americanCompaniesList
    val getCurrentUserStockList :  MutableLiveData<LinkedHashMap<String,String>>
        get() = _currentUserStockList
    val user : User
        get() = _user
        */


/*
    var altUser : LiveData<User> = liveData<User> {
        emit(User())
    }
    var altAmericanCompaniesList : LiveData<HashMap<String,String>> = liveData<HashMap<String,String>> {
        emit(HashMap<String,String>())
    }
    var altCurrentUserStockList : LiveData<LinkedHashMap<String,String>> = liveData<LinkedHashMap<String,String>> {
        emit(LinkedHashMap<String,String>())
    }

    init {


        altUser = liveData {
            try{emit(FirestoreClass(stockAndIndexApiHelper).getCurrentUser())
            } catch(exception: Exception){
                Log.e("ERROR_RETRIV_USER","UserID failed due to: $exception")
            }
        }
        altAmericanCompaniesList = liveData<HashMap<String,String>> {
            try{emit(FirestoreClass(stockAndIndexApiHelper).retrieveAllUSCompanies())
            } catch(exception: Exception){
                Log.e("ERROR_RETRIV_AMER_COMP","American Company retrival failed due to: $exception")
            }
        }

        altCurrentUserStockList = liveData<LinkedHashMap<String,String>> {
            try{emit(FirestoreClass(stockAndIndexApiHelper).fetchUserStocks(altUser))
            } catch (exception: Exception){
                Log.e("ERROR_RETRIV_USR_COMPA","User companies retrieval failed due to: $exception")
            }
        }
    } */