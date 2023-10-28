package com.example.dogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardView = findViewById<CardView>(R.id.cvUser)
        val btInformation = findViewById<ImageButton>(R.id.btInformation)

        val ibUser = findViewById<ImageButton>(R.id.ibUser)

        cardView.setCardBackgroundColor(Color.TRANSPARENT)
        cardView.setBackgroundColor(Color.TRANSPARENT)
        btInformation.setOnClickListener{
            showDialogInformation()
        }
        ibUser.setOnClickListener{
            showDialogLogin()
        }


    }



    private fun showDialogLogin() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)


        dialog.setContentView(R.layout.activity_user_login)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        dialog.window?.setLayout((width * 0.8).toInt(), (height * 0.8).toInt())

        val btLogin = dialog.findViewById<Button>(R.id.btLogin)


        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()

        val editTextUsername =dialog.findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword =dialog.findViewById<EditText>(R.id.editTextPassword)
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

        dialog.show()
    }

    private fun showDialogRegister() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.activity_user_register)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        dialog.window?.setLayout((width * 0.8).toInt(), (height * 0.8).toInt())

        val btRegister = dialog.findViewById<Button>(R.id.btRegister)

        btRegister.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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

}