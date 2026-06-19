package com.example.goofiz
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val btnBack: ImageButton = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            // Retour à la page 1
            finish()
        }
    }
}