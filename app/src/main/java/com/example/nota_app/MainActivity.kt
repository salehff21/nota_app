package com.example.nota_app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.nota_app.adapters.NoteAdapter
import com.example.nota_app.data.Task
import com.example.nota_app.data.TaskDatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import android.Manifest
import android.provider.Settings

 class MainActivity : AppCompatActivity() {
    private var isFirstLaunch = true
    private lateinit var adapter: NoteAdapter
    private val taskList = ArrayList<Task>()
    private lateinit var dbHelper: TaskDatabaseHelper
    private lateinit var TaskTypeSpinner: Spinner
    // تعريف activityLauncher مرة واحدة
    private lateinit var sharedPreferences: SharedPreferences
    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val title = data?.getStringExtra("title") ?: return@registerForActivityResult
                val type = data.getStringExtra("type") ?: ""
                val date = data.getStringExtra("date") ?: ""
                val time = data.getStringExtra("time") ?: ""
                // إضافة المهمة الجديدة إلى قاعدة البيانات
                val task =
                    Task(title = title, priority = type, date = date, time = time, status = false)

                lifecycleScope.launch {
                    // إدخال المهمة الجديدة في قاعدة البيانات باستخدام SQLite
                    val taskId = dbHelper.insertTask(task)
                    if (taskId != -1L) {
                        loadTasks() // إعادة تحميل المهام من قاعدة البيانات
                        Toast.makeText(
                            this@MainActivity,
                            "تمت إضافة المهمة بنجاح!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                // جدولة المنبه للمهمة الجديدة
                val alarmScheduler = AlarmScheduler(this)
                alarmScheduler.scheduleTaskReminder(task)
            }
        }
     private fun checkAndRequestPermissions() {
         val permissionsToRequest = mutableListOf<String>()

         // إذن إرسال الإشعارات (مطلوب من Android 13+)
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
             ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
             permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
         }

         // إذن جدولة التنبيهات الدقيقة (Android 12+)
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
             val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
             if (!alarmManager.canScheduleExactAlarms()) {
                 Toast.makeText(this, "يرجى السماح بإذن التنبيهات الدقيقة من الإعدادات!", Toast.LENGTH_LONG).show()
                 // فتح إعدادات النظام للسماح بالإذن
                 val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                 startActivity(intent)
             }
         }

         // طلب الأذونات إن وجدت أذونات غير ممنوحة
         if (permissionsToRequest.isNotEmpty()) {
             requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
         }
     }
     // كود طلب الأذونات ومعالجة الردود
     private val requestPermissionLauncher =
         registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
             permissions.entries.forEach { entry ->
                 if (!entry.value) { // إذا لم يتم منح الإذن
                     Toast.makeText(this, "يجب منح إذن ${entry.key} ليعمل التطبيق بشكل صحيح", Toast.LENGTH_LONG).show()
                 }
             }
         }

    private fun editTask(task: Task) {
        // إعداد قائمة أنواع المهام في Spinner
        val taskTypes = arrayOf("هام وعاجل", "هام وغير عاجل", "عاجل وغير هام", "غير هام وغير عاجل")
        // تحميل الـ dialogView
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_task, null)
        val editTitle = dialogView.findViewById<EditText>(R.id.edit_title)
        val editType = dialogView.findViewById<Spinner>(R.id.edit_type)
        val editDate = dialogView.findViewById<TextView>(R.id.edit_date)
        val editTime = dialogView.findViewById<TextView>(R.id.edit_time)
        // تخصيص ArrayAdapter لاستخدام الخط المخصص
        val adapter = object : ArrayAdapter<String>(
            dialogView.context,
            android.R.layout.simple_spinner_dropdown_item,
            taskTypes
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                val typeface = ResourcesCompat.getFont(
                    dialogView.context,
                    R.font.cairo_medium
                ) // تحميل الخط المخصص
                view.typeface = typeface
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                val typeface = ResourcesCompat.getFont(
                    dialogView.context,
                    R.font.cairo_medium
                ) // تحميل الخط المخصص
                view.typeface = typeface
                return view
            }
        }
        // تعيين الـ adapter إلى الـ Spinner
        editType.adapter = adapter

        val dialog = AlertDialog.Builder(this)

            .setView(dialogView)
            .create()
        dialog.show()

      // الحصول على الأزرار من واجهة المستخدم
        val btnSave = dialogView.findViewById<Button>(R.id.Edite_btn_save)
        val btnCancel = dialogView.findViewById<Button>(R.id.Edit_btn_cancel)
        // التعامل مع زر الحفظ
        btnSave.setOnClickListener {
            val updatedTask = Task(
                id = task.id,
                title = editTitle.text.toString(),
                priority = editType.selectedItem.toString(),
                date = editDate.text.toString(),
                time = editTime.text.toString(),
                isCompleted =  task.isCompleted ,
                status = task.status
            )
            lifecycleScope.launch {
                dbHelper.updateTask(updatedTask) // تحديث قاعدة البيانات
                loadTasks() // إعادة تحميل المهام بعد التعديل
                Toast.makeText(this@MainActivity, "تم تعديل المهمة!", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss() // إغلاق الحوار بعد الحفظ
        }
        // التعامل مع زر الإلغاء
        btnCancel.setOnClickListener {
            dialog.dismiss() // إغلاق الحوار بدون تنفيذ أي تعديل
        }
        editTitle.setText(task.title)
        editDate.setText(task.date)
        editTime.setText(task.time)
        val taskTypeIndex = taskTypes.indexOf(task.priority)
        if (taskTypeIndex != -1) {
            editType.setSelection(taskTypeIndex)
        }
        // عند الضغط على التاريخ، يظهر DatePicker
        editDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(dialogView.context, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                editDate.text = selectedDate
            }, year, month, day).show()
        }
        // عند الضغط على الوقت، يظهر TimePicker مع AM/PM أو صباحًا/مساءً
        editTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            editTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                    // تنسيق الوقت المختار
                    val formattedTime =
                        String.format("%02d:%02d", selectedHour % 12, selectedMinute)
                    // تحديد ما إذا كان الوقت صباحًا أو مساءً
                    val isMorning = selectedHour < 12
                    val currentLanguage = Locale.getDefault().language
                    val period = if (isMorning) {
                        if (currentLanguage == "ar") "صباحًا" else "AM"
                    } else {
                        if (currentLanguage == "ar") "مساءً" else "PM"
                    }
                    // عرض الوقت بالشكل الصحيح
                    editTime.text = "$formattedTime $period"

                }, hour, minute, false).show() // `false` لتعطيل عرض 24 ساعة
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
                // التحقق من إذن جدولة التنبيهات الدقيقة
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Toast.makeText(this, "يرجى السماح بإذن التنبيهات الدقيقة من الإعدادات!", Toast.LENGTH_LONG).show()
                    }
                }
        // طلب الأذونات عند تشغيل التطبيق
        checkAndRequestPermissions()
        // اجعل التطبيق يتبع وضع النظام تلقائيًا
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = TaskDatabaseHelper(this) // تهيئة قاعدة البيانات
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        //var isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
        // تعريف شاشة الترحيب والمحتوى الرئيسي
        val splashScreen = findViewById<View>(R.id.splash_screen)
        val mainContent = findViewById<View>(R.id.main_content)

        if (isFirstLaunch) {
            mainContent.visibility = View.GONE
            splashScreen.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                splashScreen.visibility = View.GONE
                mainContent.visibility = View.VISIBLE
                setupMainContent()
                // sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
                // تغيير الحالة بعد أول تشغيل
            }, 3000)
        } else {
            splashScreen.visibility = View.GONE
            mainContent.visibility = View.VISIBLE
        }

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNavigationView.selectedItemId = R.id.bottom_home // تحديد العنصر الافتراضي

            bottomNavigationView.setOnItemSelectedListener { item ->
                val intent = when (item.itemId) {
                    R.id.bottom_home -> {
                        return@setOnItemSelectedListener true // لا تفعل شيئًا لأننا بالفعل في MainActivity
                    }

                    R.id.bottom_add -> {
                        Intent(this, ActivityAdd::class.java)
                    }

                    R.id.bottom_report -> {
                        Intent(this, ReportsActivity::class.java)
                    }

                    else -> null
                }

                intent?.let {
                    startActivity(it)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                true
            }

        }    //Ongreate

    override fun onResume() {
        super.onResume()
        if (!isFirstLaunch)
        { // شرط لمنع ظهور شاشة الترحيب عند العودة من نشاط آخر
            findViewById<View>(R.id.splash_screen).visibility = View.GONE
            findViewById<View>(R.id.main_content).visibility = View.VISIBLE
        }
    }

    private fun setupMainContent() {
        setSupportActionBar(findViewById(R.id.main_toolbar)) // تعيين شريط الأدوات الرئيسي
        // ربط البيانات مع الـ ListView
        val listItems: ListView = findViewById(R.id.list_items)
        adapter = NoteAdapter(this, taskList, ::editTask, ::deleteTask)
        listItems.adapter = adapter

        // تحميل المهام من قاعدة البيانات عند تشغيل MainActivity
        loadTasks()
    }

    public fun loadTasks() {
        lifecycleScope.launch {
            val tasks = dbHelper.getAllTasks()
            taskList.clear()
            taskList.addAll(tasks)
            adapter.notifyDataSetChanged() // تحديث القائمة


        }
    }

  private fun deleteTask(task: Task) {
        // دالة لعرض نافذة التأكيد قبل الحذف
        AlertDialog.Builder(this@MainActivity)
            .setMessage("هل أنت متأكد من حذف هذه المهمة؟")
            .setPositiveButton("موافق") { _, _ ->
                // عند الضغط على موافق، استدعاء دالة الحذف
                dbHelper.deleteTask(task.id.toString()) // حذف المهمة من قاعدة البيانات
                loadTasks()  // إعادة تحميل المهام بعد الحذف
                Toast.makeText(this@MainActivity, "تم حذف المهمة!", Toast.LENGTH_SHORT)
                    .show() // عرض رسالة نجاح
            }
            .setNegativeButton("إلغاء") { dialog, _ ->
                dialog.dismiss()  // إغلاق الـ Dialog
            }
            .create()
            .show()
    }

    companion object

}










