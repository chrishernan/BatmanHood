package com.example.batmanhood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.batmanhood.R
import com.example.batmanhood.models.AutofillCompany

class AutoCompleteAssetAdapter(
        context : Context,
        resource : Int,
        listOfAssets : MutableList<AutofillCompany>) : ArrayAdapter<AutofillCompany>(context, resource, listOfAssets) {

    private var fullListOfAutofillCompany : MutableList<AutofillCompany>

    init {
        fullListOfAutofillCompany = listOfAssets.toMutableList()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var mutableConvertView = convertView
        if(convertView == null) {
            mutableConvertView =  LayoutInflater.from(context).inflate(R.layout.autocomplete_row,parent,false)
        }
        var assetNameTextView : TextView = mutableConvertView?.findViewById(R.id.auto_complete_asset_name) as TextView
        var assetSymbolTextView : TextView = mutableConvertView.findViewById(R.id.auto_complete_asset_symbol) as TextView

        var assetItem : AutofillCompany? = getItem(position)

        if(assetItem != null) {
            assetNameTextView.setText(assetItem.name)
            assetSymbolTextView.setText(assetItem.symbol)
        }

        return mutableConvertView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var results : FilterResults = FilterResults()
                var suggestions : MutableList<AutofillCompany> = mutableListOf()

                if(constraint == null || constraint.length == 0) {
                    suggestions.addAll(fullListOfAutofillCompany)
                }else {
                    var filterPattern : String = constraint.toString().toLowerCase().trim()
                    for(item in fullListOfAutofillCompany) {
                        if(item.name!!.toLowerCase().contains(filterPattern) ||
                                item.symbol!!.toLowerCase().contains(filterPattern)) {
                            suggestions.add(item)
                        }
                    }
                }
                results.values = suggestions
                results.count = suggestions.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                addAll(results!!.values as MutableList<AutofillCompany>)
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as AutofillCompany).name.toString()
            }

        }
    }




}