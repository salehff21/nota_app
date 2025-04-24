package com.example.nota_app
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.nota_app.data.Task
import java.util.*
class AlarmScheduler(private val context: Context) {
    // دالة لجدولة المنبه بناءً على المهمة
    fun scheduleTaskReminder(task: Task) {
        val calendar = Calendar.getInstance()
        // ضبط التاريخ والوقت للمهمة
        val taskDateParts = task.date.split("-")
        val taskTimeParts = task.time.split(":")
        calendar.set(Calendar.YEAR, taskDateParts[0].toInt())
        calendar.set(Calendar.MONTH, taskDateParts[1].toInt() - 1) // شهور تبدأ من 0
        calendar.set(Calendar.DAY_OF_MONTH, taskDateParts[2].toInt())
        calendar.set(Calendar.HOUR_OF_DAY, taskTimeParts[0].toInt())
        calendar.set(Calendar.MINUTE, taskTimeParts[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        // جدولة التنبيه في وقت المهمة
        val alarmTimeInMillis = calendar.timeInMillis
        // جدولة التنبيه على حسب الوقت المحدد
        scheduleAlarm(alarmTimeInMillis, task.title, "حان وقت تنفيذ المهمة!")
        // جدولة التنبيه قبل نصف ساعة
        scheduleAlarm(alarmTimeInMillis - 30 * 60 * 1000, task.title, "تبقى نصف ساعة على المهمة!")
    }
    // دالة لجدولة المنبه باستخدام AlarmManager
    @SuppressLint("ScheduleExactAlarm", "NewApi")
    private fun scheduleAlarm(timeInMillis: Long, taskName: String, message: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_TITLE", taskName)
            putExtra("TASK_MESSAGE", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, kotlin.random.Random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // التأكد من أن التطبيق لديه إذن لاستخدام التنبيهات الدقيقة (Android 12+)
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        } else {
            Toast.makeText(context, "⚠️ لا يمكن ضبط تنبيه دقيق! يرجى منح الإذن من الإعدادات", Toast.LENGTH_LONG).show()
        }
        // رسالة تأكيد للمستخدم عند جدولة المنبه
        Toast.makeText(context, "تم ضبط التنبيه بنجاح!", Toast.LENGTH_SHORT).show()
    }
}
