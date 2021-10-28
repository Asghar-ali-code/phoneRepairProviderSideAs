package com.funtash.mobileprovider.livedata.View.Frag

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.funtash.mobileprovider.databinding.AccountFragBinding
import com.funtash.mobileprovider.databinding.FragmentDashboardBinding
import com.funtash.mobileprovider.livedata.View.Activity.ActivityLogin
import com.funtash.mobileprovider.livedata.View.Activity.ActivitySetting
import com.pixplicity.easyprefs.library.Prefs

class AccountFragment :Fragment() {

    var ctx: Context? = null
    private var api_token: String? = null
    private  lateinit var binding : AccountFragBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= AccountFragBinding.inflate(inflater)

        initUI()
        clicks()
       return binding.root
    }

    private fun initUI() {
        try {
            api_token=Prefs.getString("api_token","")
            val name = Prefs.getString("name", "")
            val email = Prefs.getString("email", "")
            val profie_pic = Prefs.getString("profile_pic", "")
            Glide.with(this)
                .load(profie_pic) //.placeholder(R.drawable.banner)
                .into(binding.ivUser)

            binding.tvname.text = name
            binding.tvemail.text = email
        }catch (e:Exception){
            Log.e("exp","error"+e.message)
        }

    }

    private fun clicks() {

        binding.logout.setOnClickListener {
            logout()
        }
        binding.Logout.setOnClickListener {
            logout()
        }
        binding.profile.setOnClickListener {
            startActivity(Intent(ctx,ActivitySetting::class.java))
        }
        binding.profile1.setOnClickListener {
            startActivity(Intent(ctx,ActivitySetting::class.java))
        }
    }

    fun logout() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(ctx)
        alert.setTitle("Logout")
        alert.setMessage("Are you sure you wish to logout?")
            .setCancelable(true)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    logOutwithApi()
                })
            .setNegativeButton("No", null)
        alert.show()
    }

    private fun logOutwithApi() {
        Prefs.clear()
        startActivity(Intent(ctx, ActivityLogin::class.java))
        activity?.finish()
    }
}