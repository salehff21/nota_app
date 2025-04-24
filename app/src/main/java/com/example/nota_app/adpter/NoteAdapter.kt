package com.example.nota_app.adapters // حزمة جديدة لكلاس NoteAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.nota_app.MainActivity
import com.example.nota_app.R
import com.example.nota_app.data.Task // تأكد من استيراد كلاس Task

class NoteAdapter(private val context: MainActivity, private var listOfNotes: ArrayList<Task>) : BaseAdapter() {

    // إعادة تحميل البيانات من قاعدة البيانات
    fun updateData(newData: List<Task>) {
        listOfNotes.clear()
        listOfNotes.addAll(newData)
        notifyDataSetChanged() // تحديث العرض
    }

    override fun getCount(): Int = listOfNotes.size

    override fun getItem(position: Int): Any = listOfNotes[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.note_layout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // الحصول على المهمة من القائمة
        val task = listOfNotes[position]

        // تعيين قيم العناصر في الـ View
        viewHolder.taskName.text = task.title  // تأكد من استخدام task.title بدلاً من task.taskName
        viewHolder.taskType.text = task.priority  // تأكد من استخدام task.priority بدلاً من task.taskType
        viewHolder.taskTime.text = task.time  // تأكد من استخدام task.time بدلاً من task.taskTime
        viewHolder.taskDate.text = task.date  // تأكد من استخدام task.date بدلاً من task.taskDate

        return view
    }

    private class ViewHolder(view: View) {
        val taskName: TextView = view.findViewById(R.id.Task_Name)
        val taskTime: TextView = view.findViewById(R.id.task_time)
        val taskDate: TextView = view.findViewById(R.id.task_date)
        val taskType: TextView = view.findViewById(R.id.task_Type)
    }
}
