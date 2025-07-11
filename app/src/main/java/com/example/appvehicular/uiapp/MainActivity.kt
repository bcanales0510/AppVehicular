package com.example.appvehicular.uiapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appvehicular.DATA.BDHelper
import com.example.appvehicular.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var map: GoogleMap
    private lateinit var txtVehiculoInfo: TextView
    private lateinit var txtEstadoVehiculo: TextView
    private lateinit var btnAlertas: Button
    private lateinit var btnMantenimientos: Button
    private lateinit var btnHistorial: Button

    private lateinit var dbHelper: BDHelper
    private lateinit var db: SQLiteDatabase
    
    private var idVehiculo: Int = 0
    private var marcadorVehiculo: Marker? = null
    private var posicionActual = LatLng(-2.170998, -79.922359) // Guayaquil
    private var angulo = 0.0
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            simularMovimientoVehiculo()
            handler.postDelayed(this, 3000) // Actualizar cada 3 segundos
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener ID del vehículo del intent
        idVehiculo = intent.getIntExtra("id_vehiculo", 0)
        
        // Inicializar base de datos
        dbHelper = BDHelper(this)
        db = dbHelper.readableDatabase


        
        // Configurar mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar menú lateral

        configurarVistas()

        // Cargar información del vehículo
        cargarInformacionVehiculo()
        
        // Iniciar simulación de movimiento
        iniciarSimulacionMovimiento()
    }

    private fun configurarVistas() {
        txtVehiculoInfo = findViewById(R.id.txtVehiculoInfo)
        txtEstadoVehiculo = findViewById(R.id.txtEstadoVehiculo)

    }

    private fun cargarInformacionVehiculo() {
        if (idVehiculo > 0) {
            val cursor = db.rawQuery(
                "SELECT placa, marca, modelo FROM vehiculos WHERE id = ?",
                arrayOf(idVehiculo.toString())
            )

            if (cursor.moveToFirst()) {
                val placa = cursor.getString(0)
                val marca = cursor.getString(1) ?: "Sin marca"
                val modelo = cursor.getString(2) ?: "Sin modelo"


                // Mostrar información completa del vehículo
                val infoVehiculo = "$marca $modelo\nPlaca: $placa"
                txtVehiculoInfo.text = infoVehiculo
                

                
                // Actualizar título del marcador en el mapa
                marcadorVehiculo?.title = "$marca $modelo"
                marcadorVehiculo?.snippet = "Placa: $placa"
            }
            cursor.close()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        
        // Configurar mapa
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
        
        // Posición inicial (Guayaquil)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionActual, 15f))
        
        // Agregar marcador del vehículo
        agregarMarcadorVehiculo()
    }

    private fun agregarMarcadorVehiculo() {
        val markerOptions = MarkerOptions()
            .position(posicionActual)
            .title("Mi Vehículo")
            .snippet("En movimiento")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_default))
            .rotation(angulo.toFloat())

        marcadorVehiculo = map.addMarker(markerOptions)
    }

    private fun iniciarSimulacionMovimiento() {
        handler.post(runnable)
    }

    private fun simularMovimientoVehiculo() {
        // Simular movimiento en círculo
        val radio = 0.001 // Radio del círculo
        angulo += 0.1 // Incremento del ángulo
        
        val nuevaLatitud = posicionActual.latitude + radio * cos(angulo)
        val nuevaLongitud = posicionActual.longitude + radio * sin(angulo)
        
        posicionActual = LatLng(nuevaLatitud, nuevaLongitud)
        
        // Actualizar marcador
        marcadorVehiculo?.position = posicionActual
        marcadorVehiculo?.rotation = (angulo * 180 / Math.PI).toFloat()
        
        // Actualizar estado
        val velocidad = (Math.random() * 60 + 20).toInt() // 20-80 km/h
        txtEstadoVehiculo.text = "Estado: En movimiento - $velocidad km/h"
        
        // Guardar ubicación en base de datos
        guardarUbicacion(posicionActual, velocidad.toDouble())
    }

    private fun guardarUbicacion(posicion: LatLng, velocidad: Double) {
        try {
            val values = android.content.ContentValues().apply {
                put("id_vehiculo", idVehiculo)
                put("latitud", posicion.latitude)
                put("longitud", posicion.longitude)
                put("velocidad", velocidad)
                put("estado", "En movimiento")
                put("fecha", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()))
            }
            
            db.insert("ubicaciones", null, values)
        } catch (e: Exception) {
            // Manejar error silenciosamente
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.open()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        db.close()
        dbHelper.close()
    }

    private fun mostrarMensaje(mensaje: String) {
        android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_SHORT).show()
    }
}