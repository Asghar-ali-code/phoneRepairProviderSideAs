package com.funtash.mobileprovider.Utils

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.funtash.mobileprovider.R
import com.google.android.material.snackbar.Snackbar
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog

object Utility {

    lateinit var alertDialog : LottieAlertDialog
    lateinit var  successDlg : LottieAlertDialog

    fun Dlg(context: Context, title:String, message:String){
        var alertDialog : LottieAlertDialog = LottieAlertDialog.Builder(context, DialogTypes.TYPE_LOADING)
            .setTitle(title)
            .setDescription(message)
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    fun showDlg(context: Context){
        alertDialog.show()
    }
    fun hideDlg(context: Context){
        alertDialog.dismiss()
    }

    fun SuccessDlg(context: Context,title: String,message: String){

        successDlg =LottieAlertDialog.Builder(context,DialogTypes.TYPE_SUCCESS)
            .setTitle(title)
            .setDescription(message)
            .setPositiveText("OK")
            .setPositiveButtonColor(Color.parseColor("#f44242"))
            .setPositiveTextColor(Color.parseColor("#ffeaea"))
            // Error View
            .setPositiveListener(object: ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    // This is the usage same instance of view
                   successDlg.dismiss()
                }
            })
        .build()
        successDlg.show()
    }

    //display SnackBar
    fun displaySnackBar(view: View, s: String, context: Context) {

        Snackbar.make(view, s, Snackbar.LENGTH_LONG)
            .withColor(ContextCompat.getColor(context, R.color.blue))
            .setTextColor(ContextCompat.getColor(context, R.color.white))
            .show()

    }

    //display warningSnackBar
    fun warningSnackBar(view: View, s: String, context: Context) {

        Snackbar.make(view, s, Snackbar.LENGTH_LONG)
            .withColor(ContextCompat.getColor(context, R.color.gray))
            .setTextColor(ContextCompat.getColor(context, R.color.blue))
            .show()

    }
    //display errorSnackBar
    fun errorSnackBar(view: View, s: String, context: Context) {

        Snackbar.make(view, s, Snackbar.LENGTH_LONG)
            .withColor(ContextCompat.getColor(context, R.color.red_app))
            .setTextColor(ContextCompat.getColor(context, R.color.white))
            .show()

    }

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }
}