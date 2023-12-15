package com.example.dogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.example.dogs.OnLoginResultListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class HomeFragment : Fragment(), OnLoginResultListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btInformation = view.findViewById<ImageButton>(R.id.btInformation)
        val ibUser = view.findViewById<ImageButton>(R.id.ibUser)

        val userLogin = UserLogin(requireContext(), this)

        //map
        val ibMap = view.findViewById<ImageButton>(R.id.ibMap)
        ibMap.setOnClickListener {
            // Crear una intención para iniciar la actividad SplashScreen
            val intent = Intent(requireActivity(), Map::class.java)
            startActivity(intent)
        }



        validateLogin()

        btInformation.setOnClickListener {
            showDialogInformation()
        }

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
            obtenerDatosUsuario()
        }
    }

    override fun onLoginFailure() {
        activity?.runOnUiThread {
            // Actualizar el TextView con el mensaje de autenticación requerida
            val txtWelcome = view?.findViewById<TextView>(R.id.txtWelcome)
            txtWelcome?.text = getString(R.string.message_authentication_required)
        }
    }

    override fun onLogout() {
        activity?.runOnUiThread {
            // Actualizar el TextView con el mensaje de autenticación requerida
            val txtWelcome = view?.findViewById<TextView>(R.id.txtWelcome)
            txtWelcome?.text = getString(R.string.message_authentication_required)
            val txtUser = view?.findViewById<TextView>(R.id.txtUser)
            txtUser?.text = getString(R.string.login_button)
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