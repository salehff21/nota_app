package com.example.nota_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import com.example.nota_app.R
import com.example.nota_app.data.Task
import com.example.nota_app.data.TaskDatabaseHelper

class NoteAdapter(
    private val context: Context,
    private var taskList: ArrayList<Task>,
    private val editTask: (Task) -> Unit,
    private val deleteTask: (Task) -> Unit
) : android.widget.BaseAdapter() {

    override fun getCount(): Int {
        return taskList.size
    }

    override fun getItem(position: Int): Any {
        return taskList[position]
    }

    override fun getItemId(position: Int): Long {
        return taskList[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val task = taskList[position]
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.note_layout, parent, false)

        val taskTitle = view.findViewById<TextView>(R.id.Task_Name)
        val taskType = view.findViewById<TextView>(R.id.task_Type)
        val taskDate = view.findViewById<TextView>(R.id.task_date)
        val taskTime = view.findViewById<TextView>(R.id.task_time)
        val taskStatus = view.findViewById<CheckBox>(R.id.task_status)
        val btnEdit = view.findViewById<AppCompatImageButton>(R.id.btn_edit)
        val btnDelete = view.findViewById<AppCompatImageButton>(R.id.btn_delete)
        //val linerTypeTask  // تأكد من ID العنصر
     val linerTypeTask = view.findViewById<LinearLayout>(R.id.linerTypeTask)

        taskTitle.text = task.title
        taskType.text = task.priority
        taskDate.text = task.date
        taskTime.text = task.time
        taskStatus.isChecked = task.isCompleted

        // تغيير لون الخلفية بناءً على الأولوية
        when (task.priority) {
            "هام وعاجل" -> linerTypeTask.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            "عاجل وغير هام" -> linerTypeTask.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
            "هام وغير عاجل" -> linerTypeTask.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            else -> linerTypeTask.setBackgroundColor(ContextCompat.getColor(context, R.color.gray)) // لون افتراضي
        }

        // تنفيذ التعديل عند الضغط على الزر
        btnEdit.setOnClickListener {
            editTask(task)
        }

        // تنفيذ الحذف عند الضغط على الزر
        btnDelete.setOnClickListener {
            deleteTask(task)
        }

        // تغيير حالة المهمة عند تغيير الـ CheckBox
        taskStatus.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            task.status = isChecked
            updateTaskStatus(task)
        }

        return view
    }

    // دالة لتحديث حالة المهمة في قاعدة البيانات
    private fun updateTaskStatus(task: Task) {
        // تحديث حالة المهمة في قاعدة البيانات
        val dbHelper = TaskDatabaseHelper(context)
        val updatedTask = task.copy(status = task.status)
        dbHelper.updateTask(updatedTask)

        Toast.makeText(context, "تم تحديث حالة المهمة!", Toast.LENGTH_SHORT).show()
    }

    // دالة لتحديث البيانات في الـ Adapter بعد التعديل أو الحذف
    fun updateData(newTaskList: ArrayList<Task>) {
        taskList.clear()
        taskList.addAll(newTaskList)
        notifyDataSetChanged()
    }
}
