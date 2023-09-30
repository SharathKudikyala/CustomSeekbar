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
        /*binding.slider1.setTickCount(2)
        binding.slider2.setTickCount(4)
        binding.slider2.setThumbIndex(2)*/
    }
}