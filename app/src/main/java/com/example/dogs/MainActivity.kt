package com.example.dogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.analytics.FirebaseAnalytics
import com.example.dogs.OnLoginResultListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), OnLoginResultListener {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // FireBase Analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, "Email/Password")
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        //
        val cardView = findViewById<CardView>(R.id.cvUser)
        val btInformation = findViewById<ImageButton>(R.id.btInformation)
        val ibUser = findViewById<ImageButton>(R.id.ibUser)
        //
        cardView.setCardBackgroundColor(Color.TRANSPARENT)
        cardView.setBackgroundColor(Color.TRANSPARENT)

        val userLogin = UserLogin(this,this)


        btInformation.setOnClickListener{
            showDialogInformation()
        }
        ibUser.setOnClickListener{
            showDialogLogin()
        }

        val user = FirebaseAuth.getInstance().currentUser
        val dbReference = FirebaseDatabase.getInstance().getReference("User")

        user?.uid?.let { uid ->
            val userDB = dbReference.child(uid)

            userDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Obtiene los datos del usuario desde la base de datos
                        val fullName = snapshot.child("full_name").getValue(String::class.java)
                        val email = snapshot.child("email").getValue(String::class.java)
                        val username = snapshot.child("username").getValue(String::class.java)

                        // Actualiza los TextView con los datos obtenidos
                        val txtUser = findViewById<TextView>(R.id.txtUser)
                        txtUser.text = "$username"
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

    private fun showDialogLogin() {
        val customDialog = UserLogin(this,this)
        customDialog.show()
    }

    private fun showDialogInformation() {
        val dialog = Dialog(this)
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

    override fun onLoginSuccess() {
        runOnUiThread {
            // Actualizar el TextView con el mensaje de bienvenida
            val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
            txtWelcome.text = getString(R.string.message_welcome)
        }
    }

    override fun onLoginFailure() {
        runOnUiThread {
            // Actualizar el TextView con el mensaje de autenticación requerida
            val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
            txtWelcome.text = getString(R.string.message_authentication_required)
        }
    }
}