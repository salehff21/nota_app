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
import com.example.nota_app.data.TaskDatabaseHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId == -1) return

        // جلب بيانات المهمة من قاعدة البيانات
        val task = getTaskFromDatabase(context, taskId) ?: return

        // إنشاء الإشعار عند استلام التنبيه
        showNotification(context, task)

        // تشغيل الصوت مع التعامل مع الأخطاء
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

    private fun showNotification(context: Context, task: Task) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_reminder_channel"

        // إنشاء قناة الإشعارات إذا كان إصدار الأندرويد 8 أو أعلى
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "تنبيهات المهام", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // عند النقر على الإشعار، سيتم فتح التطبيق
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, task.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )




        // بناء الإشعار مع تحسين الشكل
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // شعار التطبيق
            .setContentTitle("📌 **مهامي اليومية**") // اسم التطبيق بخط بولد
            .setContentText("🔔 ${task.title} - ${task.priority}") // اسم المهمة + نوعها
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent) // فتح التطبيق عند الضغط

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // عرض الإشعار
        notificationManager.notify(task.id, notification)
        Log.d("AlarmReceiver", "✅ تم عرض الإشعار بنجاح")
    }

    // دالة جلب المهمة من قاعدة البيانات
    private fun getTaskFromDatabase(context: Context, taskId: Int): Task? {
        val dbHelper = TaskDatabaseHelper(context)
        return dbHelper.getTaskById(taskId)
    }
}
