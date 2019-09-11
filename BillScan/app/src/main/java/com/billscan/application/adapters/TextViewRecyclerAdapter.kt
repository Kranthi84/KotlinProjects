package com.billscan.application.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.billscan.application.databinding.RecyclerTextviewLayoutBinding


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


    class TextViewHolder private constructor(private val binding: RecyclerTextviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: String, listener: TextViewListener) {
            binding.websiteName = item
            binding.webSiteListener = listener
        }


        companion object {
            fun from(parent: ViewGroup): TextViewHolder {

                val binding = RecyclerTextviewLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return TextViewHolder(binding)
            }
        }
    }

    class TextViewListener(val clickListener: (website: String) -> Unit) {
        fun onClick(web: String) = clickListener(web)
    }
}