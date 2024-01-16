package com.example.dogs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dogs.R

class MyAdapter(private val data: MutableList<String>, private val listener: ItemClickListener) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onDetailButtonClick(position: Int)
        fun onDeleteButtonClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trab_soli_layout, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item
        // Establecer listeners para los botones
        holder.detailButton.setOnClickListener {
            listener.onDetailButtonClick(position)
        }
        holder.deleteButton.setOnClickListener {
            listener.onDeleteButtonClick(position)
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
        val detailButton: Button = itemView.findViewById(R.id.detailButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }
}