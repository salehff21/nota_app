package com.example.nota_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.nota_app.data.Task
import com.example.nota_app.data.TaskDatabaseHelper

class TaskAlarmReceiver(context1: Context) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val taskId = intent?.getIntExtra("TASK_ID", -1) ?: -1
            if (taskId == -1 || context == null) return

            // جلب بيانات المهمة من قاعدة البيانات SQLite
            val task = getTaskFromDatabase(context, taskId) ?: return

            showNotification(context, task)
        }

    private fun showNotification(context: Context, task: Task) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_reminder_channel"
        val notificationId = task.id

        // إنشاء القناة للإشعارات إذا كان إصدار الأندرويد 8 أو أعلى
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // بناء الإشعار
        val pendingIntent = null
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // تأكد من أن الأيقونة موجودة
            .setContentTitle("📌 **مهامي اليومية**") // عنوان الإشعار
            .setContentText("🔔 ${task.title} - ${task.priority}") // محتوى الإشعار
            .setPriority(NotificationCompat.PRIORITY_HIGH) // تحديد أولوية الإشعار
            .setAutoCancel(true) // إزالة الإشعار عند النقر عليه
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // ضبط الصوت
            .setContentIntent(pendingIntent) // فتح التطبيق عند الضغط على الإشعار

        // عرض الإشعار
        notificationManager.notify(notificationId, builder.build())

        Log.d("AlarmReceiver", "✅ تم عرض الإشعار بنجاح")
    }


    // **دالة لجلب المهمة من قاعدة البيانات SQLite**
        private fun getTaskFromDatabase(context: Context, taskId: Int): Task? {
            val dbHelper = TaskDatabaseHelper(context)
            return dbHelper.getTaskById(taskId)
        }
    }
