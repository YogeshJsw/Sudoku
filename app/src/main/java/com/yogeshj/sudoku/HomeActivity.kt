package com.yogeshj.sudoku

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import com.yogeshj.sudoku.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                binding.text.visibility=View.VISIBLE
            },2000
        )

        Handler(Looper.getMainLooper()).postDelayed(
            {
                intent = Intent(this, FirstScreenActivity::class.java)
                startActivity(intent)
                finish()
            }, 5000
        )
    }
}