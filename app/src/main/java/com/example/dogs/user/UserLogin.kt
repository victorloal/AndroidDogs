package com.example.dogs.user

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.example.dogs.R

class UserLogin(context: Context, private val loginResultListener: PerfilFragment) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        val btLogin =  findViewById<Button>(R.id.btLogin)
        val btLogup =  findViewById<Button>(R.id.btRegister)

        val btTwiter = findViewById<ImageButton>(R.id.btTwiter)
        val btGoogle = findViewById<ImageButton>(R.id.btGmail)
        val btFacebook = findViewById<ImageButton>(R.id.btFacebook)

        window?.setLayout((width * 0.8).toInt(), (height * 0.8).toInt())
        setCancelable(true)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btLogup.setOnClickListener {
            val customDialog = UserRegister(context, loginResultListener)
            customDialog.show()
            dismiss()
        }
        btLogin.setOnClickListener {
            login()
        }
        btGoogle.setOnClickListener{
            googleLogin()
        }
        //btTwitter.setOnClickListener{
        //    twitterLogin()
        //}
        //btFacebook.setOnClickListener{
        //    facebookLogin()
        //}
    }

    private fun login() {
        val email = findViewById<EditText>(R.id.editTextEmail)?.text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword)?.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(context, R.string.email_empty, Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(context, R.string.password_empty, Toast.LENGTH_SHORT).show()
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, puedes redirigir al usuario a la pantalla principal u otra actividad
                    Toast.makeText(context, R.string.message_login_success, Toast.LENGTH_SHORT).show()
                    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Email/Password")
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    loginResultListener.onLoginSuccess()
                    dismiss()
                } else {
                    // Hubo un error al iniciar sesión, puedes manejar el error aquí
                    Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    loginResultListener.onLoginFailure()
                }
            }
        }
    }

    private fun googleLogin(){
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(R.string.your_web_client_id.toString())
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()
    }





}
