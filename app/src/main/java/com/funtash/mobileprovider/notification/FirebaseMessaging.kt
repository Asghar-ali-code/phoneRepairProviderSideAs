package com.funtash.mobileprovider.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.funtash.mobileprovider.livedata.View.Activity.ActivityHome
import com.funtash.mobileprovider.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class FirebaseMessaging : FirebaseMessagingService() {
    private val ADMIN_CHANNEL_ID = "admin_channel"
    var intent: Intent? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handelMessage(remoteMessage.data)

    }

    private fun handelMessage(map: Map<String, String>) {
        if (map.isNotEmpty()) {
            val title = map["title"]
            val desc = map["description"]
            val action = map["action"]
            intent = Intent(this, ActivityHome::class.java)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //intent!!.putExtra("action", action)
            val notificationID = Random().nextInt(3000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels(notificationManager, desc)
            }
            intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val pendingIntent = PendingIntent.getActivity(
                this, notificationID, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
            val largeIcon = BitmapFactory.decodeResource(
                resources,
                R.mipmap.ic_launcher
            )
           /* val mPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.sunrise)
            mPlayer.start()*/
            val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notifi = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(desc)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            notificationManager.notify(notificationID, notifi.build())
            /*}else{
                Log.e("user", "Firebase: "+"deleted")
                Prefs.clear().commit()
            }*/

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(
        notificationManager: NotificationManager?,
        desc: String?
    ) {
        val adminChannelName = "Channel_NAME"
        val sounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL)


        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        adminChannel.description = desc
        adminChannel.enableLights(true)
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }
}