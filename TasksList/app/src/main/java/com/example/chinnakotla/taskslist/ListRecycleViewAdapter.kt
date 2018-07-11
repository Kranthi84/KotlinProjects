package com.example.chinnakotla.taskslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ListRecycleViewAdapter(val tasks: ArrayList<TaskList>, val clickListener: ListSelectionRecyclerViewListener) : RecyclerView.Adapter<ListRecycleViewHolder>() {


    interface ListSelectionRecyclerViewListener {
        fun listItemClicked(list: TaskList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRecycleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_selection_view_holder, parent, false)
        return ListRecycleViewHolder(view)

    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: ListRecycleViewHolder, position: Int) {

        if (holder != null) {
            holder.listPosition?.text = (position + 1).toString()
            holder.listTitle?.text = tasks.get(position).taskName
            clickListener.listItemClicked(tasks.get(position))
        }
    }

    fun addList(list: TaskList) {

        tasks.add(list)
        notifyDataSetChanged()
    }

}