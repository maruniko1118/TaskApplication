package com.maru2525.taskapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maru2525.taskapplication.databinding.ItemTaskBinding

class RecyclerViewTaskAdapter (private val list: ArrayList<Task>) : RecyclerView.Adapter<RecyclerViewTaskAdapter.MyViewHolder>() {
  class MyViewHolder(val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = ItemTaskBinding.inflate(inflater, parent, false)
    return MyViewHolder(binding)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val item = list[position]
    holder.binding.tvTitle.text = item.title
  }

}

class Task(var id: Int, val title: String, val date: String, val time: String)