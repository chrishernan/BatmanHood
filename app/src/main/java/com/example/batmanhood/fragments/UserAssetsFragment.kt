package com.example.batmanhood.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.batmanhood.R
import com.example.batmanhood.adapters.AssetRecyclerViewAdapter
import com.example.batmanhood.viewModels.UserProfileViewModel
import kotlinx.android.synthetic.main.fragment_user_assets.*

/**
 * A simple [Fragment] subclass.
 * Use the [userAssetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserAssetsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    /*recyclerView.adapter = assetRecyclerViewAdapter
    recyclerView.layoutManager = LinearLayoutManager(this)*/
    //Autocomplete variables

    private val autocompleteAssetList : MutableList<String> = mutableListOf("Amazon","Apple","Apples for me",
        "Apricot","Micron","Aircraft","Apple Pie",
        "Nio","Microsoft","Airbnb","Tesla","Sony","Bose","Delta Airlines","Google","Netflix")
    private val viewModel : UserProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user_assets, container, false)
        val autocompleteTextView : AutoCompleteTextView = root.findViewById(R.id.autocomplete_search_bar)
        val autocompleteAdapter : ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,autocompleteAssetList)
        autocompleteTextView.setAdapter(autocompleteAdapter)
        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val renderingAssetAdapter =
            viewModel.currentUserStockList.value?.let { it1 -> AssetRecyclerViewAdapter(it1) }
        asset_recycler_view.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = renderingAssetAdapter
        }
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
        fun newInstance() =
            UserAssetsFragment()
    }
}