package com.example.nota_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.nota_app.adapters.NoteAdapter
import com.example.nota_app.data.Task
import com.example.nota_app.data.TaskDatabaseHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter // محول لعرض قائمة المهام
    private val taskList = ArrayList<Task>() // قائمة المهام التي سيتم استرجاعها من قاعدة البيانات
    private lateinit var dbHelper: TaskDatabaseHelper // تعريف كائن قاعدة البيانات

    // تعريف activityLauncher مرة واحدة
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra("title") ?: return@registerForActivityResult
            val type = data?.getStringExtra("type") ?: ""
            val date = data?.getStringExtra("date") ?: ""
            val time = data?.getStringExtra("time") ?: ""

            // إضافة المهمة الجديدة إلى قاعدة البيانات
            val task = Task(title = title, priority = type, date = date, time = time, status = false)

            lifecycleScope.launch {
                // إدخال المهمة الجديدة في قاعدة البيانات باستخدام SQLite
                val taskId = dbHelper.insertTask(task)
                if (taskId != -1L) {
                    loadTasks() // إعادة تحميل المهام من قاعدة البيانات
                    Toast.makeText(this@MainActivity, "تمت إضافة المهمة بنجاح!", Toast.LENGTH_SHORT).show()
                }
            }

            // جدولة المنبه للمهمة الجديدة
            val alarmScheduler = AlarmScheduler(this)
            alarmScheduler.scheduleTaskReminder(task)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = TaskDatabaseHelper(this) // تهيئة قاعدة البيانات

        // تعريف شاشة الترحيب والمحتوى الرئيسي
        val splashScreen = findViewById<View>(R.id.splash_screen)
        val mainContent = findViewById<View>(R.id.main_content)

        // إخفاء المحتوى الأساسي في البداية
        mainContent.visibility = View.GONE

        // عرض شاشة الترحيب لمدة 3 ثوانٍ في كل مرة يتم فيها فتح التطبيق
        splashScreen.visibility = View.VISIBLE
        mainContent.visibility = View.GONE

        // بعد التأخير 3 ثوانٍ نقوم بإخفاء شاشة الترحيب وعرض المحتوى الرئيسي
        Handler(Looper.getMainLooper()).postDelayed({
            splashScreen.visibility = View.GONE
            mainContent.visibility = View.VISIBLE
            setupMainContent() // تشغيل باقي وظائف النشاط الرئيسي بعد عرض المحتوى
        }, 3000) // التأخير 3 ثوانٍ
    }

    override fun onResume() {
        super.onResume()

        // إذا كانت شاشة الترحيب لا تزال مرئية، نقوم بإخفائها
        val splashScreen = findViewById<View>(R.id.splash_screen)
        val mainContent = findViewById<View>(R.id.main_content)

        splashScreen.visibility = View.GONE
        mainContent.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        // يمكن استبدال أي عمليات تحتاج إلى القيام بها عند العودة من التطبيق
    }

    private fun setupMainContent() {
        setSupportActionBar(findViewById(R.id.main_toolbar)) // تعيين شريط الأدوات الرئيسي

        // ربط البيانات مع الـ ListView
        val listItems: ListView = findViewById(R.id.list_items)
        adapter = NoteAdapter(this, taskList)
        listItems.adapter = adapter

        // زر إضافة مهمة جديدة
        val btnAdd: Button = findViewById(R.id.btn_add)
        btnAdd.setOnClickListener {
            val intent = Intent(this, ActivityAdd::class.java)
            activityLauncher.launch(intent) // استبدال startActivityForResult بالأسلوب الحديث
        }

        // تحميل المهام من قاعدة البيانات عند تشغيل MainActivity
        loadTasks()
    }

    // دالة لتحميل المهام من قاعدة البيانات
    private fun loadTasks() {
        lifecycleScope.launch {
            val tasks = dbHelper.getAllTasks() // استرجاع جميع المهام من قاعدة البيانات باستخدام SQLite
            taskList.clear()
            taskList.addAll(tasks) // إضافة المهام المسترجعة إلى القائمة
            adapter.notifyDataSetChanged() // تحديث الـ Adapter
        }
    }
}
