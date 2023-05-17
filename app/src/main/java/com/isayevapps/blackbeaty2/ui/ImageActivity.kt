package com.isayevapps.blackbeaty2.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toUri
import com.isayevapps.blackbeaty2.R
import com.isayevapps.blackbeaty2.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageBinding
    private lateinit var fon: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val arguments = intent.extras
        fon = arguments?.getString("fon", "fon1") ?: "fon1"

        when (fon) {
            "fon1" -> binding.imageView.setImageResource(R.drawable.fon1)
            "fon2" -> binding.imageView.setImageResource(R.drawable.fon2)
            "fon3" -> binding.imageView.setImageResource(R.drawable.fon3)
            "fon4" -> binding.imageView.setImageResource(R.drawable.fon4)
            "fon5" -> binding.imageView.setImageResource(R.drawable.fon5)
            "fon6" -> binding.imageView.setImageResource(R.drawable.fon6)
            else -> binding.imageView.setImageURI(fon.toUri())
        }

        binding.setFonBtn.setOnClickListener {
            val sharedPref = this.getSharedPreferences("APP_SHARED_PREF", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("fon", fon)
                apply()
            }
            Toast.makeText(this, "Фон установлен", Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}