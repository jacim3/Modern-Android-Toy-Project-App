package com.example.walkingpark

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.walkingpark.databinding.ActivityMainBinding
import com.example.walkingpark.factory.PublicApiViewModelFactory
import com.example.walkingpark.repository.PublicDataApiRepository
import com.example.walkingpark.components_ui.ViewPager2Adapter
import com.example.walkingpark.database.singleton.ParkDataSet
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {


    private var binding: ActivityMainBinding? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding!!.lifecycleOwner = this
        viewModel = ViewModelProvider(
            this,
            PublicApiViewModelFactory(PublicDataApiRepository(this))
        )[MainViewModel::class.java]

        setViewPagerWithTabLayout()

        Log.e("getDataset", ParkDataSet.globalParkDataSet.records.size.toString())
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setViewPagerWithTabLayout() {
        val pagerAdapter = ViewPager2Adapter(supportFragmentManager, lifecycle)

        this.binding!!.circleIndicator.run {
            setViewPager(binding!!.viewPager2)
            createIndicators(MAIN_PAGER_TAB_NUMBER, 0)
        }

        binding!!.viewPager2.apply {
            adapter = pagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding!!.viewPager2.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding!!.circleIndicator.animatePageSelected(position % MAIN_PAGER_TAB_NUMBER)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })
        }

        binding!!.viewPager2.isUserInputEnabled = false

        TabLayoutMediator(
            binding!!.tabLayout,
            binding!!.viewPager2
        ) { tab: TabLayout.Tab?, position: Int ->
        }.attach()
    }

    companion object {
        const val MAIN_PAGER_TAB_NUMBER = 3
    }
}