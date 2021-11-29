package com.funtash.mobileprovider.livedata.View.Activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.databinding.ActivityHomeBinding
import com.funtash.mobileprovider.databinding.ActivitySettingBinding
import com.funtash.mobileprovider.livedata.View.Frag.*

class ActivitySetting : AppCompatActivity() {

    private var view: String? = "1"
    private var type: String? = ""
    private var selectedFragment: Fragment? = null
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        UI()
        clicks()

        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.containersetting,
                ProfileFragment()
            ).commit()
        }*/
    }

    private fun UI() {

        type=intent.getStringExtra("type")

        if(type.equals("lang")){
            languageFrag()
        }
        else if(type.equals("theme")){
            themeFrag()
        }
        else{
            supportFragmentManager.beginTransaction().replace(
                R.id.containersetting,
                ProfileFragment()
            ).commit()
        }

    }

    private fun clicks() {
        binding.btnprofile.setOnClickListener {
                profileFrag()
        }
        binding.btnlanguage.setOnClickListener {
                languageFrag()
        }
        binding.btntheme.setOnClickListener {
                themeFrag()
        }
    }

    private fun profileFrag() {
        selectedFragment= ProfileFragment()
        selectedFragment?.let {
            supportFragmentManager.beginTransaction().replace(
                R.id.containersetting,
                it
            ).commit()
        }
        when {
            view.equals("1") -> {
                binding.btnprofile.setTextColor(Color.parseColor("#000000"))
                binding.btnprofile.backgroundTintList = resources.
                getColorStateList(R.color.gray)
            }
            view.equals("2") -> {
                binding.btnlanguage.setTextColor(Color.parseColor("#000000"))
                binding.btnlanguage.backgroundTintList = resources.
                getColorStateList(R.color.gray)
            }
            view.equals("3") -> {
                binding.btntheme.setTextColor(Color.parseColor("#000000"))
                binding.btntheme.backgroundTintList = resources.
                getColorStateList(R.color.gray)
            }
        }

        binding.btnprofile.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.btnprofile.backgroundTintList = resources.
        getColorStateList(R.color.blue)

        view = "1"
    }

    private fun languageFrag() {
        selectedFragment= LanguageFrag()
        selectedFragment?.let {
            supportFragmentManager.beginTransaction().replace(
                R.id.containersetting,
                it
            ).commit()
        }
        when {
            view.equals("1") -> {
                binding.btnprofile.setTextColor(Color.parseColor("#000000"))
                binding.btnprofile.backgroundTintList = resources.
                getColorStateList(R.color.gray)
            }
            view.equals("2") -> {
                binding.btnlanguage.setTextColor(Color.parseColor("#000000"))
                binding.btnlanguage.backgroundTintList = resources.
                getColorStateList(R.color.gray)
            }
            view.equals("3") -> {
                binding.btntheme.setTextColor(Color.parseColor("#000000"))
                binding.btntheme.backgroundTintList = resources.
                getColorStateList(R.color.gray)
            }
        }
        binding.btnlanguage.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.btnlanguage.backgroundTintList = resources.
        getColorStateList(R.color.blue)
        view = "2"
    }

    private fun themeFrag() {
        selectedFragment= ThemeFrag()
        selectedFragment?.let {
            supportFragmentManager.beginTransaction().replace(
                R.id.containersetting,
                it
            ).commit()
        }
        when {
            view.equals("1") -> {
                binding.btnprofile.setTextColor(Color.parseColor("#000000"))
                binding.btnprofile.backgroundTintList = resources.
                getColorStateList(R.color.gray)
            }
            view.equals("2") -> {
                binding.btnlanguage.setTextColor(Color.parseColor("#000000"))
                binding.btnlanguage.backgroundTintList = resources.
                getColorStateList(R.color.gray)
            }
            view.equals("3") -> {
                binding.btntheme.setTextColor(Color.parseColor("#000000"))
                binding.btntheme.backgroundTintList = resources.
                getColorStateList(R.color.gray)
            }
        }
        binding.btntheme.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.btntheme.backgroundTintList = resources.
        getColorStateList(R.color.blue)

        view = "3"
    }

}