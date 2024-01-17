package com.example.dogs.user

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dogs.R
import com.example.dogs.maps.Map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections

class PerfilFragment : Fragment(), OnLoginResultListener{

    private val mascotasList = mutableListOf<Mascota>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val btInformation = view.findViewById<ImageButton>(R.id.btInformation)
        val userLogin = UserLogin(requireContext(), this)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference: DatabaseReference = database.reference

        val ibPet: ImageButton = view.findViewById(R.id.ibPet)
        val ibUser = view.findViewById<ImageView>(R.id.ibUser)




        //map
        val ibMap = view.findViewById<ImageButton>(R.id.ibMap)
        ibMap.setOnClickListener {
            // Crear una intención para iniciar la actividad SplashScreen
            val intent = Intent(requireActivity(), Map::class.java)
            startActivity(intent)
        }

        ibPet.setOnClickListener {
            showDialogPetRegister()
        }

        validateLogin()



        ibUser.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // El usuario ya ha iniciado sesión, mostrar el diálogo DataUserActivity
                showDialogUserData()
            } else {
                // El usuario no ha iniciado sesión, mostrar el diálogo de inicio de sesión
                showDialogLogin()
                onLoginFailure()
            }
        }
    }
    data class Mascota(val id: String, val nombre: String, val raza: String)

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDismiss(position: Int)
    }

    class NombresAdapter(private val mascotas: List<Mascota>) :
        RecyclerView.Adapter<NombresAdapter.ViewHolder>(), ItemTouchHelperAdapter {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.textView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.spinner_dropdown_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val mascota = mascotas[position]
            holder.textView.text = "${mascota.nombre}"
        }

        override fun getItemCount(): Int {
            return mascotas.size
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int) {
            // Puedes implementar lógica para cambiar el orden de los elementos si es necesario
            Collections.swap(mascotas, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemDismiss(position: Int) {
            val mascota = mascotas[position]

            // Elimina la mascota de Firebase usando la clave única
            eliminarMascotaEnFirebase(mascota.id)

            // Elimina la mascota de la lista local y notifica al adaptador
            notifyItemRemoved(position)
        }

        fun eliminarMascotaEnFirebase(mascotaId: String) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.uid?.let { uid ->
                val dbReference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Mascota")

                // Elimina la mascota de Firebase utilizando su clave única
                dbReference.child(mascotaId).removeValue()
            }
        }
    }
    class RazasAdapter(private val mascotas: List<Mascota>) :
        RecyclerView.Adapter<RazasAdapter.ViewHolder>(), ItemTouchHelperAdapter {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.textView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.spinner_dropdown_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val mascota = mascotas[position]
            holder.textView.text = "${mascota.raza}"
        }

        override fun getItemCount(): Int {
            return mascotas.size
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int) {
            // Puedes implementar lógica para cambiar el orden de los elementos si es necesario
            Collections.swap(mascotas, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemDismiss(position: Int) {
            val mascota = mascotas[position]

            // Elimina la mascota de Firebase usando la clave única
            eliminarMascotaEnFirebase(mascota.id)

            // Elimina la mascota de la lista local y notifica al adaptador
            notifyItemRemoved(position)
        }

        fun eliminarMascotaEnFirebase(mascotaId: String) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.uid?.let { uid ->
                val dbReference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Mascota")

                // Elimina la mascota de Firebase utilizando su clave única
                dbReference.child(mascotaId).removeValue()
            }
        }
    }
    private fun showDialogLogin() {
        val customDialog = UserLogin(requireContext(), this)
        customDialog.show()
    }
    private fun showDialogInformation() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.information)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        dialog.window?.setLayout((width * 1).toInt(), (height * 0.9).toInt())

        val btBack = dialog.findViewById<Button>(R.id.btBack)

        btBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun showDialogUserData() {
        val userDataDialog = DataUserActivity(requireContext(), this)
        userDataDialog.show()
    }
    private fun showDialogPetRegister() {
        val petRegisterDialog = MascotaRegister(requireContext())
        petRegisterDialog.show()
    }
    private fun obtenerDatosUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        val dbReference = FirebaseDatabase.getInstance().getReference("User")
        user?.uid?.let { uid ->
            val userDB = dbReference.child(uid)

            userDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Obtiene los datos del usuario desde la base de datos
                        val username = snapshot.child("username").getValue(String::class.java)

                        // Llama al método en la interfaz para actualizar la interfaz de usuario
                        onUserDataUpdated(username ?: "") // Si username es nulo, usa una cadena vacía
                    } else {
                        // El usuario no tiene datos en la base de datos
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Maneja el error de lectura de la base de datos
                    Log.e("LecturaBD", "Error al leer datos: ${error.message}")
                }
            })
        }
    }
    private fun obtenerDatosMascota() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { uid ->
            val dbReference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Mascota")

            // Escuchar cambios en los datos de mascotas
            dbReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        mascotasList.clear()

                        for (mascotaSnapshot in snapshot.children) {
                            val nombre = mascotaSnapshot.child("nombre").getValue(String::class.java)
                            val raza = mascotaSnapshot.child("raza").getValue(String::class.java)

                            if (nombre != null && raza != null) {
                                mascotasList.add(Mascota(mascotaSnapshot.key!!, nombre, raza))
                            }
                        }

                        // Actualizar los adaptadores con la lista de mascotas
                        actualizarAdaptadores(mascotasList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores de lectura de la base de datos
                    Log.e("LecturaBD", "Error al leer datos de mascotas: ${error.message}")
                }
            })
        }
    }

    private fun actualizarAdaptadores(mascotasList: List<Mascota>) {
        Log.e("DEBUG", "Número de mascotas: ${mascotasList.size}")
        // Separar los procesos en sus respectivos hilos
        activity?.runOnUiThread {
            // Obtener referencias a los RecyclerView
            val rc_pets_n = view?.findViewById<RecyclerView>(R.id.recyclerViewPetName)
            rc_pets_n?.layoutManager = LinearLayoutManager(requireContext())

            val rc_pets_r = view?.findViewById<RecyclerView>(R.id.recyclerViewPetBreed)
            rc_pets_r?.layoutManager = LinearLayoutManager(requireContext())


            // Crear adaptadores con la lista de mascotas
            val pet_adapter_n = NombresAdapter(mascotasList.map { Mascota(it.id, it.nombre, it.raza) })
            val pet_adapter_r = RazasAdapter(mascotasList.map { Mascota(it.id, it.nombre, it.raza) })

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
                    pet_adapter_n.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                    pet_adapter_r.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Llama a la función onItemDismiss del adaptador al deslizar un elemento
                    pet_adapter_n.onItemDismiss(viewHolder.adapterPosition)
                    pet_adapter_r.onItemDismiss(viewHolder.adapterPosition)
                }
            })

            itemTouchHelper.attachToRecyclerView(rc_pets_n)
            itemTouchHelper.attachToRecyclerView(rc_pets_r)

            // Notificar a los adaptadores que los datos han cambiado
            pet_adapter_n.notifyDataSetChanged()
            pet_adapter_r.notifyDataSetChanged()

            // Configurar los adaptadores en los RecyclerView correspondientes
            rc_pets_n?.adapter = pet_adapter_n
            rc_pets_r?.adapter = pet_adapter_r

            // Notificar a los adaptadores que los datos han cambiado
            pet_adapter_n.notifyDataSetChanged()
            pet_adapter_r.notifyDataSetChanged()
        }
    }

    fun onUserDataUpdated(username: String) {
        activity?.runOnUiThread {
            // Actualiza el TextView con los datos obtenidos
            val txtUser = view?.findViewById<TextView>(R.id.txtUser)
            txtUser?.text = username
        }
    }
    override fun onLoginSuccess() {
        // Separar los procesos en sus respectivos hilos
        activity?.runOnUiThread {
            // Actualizar el TextView con el mensaje de bienvenida
            val txtWelcome = view?.findViewById<TextView>(R.id.txtWelcome)
            txtWelcome?.text = getString(R.string.message_welcome)
            val linearlayout = view?.findViewById<LinearLayout>(R.id.linear_layout)
            linearlayout?.visibility = View.VISIBLE
            obtenerDatosUsuario()
            obtenerDatosMascota()
        }
    }

    override fun onLoginFailure() {
        activity?.runOnUiThread {
            // Actualizar el TextView con el mensaje de autenticación requerida
            val txtWelcome = view?.findViewById<TextView>(R.id.txtWelcome)
            txtWelcome?.text = getString(R.string.message_authentication_required)
            val linearlayout = view?.findViewById<LinearLayout>(R.id.linear_layout)
            linearlayout?.visibility = View.GONE
        }
    }

    override fun onLogout() {
        activity?.runOnUiThread {
            // Actualizar el TextView con el mensaje de autenticación requerida
            val txtWelcome = view?.findViewById<TextView>(R.id.txtWelcome)
            txtWelcome?.text = getString(R.string.message_authentication_required)
            val txtUser = view?.findViewById<TextView>(R.id.txtUser)
            txtUser?.text = getString(R.string.login_button)
            val linearlayout = view?.findViewById<LinearLayout>(R.id.linear_layout)
            linearlayout?.visibility = View.GONE
            mascotasList.clear()
        }
    }

    private fun validateLogin() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            onLoginSuccess()
        } else {
            onLoginFailure()
        }
    }
}