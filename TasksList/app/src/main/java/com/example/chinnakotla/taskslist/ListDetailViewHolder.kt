package com.example.chinnakotla.taskslist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ListDetailViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    val detailTaskTextView = itemView?.findViewById(R.id.detailTaskTextView) as TextView
}