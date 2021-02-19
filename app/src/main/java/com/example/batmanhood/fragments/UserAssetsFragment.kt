package com.example.batmanhood.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batmanhood.R
import com.example.batmanhood.activities.MainActivity
import com.example.batmanhood.adapters.AssetRecyclerViewAdapter
import com.example.batmanhood.adapters.AutoCompleteAssetAdapter
import com.example.batmanhood.models.AutofillCompany
import com.example.batmanhood.utils.MarginItemDecoration
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val autocompleteTextView : AutoCompleteTextView = root.findViewById(R.id.autocomplete_search_bar)
        val autocompleteAdapter =
                viewModel.americanCompaniesList.value?.let {
                    AutoCompleteAssetAdapter(
                            requireContext(),
                            R.layout.autocomplete_custom_layout,
                            it)
                }
        autocompleteTextView.setAdapter(autocompleteAdapter)
        autocompleteTextView.onItemClickListener =
                AdapterView.OnItemClickListener{ parent, view, position, id ->
            val selectedItem : AutofillCompany  = parent.getItemAtPosition(position) as AutofillCompany
            Toast.makeText(context,"Selected : ${selectedItem.name}",Toast.LENGTH_SHORT).show()
        }

        // Set a dismiss listener for auto complete text view
        autocompleteTextView.setOnDismissListener {
            Toast.makeText(context,"Suggestion closed.",Toast.LENGTH_SHORT).show()
        }
        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val renderingAssetAdapter =
            safeLet(
                    viewModel.currentUserStockList.value,
                    viewModel.listOfUserStockHistoricalPrices.value,
                    viewModel.altUser.value?.stock_list)
            { it1, it2, it3 -> AssetRecyclerViewAdapter(it1,it2,it3,this) }
        asset_recycler_view.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = renderingAssetAdapter
            addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
            addItemDecoration(MarginItemDecoration(
                    resources.getDimension(R.dimen.card_view_margin).toInt()))
        }




        //todo add a scrubber for spark
    }

    fun <T1: Any, T2: Any,T3: Any, R: Any> safeLet(p1: T1?, p2: T2?,p3: T3? ,block: (T1, T2,T3)->R?): R? {
        return if (p1 != null && p2 != null && p3 != null) block(p1, p2,p3) else null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment userAssetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = UserAssetsFragment()
    }

    override fun onAssetClick(view : View?, position: Int) {
        Timber.e("In onClick Method for fragment => $position and context => $context")
        (activity as MainActivity).singleAssetFragment(viewModel.currentUserStockList.value?.values?.toList()?.get(position)?.symbol.toString())
    }
}