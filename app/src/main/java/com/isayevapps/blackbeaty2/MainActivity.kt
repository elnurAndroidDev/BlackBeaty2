package com.isayevapps.blackbeaty2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = (application as App).viewModel
        viewModel.init(this, savedInstanceState == null)

        viewModel.states.observe(this) {
            when (it) {
                is States.WaitForConnection -> {
                }
                is States.Connection -> {
                    viewModel.searchDevice()
                }
                is States.Connected -> {

                }
            }
        }

        /* val lightTextView = findViewById<TextView>(R.id.lightTextView)

         val rgbTextView = findViewById<TextView>(R.id.rgbTextView)

         lightTextView.setOnClickListener {
             showLightDialog()
         }

         rgbTextView.setOnClickListener {
             showRGBDialog()
         }*/

    }

    private fun showLightDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        val inflater = layoutInflater
        builder.setTitle("Яркость света")
        val dialogLayout = inflater.inflate(R.layout.light_brightness_dialog, null)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
        builder.show()
    }

    private fun showRGBDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        val inflater = layoutInflater
        builder.setTitle("Управление RGB")
        val dialogLayout = inflater.inflate(R.layout.rgb_control_dialog, null)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
        builder.show()
    }
}