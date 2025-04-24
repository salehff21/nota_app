package com.example.nota_app.adpter

import com.example.nota_app.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nota_app.data.Task

class AdapterReport(private var tasks: List<Task>) : RecyclerView.Adapter<AdapterReport.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val tvType: TextView = view.findViewById(R.id.tvTaskType)
        val tvDate: TextView = view.findViewById(R.id.tvTaskDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_view_report, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvTitle.text = task.title
        holder.tvType.text = task.priority
        holder.tvDate.text = task.date
    }

    override fun getItemCount(): Int = tasks.size

    // دالة لتحديث البيانات في RecyclerView
    fun updateData(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged() // إشعار RecyclerView أن البيانات تغيرت
    }
}
