package com.example.batmanhood.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.IO.database.FirestoreClass
import com.example.batmanhood.models.AutofillCompany
import com.example.batmanhood.models.HistoricalPrices
import com.example.batmanhood.models.RealTimeStockQuote
import com.example.batmanhood.models.Result
import com.example.batmanhood.models.User
import com.example.batmanhood.utils.Constants
import com.example.batmanhood.utils.KeyType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

class UserProfileViewModel(stockAndIndexApiHelper: StockAndIndexApiHelper) : ViewModel() {
    val thisStockAndIndexApiHelper = stockAndIndexApiHelper
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        Timber.e("Exception Thrown => $exception")
    }
    private val viewModelCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val viewModelCPUScope = CoroutineScope(Dispatchers.Default)


    var altUser = MutableLiveData<Result<User>>()
    var americanCompaniesList = MutableLiveData<Result<MutableList<AutofillCompany>>>()
    var currentUserStockList = MutableLiveData<Result<LinkedHashMap<String,RealTimeStockQuote>>>()
    var listOfUserStockHistoricalPrices = MutableLiveData<Result<LinkedHashMap<String,MutableList<HistoricalPrices>>>>()
    var listOfOneStockHistoricalPrices = MutableLiveData<Result<LinkedHashMap<String,MutableList<HistoricalPrices>>>>()

    init {

        viewModelCoroutineScope.launch(coroutineExceptionHandler) {
            listOfUserStockHistoricalPrices.postValue(Result(
                Result.Status.LOADING,
                linkedMapOf(),
                "Loading User Assets Historical One Day Data"))
            altUser.postValue(Result(
                Result.Status.LOADING,
                User(),
                "Loading user"))
            americanCompaniesList.postValue(Result(
                Result.Status.LOADING,
                mutableListOf<AutofillCompany>(),
                "Loading All American Companies"))
            val userJob = asyncGetUser(stockAndIndexApiHelper)
            val americanCompaniesJob = asyncGetAllAmericanCompanies(stockAndIndexApiHelper)
            val tempUser = userJob.await()
            val tempUserTwo = tempUser?.name?.let {
                User(
                    tempUser.id,
                    it,
                    tempUser.email,
                    tempUser.image,
                    tempUser.mobile,
                    tempUser.fcmToken,
                    tempUser.stock_list,
                    tempUser.index_list)
            }
            Timber.e("User stock List in init => ${tempUser?.stock_list}")
            Timber.e("UserTwo stock List in init => ${tempUserTwo?.stock_list}")
            altUser.postValue(Result(
                Result.Status.SUCCESS,
                tempUserTwo,
                "Success Retrieving User"))
            americanCompaniesList.postValue(Result(
                Result.Status.SUCCESS,
                americanCompaniesJob.await(),
                "Success Retrieving American Companies List"))
            val userAssetsJob = asyncGetUserAssets(stockAndIndexApiHelper)
            currentUserStockList.postValue(Result(
                Result.Status.SUCCESS,
                userAssetsJob.await(),
                "Added user Stock List"))
            val tempListOfUserStocks = userAssetsJob.await()!!.keys.toMutableList()
            Timber.e("User symbol list => $tempListOfUserStocks")
            val hashMapOfHistoricalPrices = linkedMapOf<String,MutableList<HistoricalPrices>>()
            val flowListOfHistoricalPrices = FirestoreClass(stockAndIndexApiHelper)
                    .fetchHistoricalDataOfAllUserStocks(tempListOfUserStocks,Constants.ONE_DAY)
                    .buffer()
                    .onEach {
                        hashMapOfHistoricalPrices.putAll(it) }
                    .collect()

            //todo run the trimming on the default dispatcher instead of network one. It is slowing down app
            Timber.e("Trimmed user assets fragment lists")
            val mapTrimmingJob = viewModelCPUScope.async {
                trimNullPricesOfAllStocks(tempListOfUserStocks,hashMapOfHistoricalPrices)
            }
            val trimmedMap = mapTrimmingJob.await()
            listOfUserStockHistoricalPrices.postValue(Result(
                Result.Status.SUCCESS,
                trimmedMap,
                "Success Retrieving Historical One Day Data for User Assets"))
        }
    }
    //todo:decouple altAmerican and (altUser + altCurrentUserSotckList) execute together cuz of dependency

    fun refreshAutofillCompanyList(){
        //TODO run firestoreClass task

    }

    fun getOnedayDataForNewStock(symbol: String) {
        viewModelCoroutineScope.launch(coroutineExceptionHandler) {
            val historicalData = FirestoreClass(thisStockAndIndexApiHelper)
                .fetchHistoricalDataOfOneStock(
                    symbol,
                    Constants.ONE_DAY,
                    KeyType.SYMBOL_KEY)
            val realTimeDataJob = asyncGetOneStockData(thisStockAndIndexApiHelper,symbol)
            val realTimeData = realTimeDataJob.await()
            addNewAssetToDatabase(symbol)
            addNewAssetDataIntoLiveDataObjects(realTimeData,historicalData)
        }
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

    fun asyncGetUserAssets(stockAndIndexApiHelper: StockAndIndexApiHelper)
        = viewModelCoroutineScope.async {
        //altUser.postValue(deferredUser.await())
        //Log.e("USER_TAG_GET_ASSETS","${altUser.value}")
        altUser.value?.data?.let { FirestoreClass(stockAndIndexApiHelper).fetchUserStocks(it) }
    }

    fun asyncGetOneStockData(stockAndIndexApiHelper: StockAndIndexApiHelper, symbol : String) =
        viewModelCoroutineScope.async {
        FirestoreClass(stockAndIndexApiHelper).fetchOneStockRealTimeData(symbol)
    }

    fun getAllHistoricalDataForOneStock(symbol : String) {
        Timber.e("In viewmodel symbol => $symbol")
        viewModelCoroutineScope.launch(coroutineExceptionHandler) {
            listOfOneStockHistoricalPrices.postValue(Result(
                Result.Status.LOADING,
                linkedMapOf(),
                "Loading User Assets Historical One Day Data"))
            val hashMapOfOneStockHistoricalPrices = linkedMapOf<String,MutableList<HistoricalPrices>>()
            val listOfRanges = mutableListOf<String>(
                Constants.ONE_DAY,
                Constants.FIVE_DAYS,
                Constants.ONE_MONTH,
                Constants.SIX_MONTHS,
                Constants.ONE_YEAR,
                Constants.FIVE_YEARS
            )
            val flowListOfHistoricalPrices = FirestoreClass(thisStockAndIndexApiHelper)
                .fetchAllRangesOfHistoricalDataForOneStock(symbol,listOfRanges)
                .buffer().onEach {
                    hashMapOfOneStockHistoricalPrices.putAll(it)

                }.collect()

            //Trims nulls from all ranges.
            val mapTrimmingJob = viewModelCPUScope.async {
                trimNullPricesOfAllRanges(hashMapOfOneStockHistoricalPrices)
            }
            val trimmedMap = mapTrimmingJob.await()
            var trimmedOfNullPricesMap = mapTrimmingJob.await()
            listOfOneStockHistoricalPrices.postValue(Result(Result.Status.SUCCESS,
                trimmedOfNullPricesMap,
                "Success Retrieving All Historical data for one s"))
        }
    }

    fun addNewAssetToUser(symbol: String) {
        Timber.e("Adding $symbol to user")
        viewModelCoroutineScope.launch(coroutineExceptionHandler) {
            addNewAssetToDatabase(symbol)
            //Sending off async request to get RealTimeStockQuote data
            val newAssetDataJob = asyncGetOneStockData(thisStockAndIndexApiHelper, symbol)
            //letting host activity know we are loading the data still
            currentUserStockList.postValue(Result(
                Result.Status.LOADING,
                currentUserStockList.value?.data,
                "Loading New Asset RealTimeData"))
            //Adding the new stock to the ordered list and to the one day historical prices list of all stocks
            altUser.value?.data?.stock_list?.add(0, symbol)
            val tempMap = listOfUserStockHistoricalPrices.value?.data
            val oneDaymap = listOfOneStockHistoricalPrices.value?.data
            oneDaymap?.get(Constants.ONE_DAY)?.let { tempMap?.put(symbol, it) }
            listOfOneStockHistoricalPrices.value?.data?.let { tempMap?.putAll(it) }

            //TODO test what happens if RealTimeStockQuzote returns some sort of null
            //wait for the async request for RealTimeStock data and merging with the overall list
            val newAssetRealTimeData = newAssetDataJob.await()
            if(newAssetRealTimeData != null) {
                Timber.e("this asset realtimedata retrieved ${newAssetRealTimeData[symbol]?.companyName}")
            }
            listOfUserStockHistoricalPrices.postValue(Result(
                Result.Status.SUCCESS,
                tempMap,
                "Successfully Added new Stock one day historical data"))
            currentUserStockList.value!!.data?.let { newAssetRealTimeData.putAll(it) }
                //val mapOfAllAssetRealTimeData = currentUserStockList.value!!.data
                //val finalMap = mapOfAllAssetRealTimeData?.putAll(newAssetRealTimeData)
                currentUserStockList.postValue(Result(
                    Result.Status.SUCCESS,
                    newAssetRealTimeData,
                    "currentUserStockList successfully retrieved"))
            Timber.e("Finished adding $symbol to users livedata : ${currentUserStockList.value!!.status}")
        }
    }

    fun addNewAssetToDatabase(symbol : String) {
        FirestoreClass(thisStockAndIndexApiHelper).addStockFromFirebaseUserStockList(symbol)
    }

    fun removeAssetFromUser(symbol : String) {
        removeNewAssetFromDatabase(symbol)
        altUser.value?.data?.stock_list?.remove(symbol)
        currentUserStockList.value?.data?.remove(symbol)
        listOfUserStockHistoricalPrices.value?.data?.remove(symbol)
    }

    fun removeNewAssetFromDatabase(symbol : String) {
        FirestoreClass(thisStockAndIndexApiHelper).removeStockFromFirebaseUserStockList(symbol)
    }

    private fun addNewAssetDataIntoLiveDataObjects(
        realTimeData: LinkedHashMap<String, RealTimeStockQuote>,
        historicalData: HashMap<String, MutableList<HistoricalPrices>>
    ) {
        var result =
        currentUserStockList.value?.data?.putAll(realTimeData)
        listOfUserStockHistoricalPrices.value?.data?.putAll(historicalData)
    }

    /**
     * Trims null from one day historical prices
     */
    private fun trimNullPricesOneRange(range: String,mapOfHistoricalPrices :  LinkedHashMap<String, MutableList<HistoricalPrices>>)
    : LinkedHashMap<String, MutableList<HistoricalPrices>> {
        val listOfHistoricalPrices = mapOfHistoricalPrices[range]
        if (listOfHistoricalPrices != null) {
            for(i in 0 until listOfHistoricalPrices.size) {
                if(listOfHistoricalPrices[i].close == null) {
                    listOfHistoricalPrices.removeAt(i)
                }
            }
            mapOfHistoricalPrices[range] = listOfHistoricalPrices
        }
        return mapOfHistoricalPrices
    }

    /**
     * Trims null from the remaining ranges of historical prices
     * Separating this and one day for performance reasons.Faster to trim one and display then wait
     * for all the ranges to be iterated through.
     */
    private fun trimNullPricesOfAllRanges(mapOfHistoricalPrices: LinkedHashMap<String, MutableList<HistoricalPrices>>) :
            LinkedHashMap<String, MutableList<HistoricalPrices>>{

        val listOfRemainingRanges = mutableListOf(
            Constants.ONE_DAY,
            Constants.FIVE_DAYS,
            Constants.ONE_MONTH,
            Constants.SIX_MONTHS,
            Constants.ONE_YEAR,
            Constants.FIVE_YEARS
        )
        var listOfIndexesToRemove = mutableListOf<Int>()
        for(range in listOfRemainingRanges) {
            val currentListOfPrices = mapOfHistoricalPrices[range]
            Timber.e("$range => ${currentListOfPrices?.size}")
            if (currentListOfPrices != null) {
                for(i in 0 until currentListOfPrices.size) {
                    if(currentListOfPrices[i].close == null) {
                        listOfIndexesToRemove.add(i)
                    }
                }
                //This variable is to keep track of how many items have been removed from the list
                //Without this variable the list has indexOutOfBoundsException. Every item removed
                //will increment this index by 1.
                var subtractionIndex = 0
                for(index in listOfIndexesToRemove) {
                    currentListOfPrices.removeAt(index-subtractionIndex)
                    subtractionIndex++
                }
                listOfIndexesToRemove.clear()
                mapOfHistoricalPrices[range] = currentListOfPrices
            }
        }
        return mapOfHistoricalPrices
    }

    private fun trimNullPricesOfAllStocks(listOfSymbols : MutableList<String>,
        mapOfHistoricalPrices: LinkedHashMap<String, MutableList<HistoricalPrices>>) :
            LinkedHashMap<String, MutableList<HistoricalPrices>>{

        var listOfIndexesToRemove = mutableListOf<Int>()
        for(symbol in listOfSymbols) {
            val loweredSymbol = symbol.toLowerCase()
            Timber.e("Symbol to Trim => $loweredSymbol")
            val currentListOfPrices = mapOfHistoricalPrices[loweredSymbol]
            if (currentListOfPrices != null) {
                for(i in 0 until currentListOfPrices.size) {
                    if(currentListOfPrices[i].close == null) {
                        listOfIndexesToRemove.add(i)
                    }
                }
                //This variable is to keep track of how many items have been removed from the list
                //Without this variable the list has indexOutOfBoundsException. Every item removed
                //will increment this index by 1.
                var subtractionIndex = 0
                for(index in listOfIndexesToRemove) {
                    currentListOfPrices.removeAt(index-subtractionIndex)
                    subtractionIndex++
                }
                listOfIndexesToRemove.clear()
                mapOfHistoricalPrices[loweredSymbol] = currentListOfPrices
            }
        }
        return mapOfHistoricalPrices
    }
    /**
     *  Clears listOfOneStockHistoricalPrices LiveData Object to update with new item user has clicked
     */
    fun clearOneStockHistoricalDataLiveData() {
        listOfOneStockHistoricalPrices = MutableLiveData<Result<LinkedHashMap<String,MutableList<HistoricalPrices>>>>()

    }

}