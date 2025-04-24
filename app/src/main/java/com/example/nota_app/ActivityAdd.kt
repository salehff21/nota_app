package com.example.nota_app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nota_app.data.Task
import com.example.nota_app.data.TaskDatabaseHelper
import java.util.*

class ActivityAdd : AppCompatActivity() {
    // تعريف المتغيرات لعناصر الواجهة
    private lateinit var titleInput: EditText
    private lateinit var taskTypeSpinner: Spinner
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var btnSave: Button
    private lateinit var dbHelper: TaskDatabaseHelper // كائن للتعامل مع قاعدة البيانات SQLite

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        // ربط المتغيرات بعناصر الواجهة
        titleInput = findViewById(R.id.add_title)
        taskTypeSpinner = findViewById(R.id.spinner_taskType)
        btnPickDate = findViewById(R.id.btn_pickDate)
        btnPickTime = findViewById(R.id.btn_pickTime)
        btnSave = findViewById(R.id.btn_save)
        dbHelper = TaskDatabaseHelper(this) // إنشاء كائن قاعدة البيانات SQLite

        // إعداد قائمة أنواع المهام في Spinner
        val taskTypes = arrayOf("هام وعاجل", "هام وغير عاجل", "عاجل وغير هام", "غير هام وغير عاجل")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, taskTypes)
        taskTypeSpinner.adapter = adapter

        // زر اختيار التاريخ
        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                selectedDate = "$year-${month + 1}-$dayOfMonth"
                btnPickDate.text = selectedDate // عرض التاريخ المختار في الزر
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }


        btnPickTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentLanguage = Locale.getDefault().language // لغة النظام الحالي

            TimePickerDialog(this, { _, hour, minute ->
                // تنسيق الوقت المختار
                selectedTime = String.format("%02d:%02d", hour, minute)

                // تحديد ما إذا كان الوقت صباحًا أو مساءً
                val period = if (hour < 12) {
                    if (currentLanguage == "ar") "صباحًا" else "AM"
                } else {
                    if (currentLanguage == "ar") "مساءً" else "PM"
                }

                // إضافة "صباحًا" أو "مساءً" حسب الوقت واللغة
                selectedTime += " $period"

                // عرض الوقت المختار في الزر
                btnPickTime.text = selectedTime

                // تحديث الساعة في الـ Calendar
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                // جدولة تنبيه موعد المهمة
                scheduleTaskAlarm(calendar.timeInMillis, "موعد المهمة", "حان وقت تنفيذ المهمة!")
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        // زر الحفظ لإضافة المهمة
        btnSave.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val type = taskTypeSpinner.selectedItem?.toString() ?: ""

            if (title.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "يرجى إدخال جميع البيانات", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // إنشاء كائن المهمة
            val task = Task(
                title = title,
                priority = type,
                date = selectedDate,
                time = selectedTime,
                status = false
            )

            // إدراج المهمة في قاعدة بيانات SQLite
            val taskId = dbHelper.insertTask(task)

            if (taskId != -1L) {
                Toast.makeText(this, "تم حفظ المهمة ✅", Toast.LENGTH_SHORT).show()

                // جدولة التنبيهات بناءً على نوع المهمة
                scheduleTaskNotifications(task)

                finish() // العودة إلى الصفحة السابقة بعد الحفظ
            } else {
                Toast.makeText(this, "فشل في حفظ المهمة ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // دالة لجدولة التنبيهات بناءً على نوع المهمة
    private fun scheduleTaskNotifications(task: Task) {
        when (task.priority) {
            "هام وعاجل" -> scheduleTaskAlarm(System.currentTimeMillis() + 60000, "تنبيه هام", "مهامك العاجلة والمهمة لم تُنفذ بعد!")
            "هام وغير عاجل" -> scheduleTaskAlarm(System.currentTimeMillis() + 86400000, "تذكير هام", "لديك مهام هامة لم تكتمل بعد.")
        }
        // جدولة تقارير أسبوعية، شهرية، وسنوية
        scheduleWeeklyReport()
        scheduleMonthlyReport()
        scheduleYearlyReport()
    }

    // جدولة تنبيه المهمة
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleTaskAlarm(timeInMillis: Long, title: String, message: String) {
        val alarmManager = getSystemService(AlarmManager::class.java)
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, timeInMillis.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    // جدولة تقارير أسبوعية
    private fun scheduleWeeklyReport() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY) // تحديد يوم التقرير
        scheduleTaskAlarm(calendar.timeInMillis, "التقرير الأسبوعي", "إليك ملخص مهامك لهذا الأسبوع!")
    }

    // جدولة تقارير شهرية
    private fun scheduleMonthlyReport() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 30) // آخر يوم في الشهر
        scheduleTaskAlarm(calendar.timeInMillis, "التقرير الشهري", "إليك تقرير مهامك لهذا الشهر!")
    }

    // جدولة تقارير سنوية
    private fun scheduleYearlyReport() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DAY_OF_MONTH, 31) // آخر يوم في السنة
        scheduleTaskAlarm(calendar.timeInMillis, "التقرير السنوي", "إليك ملخص إنجازاتك خلال العام!")
    }
}
