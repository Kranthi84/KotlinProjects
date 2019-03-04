package com.example.chinnakotla.taskslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ListDetailRecycleViewAdapter(val detail_list: ArrayList<String>) : RecyclerView.Adapter<ListDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_task_layout, parent, false)
        return ListDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return detail_list.size
    }

    override fun onBindViewHolder(holder: ListDetailViewHolder, position: Int) {
        if (holder != null) {
            holder.detailTaskTextView.text = detail_list[position]
        }
    }

}