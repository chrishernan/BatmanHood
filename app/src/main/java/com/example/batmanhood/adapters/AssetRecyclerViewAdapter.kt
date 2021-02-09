package com.example.batmanhood.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.batmanhood.R

class AssetRecyclerViewAdapter(private val dataSet: LinkedHashMap<String,String>) : RecyclerView.Adapter<AssetRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val assetName : TextView
        val assetPrice : TextView

        init {
            assetName = view.findViewById(R.id.asset_name)
            assetPrice = view.findViewById(R.id.asset_price)

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AssetRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.asset_display,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssetRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.assetName.text = dataSet.keys.toTypedArray()[position]
        holder.assetPrice.text = dataSet.values.toTypedArray()[position]
    }

    override fun getItemCount(): Int  = dataSet.size


}