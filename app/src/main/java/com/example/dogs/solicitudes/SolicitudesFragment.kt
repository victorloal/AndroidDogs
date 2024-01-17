package com.example.dogs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.recyclerview.widget.RecyclerView
import com.example.dogs.maps.LocationService
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.launch
import java.util.Collections

class SolicitudesFragment : Fragment() {

    private lateinit var dbReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val solicitudesList = mutableListOf<Solicitud>()
    private val solicitudesAcceptList = mutableListOf<Solicitud>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_solicitudes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = view.findViewById(R.id.spPet)
        val btnSolicitud = view.findViewById<Button>(R.id.btnSolicitud)
        val spPeticion: Spinner = view.findViewById(R.id.spPeticion)

        // Obtener el ID del usuario actual
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        auth = FirebaseAuth.getInstance()
        dbReference = FirebaseDatabase.getInstance().reference

        // Verificar si el usuario está autenticado
        if (userId != null) {
            // Obtener una referencia al nodo de información de mascotas del usuario
            val userMascotaInfoDB = FirebaseDatabase.getInstance().reference
                .child("User").child(userId).child("Mascota")

            // Escuchar cambios en los datos de mascotas y actualizar el Spinner
            userMascotaInfoDB.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val mascotasList = mutableListOf<String>()

                    for (mascotaSnapshot in dataSnapshot.children) {
                        // Obtener el nombre de la mascota
                        val nombre = mascotaSnapshot.child("nombre").value.toString()

                        // Añadir el nombre de la mascota a la lista
                        mascotasList.add(nombre)
                    }

                    // Crear un adaptador para el Spinner utilizando la lista de nombres de mascotas
                    val adapter = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item, mascotasList)

                    // Aplicar el adaptador al Spinner
                    spinner.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores en la lectura de datos
                }
            })
        }

        // Define los elementos para el combo box
        val items = arrayOf("Seleccione", "Paseo por 45 minutos", "Paseo por 1 hora",
            "Paseo por 1.5 horas", "Paseo por 2 horas", "Paseo por 2.5 horas")

        // Crea un adaptador para el Spinner utilizando el diseño personalizado
        val adapterPeticion = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item, items)

        // Aplica el adaptador al Spinner
        spPeticion.adapter = adapterPeticion

        // Configura un listener para manejar la selección del Spinner
        spPeticion.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Maneja la selección del elemento
                val selectedItem = items[position]
                // Puedes realizar acciones con el elemento seleccionado aquí
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Maneja la ausencia de selección
            }
        })

        btnSolicitud.setOnClickListener {
            if (spPeticion.selectedItem.toString() == "Seleccione" || spinner.isEmpty()){
                Toast.makeText(context, "Debe escribir para que es la solicitud", Toast.LENGTH_SHORT).show()
            }else if (spinner.selectedItem.toString() == "Seleccione" || spinner.isEmpty()){
                Toast.makeText(context, "Debe seleccionar la mascota", Toast.LENGTH_SHORT).show()
            }else{
                guardarSolicitud(spPeticion.selectedItem.toString(), spinner.selectedItem.toString())
            }
        }

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            obtenerDatosSolicitud()
            obtenerDatosSolicitudAccept()
        }
    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDismiss(position: Int)
    }

    data class Solicitud(val id: String, val peticion: String, val nombre: String, val estado: Boolean, val paseador: String)

    class SolicitudesAdapter(private val solicitudes: List<Solicitud>) :
        RecyclerView.Adapter<SolicitudesAdapter.ViewHolder>(), ItemTouchHelperAdapter {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
            val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
            val textViewPaseador: TextView = itemView.findViewById(R.id.textViewPaseador)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_solicitud, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val solicitud = solicitudes[position]
            if (solicitud.estado) {
                holder.textViewTitle.text = solicitud.nombre
                holder.textViewDescription.text = solicitud.peticion
                holder.textViewPaseador.text = solicitud.paseador
            } else {
                // Si el estado es false, puedes ocultar o manejar de otra manera las solicitudes con estado false
                holder.itemView.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            return solicitudes.size
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int) {
            // Puedes implementar lógica para cambiar el orden de los elementos si es necesario
            Collections.swap(solicitudes, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemDismiss(position: Int) {
            val solicitud = solicitudes[position]

            // Elimina la mascota de Firebase usando la clave única
            eliminarMascotaEnFirebase(solicitud.id)

            // Elimina la mascota de la lista local y notifica al adaptador
            notifyItemRemoved(position)

            // Notifica al adaptador que los datos han cambiado
            notifyItemRangeChanged(position, solicitudes.size)
        }

        fun eliminarMascotaEnFirebase(solicitudId: String) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.uid?.let { uid ->
                val dbReference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Solicitud")

                // Elimina la mascota de Firebase utilizando su clave única
                dbReference.child(solicitudId).removeValue()
            }
        }
    }
    private fun obtenerDatosSolicitud() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { uid ->
            val dbReference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Solicitud")

            // Escuchar cambios en los datos de solicitudes con estado true
            dbReference.orderByChild("estado").equalTo(true).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        solicitudesList.clear()

                        for (solicitudSnapshot in snapshot.children) {
                            val nombre = solicitudSnapshot.child("nombre").getValue(String::class.java)
                            val peticion = solicitudSnapshot.child("peticion").getValue(String::class.java)
                            val paseador = solicitudSnapshot.child("paseador").getValue(String::class.java)

                            if (nombre != null && peticion != null && paseador != null) {
                                solicitudesList.add(Solicitud(solicitudSnapshot.key!!, peticion, nombre, true, paseador))
                            }
                        }

                        // Actualizar los adaptadores con la lista de solicitudes
                        actualizarAdaptadores(solicitudesList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores de lectura de la base de datos
                    Log.e("LecturaBD", "Error al leer datos de mascotas: ${error.message}")
                }
            })
        }
    }

    private fun actualizarAdaptadores(solicitudesList: List<Solicitud>) {
        Log.e("DEBUG", "Número de solicitudes: ${solicitudesList.size}")
        // Separar los procesos en sus respectivos hilos
        activity?.runOnUiThread {
            // Obtener referencias a los RecyclerView
            val rvSolicitud = view?.findViewById<RecyclerView>(R.id.rvSolicitudes)
            rvSolicitud?.layoutManager = LinearLayoutManager(requireContext())

            // Crear adaptadores con la lista de solicitudes
            val solicitud_adapter = SolicitudesAdapter(solicitudesList.map { Solicitud(it.id, it.peticion, it.nombre, it.estado, it.paseador) })

            // Adjuntar ItemTouchHelper al RecyclerView
            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    // Llama a la función onItemMove del adaptador al mover un elemento
                    solicitud_adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Llama a la función onItemDismiss del adaptador al deslizar un elemento
                    solicitud_adapter.onItemDismiss(viewHolder.adapterPosition)
                }
            })

            itemTouchHelper.attachToRecyclerView(rvSolicitud)

            // Notificar a los adaptadores que los datos han cambiado
            solicitud_adapter.notifyDataSetChanged()

            // Configurar los adaptadores en el RecyclerView correspondiente
            rvSolicitud?.adapter = solicitud_adapter

            // Notificar a los adaptadores que los datos han cambiado
            solicitud_adapter.notifyDataSetChanged()
        }
    }

    private val locationService = LocationService()  // Agrega esta línea para instanciar el servicio de ubicación

    private fun guardarSolicitud(peticion: String, nombre: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            lifecycleScope.launch {
                val location = locationService.getUserLocation(requireContext())

                val userDB = dbReference.child("User").child(userId)

                // Generar un nuevo identificador único para la mascota
                val solicitudId = userDB.child("Solicitud").push().key

                // Guardar la información de la mascota en el nodo del usuario
                val nuevaSolicitud = hashMapOf(
                    "peticion" to peticion,
                    "nombre" to nombre,
                    "estado" to true,
                    "paseador" to "Sin paseador",
                    "latitud" to location?.latitude,
                    "longitud" to location?.longitude
                )
                userDB.child("Solicitud").child(solicitudId!!).setValue(nuevaSolicitud)

                Toast.makeText(context, "Solicitud realizada exitosamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class SolicitudesAcceptAdapter(private val solicitudes: List<Solicitud>) :
        RecyclerView.Adapter<SolicitudesAcceptAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
            val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
            val textViewPaseador: TextView = itemView.findViewById(R.id.textViewPaseador)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_solicitud, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val solicitud = solicitudes[position]
            if (!solicitud.estado) {
                holder.textViewTitle.text = solicitud.nombre
                holder.textViewDescription.text = solicitud.peticion
                holder.textViewPaseador.text = solicitud.paseador
            } else {
                // Si el estado es true, puedes ocultar o manejar de otra manera las solicitudes con estado true
                holder.itemView.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            return solicitudes.size
        }
    }

    private fun obtenerDatosSolicitudAccept() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { uid ->
            val dbReference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Solicitud")

            // Escuchar cambios en los datos de solicitudes con estado false
            dbReference.orderByChild("estado").equalTo(false).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        solicitudesAcceptList.clear()

                        for (solicitudSnapshot in snapshot.children) {
                            val nombre = solicitudSnapshot.child("nombre").getValue(String::class.java)
                            val peticion = solicitudSnapshot.child("peticion").getValue(String::class.java)
                            val paseador = solicitudSnapshot.child("paseador").getValue(String::class.java)

                            if (nombre != null && peticion != null && paseador != null) {
                                solicitudesAcceptList.add(Solicitud(solicitudSnapshot.key!!, peticion, nombre, false, paseador))
                            }
                        }

                        // Actualizar los adaptadores con la lista de solicitudes aceptadas
                        actualizarAdaptadoresAccept(solicitudesAcceptList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores de lectura de la base de datos
                    Log.e("LecturaBD", "Error al leer datos de solicitudes aceptadas: ${error.message}")
                }
            })
        }
    }

    private fun actualizarAdaptadoresAccept(solicitudesAcceptList: List<Solicitud>) {
        Log.e("DEBUG", "Número de solicitudes aceptadas: ${solicitudesAcceptList.size}")
        // Separar los procesos en sus respectivos hilos
        activity?.runOnUiThread {
            // Obtener referencias a los RecyclerView
            val rvSAceptadas = view?.findViewById<RecyclerView>(R.id.rvSAceptadas)
            rvSAceptadas?.layoutManager = LinearLayoutManager(requireContext())

            // Crear adaptadores con la lista de solicitudes aceptadas
            val solicitudAccept_adapter = SolicitudesAcceptAdapter(solicitudesAcceptList.map { Solicitud(it.id, it.peticion, it.nombre, it.estado, it.paseador) })

            // Configurar los adaptadores en el RecyclerView correspondiente
            rvSAceptadas?.adapter = solicitudAccept_adapter

            // Notificar a los adaptadores que los datos han cambiado
            solicitudAccept_adapter.notifyDataSetChanged()
        }
    }
}