package com.example.nota_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.nota_app.data.Task

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("TASK_TITLE") ?: "تنبيه"
        val message = intent.getStringExtra("TASK_MESSAGE") ?: "حان وقت المهمة!"

        // ✅ إنشاء الإشعار عند استلام التنبيه
        showNotification(context, title, message)

        // ✅ تشغيل الصوت مع التعامل مع الأخطاء
        try {
            val mediaPlayer = MediaPlayer.create(context, R.raw.notification_sound)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                it.release()
                Log.d("AlarmReceiver", "🎵 تم تشغيل الصوت بنجاح وإيقافه بعد الانتهاء")
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "❌ خطأ في تشغيل الصوت: ${e.message}")
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_reminder_channel"

        // ✅ إنشاء قناة الإشعارات إذا كان إصدار الأندرويد 8 أو أعلى
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "تنبيهات المهام", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // ✅ عند النقر على الإشعار، سيتم فتح التطبيق بدلاً من إغلاقه
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ✅ بناء الإشعار وتحسينه
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // استبدل بالأيقونة المناسبة
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent) // إضافة حدث النقر لفتح التطبيق
            .build()

        // ✅ عرض الإشعار
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        Log.d("AlarmReceiver", "✅ تم عرض الإشعار بنجاح")
    }
}
