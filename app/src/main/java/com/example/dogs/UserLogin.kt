package com.example.dogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class UserLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
    }
    private fun showDialogLogin() {
        val btLogin =  findViewById<Button>(R.id.btLogin)


        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        bundle.putString("message","Integración de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        btLogin.setOnClickListener {
            if (editTextUsername.text.isNotEmpty() && editTextPassword.text.isNotEmpty() ){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(editTextUsername.text.toString(),
                        editTextPassword.text.toString()).addOnCompleteListener {

                    }
            }
        }
    }
}