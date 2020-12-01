package com.example.batmanhood.utils

import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.IO.iexApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import yahoofinance.Stock
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class stockFetcherModule {

    @Singleton
    @Provides
    fun bindIexApiService(): StockAndIndexApiHelper {
        return StockAndIndexApiHelper(iexApiService.create())
    }


}