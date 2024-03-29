package com.example.batmanhood.IO.database

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.activities.*
import com.example.batmanhood.models.AutofillCompany
import com.example.batmanhood.models.HistoricalPrices
import com.example.batmanhood.models.RealTimeStockQuote
import com.example.batmanhood.models.User
import com.example.batmanhood.utils.Constants
import com.example.batmanhood.utils.KeyType
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import kotlin.system.exitProcess

/**
 * This is our repository class(pattern) since we are using firebase for database and authentication.
 * This class will feed our views with network and database
 * data.
 */
//TODO integrate dependency injection with HILT
class FirestoreClass(private val stockAndIndexFetcher: StockAndIndexApiHelper) {

    private val mFireStore = FirebaseFirestore.getInstance()
    private lateinit var mUserName: String
    val cpuScope = CoroutineScope(Dispatchers.Default)
    val networkingScope = CoroutineScope(Dispatchers.IO)
    private var currentUserID = ""

    fun getCurrentUser() : MutableLiveData<User>{
        var loggedInUser: User
        loggedInUser = mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.d("SUCCESS_GETTING_USER","Successfully retrieved user from firebase")
                }.addOnFailureListener{ e ->
                    Log.e(
                            "FAILED_TO_GET_USER",
                            "Error while getting logged in details -> ${e.toString()}"
                    )
                }.result?.toObject(User::class.java)!!
        Log.d("LOGGED_IN_USER","right before logged in user is returned ${loggedInUser.name} -")
        var liveDataUser = MutableLiveData<User>()
        liveDataUser.postValue(loggedInUser)
        return liveDataUser
    }

    suspend fun getCurrentUserCoroutineImplementation() : User? {
            val loggedInUser = try {
                mFireStore.collection(Constants.USERS)
                        .document(getCurrentUserID())
                        .get()
                        .await()
            } catch(e: Exception) {
                Log.e("EXCEPTION_KTX_GETUSER","Exception retrieving user in Coroutine implementation => ${e.toString()}")
                null
            }
        return loggedInUser?.toObject(User::class.java)
    }

    /**
     * Removes stock from Firebase database
     */
    fun removeStockFromFirebaseUserStockList(symbol: String) {
        val userRef = mFireStore.collection(Constants.USERS).document(getCurrentUserID())
        Timber.e("Stock to remove => $symbol")
        userRef
            .update("stock_list",FieldValue.arrayRemove(symbol.toUpperCase()))
        Timber.e("Stock removed => $symbol")
    }

    /**
     * Adds stock to Firebase database
     */
    fun addStockFromFirebaseUserStockList(symbol: String) {
        val userRef = mFireStore.collection(Constants.USERS).document(getCurrentUserID())
        userRef
            .update("stock_list",FieldValue.arrayUnion(symbol.toUpperCase()))
    }

    private fun getFirebaseDocumentSnapshot(userId : String) : Task<DocumentSnapshot> = runBlocking {
        Log.d("BEFORE_DOCUMENT_GET","right before with context colleciton document")
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.USERS)
                    .document(userId)
                    .get()
                    .addOnCompleteListener {
                        Log.d("TEST_FIREBASE",it.toString())
                    }
        }
    }

    /**
     * Makes an entry of the registered user in the firestore database with [userInfo]
     * Executes code sequences based on [activity]
     */
    fun registerUser(activity: RegisterActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
                // Document ID for users fields. Here the document it is the User ID.
                .document(getCurrentUserID())
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we want to merge
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.userRegisteredSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error writing document",
                            e)
                }
    }

    /**
     * Retrieves user retrieve data from firebase database
     */
    fun loadUserData(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName,document.toString())
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    //is MyProfileActivity -> { }
                }

            }.addOnFailureListener{ e ->
                when(activity){
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting logged in details"
                )
            }

    }

    /**
     * This takes in a [user] and returns all the users stocks in a LinkedHashMap for rendering
     */
    suspend fun fetchUserStocks(user : User) : LinkedHashMap<String,RealTimeStockQuote>{
        //val listOfLiveData: MutableList<MutableLiveData<RealTimeStockQuote>> = mutableListOf()
        //val stockLiveData: MutableLiveData<RealTimeStockQuote> = MutableLiveData()
        Timber.e("FireStore Stock List => ${user.stock_list}")
        var mapOfUserStockSymbolAndPrice: LinkedHashMap<String, RealTimeStockQuote>
        //var liveDataMap = MutableLiveData<LinkedHashMap<String, String>>()
        var userStockJob = networkingScope.async {
            stockAndIndexFetcher.getMultipleStockQuotes(
                    user.stock_list.joinToString(separator = ",").toLowerCase(),
                    "quote",
                    Constants.IEX_TOKEN)
        }
        var userStockResponse = userStockJob.await()
        var parseUserStockResponse = cpuScope.async {
            parseUserStocks(userStockResponse)
        }
        mapOfUserStockSymbolAndPrice = parseUserStockResponse.await()
       // Log.e("SUCCESS_FETCH_USR_STCKS",mapOfUserStockSymbolAndPrice.keys.toString())
        //liveDataMap.postValue( mapOfUserStockSymbolAndPrice)
        Timber.e("Done fetching Stock List Quotes")
        return mapOfUserStockSymbolAndPrice
    }

    suspend fun fetchOneStockRealTimeData(symbol: String) :  LinkedHashMap<String,RealTimeStockQuote>{
        var mapOfStockSymbolAndPrice: LinkedHashMap<String, RealTimeStockQuote> = linkedMapOf()
        var userStockJob = networkingScope.async {
            stockAndIndexFetcher.getRealTimeStockQuoteAllFields(
                symbol,
                Constants.IEX_TOKEN)
        }
        var userStockResponse = userStockJob.await()
        mapOfStockSymbolAndPrice.put(symbol,userStockResponse)
        return mapOfStockSymbolAndPrice
    }

    fun fetchHistoricalDataOfAllUserStocks(listOfUserStocks: MutableList<String>,rangeOfDays : String)
    : Flow<HashMap<String,MutableList<HistoricalPrices>>>  = flow {
        for (userStock in listOfUserStocks) {
            val userStockHistoricalData = fetchHistoricalDataOfOneStock(userStock,rangeOfDays,KeyType.SYMBOL_KEY)
            emit(userStockHistoricalData)
        }
    }

    fun fetchAllRangesOfHistoricalDataForOneStock(symbol : String, listOfRanges : MutableList<String>)
    : Flow<HashMap<String,MutableList<HistoricalPrices>>> = flow {
        for(range in listOfRanges) {
            val stockHistoricalData = fetchHistoricalDataOfOneStock(symbol,range,KeyType.RANGE_KEY)
            emit(stockHistoricalData)
        }
    }

    suspend fun fetchHistoricalDataOfOneStock(symbol : String, rangeOfDays : String, keyType: KeyType)
    : HashMap<String,MutableList<HistoricalPrices>>{
        val historicalDataRequest = networkingScope.async {
            stockAndIndexFetcher.getHistoricalStockPrices(
                symbol,
                Constants.HISTORICAL_DATA_API_FILTER,
                rangeOfDays,
                Constants.CHART_SIMPLIFY,
                Constants.IEX_TOKEN)
        }
        val response = historicalDataRequest.await()
        val mapOfSymbolToHistoricalPrices = HashMap<String,MutableList<HistoricalPrices>>()
        when(keyType) {
            KeyType.SYMBOL_KEY -> {
                mapOfSymbolToHistoricalPrices.put(symbol,response)
            }
            KeyType.RANGE_KEY -> {
                mapOfSymbolToHistoricalPrices.put(rangeOfDays,response)

            }
        }
        return mapOfSymbolToHistoricalPrices
    }

    /**
     * This is used for our autocomplete functionality
     */
    suspend fun retrieveAllUSCompanies() : MutableList<AutofillCompany>{
        //var liveDataAmericanCompaniesMap = MutableLiveData<HashMap<String,String>>()
        val retrievingAmericanCompaniesJob = networkingScope.async {
            stockAndIndexFetcher.getAllUSCompanies(
                Constants.IEX_TOKEN,
                Constants.ALL_COMPANIES_API_FILTER)
        }
        try {
            var tempAwait = retrievingAmericanCompaniesJob.await()
            //Log.e("AMERICAN_COMPANY_AWAIT","american companies response => $tempAwait")
        } catch(exception : Exception) {
            Log.e("EXCEPTION_AWAIT_US","ExceptionTag: ${exception.toString()}")
        }
        var americanCompanies = retrievingAmericanCompaniesJob.await()
        return americanCompanies
    }

    /**
     * Deprecated
     */
    private suspend fun parseAutofillJSON(listOfCompanies : List<AutofillCompany>)
            : HashMap<String,String> {
        var companySymbolAndNameMap : HashMap<String,String> = hashMapOf()
            listOfCompanies.map {
                companySymbolAndNameMap.put(it.symbol.toString(),it.name.toString())
            }
        return companySymbolAndNameMap
    }

    /**
     * This parses through all the RealTimeStockQuotes and returns only two argumens for now
     * @returns Stock symbol and current stock price
     */
    private suspend fun parseUserStocks(listOfRealTimeQuotes : HashMap<String,HashMap<String,RealTimeStockQuote>> )
    : LinkedHashMap<String, RealTimeStockQuote> {
        var linkedHashMap = LinkedHashMap<String, RealTimeStockQuote>()
            listOfRealTimeQuotes.values.map {
                it.get("quote")?.let { it1 -> linkedHashMap.put(it1.symbol.toString(), it1) }
            }
        //Log.e("SUCCESS_PARSE_USR_STCKS","Stock successfully parsed => /n ${linkedHashMap.get("TSLA")}")
        return linkedHashMap
    }

    /**
     * A function for getting the user id of current logged user.
     */
    private fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}
 

