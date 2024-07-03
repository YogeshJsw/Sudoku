package com.yogeshj.sudoku

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yogeshj.sudoku.databinding.ActivityFirstScreenBinding

class FirstScreenActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFirstScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFirstScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.easy.setOnClickListener {
            val intent=Intent(this@FirstScreenActivity,MainActivity::class.java)
            intent.putExtra("level","easy")
            startActivity(intent)
        }

        binding.medium.setOnClickListener {
            val intent=Intent(this@FirstScreenActivity,MainActivity::class.java)
            intent.putExtra("level","medium")
            startActivity(intent)
        }

        binding.hard.setOnClickListener {
            val intent=Intent(this@FirstScreenActivity,MainActivity::class.java)
            intent.putExtra("level","hard")
            startActivity(intent)
        }

    }
}