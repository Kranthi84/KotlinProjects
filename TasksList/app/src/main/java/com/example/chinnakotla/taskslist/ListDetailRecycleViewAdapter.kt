package com.example.chinnakotla.taskslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ListDetailRecycleViewAdapter(var list: TaskList) : RecyclerView.Adapter<ListDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_task_layout, parent, false)
        return ListDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.taskList.size
    }

    override fun onBindViewHolder(holder: ListDetailViewHolder, position: Int) {
        if (holder != null) {
            holder.detailTaskTextView.text = list.taskList[position]
        }
    }

}