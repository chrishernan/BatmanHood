package com.example.batmanhood.activities

import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.iterator

import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.*
import com.example.batmanhood.IO.StockAndIndexApiHelper
import com.example.batmanhood.R
import com.example.batmanhood.adapters.TestSparkAdapter
import com.example.batmanhood.fragments.*
import com.example.batmanhood.models.Result
import com.example.batmanhood.utils.*
import com.example.batmanhood.viewModels.UserProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_single_asset.view.*
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var stockAndIndexApiHelper: StockAndIndexApiHelper
    val viewModel : UserProfileViewModel by viewModels { UserProfileViewModelFactory(
        stockAndIndexApiHelper
    )}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
            replace<LoadingScreenFragment>(R.id.main_activity_fragment_container_view)
            setReorderingAllowed(true)
        }

        observeUserProfileModel()
    }

    private fun observeUserProfileModel() {

        viewModel.altUser.observe(this, Observer {
            //todo POSSIBLE SHOW USERNAME AT THE TOP, OR MAYBE DO NOTHING
        })

        viewModel.listOfUserStockHistoricalPrices.observe(this, Observer {
            //render main ui fragment
            when (it.status){
                Result.Status.LOADING -> {
                    supportFragmentManager.commit {
                        replace<LoadingScreenFragment>(R.id.main_activity_fragment_container_view)
                        setReorderingAllowed(true)
                    }
                }
                Result.Status.SUCCESS -> {
                    supportFragmentManager.commit {
                        replace<UserAssetsFragment>(R.id.main_activity_fragment_container_view)
                        setReorderingAllowed(true)
                    }
                    stopObservingAllOfUsersHistoricalPrices()
                }
                Result.Status.ERROR -> {
                    supportFragmentManager.commit {
                        replace<UserAssetsFragment>(R.id.main_activity_fragment_container_view)
                        setReorderingAllowed(true)
                    }
                    Toast.makeText(
                        this,
                        "Error loading Data. Please Try Again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        })
    }

    /**
     * This function will be ran after observeUserProfileModel(). It is no longer needed once all
     * of the user assets have been retrieved. It is causing issue with observing another livedata
     * when new asset is added.
     *
     */
    fun stopObservingAllOfUsersHistoricalPrices() {
        viewModel.listOfUserStockHistoricalPrices.removeObservers(this)
    }

    /**
     * Clearing this observer so that it is only enacted when we recreate the UserAssets Fragment
     * If we do not do this, they remain alive and affect other fragments.
     */
    fun stopObservingCurrentUserStockList() {
        viewModel.currentUserStockList.removeObservers(this)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

    fun singleAssetFragment(assetSymbol : String) {
        Timber.e("Symbol => $assetSymbol")
        val viewModel : UserProfileViewModel by viewModels()
        viewModel.getAllHistoricalDataForOneStock(assetSymbol)
        viewModel.listOfOneStockHistoricalPrices.observe(this, Observer {
            when (it.status){
                Result.Status.LOADING -> {
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
                        replace<LoadingScreenFragment>(R.id.main_activity_fragment_container_view)
                        setReorderingAllowed(true)
                    }
                }
                Result.Status.SUCCESS -> {
                    supportFragmentManager.commit {
                        val args =  Bundle()
                        Timber.e("${viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.last()?.close}")
                        args.putString("symbol",assetSymbol)
                        setCustomAnimations(R.anim.slide_in_right_to_left,R.anim.slide_out_right_to_left)
                        replace<singleAssetFragment>(R.id.main_activity_fragment_container_view, "symbol",args)
                        setReorderingAllowed(true)
                    }
                }
                Result.Status.ERROR -> {
                    supportFragmentManager.commit {
                        replace<UserAssetsFragment>(R.id.main_activity_fragment_container_view)
                        setReorderingAllowed(true)
                    }
                    Toast.makeText(
                        this,
                        "Error loading Data. Please Try Again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSingleAssetFragmentClick(view: View)
    {

        val radioGroup = view.parent as RadioGroup
        val layout = view.parent.parent as ConstraintLayout
        if(view is RadioButton) {
            // Is the radio Button now checked?
            val checked = view.isChecked
            val viewModel : UserProfileViewModel by viewModels()

            when(view.id) {
                R.id.one_day_radio_button ->
                    if(checked && checkIfRadioButtonIsAlreadyChecked(view)) {
                        val dollarDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.first()?.close)
                        { it1, it2 -> calculateDollarDifference(it1,it2) }
                        val percentageDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.first()?.close)
                        { it1, it2 -> calculatePercentageDifference(it1,it2) }

                        layout.text_view_stock_change.apply {
                            if (percentageDifference != null) {
                                if(percentageDifference >= 0) {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
                                } else {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
                                }
                            }
                        }

                        layout.text_view_stock_change.text = "\$$dollarDifference ($percentageDifference%)"
                        setupSparkView(layout,Constants.ONE_DAY)

                        if (percentageDifference != null) {
                            if(percentageDifference >= 0) {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_above_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_green_selected_rounded)
                            } else {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_below_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_orange_selected_rounded)

                            }
                        }
                        view.setTextColor(ContextCompat.getColor(applicationContext,R.color.white))
                    }
                R.id.five_day_radio_button ->
                    if(checked && checkIfRadioButtonIsAlreadyChecked(view)) {
                        val dollarDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.FIVE_DAYS)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.FIVE_DAYS)?.first()?.close)
                        { it1, it2 -> calculateDollarDifference(it1,it2) }
                        val percentageDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.FIVE_DAYS)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.FIVE_DAYS)?.first()?.close)
                        { it1, it2 -> calculatePercentageDifference(it1,it2) }

                        layout.text_view_stock_change.apply {
                            if (percentageDifference != null) {
                                if(percentageDifference >= 0) {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
                                } else {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
                                }
                            }
                        }

                        layout.text_view_stock_change.text = "\$$dollarDifference ($percentageDifference%)"
                        setupSparkView(layout,Constants.FIVE_DAYS)

                        if (percentageDifference != null) {
                            if(percentageDifference >= 0) {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_above_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_green_selected_rounded)
                            } else {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_below_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_orange_selected_rounded)

                            }
                        }

                        view.setTextColor(ContextCompat.getColor(applicationContext,R.color.white))
                    }
                R.id.one_month_radio_button ->
                    if(checked && checkIfRadioButtonIsAlreadyChecked(view)) {
                        val dollarDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_MONTH)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_MONTH)?.first()?.close)
                        { it1, it2 -> calculateDollarDifference(it1,it2) }
                        val percentageDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_MONTH)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_MONTH)?.first()?.close)
                        { it1, it2 -> calculatePercentageDifference(it1,it2) }

                        layout.text_view_stock_change.apply {
                            if (percentageDifference != null) {
                                if(percentageDifference >= 0) {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
                                } else {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
                                }
                            }
                        }

                        layout.text_view_stock_change.text = "\$$dollarDifference ($percentageDifference%)"
                        setupSparkView(layout,Constants.ONE_MONTH)

                        if (percentageDifference != null) {
                            if(percentageDifference >= 0) {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_above_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_green_selected_rounded)
                            } else {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_below_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_orange_selected_rounded)

                            }
                        }
                        view.setTextColor(ContextCompat.getColor(applicationContext,R.color.white))
                    }
                R.id.six_month_radio_button ->
                    if(checked && checkIfRadioButtonIsAlreadyChecked(view)) {
                        val dollarDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.SIX_MONTHS)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.SIX_MONTHS)?.first()?.close)
                        { it1, it2 -> calculateDollarDifference(it1,it2) }
                        val percentageDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.SIX_MONTHS)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.SIX_MONTHS)?.first()?.close)
                        { it1, it2 -> calculatePercentageDifference(it1,it2) }

                        layout.text_view_stock_change.apply {
                            if (percentageDifference != null) {
                                if(percentageDifference >= 0) {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
                                } else {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
                                }
                            }
                        }

                        layout.text_view_stock_change.text = "\$$dollarDifference ($percentageDifference%)"
                        setupSparkView(layout,Constants.SIX_MONTHS)

                        if (percentageDifference != null) {
                            if(percentageDifference >= 0) {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_above_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_green_selected_rounded)
                            } else {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_below_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_orange_selected_rounded)

                            }
                        }
                        view.setTextColor(ContextCompat.getColor(applicationContext,R.color.white))
                    }
                R.id.one_year_radio_button ->
                    if(checked && checkIfRadioButtonIsAlreadyChecked(view)) {
                        val dollarDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_YEAR)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_YEAR)?.first()?.close)
                        { it1, it2 -> calculateDollarDifference(it1,it2) }
                        val percentageDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_YEAR)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_YEAR)?.first()?.close)
                        { it1, it2 -> calculatePercentageDifference(it1,it2) }

                        layout.text_view_stock_change.apply {
                            if (percentageDifference != null) {
                                if(percentageDifference >= 0) {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
                                } else {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
                                }
                            }
                        }

                        layout.text_view_stock_change.text = "\$$dollarDifference ($percentageDifference%)"
                        setupSparkView(layout,Constants.ONE_YEAR)

                        if (percentageDifference != null) {
                            if(percentageDifference >= 0) {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_above_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_green_selected_rounded)
                            } else {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_below_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_orange_selected_rounded)

                            }
                        }
                        view.setTextColor(ContextCompat.getColor(applicationContext,R.color.white))
                    }
                R.id.five_year_radio_button ->
                    if(checked && checkIfRadioButtonIsAlreadyChecked(view)) {
                        val dollarDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.FIVE_YEARS)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.FIVE_YEARS)?.first()?.close)
                        { it1, it2 -> calculateDollarDifference(it1,it2) }
                        val percentageDifference = safeLetTwo(
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.FIVE_YEARS)?.last()?.close,
                            viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.FIVE_YEARS)?.first()?.close)
                        { it1, it2 -> calculatePercentageDifference(it1,it2) }

                        layout.text_view_stock_change.apply {
                            if (percentageDifference != null) {
                                if(percentageDifference >= 0) {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
                                } else {
                                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
                                }
                            }
                        }

                        layout.text_view_stock_change.text = "\$$dollarDifference ($percentageDifference%)"
                        setupSparkView(layout,Constants.FIVE_YEARS)

                        if (percentageDifference != null) {
                            if(percentageDifference >= 0) {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_above_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_green_selected_rounded)
                            } else {
                                unCheckPreviousSelection(
                                    radioGroup,
                                    ContextCompat.getColor(applicationContext,R.color.color_for_stock_below_open),
                                    view.id)
                                view.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.range_button_orange_selected_rounded)

                            }
                        }
                        view.setTextColor(ContextCompat.getColor(applicationContext,R.color.white))
                    }
            }

        }
    }

    private fun unCheckPreviousSelection(radioGroup: RadioGroup , textColor : Int,checkedRadioButtonID : Int) {
        for(radioButton in radioGroup) {
            if(radioButton.id != checkedRadioButtonID) {
                findViewById<RadioButton>(radioButton.id).setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.black))
                findViewById<RadioButton>(radioButton.id).setTextColor(textColor)
            }
        }
    }

    fun searchFragment() {
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in_right_to_left,R.anim.slide_out_right_to_left)
            replace<SearchFragment>(R.id.main_activity_fragment_container_view)
            setReorderingAllowed(true)
        }
    }


    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSparkView(view : View, range : String) : Boolean {
        val filteredList : MutableList<Float> = mutableListOf()
        val viewModel : UserProfileViewModel by viewModels()
        viewModel.listOfOneStockHistoricalPrices.value?.data?.get(range)?.forEach {
            it.close?.let { it1 -> filteredList.add(it1) } }
        Timber.e("filtered list => $filteredList")
        val isStockHigherThanAtBeginning  = filteredList.last() >= filteredList.first()
        val myAdapter = TestSparkAdapter(filteredList)
        Timber.e("$view")

        view.single_user_asset_sparkview.apply {
            adapter = myAdapter
            lineColor = if(isStockHigherThanAtBeginning) {
                ContextCompat.getColor(context,R.color.color_for_stock_above_open)
            } else {
                ContextCompat.getColor(context,R.color.color_for_stock_below_open)
            }
            val baseLinePaint : Paint = single_user_asset_sparkview.baseLinePaint
            val dashpathEffect  = DashPathEffect(floatArrayOf(10.0F, 10.0F),0F)
            baseLinePaint.pathEffect = dashpathEffect
            baseLineColor = ContextCompat.getColor(context,R.color.white)
            val daySoFarRatio : Double  = daySoFarTradingRatio()
            if(daySoFarRatio >=  0) {
                val rightPadding = view.width - (view.width * daySoFarRatio)
                setPadding(0,40,rightPadding.toInt(),40)
            }
            setPadding(0,40,0,40)
        }
        return isStockHigherThanAtBeginning

    }

    private fun checkIfRadioButtonIsAlreadyChecked(radioButton: RadioButton) : Boolean {
        //int color of white is -1
        return radioButton.currentTextColor != ContextCompat.getColor(applicationContext,R.color.white)
    }

    fun slideInUserAssetsFragment() {
        viewModel.currentUserStockList.observe(this, Observer {
            when (it.status){
                Result.Status.LOADING -> {
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
                        replace<LoadingScreenFragment>(R.id.main_activity_fragment_container_view)
                        setReorderingAllowed(true)
                    }
                }
                Result.Status.SUCCESS -> {
                    Timber.e("Successfully loaded asset.")
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.slide_in_left_to_right,R.anim.slide_out_left_to_right)
                        replace<UserAssetsFragment>(R.id.main_activity_fragment_container_view)
                        setReorderingAllowed(true)
                    }
                }
                Result.Status.ERROR -> {
                    supportFragmentManager.commit {
                        replace<UserAssetsFragment>(R.id.main_activity_fragment_container_view)
                        setReorderingAllowed(true)
                    }
                    Toast.makeText(
                        this,
                        "Error loading Data. Please Try Again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    fun showLoadingUserFragment() {
        supportFragmentManager.commit {
            setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
            replace<LoadingScreenFragment>(R.id.main_activity_fragment_container_view)
            setReorderingAllowed(true)
        }
    }

    private fun configureUserPreferences() {
        //TODO get user preferences from database

    }

}