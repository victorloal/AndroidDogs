package com.example.dogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class HelpTrabajoActivity : AppCompatActivity() {

    private lateinit var textPasos: TextView
    private lateinit var textInstrucciones: TextView
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private var pasoActual = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_trabajo)

        // Inicializar vistas
        textPasos = this.findViewById<TextView>(R.id.textPasos)
        textInstrucciones = this.findViewById(R.id.textInstrucciones)
        btnAnterior = this.findViewById(R.id.btnAnterior)
        btnSiguiente = this.findViewById(R.id.btnSiguiente)

        // Configurar acciones de botones
        btnAnterior.setOnClickListener { irPasoAnterior() }
        btnSiguiente.setOnClickListener { irPasoSiguiente() }

        // Mostrar el primer paso
        mostrarPaso()
    }

    private fun irPasoAnterior() {
        if (pasoActual > 1) {
            pasoActual--
            mostrarPaso()
        }
    }

    private fun irPasoSiguiente() {
        // Puedes agregar lógica aquí para validar si se permite ir al siguiente paso
        if (pasoActual == 7){
            finish()
        }else{
            pasoActual++
            mostrarPaso()
        }
    }

    private fun mostrarPaso() {
        textPasos.text = "Paso $pasoActual"
        textInstrucciones.text = "Aquí van las instrucciones del paso $pasoActual"
        if (pasoActual == 7){
            btnSiguiente.text = "CONTINUAR"
        }else{
            btnSiguiente.text = "SIGUIENTE"
        }
    }

}