package com.billscan.application.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.billscan.application.database.BillEntity
import com.billscan.application.databinding.RecyclerViewLayoutBinding


class ImageRecyclerAdapter(var data: List<BillEntity>, var selectListener: BillsSelectListener) :
    RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder>() {

    init {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder.from(parent)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(data[position], selectListener)
    }


    class ImageViewHolder private constructor(val binding: RecyclerViewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bill: BillEntity, selectListener: BillsSelectListener) {
            binding.billEntity = bill
            binding.billListener = selectListener
            binding.nameTextView.text = bill.billName
            binding.dateTextView.text = bill.billDate
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ImageViewHolder {

                val binding = RecyclerViewLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return ImageViewHolder(binding)
            }
        }

    }

    class BillsSelectListener(val clicklistener: (billId: Long) -> Unit) {
        fun onClick(bill: BillEntity) = clicklistener(bill.billNum)
    }
}