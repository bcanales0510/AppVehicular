package com.example.appvehicular.uiapp
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.appvehicular.R
import com.example.appvehicular.model.Alerta

class AlertaAdapter(context: Context, private val alertas: List<Alerta>) :
    ArrayAdapter<Alerta>(context, 0, alertas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_alerta, parent, false)
        val alerta = alertas[position]

        view.findViewById<TextView>(R.id.txtTipo).text = alerta.tipo
        view.findViewById<TextView>(R.id.txtDescripcion).text = alerta.descripcion
        view.findViewById<TextView>(R.id.txtFecha).text = alerta.fecha

        return view
    }
}
