package com.example.dogs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val appTitle = findViewById<LinearLayoutCompat>(R.id.appTitle)
        val appName = getString(R.string.app_name)
        val colorArray = arrayOf(
            R.color.brown_1,
            R.color.light_green_1,
            R.color.brown_2,
            R.color.light_green_2,
            R.color.brown_3,
            R.color.green_1
        )

        for (i in 0 until appName.length) {
            val character = appName[i]
            val textView = TextView(this)
            textView.text = character.toString()

            // Alternar colores en un bucle
            val colorResId = colorArray[i % colorArray.size]

            // Asignar el color del recurso
            textView.setTextColor(resources.getColor(colorResId, null))

            appTitle.addView(textView)
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        },3000)

    }
}