package com.example.appvehicular.uiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.appvehicular.R
import com.example.appvehicular.model.Mantenimiento

class MantenimientoAdapter(
    context: Context,
    private val lista: List<Mantenimiento>
) : ArrayAdapter<Mantenimiento>(context, 0, lista) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_mantenimiento, parent, false)

        val item = lista[position]

        val txtDescripcion = view.findViewById<TextView>(R.id.txtDescripcion)
        val txtFecha = view.findViewById<TextView>(R.id.txtFecha)
        val txtKilometraje = view.findViewById<TextView>(R.id.txtKilometraje)

        txtDescripcion.text = item.descripcion
        txtFecha.text = "Fecha: ${item.fecha}"
        txtKilometraje.text = "Kilometraje: ${item.kilometraje} km"

        return view
    }
}