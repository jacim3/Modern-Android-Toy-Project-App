package com.example.walkingpark.template_resource

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.walkingpark.tabs.tab_2.ParkMapsViewModel

/*
* 프래그먼트 간편 생성을 위한 복붙용 템플릿.
* */

class FragmentTemplate : Fragment() {

    private lateinit var viewModel: ParkMapsViewModel
    //private var binding: ParkMapsFragmentBinding? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

/*    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ParkMapsFragmentBinding.inflate(layoutInflater, container, false)

        return binding!!.root
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }

    companion object {
        fun newInstance() = Fragment()
    }

}