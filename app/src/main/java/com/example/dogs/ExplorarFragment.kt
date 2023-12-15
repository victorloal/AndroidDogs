package com.example.dogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExplorarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_explorar, container, false)

        val rvService = view.findViewById<RecyclerView>(R.id.rv_sercivios)
        val dataList = listOf("Texto 1", "Texto 2", "Texto 3")
        val adapter = ServiceAdapter(this, dataList)
        rvService.layoutManager = LinearLayoutManager(requireContext())
        rvService.adapter = adapter

        return view
    }

}