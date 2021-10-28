package com.funtash.mobileprovider.livedata.View.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.livedata.View.Frag.HomeFragment
import com.funtash.mobileprovider.livedata.View.Frag.ProfileFragment

class ActivitySetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.containersetting,
                ProfileFragment()
            ).commit()
        }
    }
}