package com.isayevapps.blackbeaty2.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.isayevapps.blackbeaty2.databinding.ActivityChooseImageBinding
import java.io.ByteArrayOutputStream

class ChooseImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.fonImageView1.setOnClickListener {
            startActivityWithFon("fon1")
        }
        binding.fonImageView2.setOnClickListener {
            startActivityWithFon("fon2")
        }
        binding.fonImageView3.setOnClickListener {
            startActivityWithFon("fon3")
        }
        binding.fonImageView4.setOnClickListener {
            startActivityWithFon("fon4")
        }
        binding.fonImageView5.setOnClickListener {
            startActivityWithFon("fon5")
        }
        binding.fonImageView6.setOnClickListener {
            startActivityWithFon("fon6")
        }

        binding.chooseFromGalleryBtn.setOnClickListener {
            openGalleryForResult()
        }

        binding.chooseFromGalleryIcon.setOnClickListener {
            openGalleryForResult()
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = this.getSharedPreferences("APP_SHARED_PREF", Context.MODE_PRIVATE)
        val fon = sharedPref.getString("fon", "fon1") ?: "fon1"
        selectChosenFon(fon)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun startActivityWithFon(fon: String) {
        val intent = Intent(this, ImageActivity::class.java)
        intent.putExtra("fon", fon)
        startActivity(intent)
    }

    private fun openGalleryForResult() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data
                if (imageUri != null) {
                    val bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            imageUri.data
                        )
                    } else {
                        val source =
                            ImageDecoder.createSource(contentResolver, imageUri.data!!)
                        ImageDecoder.decodeBitmap(source)
                    }
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path =
                        MediaStore.Images.Media.insertImage(contentResolver, bitmap, "File", null)
                    val uri = Uri.parse(path.toString())
                    startActivityWithFon(uri.toString())
                }
            }
        }

    private fun selectChosenFon(fon: String) {
        binding.fonImageView1.scaleX = if (fon == "fon1") 0.95f else 1f
        binding.fonImageView1.scaleY = if (fon == "fon1") 0.95f else 1f

        binding.fonImageView2.scaleX = if (fon == "fon2") 0.95f else 1f
        binding.fonImageView2.scaleY = if (fon == "fon2") 0.95f else 1f

        binding.fonImageView3.scaleX = if (fon == "fon3") 0.95f else 1f
        binding.fonImageView3.scaleY = if (fon == "fon3") 0.95f else 1f

        binding.fonImageView4.scaleX = if (fon == "fon4") 0.95f else 1f
        binding.fonImageView4.scaleY = if (fon == "fon4") 0.95f else 1f

        binding.fonImageView5.scaleX = if (fon == "fon5") 0.95f else 1f
        binding.fonImageView5.scaleY = if (fon == "fon5") 0.95f else 1f

        binding.fonImageView6.scaleX = if (fon == "fon6") 0.95f else 1f
        binding.fonImageView6.scaleY = if (fon == "fon6") 0.95f else 1f
    }
}