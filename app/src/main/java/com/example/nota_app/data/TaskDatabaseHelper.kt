package com.example.nota_app.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale
private const val DATABASE_NAME = "task_database.db"
private const val DATABASE_VERSION = 1
private const val TABLE_NAME = "tasks"


class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


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

        db.execSQL(createTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

        onCreate(db)
    }

    fun formatDate(inputDate: String): String {

        val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.ENGLISH)

        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)


        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }



    fun insertTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", task.title)
            put("priority", task.priority)
            put("date",  formatDate(task.date))
            put("time", task.time)
            put("isCompleted", if (task.isCompleted) 1 else 0)
            put("status", if (task.status) 1 else 0)
        }

        return db.insert(TABLE_NAME, null, values)
    }
    fun getTaskStatistics(): Pair<Int, Int> {
        val db = this.readableDatabase

        val completedQuery = "SELECT COUNT(*) FROM tasks  WHERE  isCompleted= 1"
        val pendingQuery = " SELECT COUNT(*) FROM tasks  WHERE  isCompleted= 0"

        val completedCount = DatabaseUtils.longForQuery(db, completedQuery, null).toInt()
        val pendingCount = DatabaseUtils.longForQuery(db, pendingQuery, null).toInt()
        return Pair(completedCount, pendingCount)
    }

    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", task.title)
            put("priority", task.priority)
            put("date", formatDate(task.date))
            put("time", task.time)
            put("isCompleted", if (task.isCompleted) 1 else 0)
            put("status", if (task.status) 1 else 0)
        }

        return db.update(TABLE_NAME, values, "id = ?", arrayOf(task.id.toString()))
    }

    fun deleteTask(taskId: String?): Int {
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

        while (cursor.moveToNext()) {
            val task = Task(
                id = cursor.getInt(0),
                title = cursor.getString(1),
                priority = cursor.getString(2),
                date = cursor.getString(3),
                time = cursor.getString(4),
                isCompleted = cursor.getInt(5) == 1,
                status = cursor.getInt(6) == 1
            )
            tasks.add(task)
        }

        cursor.close()
        return tasks
    }


    fun getAllTasksCompleted(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE isCompleted = 1 ORDER BY date ASC, time ASC", null)


            while (cursor.moveToNext()) {
                val task = Task(
                    id = cursor.getInt(0),
                    title = cursor.getString(1),
                    priority = cursor.getString(2),
                    date = cursor.getString(3),
                    time = cursor.getString(4),
                    isCompleted = cursor.getInt(5) == 1,
                    status = cursor.getInt(6) == 1
                )
                tasks.add(task)
            }
        } catch (e: Exception) {

        } finally {
            cursor?.close()

        }

        return tasks
    }

    @SuppressLint("Range")
    fun getTaskById(taskId: Int): Task? {
        val db = readableDatabase
        val query = "SELECT title, priority FROM tasks WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(taskId.toString()))


        return if (cursor != null && cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndex("title"))
            val priority = cursor.getString(cursor.getColumnIndex("priority"))
            cursor.close()


            Task(
                taskId, title, priority,
                date = TODO(),
                time = TODO(),
                isCompleted = TODO(),
                status = TODO()
            )
        } else {
            cursor.close()
            null
        }
    }

    @SuppressLint("Range")
    fun getTasksByDateRange(days: Int): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase

        val query="SELECT title, priority, date FROM tasks WHERE date(date) BETWEEN date('now', '-$days days') AND date('now')ORDER BY date DESC"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val title = cursor.getString(0)
            val priority = cursor.getString(1)
            val date: String = cursor.getString(2) ?: ""
            Log.d("DEBUG:getTasksByDateRange", "تم تحميل المهام لنوع التقرير: $days")
            tasks.add(Task(
                title = title,
                priority = priority,
                date = date,
                time = "",
                isCompleted =true,
                status = false
            ))
        }

        cursor.close()
        db.close()
        return tasks
    }


}
