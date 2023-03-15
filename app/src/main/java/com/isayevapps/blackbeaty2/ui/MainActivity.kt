package com.isayevapps.blackbeaty2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.isayevapps.blackbeaty2.App
import com.isayevapps.blackbeaty2.R
import com.isayevapps.blackbeaty2.databinding.ActivityMainBinding
import com.isayevapps.blackbeaty2.viewmodels.States

class MainActivity : AppCompatActivity() {

    private lateinit var lightAlertDialog: AlertDialog
    private lateinit var rgbAlertDialog: AlertDialog
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareLightDialog()
        prepareRGBDialog()

        val viewModel = (application as App).viewModel
        viewModel.init(this, savedInstanceState == null)
        viewModel.states.observe(this) {
            showOrHideStatus(it)
            if (it is States.Connection) {
                Log.d("MyTag", "connection...")
                viewModel.searchDevice()
            }
        }

        binding.seatButton.setOnClickListener {
            startActivity(Intent(this, SeatActivity::class.java))
        }

        binding.lightTextView.setOnClickListener {
            lightAlertDialog.show()
        }

        binding.rgbTextView.setOnClickListener {
            rgbAlertDialog.show()
        }

        binding.lightOffButton

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

    private fun showOrHideStatus(state: States) {
        if (state is States.WaitForConnection) {
            if (binding.connectionStatus.visibility != View.VISIBLE)
                binding.connectionStatus.visibility = View.VISIBLE
            if (binding.connectionStatus.text.toString() != "Ожидание подключения...")
                binding.connectionStatus.text = "Ожидание подключения..."
        }
        if (state is States.Connection) {
            if (binding.connectionStatus.visibility != View.VISIBLE)
                binding.connectionStatus.visibility = View.VISIBLE
            if (binding.connectionStatus.text.toString() != "Поиск устройства...")
                binding.connectionStatus.text = "Поиск устройства..."
        }
        if (state is States.Connected) {
            if (binding.connectionStatus.visibility != View.GONE)
                binding.connectionStatus.visibility = View.GONE
        }
    }
}