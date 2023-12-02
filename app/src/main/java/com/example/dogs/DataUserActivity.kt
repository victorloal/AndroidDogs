package com.example.dogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.dogs.OnLoginResultListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataUserActivity (context: Context, private val loginResultListener: OnLoginResultListener) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_user)
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        window?.setLayout((width * 0.8).toInt(), (height * 0.8).toInt())
        setCancelable(true)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        obtenerDatosUsuario()

        val logoutButton = findViewById<Button>(R.id.btLogout)

        logoutButton.setOnClickListener {
            // Llamada a la función para cerrar sesión
            signOut()
            loginResultListener.onLogout()
            dismiss()
        }
    }

    private fun obtenerDatosUsuario() {
        val user = FirebaseAuth.getInstance().currentUser

        //Decirle a la app como se llama la tabla dentro de Realtime Database de Firebase
        val dbReference = FirebaseDatabase.getInstance().getReference("User")

        user?.uid?.let { uid ->
            val userDB = dbReference.child(uid)

            userDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Obtiene los datos del usuario desde la base de datos
                        val username = snapshot.child("username").getValue(String::class.java)
                        val full_name = snapshot.child("full_name").getValue(String::class.java)
                        val email = snapshot.child("email").getValue(String::class.java)

                        // Llama al método en la interfaz para actualizar la interfaz de usuario
                        onUserDataUpdated(username ?: "", full_name ?: "", email ?: "") // Si username es nulo, usa una cadena vacía
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

    fun onUserDataUpdated(username: String, fullName: String, email: String) {
        // Actualiza el TextView con los datos obtenidos
        val tvTitleUser = findViewById<TextView>(R.id.tvTitleUser)
        val tvFullname = findViewById<TextView>(R.id.tvFullname)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)

        tvTitleUser.text = username
        tvFullname.text = fullName
        tvEmail.text = email
    }

    private fun signOut() {
        // Utiliza FirebaseAuth para cerrar sesión
        FirebaseAuth.getInstance().signOut()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Toast.makeText(context, "No se ha podido cerrar la sesión", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(context, "Se ha cerrado la sesión", Toast.LENGTH_SHORT).show()
        }
    }
}