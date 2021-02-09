package com.example.batmanhood.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.batmanhood.R
import kotlinx.android.synthetic.main.fragment_user_assets.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [userAssetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserAssetsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    var recyclerView : RecyclerView = asset_recycler_view
    /*recyclerView.adapter = assetRecyclerViewAdapter
    recyclerView.layoutManager = LinearLayoutManager(this)*/
    //Autocomplete variables
    val autocompleteTextView : AutoCompleteTextView = autocomplete_search_bar
    val autocompleteAssetList : MutableList<String> = mutableListOf("Amazon","Apple","Apples for me",
        "Apricot","Micron","Aircraft","Apple Pie",
        "Nio","Microsoft","Airbnb","Tesla","Sony","Bose","Delta Airlines","Google","Netflix")
    val autocompleteAdapter : ArrayAdapter<String> =
        ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,autocompleteAssetList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        autocompleteTextView.setAdapter(autocompleteAdapter)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_assets, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment userAssetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            UserAssetsFragment()
    }
}