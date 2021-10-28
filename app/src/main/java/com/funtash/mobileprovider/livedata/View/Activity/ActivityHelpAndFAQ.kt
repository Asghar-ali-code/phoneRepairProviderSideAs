package com.funtash.mobileprovider.livedata.View.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.databinding.ActivityHelpAndFaqBinding
import com.funtash.mobileprovider.databinding.ActivityHomeBinding

class ActivityHelpAndFAQ : AppCompatActivity() {

    private lateinit var binding: ActivityHelpAndFaqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHelpAndFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)


        clicks()
    }

    private fun clicks() {
        binding.ivback.setOnClickListener {
            finish()
        }

    }
}