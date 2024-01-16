package com.example.dogs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dogs.adapters.MyAdapter
import com.example.dogs.adapters.MyAdapter.ItemClickListener


class SolicitudesFragment : Fragment(), MyAdapter.ItemClickListener {

    val data = mutableListOf("item 1")
    val adapter = MyAdapter(data, this)

    // Antes que inicie el fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    // Parte donde pone el fragment en la view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_solicitudes, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val addButton = view.findViewById<Button>(R.id.addButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            val newItem = "Nuevo Item"
            data.add(newItem)
            adapter.notifyItemInserted(data.size - 1)
            recyclerView.smoothScrollToPosition(data.size - 1)
        }

        return view
    }

    // Durante la ejecucion del fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val help_buton = view.findViewById<LinearLayout>(R.id.help_solicitudesfragment)
        help_buton.setOnClickListener {
            val intent = Intent(view.context,HelpSolicitudActivity::class.java)
            startActivity(intent)
            //requireActivity().finish()
        }

    }

    override fun onDetailButtonClick(position: Int) {
        // Lógica para manejar el clic en "Ver detalles"
        val selectedItem = data[position]
        // Puedes abrir una nueva actividad o fragmento para mostrar más detalles
        Toast.makeText(requireContext(), "Detalles de $selectedItem", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteButtonClick(position: Int) {
        // Muestra un cuadro de diálogo de confirmación
        showDeleteConfirmationDialog(position)
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Elemento")
        builder.setMessage("¿Estás seguro de que deseas eliminar este elemento?")
        builder.setPositiveButton("Eliminar") { dialog, _ ->
            // Elimina el elemento después de la confirmación
            deleteItem(position)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            // No hace nada si se cancela la eliminación
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteItem(position: Int) {
        if (position in 0 until data.size) {
            data.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, data.size - position)
        }
    }


}