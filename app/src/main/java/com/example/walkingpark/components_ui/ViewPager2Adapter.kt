package com.example.walkingpark.components_ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.walkingpark.MainActivity
import com.example.walkingpark.fragment_tab_2.ParkMapsFragment

class ViewPager2Adapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    // 뷰페이저에서 출력할 프래그먼트 갯수
    override fun getItemCount(): Int {
        return MainActivity.MAIN_PAGER_TAB_NUMBER
    }

    // Position 에 따라 리턴할 프래그먼트 설정
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> Fragment()
            1 -> ParkMapsFragment()
            2 -> Fragment()
            else -> Fragment()
        }
    }
}