package com.example.appvehicular.model

data class Alerta(
    val id: Int = 0,
    val idVehiculo: Int,
    val tipo: String,          // ejemplo: "Geocerca"
    val descripcion: String,   // ejemplo: "Vehículo fuera de zona"
    val fecha: String,
    val latitud: Double,
    val longitud: Double
)