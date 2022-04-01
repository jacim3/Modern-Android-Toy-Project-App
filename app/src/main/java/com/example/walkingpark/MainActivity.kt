package com.example.walkingpark

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.walkingpark.databinding.ActivityMainBinding
import com.example.walkingpark.factory.PublicApiViewModelFactory
import com.example.walkingpark.repository.PublicApiRepository

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(
            this,
            PublicApiViewModelFactory(PublicApiRepository(this))
        )[MainViewModel::class.java]

        viewModel.getParkDataSet(this)

        viewModel.parkDataResponse.observe(this) {
            if (it != null) {
                Log.e("123123", it.body()!!.response.body.items[0].toString())
            }
        }
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
    }

    companion object {

    }
}