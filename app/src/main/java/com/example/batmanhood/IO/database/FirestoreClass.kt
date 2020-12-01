package com.example.batmanhood.IO.database

import android.app.Activity
import android.util.Log
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.activities.*
import com.example.batmanhood.models.User
import com.example.batmanhood.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.*
import yahoofinance.Stock
import yahoofinance.YahooFinance
import java.io.IOException
import java.lang.Exception

/**
 * This is our repository class(pattern) since we are using firebase for database and authentication.
 * This class will feed our views with network and database
 * data.
 */
//TODO integrate dependency injection with HILT
class FirestoreClass(private val stockAndIndexFetcher: StockAndIndexApiHelper) {

    private val mFireStore = FirebaseFirestore.getInstance()
    val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private  var mStockList: HashMap<String, String> = hashMapOf<String, String>()
    private  var mCryptoList: HashMap<String, Stock> = hashMapOf<String, Stock>()
    private  var mIndexList: HashMap<String, Stock> = hashMapOf<String, Stock>()
    private lateinit var mUserName: String

    /**
     * A function to make an entry of the registered user in the firestore database.
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
                            e
                    )
                }
    }

    //function used retrieve data from firebase database
    fun loadUserData(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName,document.toString())
                val LoggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity -> {
                        activity.signInSuccess(LoggedInUser)
                    }
                    is MainActivity ->{
                        ioScope.launch {
                            var user_stocks = fetchStocks(LoggedInUser)
                            //todo change configureUserAssetList to display stocks on MainACtivity
                           // activity.configureUserAssetList(LoggedInUser)
                        }
                    }
                    //is MyProfileActivity -> { }
                }

            }.addOnFailureListener{ e ->
                when(activity){
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.showErrorSnackBar("Error retrieving User preferences. Please restart app")
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting logged in details"
                )
            }


    }

    /**
     * This
     */
    private suspend fun fetchStocks(user: User) {

        withContext(Dispatchers.IO) {
            try {
                for(stock in user.stock_list){
                    //mStockList.put(stock, stockAndIndexFetcher .getRealTimeStockQuoteAllFields(stock,Constants.IEX_TOKEN).toString())
                    var stockResponse = stockAndIndexFetcher
                            .getRealTimeStockQuoteAllFields(stock,Constants.IEX_TOKEN)
                    Log.d("STOCK_HISTORY", stockResponse.toString())

                }
            } catch (e : Exception) {
                Log.e("STOCK_EXCEPTION",e.toString())
            }
        }
    }

    /**
     * This
     */
    private suspend fun fetchCrypto(currentUser: User) {

    }



    /**
     * This
     */
    private suspend fun fetchIndexes(currentUser: User) {

    }




    /**
     * This function will fetch user data for main activity to use to render
     */
    fun fetchUserDataFromFirebase(currentUser: User){
        //mFireStore.collection(Constants.)
    }

    /**
     * A function for getting the user id of current logged user.
     */
    private fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
}


