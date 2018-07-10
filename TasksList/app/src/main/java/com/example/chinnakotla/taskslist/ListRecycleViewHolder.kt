package com.example.chinnakotla.taskslist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import org.w3c.dom.Text

class ListRecycleViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    val listPosition = itemView?.findViewById<TextView>(R.id.itemNumber)
    val listTitle = itemView?.findViewById<TextView>(R.id.itemString)

}