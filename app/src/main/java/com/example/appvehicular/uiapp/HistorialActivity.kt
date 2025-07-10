package com.example.appvehicular.uiapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appvehicular.R

class HistorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)
        title = "Historial"
    }
}