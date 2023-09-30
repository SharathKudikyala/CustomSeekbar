package com.app.customseekbar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.customseekbar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customiseSeekbar()
    }

    private fun customiseSeekbar() {
        binding.slider1.setTickCount(4)
        binding.slider2.setTickCount(3)
        binding.slider3.setTickCount(1)
        binding.slider4.setTickCount(2)
        binding.slider5.setTickCount(5)

        binding.slider5.setThumbIndex(4)
    }
}