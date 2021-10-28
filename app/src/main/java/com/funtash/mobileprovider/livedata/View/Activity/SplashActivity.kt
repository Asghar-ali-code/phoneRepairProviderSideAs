package com.funtash.mobileprovider.livedata.View.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.databinding.ActivityMainBinding
import com.pixplicity.easyprefs.library.Prefs


class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UI()
    }

    private fun UI() {

        Prefs.initPrefs(this)
        val window: Window = this.window

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)


        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        Handler().postDelayed(Runnable {
            if(!Prefs.getString("api_token","").equals("")){
                val intent = Intent(this@SplashActivity, ActivityHome::class.java)
                startActivity(intent)
                finish()
            }
            else {
                val intent = Intent(this@SplashActivity, ActivityLogin::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}