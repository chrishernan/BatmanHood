package com.example.batmanhood.fragments

import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import com.example.batmanhood.R
import com.example.batmanhood.activities.MainActivity
import com.example.batmanhood.adapters.TestSparkAdapter
import com.example.batmanhood.utils.*
import com.example.batmanhood.viewModels.UserProfileViewModel
import kotlinx.android.synthetic.main.fragment_single_asset.*
import kotlinx.android.synthetic.main.fragment_single_asset.view.*
import kotlinx.android.synthetic.main.single_asset_toolbar.view.*
import timber.log.Timber

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "symbol"

/**
 * A simple [Fragment] subclass.
 * Use the [singleAssetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class singleAssetFragment : Fragment() {
    private var stockSymbol: String? = "param1"
    private val viewModel : UserProfileViewModel by activityViewModels()
    private var isAssetAddedToUserList : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val supportFragmentManager : FragmentManager? = activity?.supportFragmentManager
        activity?.onBackPressedDispatcher?.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                (activity as MainActivity).slideInUserAssetsFragment()
            }
        })
        //todo make it so that exception is thrown if param1 is null
        arguments?.let {
            stockSymbol = it.getString("symbol")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!(viewModel.currentUserStockList.value?.data?.containsKey(stockSymbol))!!){
            isAssetAddedToUserList = false
        }
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_single_asset, container, false)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeToolbarListeners()

        //sets the correct image for the add_to_watch button
        val singleAssetAddButton = single_asset_toolbar_include.findViewById<ImageButton>(R.id.single_asset_fragment_add_button)
        if(!isAssetAddedToUserList){
            singleAssetAddButton.setImageResource(R.drawable.ic_add_to_watch_button_ripple_bordered)
        }
        //Sets text of the different text views above the sparkyview
        text_view_stock_symbol.text = stockSymbol
        text_view_stock_price.text = viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.last()?.close.toString()

        //calculates the differences between open and close/latest stock price
        val dollarDifference = safeLetTwo(
                viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.last()?.close,
                viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.first()?.close)
        { it1, it2 -> calculateDollarDifference(it1,it2) }
        val percentageDifference = safeLetTwo(
                viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.last()?.close,
                viewModel.listOfOneStockHistoricalPrices.value?.data?.get(Constants.ONE_DAY)?.first()?.close)
        { it1, it2 -> calculatePercentageDifference(it1,it2) }

        view.text_view_stock_change.apply {
            if (percentageDifference != null) {
                if(percentageDifference >= 0) {
                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
                } else {
                    setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
                }
            }
        }
        text_view_stock_change.text = "\$$dollarDifference ($percentageDifference%)"

        //sets up the sparky view for the asset
        val isStockHigherThanAtBeginning = setupSparkView(view,Constants.ONE_DAY)

        //sets the color of the range radio buttons
        view.one_day_radio_button.apply {
            setTextColor(ContextCompat.getColor(context,R.color.white))
            if(isStockHigherThanAtBeginning) {
                background = ContextCompat.getDrawable(context,R.drawable.range_button_green_selected_rounded)
            } else {
                background = ContextCompat.getDrawable(context,R.drawable.range_button_orange_selected_rounded)
            }
        }

        view.five_day_radio_button.apply {
            if(isStockHigherThanAtBeginning) {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
            } else {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
            }
        }

        view.one_month_radio_button.apply {
            if(isStockHigherThanAtBeginning) {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
            } else {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
            }
        }

        view.six_month_radio_button.apply {
            if(isStockHigherThanAtBeginning) {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
            } else {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
            }
        }

        view.one_year_radio_button.apply {
            if(isStockHigherThanAtBeginning) {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
            } else {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
            }
        }

        view.five_year_radio_button.apply {
            if(isStockHigherThanAtBeginning) {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_above_open))
            } else {
                setTextColor(ContextCompat.getColor(context,R.color.color_for_stock_below_open))
            }
        }

        //fills in the textviews for the statistics section
        val currentStockInfo = viewModel.currentUserStockList.value?.data?.get(stockSymbol)
        if (currentStockInfo != null) {
            view.single_user_fragment_open_price.text = currentStockInfo.open.toString()
            view.single_user_fragment_volume.text = currentStockInfo.volume.toString()
            view.single_user_fragment_today_high.text = currentStockInfo.high.toString()
            view.single_user_fragment_average_volume.text = currentStockInfo.avgTotalVolume.toString()
            view.single_user_fragment_today_low.text = currentStockInfo.low.toString()
            view.single_user_fragment_market_cap.text = currentStockInfo.marketCap.toString()
            view.single_user_fragment_52_week_high.text = currentStockInfo.week52High.toString()
            view.single_user_fragment_52_week_low.text = currentStockInfo.week52Low.toString()
            view.single_user_fragment_pe_ratio.text = currentStockInfo.peRatio.toString()
            view.single_user_fragment_previous_close.text = currentStockInfo.previousClose.toString()
            single_asset_toolbar_include.findViewById<TextView>(R.id.single_asset_fragment_toolbar_title)
                .text = currentStockInfo.companyName
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeToolbarListeners() {
        val singleAssetToolbar = single_asset_toolbar_include
        val singleAssetAddButton = single_asset_toolbar_include.findViewById<ImageButton>(R.id.single_asset_fragment_add_button)
        singleAssetToolbar.single_asset_fragment_back_button.setOnClickListener {
            (activity as MainActivity).slideInUserAssetsFragment()
        }

        singleAssetToolbar.single_asset_fragment_add_button.setOnClickListener {
            isAssetAddedToUserList = if(isAssetAddedToUserList) {
                Timber.e("isAssetAddedToList  should be true => $isAssetAddedToUserList")
                singleAssetAddButton.setImageResource(R.drawable.ic_add_to_watch_button_ripple_bordered)
                viewModel.removeAssetFromUser(stockSymbol!!)
                false
            } else {
                Timber.e("isAssetAddedToList should be false=> $isAssetAddedToUserList")

                singleAssetAddButton.setImageResource(R.drawable.ic_checked_off_button_ripple_bordered)
                viewModel.addNewAssetToUser(stockSymbol!!)
                true
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun setupSparkView(view : View,range : String) : Boolean {
        val filteredList : MutableList<Float> = mutableListOf()
        viewModel.listOfOneStockHistoricalPrices.value?.data?.get(range)?.forEach {
            it.close?.let { it1 -> filteredList.add(it1) } }
        Timber.e("filtered list => $filteredList")
        val isStockHigherThanAtBeginning  = filteredList.last() >= filteredList.first()
        val myAdapter = TestSparkAdapter(filteredList)

        single_user_asset_sparkview.apply {
            adapter = myAdapter
            if(isStockHigherThanAtBeginning) {
                lineColor = ContextCompat.getColor(context,R.color.color_for_stock_above_open)
            } else {
                lineColor = ContextCompat.getColor(context,R.color.color_for_stock_below_open)
            }
            var baseLinePaint : Paint = single_user_asset_sparkview.baseLinePaint
            var dashpathEffect  = DashPathEffect(floatArrayOf(10.0F, 10.0F),0F)
            baseLinePaint.pathEffect = dashpathEffect
            baseLineColor = ContextCompat.getColor(context,R.color.white)
            val daySoFarRatio : Double  = daySoFarTradingRatio()
            val parentView = view.parent as View
            if(daySoFarRatio >=  0) {
                val rightPadding = parentView.width - (parentView.width * daySoFarRatio)
                setPadding(0,40,rightPadding.toInt(),40)
            }
            setPadding(0,40,0,40)
        }
        return isStockHigherThanAtBeginning

    }


    override fun onDestroy() {
        Timber.e("On destroy")
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param stockSymbol Parameter 1.
         * @return A new instance of fragment singleAssetFragment.
         */
        @JvmStatic
        fun newInstance(stockSymbol: String) =
            singleAssetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, stockSymbol)
                }
            }
    }
}