package com.example.nota_app
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nota_app.adapters.NoteAdapter
import com.example.nota_app.adpter.TaskAdpter
import com.example.nota_app.data.Task
import com.example.nota_app.data.TaskDatabaseHelper
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ReportsActivity : AppCompatActivity() {
    val dbHelper = TaskDatabaseHelper(this)
    private val taskList = ArrayList<Task>()
    val taskTypes = arrayOf("هام وعاجل", "هام وغير عاجل", "عاجل وغير هام", "غير هام وغير عاجل")
    private lateinit var adapter: NoteAdapter // محول لعرض قائمة المهام
  // قائمة المهام التي سيتم استرجاعها من قاعدة البيانات
    private lateinit var adapterP:  TaskAdpter
    private fun setupMainContent() {

        setSupportActionBar(findViewById(R.id.main_toolbar)) // تعيين شريط الأدوات الرئيسي
        // ربط البيانات مع الـ ListView
        val listItems: ListView = findViewById(R.id.list_items_Completed)
        adapterP =  TaskAdpter(this, taskList)
        listItems.adapter = adapterP

        // تحميل المهام من قاعدة البيانات عند تشغيل MainActivity
        loadTasks()
    }

 // قائمة المهام التي سيتم استرجاعها من قاعدة البيانات
 fun loadTasks() {
     lifecycleScope.launch {
         val tasks = dbHelper.getAllTasksCompleted()
         taskList.clear()
         taskList.addAll(tasks)
         adapterP.notifyDataSetChanged() // تحديث القائمة
     }
 }
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        // البحث عن الـ ViewGroup الرئيسي في activity_reports.xml لإضافة التخطيط الجديد داخله
        val rootLayout = findViewById<ViewGroup>(R.id.main) // تأكد أن لديك عنصر في activity_reports.xml

        setupMainContent()


        val btnViewReportWeek = findViewById<Button>(R.id.btn_ViewReport_week)

        btnViewReportWeek.setOnClickListener {
            val intent = Intent(this, ReMYWActivity::class.java)
            startActivity(intent)
        }




        val db = dbHelper.readableDatabase
        val (completed, pending) = dbHelper.getTaskStatistics()

        val pieChart = findViewById<PieChart>(R.id.pieChart)
        setupPieChart(pieChart, completed, pending)

        //تحميل المهام التي انجزت
        //setupMainContent()

        // تخصيص الـ BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_report // تحديد العنصر الافتراضي

        val barChart = findViewById<BarChart>(R.id.barChart) // تأكد أن لديك العنصر في XML
        // 1️⃣ جلب البيانات من قاعدة البيانات
        val taskCounts = getTaskCountsFromDatabase()

        // 2️⃣ تجهيز البيانات للمخطط
        val barEntries = ArrayList<BarEntry>()
        for (i in taskCounts.indices) {
            barEntries.add(BarEntry(i.toFloat(), taskCounts[i].toFloat()))
        }
        // 3️⃣ استدعاء `setupBarChart`
       setupBarChart(barChart, this, barEntries, taskTypes)

        bottomNavigationView.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.bottom_home -> {
                    Intent(this, MainActivity::class.java) // الانتقال إلى MainActivity
                }

                R.id.bottom_add -> {
                    Intent(this, ActivityAdd::class.java) // الانتقال إلى ActivityAdd
                }

                R.id.bottom_report -> {
                    // لا حاجة لتغيير شيء هنا لأنه موجود بالفعل في ReportsActivity
                    return@setOnItemSelectedListener true

                }

                else -> null
            }

            intent?.let {
                startActivity(it)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            true
        }
        // إضافة نافذة التفاعل مع الحواف للنظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }


    fun getTaskCountsFromDatabase(): List<Int> {
        val dbHelper = TaskDatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val taskCounts = MutableList(4) { 0 } // قائمة لحفظ عدد المهام لكل نوع

        for ((index, type) in taskTypes.withIndex()) {
            val cursor = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE  priority= ?", arrayOf(type))
            if (cursor.moveToFirst()) {
                taskCounts[index] = cursor.getInt(0)
            }
            cursor.close()
        }
        db.close()
        print("taskCounts{$taskCounts}")
        return taskCounts
    }

    private fun setupPieChart(pieChart: PieChart, completed: Int, pending: Int) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(completed.toFloat(), "منجزة"))
        entries.add(PieEntry(pending.toFloat(), "غير منجزة"))

        val dataSet = PieDataSet(entries, "حالة المهام")
        dataSet.colors = listOf(Color.BLUE, Color.MAGENTA)
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor = Color.BLACK

        val pieData = PieData(dataSet)
        pieChart.data = pieData

        val typeface =
            ResourcesCompat.getFont(this, R.font.cairo_medium) // تحميل الخط من مجلد res/font/
        pieData.setValueTypeface(typeface)  // تعيين الخط على القيم داخل الدائرة
        pieChart.setEntryLabelTypeface(typeface)  // تعيين الخط على النصوص داخل المخطط
        pieChart.setCenterTextTypeface(typeface)  // تعيين الخط على النص داخل مركز المخطط

        pieChart.setHoleRadius(35f)  // زيادة القطر الداخلي (50f قيمة قد تحتاج لتعديلها)
        pieChart.setTransparentCircleRadius(50f) // تعيين قطر الدائرة الشفافة (إذا أردت المزيد من التفاعل)
        // إعدادات إضافية للمخطط
        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.animateY(1500)
        pieChart.invalidate() // تحديث المخطط

        // تعيين الخط في الأسطورة (القيم التي تظهر خارج المخطط)
        val legend = pieChart.legend
        legend.setTypeface(typeface)  // تعيين الخط على النصوص في الأسطورة
    }

    // دالة إعداد المخطط العمودي (BarChart)
    fun setupBarChart(barChart: BarChart, context: Context, barEntries: List<BarEntry>, taskTypes: Array<String>) {
        val typeface = ResourcesCompat.getFont(context, R.font.cairo_medium)

        // إعداد المحور X
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(taskTypes) // تعيين أسماء الأولويات
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.typeface = typeface
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -15f // لمنع تداخل النصوص

        // إعداد المحور Y
        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f // القيم تبدأ من 0
        leftAxis.granularity = 1f // منع القيم العشرية
        leftAxis.typeface = typeface

        barChart.axisRight.isEnabled = false // إخفاء المحور Y الأيمن

        if (barEntries.isEmpty()) {
            Log.e("BarChart", "لا توجد بيانات لإضافتها للمخطط العمودي")
            return
        }

        // إعداد البيانات
        val barDataSet = BarDataSet(barEntries, "عدد المهام حسب الأولوية")
        barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS) // تعيين ألوان الأعمدة
        barDataSet.valueTypeface = typeface
        barDataSet.valueTextSize = 10f

        val barData = BarData(barDataSet)
        barData.barWidth = 0.4f // تصغير عرض الأعمدة ليكون مظهرها مرتباً
        barChart.data = barData

        // تحسين العرض
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.animateXY(1000,1500)


        // ضبط نطاق الرؤية
        barChart.setVisibleXRangeMaximum(4f) // عرض جميع الأنواع معًا
        barChart.setScaleEnabled(false) // منع التكبير لتجنب تشويه البيانات

        barChart.invalidate() // تحديث المخطط
    }

    }












