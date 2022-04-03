package com.example.walkingpark.fragment_tab_1

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.walkingpark.R
import com.example.walkingpark.databinding.FragmentHomeBinding
import com.example.walkingpark.fragment_tab_2.ParkMapsViewModel

class HomeFragment : Fragment() {

    private lateinit var viewModel: ParkMapsViewModel
    private var binding: FragmentHomeBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("HomeFragment()", "onAttach()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("HomeFragment()", "onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        Log.e("HomeFragment()", "onCreateView()")

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("HomeFragment()", "onViewCreated()")

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.e("HomeFragment()", "onViewStateRestored()")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        Log.e("HomeFragment()", "onDestroyView()")
    }

    override fun onStop() {
        super.onStop()
        Log.e("HomeFragment()", "onStop()")
    }

    companion object {
        fun newInstance() = Fragment()

    }
}