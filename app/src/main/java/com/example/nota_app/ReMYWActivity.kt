package com.example.nota_app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nota_app.adpter.AdapterReport
import com.example.nota_app.data.TaskDatabaseHelper

class ReMYWActivity : AppCompatActivity() {

    private lateinit var spinnerReportType: Spinner
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var dbHelper: TaskDatabaseHelper
    private lateinit var taskAdapter: AdapterReport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_re_mywactivity)

        // ربط العناصر من واجهة المستخدم
        spinnerReportType = findViewById(R.id.spinnerReportType)
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)
        dbHelper = TaskDatabaseHelper(this)

        // إعداد RecyclerView
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)

        // إنشاء Adapter مرة واحدة
        taskAdapter = AdapterReport(emptyList())
        recyclerViewTasks.adapter = taskAdapter

        // إعداد Spinner
        val reportTypes = arrayOf("أسبوعي", "شهري", "سنوي")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, reportTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerReportType.adapter = spinnerAdapter

        // حدث عند تغيير الاختيار
        spinnerReportType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = parent.getItemAtPosition(position).toString()
                Log.d("DEBUG Test Application", "تم تحميل المهام لنوع التقرير: $selectedType")
                loadTasks(selectedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("DEBUG Test Application" , "لم يتم تحديد أي نوع تقرير")
            }
        }

    }

    private fun loadTasks(reportType: String) {
        val tasks = when (reportType) {
            "أسبوعي" -> dbHelper.getTasksByDateRange(7)
            "شهري" -> dbHelper.getTasksByDateRange(30)
            "سنوي" -> dbHelper.getTasksByDateRange(365)
            else -> emptyList()
        }

        // تحديث RecyclerView
        taskAdapter.updateData(tasks) // تحديث البيانات في الـ Adapter دون إعادة إنشائه
    }
}

