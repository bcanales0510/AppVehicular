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
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.FusedLocationProviderClient

import android.location.Location
import android.widget.Toast

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
    private var idUsuario: Int = 0

    private var idVehiculo: Int = 0
    private var marcadorVehiculo: Marker? = null
    private var posicionActual = LatLng(-2.170998, -79.922359) // Guayaquil
    private var angulo = 0.0
    private val centroGeocerca = LatLng(-2.170998, -79.922359) // Centro de la zona segura
    private val radioGeocerca = 0.002 // Radio en grados (aprox. 200 metros)
    private val REQUEST_LOCATION_PERMISSION = 1


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
        idUsuario = intent.getIntExtra("id_usuario", 0)
        idVehiculo = intent.getIntExtra("id_vehiculo", 1)

        
        // Inicializar base de datos
        dbHelper = BDHelper(this)
        db = dbHelper.readableDatabase


        
        // Configurar mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        configurarVistas()

        // Configurar menú lateral
        configurarToolbar()
        configurarMenuLateral()

        // Cargar información del vehículo
        cargarInformacionVehiculo()
        
        // Iniciar simulación de movimiento
        iniciarSimulacionMovimiento()

        mostrarUsuarioEnMenuLateral()
    }

    private fun solicitarPermisosUbicacion() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            obtenerUbicacionInicial()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionInicial()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            iniciarSimulacionMovimiento() // usa Guayaquil como fallback
        }
    }

    private fun obtenerUbicacionInicial() {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: android.location.Location? ->
                    if (location != null) {
                        posicionActual = LatLng(location.latitude, location.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionActual, 15f))
                        marcadorVehiculo?.position = posicionActual
                    }
                    iniciarSimulacionMovimiento()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "No se pudo obtener ubicación", Toast.LENGTH_SHORT).show()
                    iniciarSimulacionMovimiento()
                }
        } else {
            // Si no tenemos permiso, usamos simulación
            iniciarSimulacionMovimiento()
        }
    }




    private fun estaFueraDeGeocerca(pos: LatLng): Boolean {
        val resultado = FloatArray(1)
        Location.distanceBetween(
            centroGeocerca.latitude, centroGeocerca.longitude,
            pos.latitude, pos.longitude,
            resultado
        )
        return resultado[0] > (radioGeocerca * 111139) // Comparación en metros
    }

    private fun configurarVistas() {
        txtVehiculoInfo = findViewById(R.id.txtVehiculoInfo)
        txtEstadoVehiculo = findViewById(R.id.txtEstadoVehiculo)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)


    }
    private fun configurarMenuLateral() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            val intent = when (menuItem.itemId) {
                R.id.nav_inicio -> Intent(this, MainActivity::class.java)
                R.id.nav_alertas -> Intent(this, AlertasActivity::class.java)
                R.id.nav_mantenimiento -> Intent(this, MantenimientosActivity::class.java)
                R.id.nav_historial -> Intent(this, HistorialActivity::class.java)
                else -> null
            }

            intent?.putExtra("id_vehiculo", idVehiculo)
            intent?.let { startActivity(it) }

            drawerLayout.closeDrawers()
            true
        }

    }

    private fun setupBotonesNavegacion() {
        btnAlertas.setOnClickListener {
            startActivity(Intent(this, AlertasActivity::class.java))
        }

        btnMantenimientos.setOnClickListener {
            startActivity(Intent(this, MantenimientosActivity::class.java))
        }

        btnHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }
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

        agregarMarcadorVehiculo() // se moverá luego
        solicitarPermisosUbicacion()
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

        if (estaFueraDeGeocerca(posicionActual)) {
            guardarAlertaGeocerca(posicionActual.latitude, posicionActual.longitude)
        }


        // Actualizar estado
        val velocidad = (Math.random() * 60 + 20).toInt() // 20-80 km/h
        txtEstadoVehiculo.text = "Estado: En movimiento - $velocidad km/h"
        
        // Guardar ubicación en base de datos
        guardarUbicacion(posicionActual, velocidad.toDouble())
    }

    private fun guardarAlertaGeocerca(lat: Double, lon: Double) {
        val values = android.content.ContentValues().apply {
            put("id_vehiculo", idVehiculo)
            put("tipo", "Geocerca")
            put("descripcion", "Vehículo fuera de la zona permitida")
            put("fecha", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()))
            put("latitud", lat)
            put("longitud", lon)
        }

        db.insert("alertas", null, values)
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
    private fun configurarToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
    private fun mostrarUsuarioEnMenuLateral() {
        val headerView = navView.getHeaderView(0)
        val txtUsuario = headerView.findViewById<TextView>(R.id.txtUsuario)

        val cursor = db.rawQuery(
            "SELECT usuario FROM usuarios WHERE id = ?",
            arrayOf(idUsuario.toString())
        )

        if (cursor.moveToFirst()) {
            val correo = cursor.getString(0)
            txtUsuario.text = correo
        }

        cursor.close()
    }



}