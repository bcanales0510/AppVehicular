package com.example.appvehicular.uiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter
import com.example.appvehicular.R
import com.example.appvehicular.model.Ubicacion

class UbicacionAdapter(private val context: Context, private val ubicaciones: List<Ubicacion>) : BaseAdapter() {
    override fun getCount(): Int = ubicaciones.size

    override fun getItem(position: Int): Any = ubicaciones[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = ubicaciones[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_ubicacion, parent, false)

        view.findViewById<TextView>(R.id.txtFechaUbicacion).text = item.fecha
        view.findViewById<TextView>(R.id.txtCoordenadas).text = "Lat: ${item.latitud}, Lon: ${item.longitud}"
        view.findViewById<TextView>(R.id.txtVelocidad).text = "Velocidad: ${item.velocidad.toInt()} km/h"
        view.findViewById<TextView>(R.id.txtEstado).text = "Estado: ${item.estado}"

        return view
    }
}