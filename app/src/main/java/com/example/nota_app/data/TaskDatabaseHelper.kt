package com.example.nota_app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

private const val DATABASE_NAME = "task_database.db"
private const val DATABASE_VERSION = 1
private const val TABLE_NAME = "tasks"

// كلاس مساعد لإدارة قاعدة البيانات
class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // عند إنشاء قاعدة البيانات لأول مرة
    override fun onCreate(db: SQLiteDatabase) {
        // استعلام لإنشاء الجدول
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                priority TEXT NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                isCompleted INTEGER DEFAULT 0,
                status INTEGER DEFAULT 0
            )
        """.trimIndent()
        // تنفيذ الاستعلام
        db.execSQL(createTableQuery)
    }

    // عند ترقية قاعدة البيانات (تغيير النسخة)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // حذف الجدول القديم في حالة الترقية
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        // إعادة إنشاء الجدول
        onCreate(db)
    }

    // دالة لإدخال مهمة جديدة في قاعدة البيانات
    fun insertTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", task.title)       // عنوان المهمة
            put("priority", task.priority) // الأولوية
            put("date", task.date)         // التاريخ
            put("time", task.time)         // الوقت
            put("isCompleted", if (task.isCompleted) 1 else 0) // حالة الإنجاز
            put("status", if (task.status) 1 else 0) // الحالة (نشط أو غير نشط)
        }
        // إدخال المهمة وإرجاع المعرف الخاص بالمهمة
        return db.insert(TABLE_NAME, null, values)
    }

    // دالة لتحديث مهمة موجودة
    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", task.title)       // عنوان المهمة
            put("priority", task.priority) // الأولوية
            put("date", task.date)         // التاريخ
            put("time", task.time)         // الوقت
            put("isCompleted", if (task.isCompleted) 1 else 0) // حالة الإنجاز
            put("status", if (task.status) 1 else 0) // الحالة (نشط أو غير نشط)
        }
        // تحديث المهمة باستخدام المعرف الخاص بالمهمة
        return db.update(TABLE_NAME, values, "id = ?", arrayOf(task.id.toString()))
    }

    // دالة لحذف مهمة باستخدام المعرف
    fun deleteTask(taskId: Int): Int {
        val db = writableDatabase
        // حذف المهمة باستخدام المعرف
        return db.delete(TABLE_NAME, "id = ?", arrayOf(taskId.toString()))
    }

    // دالة لاسترجاع جميع المهام من قاعدة البيانات
    fun getAllTasks(): List<Task> {
        val db = readableDatabase
        // استعلام لاسترجاع جميع المهام مرتبة حسب التاريخ والوقت
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY date ASC, time ASC", null)
        val tasks = mutableListOf<Task>()

        // قراءة البيانات من الكورسور وتحويلها إلى قائمة من المهام
        while (cursor.moveToNext()) {
            val task = Task(
                id = cursor.getInt(0),           // المعرف
                title = cursor.getString(1),      // العنوان
                priority = cursor.getString(2),   // الأولوية
                date = cursor.getString(3),       // التاريخ
                time = cursor.getString(4),       // الوقت
                isCompleted = cursor.getInt(5) == 1, // حالة الإنجاز
                status = cursor.getInt(6) == 1    // الحالة
            )
            tasks.add(task)
        }
        cursor.close()  // إغلاق الكورسور بعد استخدامه
        return tasks
    }
}
