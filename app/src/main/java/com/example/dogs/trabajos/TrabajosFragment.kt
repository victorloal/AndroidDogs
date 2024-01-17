package com.example.dogs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TrabajosFragment : Fragment() {

    private lateinit var dbReference: DatabaseReference
    private val ofertasList = mutableListOf<Oferta>()
    private val trabajosAceptadosList = mutableListOf<Trabajo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trabajos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa las referencias de Firebase y otros componentes necesarios
        dbReference = FirebaseDatabase.getInstance().getReference("User")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Obtiene todas las solicitudes realizadas por todos los usuarios
            obtenerTodasLasSolicitudes()
            obtenerTrabajosAceptados()
        }
    }

    private fun onAcceptWorkClick(oferta: Oferta) {
        // Aquí implementas la lógica para actualizar la solicitud y crear el nodo "Trabajo"
        // para el usuario autenticado
        // 1. Actualizar la solicitud a false
        // 2. Crear el nodo "Trabajo" con la información de la solicitud en la base de datos
        // 3. Actualizar el RecyclerView de trabajos aceptados (rvOAceptadas) con la nueva información

        // Ejemplo:
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            actualizarSolicitud(oferta.userId, oferta.id)
            crearNodoTrabajo(oferta, currentUser.uid)
            obtenerTrabajosAceptados()
        }
    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDismiss(position: Int)
    }

    data class Oferta(val id: String, val peticion: String, val nombre: String, val estado: Boolean, val userId: String)

    data class Trabajo(val id: String, val peticion: String, val nombre: String, val userId: String)
    class OfertasAdapter(
        private val ofertas: List<Oferta>,
        private val onItemClick: (Oferta) -> Unit
    ) : RecyclerView.Adapter<OfertasAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
            val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
            val btnAcceptWork: ImageView = itemView.findViewById(R.id.btnAcceptWork)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_trabajo, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val oferta = ofertas[position]
            if (oferta.estado) {
                holder.textViewTitle.text = oferta.nombre
                holder.textViewDescription.text = oferta.peticion

                // Agrega el OnClickListener al botón
                holder.btnAcceptWork.setOnClickListener {
                    onItemClick(oferta)
                }
            } else {
                // Si el estado es false, puedes ocultar o manejar de otra manera las solicitudes con estado false
                holder.itemView.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            return ofertas.size
        }
    }


    private fun obtenerTodasLasSolicitudes() {
        // Escucha cambios en los datos de todas las solicitudes
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    ofertasList.clear()

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val currentUserId = currentUser?.uid

                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.key

                        // Verifica que userId no sea nulo y que tenga al menos una solicitud
                        if (userId != null && userSnapshot.hasChild("Solicitud") && userId != currentUserId) {
                            val solicitudSnapshot = userSnapshot.child("Solicitud")

                            for (solicitudDataSnapshot in solicitudSnapshot.children) {
                                val solicitudId = solicitudDataSnapshot.key
                                val nombre = solicitudDataSnapshot.child("nombre").getValue(String::class.java)
                                val peticion = solicitudDataSnapshot.child("peticion").getValue(String::class.java)
                                val estado = solicitudDataSnapshot.child("estado").getValue(Boolean::class.java)

                                // Verifica que el estado sea true
                                if (solicitudId != null && nombre != null && peticion != null && estado == true) {
                                    ofertasList.add(Oferta(solicitudId, peticion, nombre, true, userId))
                                }
                            }
                        }
                    }

                    // Actualizar el adaptador de rvOfertas con la lista de todas las solicitudes con estado true
                    actualizarAdaptadores(ofertasList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores de lectura de la base de datos
                Log.e("LecturaBD", "Error al leer datos de solicitudes: ${error.message}")
            }
        })
    }

    private fun actualizarAdaptadores(ofertasList: List<Oferta>) {
        val rvOfertas = view?.findViewById<RecyclerView>(R.id.rvOfertas)
        rvOfertas?.layoutManager = LinearLayoutManager(requireContext())

        val ofertas_adapter = OfertasAdapter(ofertasList.map {
            Oferta(it.id, it.peticion, it.nombre, it.estado, it.userId)
        }) { oferta ->
            // Manejar el clic en el botón
            onAcceptWorkClick(oferta)
        }

        rvOfertas?.adapter = ofertas_adapter
        ofertas_adapter.notifyDataSetChanged()
    }

    class TrabajosAceptadosAdapter(private val trabajosAceptados: List<Trabajo>) :
        RecyclerView.Adapter<TrabajosAceptadosAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
            val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_solicitud, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val trabajo = trabajosAceptados[position]
            holder.textViewTitle.text = trabajo.nombre
            holder.textViewDescription.text = trabajo.peticion
        }

        override fun getItemCount(): Int {
            return trabajosAceptados.size
        }
    }

    private fun actualizarSolicitud(userId: String, solicitudId: String) {
        // Actualizar el estado de la solicitud a false en la base de datos
        val solicitudRef = dbReference.child(userId).child("Solicitud").child(solicitudId)
        solicitudRef.child("estado").setValue(false)
    }

    private fun crearNodoTrabajo(oferta: Oferta, userId: String) {
        // Crear un nuevo nodo "Trabajo" en la base de datos dentro del nodo del usuario específico
        val trabajoRef = dbReference.child(userId).child("Trabajo").push()
        val trabajo = mapOf(
            "id" to trabajoRef.key,
            "nombre" to oferta.nombre,
            "peticion" to oferta.peticion,
            "userId" to oferta.userId
        )
        trabajoRef.setValue(trabajo)
    }

    private fun obtenerTrabajosAceptados() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { uid ->
            val dbReference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Trabajo")

            dbReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        trabajosAceptadosList.clear()

                        for (trabajoSnapshot in snapshot.children) {
                            val trabajoId = trabajoSnapshot.key
                            val nombre = trabajoSnapshot.child("nombre").getValue(String::class.java)
                            val peticion = trabajoSnapshot.child("peticion").getValue(String::class.java)

                            if (trabajoId != null && nombre != null && peticion != null) {
                                trabajosAceptadosList.add(Trabajo(trabajoId, peticion, nombre, currentUser.uid))
                            }
                        }

                        // Verificar si la lista está vacía o no
                        Log.d("TrabajosAceptados", "Número de trabajos aceptados: ${trabajosAceptadosList.size}")

                        // Actualizar el adaptador de rvOAceptadas con la lista de trabajos aceptados
                        actualizarAdaptadorTrabajosAceptados(trabajosAceptadosList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("LecturaBD", "Error al leer datos de trabajos aceptados: ${error.message}")
                }
            })
        }
    }

    private fun actualizarAdaptadorTrabajosAceptados(trabajosAceptadosList: List<Trabajo>) {
        activity?.runOnUiThread {
            // Obtener referencias a los RecyclerView
            val rvOAceptadas = view?.findViewById<RecyclerView>(R.id.rvOAceptadas)
            rvOAceptadas?.layoutManager = LinearLayoutManager(requireContext())

            // Crear adaptadores con la lista de solicitudes aceptadas
            val ofertaAccept_adapter = TrabajosAceptadosAdapter(trabajosAceptadosList.map {
                Trabajo(
                    it.id,
                    it.peticion,
                    it.nombre,
                    it.userId,
                )
            })

            // Configurar los adaptadores en el RecyclerView correspondiente
            rvOAceptadas?.adapter = ofertaAccept_adapter

            // Notificar a los adaptadores que los datos han cambiado
            ofertaAccept_adapter.notifyDataSetChanged()
        }
    }
}