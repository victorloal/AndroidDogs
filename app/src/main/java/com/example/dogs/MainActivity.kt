package com.example.dogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Messenger
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






        btInformation.setOnClickListener{
            showDialogInformation()
        }
        ibUser.setOnClickListener{
            showDialogLogin()
        }
    }

    private fun showDialogLogin() {
        val customDialog = UserLogin(this)
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

}