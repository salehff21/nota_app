package com.example.nota_app.adpter
import android.content.Context
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import com.example.nota_app.R
import com.example.nota_app.data.Task
import java.util.ArrayList
class TaskAdpter(private val context: Context, private val taskList: List<Task>) : BaseAdapter() {

    override fun getCount(): Int = taskList.size

    override fun getItem(position: Int): Task = taskList[position]

    override fun getItemId(position: Int): Long = taskList[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false)

        val titleTextView: TextView = view.findViewById(R.id.Task_Name)
        val dateTextView: TextView = view.findViewById(R.id.task_date)
        val timeTextView: TextView = view.findViewById(R.id.task_time)
        val taskStatus:CheckBox = view.findViewById<CheckBox>(R.id.task_status)
        val deleteButton: ImageButton = view.findViewById<AppCompatImageButton>(R.id.btn_delete)
        val editButton:ImageButton = view.findViewById<AppCompatImageButton>(R.id.btn_edit)
        val linerTypeTask = view.findViewById<LinearLayout>(R.id.linerTypeTask)

        deleteButton.isEnabled=false
        editButton.isEnabled=false
       // deleteButton.visibility = View.GONE
      //  editButton.visibility = View.GONE
        taskStatus.isEnabled=false

        val task = getItem(position)

        titleTextView.text = task.title
        dateTextView.text = task.date
        timeTextView.text = task.time
        taskStatus.isChecked = task.isCompleted
        // تغيير لون الخلفية بناءً على الأولوية
        when (task.priority) {
            "هام وعاجل" -> linerTypeTask.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            "عاجل وغير هام" -> linerTypeTask.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
            "هام وغير عاجل" -> linerTypeTask.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            else -> linerTypeTask.setBackgroundColor(ContextCompat.getColor(context, R.color.gray)) // لون افتراضي
        }

        return view
    }
}
