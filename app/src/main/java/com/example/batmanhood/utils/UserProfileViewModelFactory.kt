package com.example.batmanhood.utils

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.batmanhood.IO.StockAndIndexApiHelper
import java.lang.IllegalArgumentException
import kotlin.reflect.full.primaryConstructor

class UserProfileViewModelFactory(private vararg val args : Any)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            modelClass.kotlin.primaryConstructor?.call(*args)
                    ?: throw IllegalArgumentException("$modelClass primaryConstructor is null")


}