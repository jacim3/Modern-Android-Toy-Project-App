package com.example.walkingpark.tabs.tab_1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.MainViewModel
import com.example.walkingpark.databinding.FragmentHomeBinding
import com.example.walkingpark.tabs.tab_2.ParkMapsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO 1. 동네예보 Api 연동 -> x,y 구하는 방법에 대한 고민 필요.
// TODO 미세먼지 정보를 가져오는 가장 쉬운 방법

@AndroidEntryPoint
class HomeFragment : Fragment() {

    //private val mainViewModel by activityViewModels<MainViewModel>()
    val mainViewModel:MainViewModel by viewModels()
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


    /*    //TODO DI 를통하여 주입받은 ViewModel 이 Observer 패턴이 적용되지 않음.

        mainViewModel.userAddressHolder.observe(viewLifecycleOwner){
            Log.e("received2", it.toString())
        }
        mainViewModel.userLocationHolder.observe(viewLifecycleOwner) {
            Log.e("received1", it.toString())
        }
        mainViewModel.userStationHolder.observe(viewLifecycleOwner) {
            CoroutineScope(Dispatchers.IO).launch {
                Log.e("received3",it.stationName)
                mainViewModel.getAirDataFromApi(it.stationName)
            }
        }*/
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