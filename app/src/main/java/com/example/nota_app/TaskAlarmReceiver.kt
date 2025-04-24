package com.example.nota_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("TASK_TITLE") ?: "مهمة قادمة"
        val message = intent?.getStringExtra("TASK_MESSAGE") ?: "حان وقت تنفيذ المهمة!"

        showNotification(context, title, message)
    }

    private fun showNotification(context: Context?, title: String, message: String) {
        val channelId = "task_reminder_channel"
        val notificationId = kotlin.random.Random.nextInt() // تعديل هنا لاستخدام Random بشكل صحيح

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // إنشاء القناة للإشعارات في الأنظمة الجديدة (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}
