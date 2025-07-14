package com.example.appvehicular.uiapp

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appvehicular.DATA.BDHelper
import com.example.appvehicular.R
import java.text.SimpleDateFormat
import java.util.*
import com.example.appvehicular.model.Mantenimiento

class MantenimientosActivity : AppCompatActivity() {
    
    private lateinit var listViewMantenimientos: ListView
    private lateinit var txtTitulo: TextView
    private lateinit var btnAgregarMantenimiento: Button
    private lateinit var dbHelper: BDHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var adapter: MantenimientoAdapter
    private val mantenimientos = mutableListOf<Mantenimiento>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mantenimientos)
        
        title = "Mantenimientos del Vehículo"
        
        // Inicializar vistas
        listViewMantenimientos = findViewById(R.id.listViewMantenimientos)
        txtTitulo = findViewById(R.id.txtTituloMantenimientos)
        btnAgregarMantenimiento = findViewById(R.id.btnAgregarMantenimiento)
        
        // Inicializar base de datos
        dbHelper = BDHelper(this)
        db = dbHelper.writableDatabase
        
        // Configurar adapter
        adapter = MantenimientoAdapter(this, mantenimientos)
        listViewMantenimientos.adapter = adapter


        // Configurar botón agregar
        btnAgregarMantenimiento.setOnClickListener {
            mostrarDialogoAgregarMantenimiento()
        }
        
        // Configurar click en lista
        listViewMantenimientos.setOnItemClickListener { _, _, position, _ ->
            mostrarDetalleMantenimiento(position)
        }
        
        // Cargar mantenimientos
        cargarMantenimientos()
    }

    private fun cargarMantenimientos() {
        mantenimientos.clear()

        // Mantenimientos simulados
        mantenimientos.add(Mantenimiento("Cambio de aceite", "15/12/2024", 50000))
        mantenimientos.add(Mantenimiento("Rotación de llantas", "10/11/2024", 45000))
        mantenimientos.add(Mantenimiento("Revisión general", "05/10/2024", 40000))
        mantenimientos.add(Mantenimiento("Cambio de filtro de combustible", "20/09/2024", 35000))
        mantenimientos.add(Mantenimiento("Cambio de filtro de aire", "15/08/2024", 30000))

        adapter.notifyDataSetChanged()
        txtTitulo.text = "Mantenimientos Realizados (${mantenimientos.size})"
    }


    private fun mostrarDialogoAgregarMantenimiento() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_agregar_mantenimiento, null)
        val edtDescripcion = dialogView.findViewById<EditText>(R.id.edtDescripcionMantenimiento)
        val edtKilometraje = dialogView.findViewById<EditText>(R.id.edtKilometrajeMantenimiento)
        
        AlertDialog.Builder(this)
            .setTitle("Agregar Mantenimiento")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val descripcion = edtDescripcion.text.toString().trim()
                val kilometraje = edtKilometraje.text.toString().trim()
                
                if (descripcion.isNotEmpty() && kilometraje.isNotEmpty()) {
                    agregarMantenimiento(descripcion, kilometraje.toInt())
                } else {
                    Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun agregarMantenimiento(descripcion: String, kilometraje: Int) {
        try {
            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            // Crear el ContentValues correctamente
            val values = ContentValues()
            values.put("id_vehiculo", 1) // Puedes reemplazarlo con el ID real si lo tienes
            values.put("descripcion", descripcion)
            values.put("fecha", fecha)
            values.put("kilometraje", kilometraje)

            // Insertar en la base de datos
            val result = db.insert("mantenimientos", null, values)

            if (result != -1L) {
                // Agregar a la lista y actualizar adaptador
                val nuevoMantenimiento = Mantenimiento(descripcion, fecha, kilometraje)
                mantenimientos.add(0, nuevoMantenimiento)
                adapter.notifyDataSetChanged()

                txtTitulo.text = "Mantenimientos Realizados (${mantenimientos.size})"
                Toast.makeText(this, "Mantenimiento agregado exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al agregar mantenimiento", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun mostrarDetalleMantenimiento(position: Int) {
        val mantenimiento = mantenimientos[position]  // mantenimiento es un objeto de tipo Mantenimiento

        val mensaje = """
         Descripción: ${mantenimiento.descripcion}
         Fecha: ${mantenimiento.fecha}
         Kilometraje: ${mantenimiento.kilometraje} km
        
        Este mantenimiento fue realizado según el programa de mantenimiento preventivo del vehículo.
    """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Detalle del Mantenimiento")
            .setMessage(mensaje)
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        db.close()
        dbHelper.close()
    }
}