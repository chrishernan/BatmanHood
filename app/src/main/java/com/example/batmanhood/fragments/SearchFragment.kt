package com.example.batmanhood.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import com.example.batmanhood.R
import com.example.batmanhood.activities.MainActivity
import com.example.batmanhood.adapters.AutoCompleteAssetAdapter
import com.example.batmanhood.models.AutofillCompany
import com.example.batmanhood.models.Result
import com.example.batmanhood.viewModels.UserProfileViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private val viewModel : UserProfileViewModel by activityViewModels()
    private val supportFragmentManager : FragmentManager? = activity?.supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val supportFragmentManager : FragmentManager? = activity?.supportFragmentManager
        activity?.onBackPressedDispatcher?.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                (activity as MainActivity).slideInUserAssetsFragment()
            }
        })

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val autocompleteTextView : AutoCompleteTextView = view.findViewById(R.id.autocomplete_search_bar)
        val autocompleteAdapter =
            viewModel.americanCompaniesList.value?.data?.let {
                AutoCompleteAssetAdapter(
                    requireContext(),
                    R.layout.autocomplete_custom_layout,
                    it)
            }
        autocompleteTextView.setAdapter(autocompleteAdapter)

        autocompleteTextView.onItemClickListener  =
            AdapterView.OnItemClickListener{ parent, view, position, id ->
                val selectedItem : AutofillCompany = parent.getItemAtPosition(position) as AutofillCompany
                viewModel.clearOneStockHistoricalDataLiveData()
                selectedItem.symbol?.let { (activity as MainActivity).singleAssetFragment(it) }
            }


        // Set a dismiss listener for auto complete text view
        autocompleteTextView.setOnDismissListener {
            Toast.makeText(context,"Suggestion closed.", Toast.LENGTH_SHORT).show()
        }


        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}