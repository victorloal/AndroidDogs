package com.example.dogs

// SettingsActivity.kt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Configurar la ActionBar para mostrar la flecha hacia atrás
        supportActionBar?.apply {
            title = "Configuraciones"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    // Manejar el clic en la flecha hacia atrás en la ActionBar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}