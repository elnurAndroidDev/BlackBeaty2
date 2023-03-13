package com.isayevapps.blackbeaty2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var lightAlertDialog: AlertDialog
    private lateinit var rgbAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareLightDialog()
        prepareRGBDialog()

        /* val viewModel = (application as App).viewModel
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
         }*/

        val connectionStatusLayout = findViewById<FrameLayout>(R.id.connectionStatusLayout)
        connectionStatusLayout.visibility = View.GONE


        val lightTextView = findViewById<TextView>(R.id.lightTextView)
        val rgbTextView = findViewById<TextView>(R.id.rgbTextView)
        val seatButton = findViewById<AppCompatButton>(R.id.seatButton)

        seatButton.setOnClickListener {
            startActivity(Intent(this, SeatActivity::class.java))
        }

        lightTextView.setOnClickListener {
            lightAlertDialog.show()
        }

        rgbTextView.setOnClickListener {
            rgbAlertDialog.show()
        }

    }

    private fun prepareLightDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        val inflater = layoutInflater
        builder.setTitle("Яркость света")
        val dialogLayout = inflater.inflate(R.layout.light_brightness_dialog, null)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
        lightAlertDialog = builder.create()
    }

    private fun prepareRGBDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        val inflater = layoutInflater
        builder.setTitle("Управление RGB")
        val dialogLayout = inflater.inflate(R.layout.rgb_control_dialog, null)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
        rgbAlertDialog = builder.create()
    }
}