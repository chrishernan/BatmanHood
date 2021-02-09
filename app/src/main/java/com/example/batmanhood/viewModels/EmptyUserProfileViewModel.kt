package com.example.batmanhood.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.batmanhood.models.User

/**
 *
 * Used as a empty overloaded constructor to check whether to display loading fragment
 *
 */
class EmptyUserProfileViewModel : ViewModel() {

    var test: String = ""
    var altUser : LiveData<User> = liveData<User> {
        emit(User())
    }
    var altAmericanCompaniesList : LiveData<HashMap<String,String>> = liveData<HashMap<String,String>> {
        emit(HashMap<String,String>())
    }
    var altCurrentUserStockList : LiveData<LinkedHashMap<String,String>> = liveData<LinkedHashMap<String,String>> {
        emit(LinkedHashMap<String,String>())
    }

}