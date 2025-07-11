package com.example.appvehicular.uiapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.appvehicular.R
import com.example.appvehicular.DATA.BDHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsuario: EditText
    private lateinit var edtClave: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtRegistrarse: TextView
    private lateinit var dbHelper: BDHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtUsuario = findViewById(R.id.edtUsuario)
        edtClave = findViewById(R.id.edtClave)
        btnLogin = findViewById(R.id.btnLogin)
        txtRegistrarse = findViewById(R.id.txtRegistrarse)

        dbHelper = BDHelper(this)
        db = dbHelper.readableDatabase

        btnLogin.setOnClickListener {
            val usuario = edtUsuario.text.toString().trim()
            val clave = edtClave.text.toString().trim()

            if (usuario.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                validarCredenciales(usuario, clave)
            }
        }

        txtRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validarCredenciales(usuario: String, clave: String) {
        val cursor = db.rawQuery(
            "SELECT id_vehiculo FROM usuarios WHERE usuario = ? AND clave = ?",
            arrayOf(usuario, clave)
        )
        if (cursor.moveToFirst()) {
            val idVehiculo = cursor.getInt(0)
            cursor.close()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("id_vehiculo", idVehiculo)
            startActivity(intent)
            finish()
        } else {
            cursor.close()
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }
    }
}