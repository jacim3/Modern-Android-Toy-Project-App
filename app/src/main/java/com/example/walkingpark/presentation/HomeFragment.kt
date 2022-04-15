package com.example.walkingpark.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.walkingpark.databinding.FragmentHomeBinding
import com.example.walkingpark.presentation.viewmodels.HomeViewModel
import com.example.walkingpark.presentation.viewmodels.MainViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

// TODO 1. 동네예보 Api 연동 -> x,y 구하는 방법에 대한 고민 필요.
// TODO 미세먼지 정보를 가져오는 가장 쉬운 방법

@AndroidEntryPoint
class HomeFragment : Fragment() {

/*
    기존 뷰모델 생성법 : private val searchViewModel: SearchViewModel by viewModels()
    프래그먼트- 액티비티간 뷰모델 공유 : private val searchViewModel: SearchViewModel by activityViewModels()
    프래그먼트끼리 뷰모델 공유 : private val viewModel: ManageLocationViewModel by viewModels({requireParentFragment()})
*/

    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeVIewModel: HomeViewModel by viewModels()
    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("homeFragment", mainViewModel.hashCode().toString())

        mainViewModel.userLiveHolderLatLng.observe(viewLifecycleOwner){
            startApiConnection(it)
            it.also {  }
        }
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

    private fun startApiConnection(latLng: LatLng){

    }
}