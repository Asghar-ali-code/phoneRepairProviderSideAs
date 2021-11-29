package com.funtash.mobileprovider.Utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.funtash.mobileprovider.R
import com.google.android.material.snackbar.Snackbar
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import pl.droidsonroids.gif.GifDrawable

object CustomLoader {

    lateinit var customdlg : Dialog

    fun CustomDlg(context: Context, message:String){

        customdlg= Dialog(context)
        customdlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customdlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customdlg.setContentView(R.layout.loader_dialog)
        customdlg.setCancelable(false)


        val message= customdlg.findViewById<TextView>(R.id.text)
        //message.text= message.toString()

        customdlg.show()
    }
    fun showDlg(context: Context){
        customdlg.show()
    }
    fun hideDlg(context: Context){
        customdlg.dismiss()
    }



}