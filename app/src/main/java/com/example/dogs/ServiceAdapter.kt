package com.example.dogs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter(private val context: ExplorarFragment, private val data: List<String>) :
    RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_servicios, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Configurar los datos para cada elemento en la posición "position"
        holder.imageView.setImageResource(R.drawable.twitter)
        holder.textView.text = data[position]
    }
    override fun getItemCount(): Int {
        return data.size
    }
}