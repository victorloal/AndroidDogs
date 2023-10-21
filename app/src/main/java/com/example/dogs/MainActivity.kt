package com.example.dogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btRegister = findViewById<Button>(R.id.btRegister)
        val btLogin = findViewById<Button>(R.id.btLogin)
        val btInformation = findViewById<ImageButton>(R.id.btInformation)

        btLogin.setOnClickListener{
            showDialogLogin()
        }
        btRegister.setOnClickListener{
            showDialogRegister()
        }
        btInformation.setOnClickListener{
            showDialogInformation()
        }

    }
    private fun showDialogLogin() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.activity_user_login)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btLogin = dialog.findViewById<Button>(R.id.btLogin)

        btLogin.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDialogRegister() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.activity_user_register)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btRegister = dialog.findViewById<Button>(R.id.btRegister)

        btRegister.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun showDialogInformation() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.information)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btBack = dialog.findViewById<Button>(R.id.btBack)

        btBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}