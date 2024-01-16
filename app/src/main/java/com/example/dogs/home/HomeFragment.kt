package com.example.dogs.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import com.example.dogs.R


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var button1: ImageButton
    private lateinit var button2: ImageButton
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Inicializar vistas
        viewFlipper = view.findViewById(R.id.viewFlipper)
        button1 = view.findViewById(R.id.button1)
        button2 = view.findViewById(R.id.button2)

        // Configurar listeners para los botones
        button1.setOnClickListener {
            viewFlipper.showPrevious()
        }

        button2.setOnClickListener {
            viewFlipper.showNext()
        }

        val sliderPaseos: LinearLayout = view.findViewById(R.id.sliderPaseos)// Reemplaza con tu ID
        val sliderSocial: LinearLayout = view.findViewById(R.id.sliderSocial)// Reemplaza con tu ID
        val sliderVeterinaria: LinearLayout = view.findViewById(R.id.sliderVeterinaria)// Reemplaza con tu ID

        sliderPaseos.setOnClickListener {

            viewFlipper.showNext()
        }
        sliderSocial.setOnClickListener {

            viewFlipper.showNext()
        }
        sliderVeterinaria.setOnClickListener {

            viewFlipper.showNext()
        }

    }


}


