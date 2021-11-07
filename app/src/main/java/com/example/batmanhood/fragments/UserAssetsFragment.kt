package com.example.batmanhood.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batmanhood.R
import com.example.batmanhood.activities.MainActivity
import com.example.batmanhood.adapters.AssetRecyclerViewAdapter
import com.example.batmanhood.utils.MarginItemDecoration
import com.example.batmanhood.utils.safeLetThree
import com.example.batmanhood.viewModels.UserProfileViewModel
import kotlinx.android.synthetic.main.asset_recycler_view_row.*
import kotlinx.android.synthetic.main.fragment_user_assets.*
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 * Use the [userAssetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserAssetsFragment : Fragment(), AssetRecyclerViewAdapter.OnAssetListener {
    private val viewModel : UserProfileViewModel by activityViewModels()
    lateinit var renderingAssetAdapter : AssetRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(viewModel.listOfOneStockHistoricalPrices.value != null) {
            viewModel.clearOneStockHistoricalDataLiveData()
        }
        activity?.onBackPressedDispatcher?.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.onBackPressed()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user_assets, container, false)
        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = view.findViewById<Toolbar>(R.id.user_fragment_toolbar)
        val searchButton = view.findViewById<ImageButton>(R.id.toolbar_search_button)

        searchButton.setOnClickListener {
            (activity as MainActivity).stopObservingCurrentUserStockList()
            (activity as MainActivity).searchFragment()
        }

        renderingAssetAdapter =
            safeLetThree(
                viewModel.currentUserStockList.value?.data,
                viewModel.listOfUserStockHistoricalPrices.value?.data,
                viewModel.altUser.value?.data?.stock_list)
            { it1, it2, it3 -> AssetRecyclerViewAdapter(it1,it2,it3,this) } !!
        asset_recycler_view.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = renderingAssetAdapter
            addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
            addItemDecoration(MarginItemDecoration(
                    resources.getDimension(R.dimen.card_view_margin).toInt()))

        }

        //set the colors of the pull to refresh view
        stockSwipeToRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(requireActivity(), R.color.gray))
        //stockSwipeToRefresh.setColorSchemeColors(Color.WHITE)

        stockSwipeToRefresh.setOnRefreshListener {
            Toast.makeText(
                requireActivity(),
                "Refreshed page",
                Toast.LENGTH_LONG
                ).show()
            stockSwipeToRefresh.isRefreshing = false
        }

        super.onViewCreated(view, savedInstanceState)
        //todo add a scrubber for spark

        //todo add so that if user pulls down from this fragment it refreshes the requests. Might be more difficult
        //than normal. Have to somehow get the most current data
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment userAssetFragment.
         */
        @JvmStatic
        fun newInstance() = UserAssetsFragment()
    }

    override fun onAssetClick(view : View?, position: Int) {
        Timber.e("In onClick Method for fragment => $position and context => $context")
        (activity as MainActivity).stopObservingCurrentUserStockList()
        viewModel.altUser.value?.data?.stock_list?.get(position)?.let { (activity as MainActivity).singleAssetFragment(it) }
    }

    /**
     * removes asset viewModel and creates dialog
     */
    override fun onLongAssetClick(view: View?, position: Int) {
        Timber.e("In onLongClick Method for fragment => $position and context => $context")
        var builder : AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setCancelable(true)
        builder.setTitle("Delete asset")
        builder.setMessage("Would you like to delete this asset from your list?")
        builder.setPositiveButton("yes", DialogInterface.OnClickListener{ dialog, id ->
            //remove asset from Firebase and from User Object and historical data
            var assetToRemove= viewModel.altUser.value?.data?.stock_list?.get(position)?.toUpperCase()
            viewModel.removeAssetFromUser(assetToRemove)
            //remove from recyclerview
            renderingAssetAdapter.removeItem(assetToRemove,position)
            dialog.cancel()
        })
        builder.setNegativeButton("no", DialogInterface.OnClickListener{ dialog, id ->
            dialog.cancel()
        })
        var alert = builder.create()
        alert.show()

    }

}