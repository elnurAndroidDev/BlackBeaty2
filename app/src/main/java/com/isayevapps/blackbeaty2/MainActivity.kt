package com.isayevapps.blackbeaty2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    private lateinit var lightAlertDialog: AlertDialog
    private lateinit var rgbAlertDialog: AlertDialog
    private lateinit var connectionStatusLayout: FrameLayout
    private lateinit var connectionStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareLightDialog()
        prepareRGBDialog()

        connectionStatusLayout = findViewById(R.id.connectionStatusLayout)
        connectionStatusTextView = findViewById(R.id.connectionStatus)

        val viewModel = (application as App).viewModel
        viewModel.init(this, savedInstanceState == null)
        viewModel.states.observe(this) {
            showOrHideStatusLayout(it)
            if (it is States.Connection) {
                viewModel.searchDevice()
            }
        }

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

    private fun showOrHideStatusLayout(state: States) {
        if (state is States.WaitForConnection) {
            if (connectionStatusLayout.visibility != View.VISIBLE)
                connectionStatusLayout.visibility = View.VISIBLE
            if (connectionStatusTextView.text.toString() != "Ожидание подключения...")
                connectionStatusTextView.text = "Ожидание подключения..."
        }
        if (state is States.Connection) {
            if (connectionStatusLayout.visibility != View.VISIBLE)
                connectionStatusLayout.visibility = View.VISIBLE
            if (connectionStatusTextView.text.toString() != "Поиск устройства...")
                connectionStatusTextView.text = "Поиск устройства..."
        }
        if (state is States.Connected) {
            if (connectionStatusLayout.visibility != View.GONE)
                connectionStatusLayout.visibility = View.GONE
        }
    }
}