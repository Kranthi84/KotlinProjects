package com.billscan.application.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.billscan.application.R
import kotlinx.android.synthetic.main.recycler_textview_layout.view.*


class TextViewRecyclerAdapter(var list: List<String>, var textListener: TextViewListener) :
    RecyclerView.Adapter<TextViewRecyclerAdapter.TextViewHolder>() {

    fun updateList(newlist: List<String>) {
        this.list = newlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {

        holder.bind(this.list[position], textListener)
    }


    class TextViewHolder private constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {


        fun bind(item: String, listener: TextViewListener) {

            view.tv_website.text = item
            view.tv_website.setOnClickListener {

                listener.onClick(item)

            }

        }


        companion object {
            fun from(parent: ViewGroup): TextViewHolder {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_textview_layout, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    class TextViewListener(val clickListener: (website: String) -> Unit) {
        fun onClick(web: String) = clickListener(web)
    }
}