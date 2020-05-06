package com.mahesch.trymyui.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.mahesch.trymyui.R
import com.mahesch.trymyui.services.NativeAppRecordingService


/*This class show the trymyui icon in status bar if the recorder is on*/

class MoveToForeGround {

    companion object{

         fun moveToForeGround(context: Context,availableTestId : String){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationIconForOreoAndAbove(context,availableTestId)
            }
            else{
                notificationIconForLessThanOreo(context,availableTestId)
            }
        }

        private fun notificationIconForLessThanOreo(context: Context,availableTestId : String){

            val notification_msg = "Performing test #$availableTestId"
            val notification: Notification = Notification.Builder(context)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("TryMyUI")
                .setContentText(notification_msg)
                .setAutoCancel(false)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .build()
            (context as NativeAppRecordingService).startForeground(1, notification)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun notificationIconForOreoAndAbove(context: Context, availableTestId : String){
            val NOTIFICATION_CHANNEL_ID = "com.seattleapplab.trymyui"
            val channel_name = "My Background Service"

            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channel_name, NotificationManager.IMPORTANCE_NONE)

            notificationChannel.lightColor = Color.BLUE
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!

            manager!!.createNotificationChannel(notificationChannel)

            val notificationBuilder =
                Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            val notification: Notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("App is running in background")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            (context as NativeAppRecordingService).startForeground(2, notification)
        }
    }





}