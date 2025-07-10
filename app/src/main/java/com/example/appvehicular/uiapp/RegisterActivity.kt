package com.example.appvehicular.uiapp

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appvehicular.R
import com.example.appvehicular.DATA.BDHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtUsuario: EditText
    private lateinit var edtClave: EditText
    private lateinit var edtPlaca: EditText
    private lateinit var btnRegistrar: Button

    private lateinit var dbHelper: BDHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Referencias de vista
        edtUsuario = findViewById(R.id.edtUsuario)
        edtClave = findViewById(R.id.edtClave)
        edtPlaca = findViewById(R.id.edtPlaca)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        dbHelper = BDHelper(this)
        db = dbHelper.writableDatabase

        btnRegistrar.setOnClickListener {
            val usuario = edtUsuario.text.toString().trim()
            val clave = edtClave.text.toString().trim()
            val placa = edtPlaca.text.toString().trim().uppercase()

            if (usuario.isEmpty() || clave.isEmpty() || placa.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                registrarUsuario(usuario, clave, placa)
            }
        }
    }

    private fun registrarUsuario(usuario: String, clave: String, placa: String) {
        try {
            // 1. Verificar si el vehículo ya existe
            val cursorVehiculo = db.rawQuery("SELECT id FROM vehiculos WHERE placa = ?", arrayOf(placa))
            var idVehiculo: Int? = null

            if (cursorVehiculo.moveToFirst()) {
                idVehiculo = cursorVehiculo.getInt(0)
            } else {
                // Insertar nuevo vehículo
                val valuesVehiculo = ContentValues().apply {
                    put("placa", placa)
                }
                val newRowId = db.insert("vehiculos", null, valuesVehiculo)
                if (newRowId != -1L) {
                    idVehiculo = newRowId.toInt()
                }
            }
            cursorVehiculo.close()

            if (idVehiculo != null) {
                // Verificar si usuario ya existe
                val cursorUsuario = db.rawQuery("SELECT id FROM usuarios WHERE usuario = ?", arrayOf(usuario))
                if (cursorUsuario.moveToFirst()) {
                    Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                    cursorUsuario.close()
                    return
                }
                cursorUsuario.close()

                // Insertar usuario
                val valuesUsuario = ContentValues().apply {
                    put("usuario", usuario)
                    put("clave", clave)
                    put("id_vehiculo", idVehiculo)
                }

                val result = db.insert("usuarios", null, valuesUsuario)

                if (result != -1L) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Error al registrar vehículo", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}