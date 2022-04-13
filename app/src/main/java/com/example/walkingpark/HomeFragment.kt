package com.example.walkingpark

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.walkingpark.viewmodels.MainViewModel
import com.example.walkingpark.databinding.FragmentHomeBinding
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

        binding!!.mainViewModel = mainViewModel
        binding!!.lifecycleOwner = this

        Log.e("HomeFragment()", "onCreateView()")

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("HomeFragment()", "onViewCreated()")

        Log.e("homeFragment", mainViewModel.hashCode().toString())


    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.e("HomeFragment()", "onViewStateRestored()")

        mainViewModel.userLiveHolderWeather.observe(viewLifecycleOwner) {

        }
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