package com.yogeshj.sudoku

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.yogeshj.sudoku.databinding.ActivityFirstScreenBinding

class FirstScreenActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFirstScreenBinding

    private lateinit var sharedPref: SharedPreferences



    override fun onResume() {
        super.onResume()
        val easyTime=sharedPref.getString("easy",null)
        val mediumTime=sharedPref.getString("medium",null)
        val hardTime=sharedPref.getString("hard",null)
        if(easyTime!=null)
        {
            binding.easyTime.text=easyTime
            binding.easyTime.visibility=View.VISIBLE
            binding.easyRecordText.visibility=View.VISIBLE
        }
        else
        {
            binding.easyTime.visibility=View.GONE
            binding.easyRecordText.visibility=View.GONE
        }
        if(mediumTime!=null)
        {
            binding.mediumTime.text=mediumTime
            binding.mediumTime.visibility=View.VISIBLE
            binding.mediumRecordText.visibility=View.VISIBLE
        }
        else
        {
            binding.mediumTime.visibility=View.GONE
            binding.mediumRecordText.visibility=View.GONE

        }
        if(hardTime!=null)
        {
            binding.hardTime.text=hardTime
            binding.hardTime.visibility=View.VISIBLE
            binding.hardRecordText.visibility=View.VISIBLE

        }
        else
        {
            binding.hardTime.visibility=View.GONE
            binding.hardRecordText.visibility=View.GONE

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFirstScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        MobileAds.initialize(this) {}
        val adRequest= AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        sharedPref= getSharedPreferences("records", Context.MODE_PRIVATE)

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