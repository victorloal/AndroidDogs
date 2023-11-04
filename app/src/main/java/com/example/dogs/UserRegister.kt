package com.example.dogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class UserRegister(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        val btLogin =  findViewById<Button>(R.id.btLogin)
        val btLogup =  findViewById<Button>(R.id.btRegister)
        window?.setLayout((width * 0.8).toInt(), (height * 0.8).toInt())
        setCancelable(true)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btLogin.setOnClickListener {
            val customDialog = UserLogin(context)
            customDialog.show()
            dismiss()
        }
        btLogup.setOnClickListener {
            logup()
        }
    }

    private fun logup() {
        val fullName = findViewById<EditText>(R.id.editTextName)?.text.toString()
        val email = findViewById<EditText>(R.id.editTextEmail)?.text.toString()
        val username = findViewById<EditText>(R.id.editTextUsername)?.text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword)?.text.toString()
        val rep_password = findViewById<EditText>(R.id.editTextPassword)?.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(context, R.string.email_empty, Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(context, R.string.password_empty, Toast.LENGTH_SHORT).show()
        } else if (fullName.isEmpty()) {
            Toast.makeText(context, R.string.fullname_empty, Toast.LENGTH_SHORT).show()
        } else if (username.isEmpty()) {
            Toast.makeText(context, R.string.username_empty, Toast.LENGTH_SHORT).show()
        } else {
            if (password == rep_password) {
                // Todas las variables tienen contenido, puedes proceder con el registro
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Email/Password")
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
                        } else {
                            // Hubo un error al registrar al usuario, puedes manejar el error aquí
                        }
                    }
            }else{
                Toast.makeText(context, R.string.password_difer, Toast.LENGTH_SHORT).show()
            }
        }
    }




}