package com.example.dogs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast


class TrabajosFragment : Fragment() {

    // Antes que inicie el fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    // Parte donde pone el fragment en la view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trabajos, container, false)
        return view
    }

    // Durante la ejecucion del fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val help_buton = view.findViewById<LinearLayout>(R.id.help_trabajosfragment)
        help_buton.setOnClickListener {
            val intent = Intent(view.context,HelpSolicitudActivity::class.java)
            startActivity(intent)
            //requireActivity().finish()
        }


    }
}