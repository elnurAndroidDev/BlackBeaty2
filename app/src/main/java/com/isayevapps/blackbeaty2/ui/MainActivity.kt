package com.isayevapps.blackbeaty2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.isayevapps.blackbeaty2.App
import com.isayevapps.blackbeaty2.R
import com.isayevapps.blackbeaty2.databinding.ActivityMainBinding
import com.isayevapps.blackbeaty2.models.Command
import com.isayevapps.blackbeaty2.viewmodels.States
import com.isayevapps.blackbeaty2.viewmodels.ViewModel
import top.defaults.colorpicker.ColorPickerView

class MainActivity : AppCompatActivity() {

    private lateinit var lightAlertDialog: AlertDialog
    private lateinit var rgbAlertDialog: AlertDialog
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareLightDialog()
        prepareRGBDialog()

        viewModel = (application as App).viewModel
        viewModel.init(this, savedInstanceState == null)
        viewModel.states.observe(this) {
            showOrHideStatus(it)
            if (it is States.Connection) {
                Log.d("MyTag", "connection...")
                viewModel.searchDevice()
            }
        }

        binding.curtainButton.setOnClickListener{
            startActivity(Intent(this, CurtainActivity::class.java))
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

        binding.rgbOffButton.setOnClickListener {
            viewModel.onOffRGB()
        }

        binding.lightOffButton.setOnClickListener {
            viewModel.onOffLight()
        }

        binding.downDrawerCloseButton.setOnClickListener {
            viewModel.openCloseBar(Command.CLOSE)
        }

        binding.downDrawerOpenButton.setOnClickListener {
            viewModel.openCloseBar(Command.OPEN)
        }

        binding.tvDownButton.setOnClickListener {
            viewModel.upDownTV(Command.DOWN)
        }

        binding.tvUpButton.setOnClickListener {
            viewModel.upDownTV(Command.UP)
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
        val lightBrightness = dialogLayout.findViewById<SeekBar>(R.id.lightSeekBar)
        lightBrightness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.sendLightBrightness(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    private fun prepareRGBDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        val inflater = layoutInflater
        builder.setTitle("Управление RGB")
        val dialogLayout = inflater.inflate(R.layout.rgb_control_dialog, null)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
        rgbAlertDialog = builder.create()
        val colorPicker = dialogLayout.findViewById<ColorPickerView>(R.id.colorPicker)
        colorPicker.subscribe { color, _, _ ->
            val hexColor = String.format("#%06X", 0xFFFFFF and color)
            viewModel.sendRGBColor(hexColorToInt(hexColor))
        }

        val redCircle = dialogLayout.findViewById<ImageView>(R.id.redCircle)
        val violetCircle = dialogLayout.findViewById<ImageView>(R.id.violetCircle)
        val yellowCircle = dialogLayout.findViewById<ImageView>(R.id.yellowCircle)
        val blueCircle = dialogLayout.findViewById<ImageView>(R.id.blueCircle)
        val greenCircle = dialogLayout.findViewById<ImageView>(R.id.greenCircle)

        redCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#FF0000"))
        }
        violetCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#673AB7"))
        }
        yellowCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#FFEB3B"))
        }
        blueCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#2196F3"))
        }
        greenCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#00FF0A"))
        }
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

    private fun hexColorToInt(hex: String): Int {
        var result = 0
        try {
            result = if (hex[0] == '#') {
                hex.substring(1).toInt(radix = 16)
            } else {
                hex.toInt(radix = 16)
            }
        } catch (_: Exception) {
        }
        return result
    }
}