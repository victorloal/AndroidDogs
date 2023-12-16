package com.example.dogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MascotaRegister(context: Context) : Dialog(context) {
    private lateinit var dbReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascota_register)

        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        val btSave = findViewById<Button>(R.id.btSave)
        window?.setLayout((width * 0.8).toInt(), (height * 0.8).toInt())
        setCancelable(true)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Inicializar Firebase
        dbReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        val spinner: Spinner = findViewById(R.id.spBreed)
        val petName = findViewById<EditText>(R.id.editPetName)

        // Define los elementos para el combo box
        val items = arrayOf("Seleccione", "Pastor alemán", "Husky siberiano", "Golden retriever", "Bulldog francés",
            "Beagle", "Rottweiler", "Pit Bull", "Poodle", "Schnauzer", "Chihuahua", "Maltese")

        // Crea un adaptador para el Spinner utilizando el diseño personalizado
        val adapter = ArrayAdapter(context, R.layout.spinner_dropdown_item, items)

        // Aplica el adaptador al Spinner
        spinner.adapter = adapter

        // Configura un listener para manejar la selección del Spinner
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Maneja la selección del elemento
                val selectedItem = items[position]
                // Puedes realizar acciones con el elemento seleccionado aquí
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Maneja la ausencia de selección
            }
        })

        btSave.setOnClickListener {
            if (petName == null){
                Toast.makeText(context, "Debe ingresar el nombre de la mascota", Toast.LENGTH_SHORT).show()
            }else if (spinner.selectedItem.toString() == "Seleccione" || spinner.isEmpty()){
                Toast.makeText(context, "Debe seleccionar la raza de la mascota", Toast.LENGTH_SHORT).show()
            }else{
                guardarMascota(petName.text.toString(), spinner.selectedItem.toString())
            }
        }
    }

    private fun guardarMascota(nombre: String, raza: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userDB = dbReference.child("User").child(userId)

            // Generar un nuevo identificador único para la mascota
            val mascotaId = userDB.child("Mascota").push().key

            // Guardar la información de la mascota en el nodo del usuario
            val nuevaMascota = hashMapOf(
                "nombre" to nombre,
                "raza" to raza
            )
            userDB.child("Mascota").child(mascotaId!!).setValue(nuevaMascota)

            Toast.makeText(context, "Mascota guardada exitosamente", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}
