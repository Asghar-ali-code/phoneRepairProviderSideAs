package com.funtash.mobileprovider.livedata.View.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.funtash.mobileprovider.Api.RetrofitClient
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.Utils.GpsTracker
import com.funtash.mobileprovider.Utils.NoConnectivityException
import com.funtash.mobileprovider.Utils.Utility
import com.funtash.mobileprovider.databinding.ActivityHomeBinding
import com.funtash.mobileprovider.livedata.View.Frag.AccountFragment
import com.funtash.mobileprovider.livedata.View.Frag.HomeFragment
import com.funtash.mobileprovider.livedata.View.Frag.ReviewFragment
import com.funtash.mobileprovider.response.MessageClass
import com.funtash.mobilerepairinguserapp.Api.ApiInterface
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityHome : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Prefs.initPrefs(this)

        UI()
        clicks()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentContainer,
                HomeFragment()
            ).commit()
        }
        else
            Log.e("savedinstance","notnull")


        binding.ivDrawer.setOnClickListener {
            binding.drawer.openDrawer(GravityCompat.START)
        }

        binding.bottomNav.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null
            when{
                it==0->{
                    selectedFragment=HomeFragment()
                    binding.tvTitle.text="Mobile Repairing"
                    binding.tvTitle.setTextColor(Color.parseColor("#055C9D"))
                    binding.toolbar.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                it==1->{
                    selectedFragment=ReviewFragment()
                    binding.tvTitle.text="Reviews"
                    binding.tvTitle.setTextColor(Color.parseColor("#FFFFFFFF"))
                    binding.toolbar.setBackgroundColor(Color.parseColor("#055C9D"))
                }
                it==2->{
                    binding.tvTitle.text="Account"
                    selectedFragment=AccountFragment()
                    binding.tvTitle.setTextColor(Color.parseColor("#FFFFFFFF"))
                    binding.toolbar.setBackgroundColor(Color.parseColor("#055C9D"))
                }

            }
            selectedFragment?.let {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContainer,
                    it
                ).commit()
            }

        }
    }

    private fun UI() {

        try {
            val window: Window = this.window

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)


            // finally change the color
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

            val name = Prefs.getString("name", "")
            val email = Prefs.getString("email", "")
            val profie_pic = Prefs.getString("profile_pic", "")
            Glide.with(this)
                .load(profie_pic) //.placeholder(R.drawable.banner)
                .into(binding.circleImageView)

            binding.textV.text = name
            binding.textV1.text = email




        }catch (e:Exception){}

    }



    private fun clicks() {
        binding.logout.setOnClickListener {

            val alert: AlertDialog.Builder = AlertDialog.Builder(this)
            alert.setTitle("Logout")
            alert.setMessage("Are you sure you wish to logout?")
                .setCancelable(true)
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        Prefs.clear()
                        startActivity(Intent(this,ActivityLogin::class.java))
                        finish()
                    })
                .setNegativeButton("No", null)
            alert.show()

        }
        binding.notifications.setOnClickListener {
            startActivity(Intent(this,ActivityNotifaction::class.java))
        }

        binding.helpandpri.setOnClickListener {
            startActivity(Intent(this,ActivityHelpAndFAQ::class.java))
        }

        binding.ivNoti.setOnClickListener {
            startActivity(Intent(this,ActivityNotifaction::class.java))
        }

        binding.settings.setOnClickListener {
            startActivity(Intent(this,ActivitySetting::class.java))
        }

        binding.language.setOnClickListener {
            val intent= Intent(this,ActivitySetting::class.java)
            intent.putExtra("type","lang")
            startActivity(intent)
        }
        binding.darktheme.setOnClickListener {
            val intent= Intent(this,ActivitySetting::class.java)
            intent.putExtra("type","theme")
            startActivity(intent)
        }

    }

    override fun onRestart() {
        super.onRestart()
        Log.e("onrestart","onrestart")
        /*startActivity(Intent(this,HomeActivity::class.java))
        finish()*/
    }

    override fun onStart() {
        super.onStart()
        Log.e("onstart","onstart")
    }

    override fun onStop() {
        super.onStop()
        Log.e("onstop","onstop")
    }
    override fun onPause() {
        super.onPause()
        Log.e("onpause","onpause")
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        finishAffinity()
    }

    override fun onBackPressed() {
        if(binding.drawer.isDrawerOpen(GravityCompat.START)){
            binding.drawer.closeDrawers()
        }
        else
            finishAffinity()
    }
}
