package com.funtash.mobileprovider.livedata.View.Adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.funtash.mobileprovider.R
import com.funtash.mobileprovider.response.ChatClass
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MessageAdapter(context: Context, chatList: ChatClass) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private val context: Context = context
    private val chatList: ChatClass = chatList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == message_right) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.right_message_layout, parent, false)
            ViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.left_message_layout, parent, false)
            ViewHolder(view)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.message.text = chatList.data[position].message.toString()
            holder.time.text = parseDateToddMMyyyy(chatList.data[position].created_at.toString())

        }catch (e:Exception){

        }
    }

    override fun getItemCount(): Int {
        return chatList.data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.tvmessage)
        var time: TextView = itemView.findViewById(R.id.tvtime)

    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList.data[position].message_type.equals("send",true)) {
            Log.e("view", "right"+chatList.data[position].message_type)
            message_right
        } else {
            Log.e("view","right"+chatList.data[position].message_type)
            message_left
        }
    }



    companion object {
        const val message_left = 0
        const val message_right = 1
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun parseDateToddMMyyyy(time: String?): String? {
        return try {
            val inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";
            //val inputPattern = "yyyy-MM-dd HH:mm"
            val outputPattern = "HH:mm"
            var inputFormat: SimpleDateFormat? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputFormat = SimpleDateFormat(inputPattern)
            }
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                assert(inputFormat != null)
                date = inputFormat!!.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            str
        } catch (e: java.lang.Exception) {
            Log.e("exception", e.message!!)
            ""
        }
    }
}
