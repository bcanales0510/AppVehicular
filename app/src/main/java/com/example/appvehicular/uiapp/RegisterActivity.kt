package com.example.appvehicular.uiapp

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appvehicular.DATA.BDHelper
import com.example.appvehicular.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtUsuario: EditText
    private lateinit var edtClave: EditText
    private lateinit var edtPlaca: EditText
    private lateinit var edtMarca: EditText
    private lateinit var edtModelo: EditText
    private lateinit var edtAnio: EditText
    private lateinit var btnRegistrar: Button

    private lateinit var dbHelper: BDHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        edtUsuario = findViewById(R.id.edtUsuario)
        edtClave = findViewById(R.id.edtClave)
        edtPlaca = findViewById(R.id.edtPlaca)
        edtMarca = findViewById(R.id.edtMarca)
        edtModelo = findViewById(R.id.edtModelo)

        btnRegistrar = findViewById(R.id.btnRegistrar)

        dbHelper = BDHelper(this)
        db = dbHelper.writableDatabase

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        val usuario = edtUsuario.text.toString().trim()
        val clave = edtClave.text.toString().trim()
        val placa = edtPlaca.text.toString().trim()
        val marca = edtMarca.text.toString().trim()
        val modelo = edtModelo.text.toString().trim()


        if (usuario.isEmpty() || clave.isEmpty() || placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() ) {
            Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        val valuesVehiculo = ContentValues().apply {
            put("placa", placa)
            put("marca", marca)
            put("modelo", modelo)

        }

        val vehiculoId = db.insert("vehiculos", null, valuesVehiculo)

        if (vehiculoId == -1L) {
            Toast.makeText(this, "Error al registrar vehículo. ¿Placa duplicada?", Toast.LENGTH_SHORT).show()
            return
        }

        val valuesUsuario = ContentValues().apply {
            put("usuario", usuario)
            put("clave", clave)
            put("id_vehiculo", vehiculoId.toInt())
        }

        val usuarioId = db.insert("usuarios", null, valuesUsuario)

        if (usuarioId != -1L) {
            Toast.makeText(this, "Registro exitoso. Inicia sesión.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Error al registrar usuario.", Toast.LENGTH_SHORT).show()
        }
    }
}