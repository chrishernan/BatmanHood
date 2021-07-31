package com.example.batmanhood.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.batmanhood.R

/**
 * A simple [Fragment] subclass.
 * Use the [loadingScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoadingScreenFragment : Fragment() {
    //lateinit var mProgressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view: View = inflater.inflate(R.layout.fragment_loading_screen, container, false)
        //mProgressBar = ProgressBar(view.findViewById(R.id.loadingAssetsProgressBar))
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment loadingScreenFragment.
         */
        @JvmStatic fun newInstance() = LoadingScreenFragment()
    }
}