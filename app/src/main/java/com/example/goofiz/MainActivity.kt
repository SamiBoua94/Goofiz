package com.example.goofiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNext: Button = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            // Aller à la page 2
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }
}