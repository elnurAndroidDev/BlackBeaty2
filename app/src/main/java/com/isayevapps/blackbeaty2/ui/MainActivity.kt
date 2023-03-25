package com.isayevapps.blackbeaty2.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
    private lateinit var lightBrightness: SeekBar
    private lateinit var rgbBrightness: SeekBar

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = (application as App).viewModel
        viewModel.init(this, savedInstanceState == null)
        viewModel.states.observe(this) {
            showOrHideStatus(it)
            if (it is States.Connection) {
                Log.d("MyTag", "connection...")
                viewModel.searchDevice()
            }
        }

        viewModel.rgbBrightness.observe(this) {
            if (it == 0) {
                binding.rgbOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.rgbOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            }
        }

        viewModel.ledBrightness.observe(this) {
            if (it == 0) {
                binding.lightOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.lightOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            }
        }

        prepareLightDialog()
        prepareRGBDialog()

        binding.seatButton.setOnClickListener {
            startActivity(Intent(this, SeatActivity::class.java))
        }

        binding.lightTextView.setOnClickListener {
            lightBrightness.progress = viewModel.ledBrightness.value ?: 0
            lightAlertDialog.show()
        }

        binding.rgbTextView.setOnClickListener {
            rgbBrightness.progress = viewModel.rgbBrightness.value ?: 0
            rgbAlertDialog.show()
        }

        binding.rgbOffButton.setOnClickListener {
            viewModel.onOffRGB()
        }

        binding.lightOffButton.setOnClickListener {
            viewModel.onOffLight()
        }

        binding.downDrawerCloseButton.setOnClickListener {
            viewModel.openCloseBar(Command.CLOSE_BAR)
        }

        binding.downDrawerOpenButton.setOnClickListener {
            viewModel.openCloseBar(Command.OPEN_BAR)
        }

        binding.leftCurtain1CloseButton.setOnTouchListener { view, event ->
            curtainTouchListener(4, view, event, Command.CLOSE_CURTAIN)
            true
        }

        binding.leftCurtain1OpenButton.setOnTouchListener { view, event ->
            curtainTouchListener(4, view, event, Command.OPEN_CURTAIN)
            true
        }

        binding.leftCurtain2CloseButton.setOnTouchListener { view, event ->
            curtainTouchListener(5, view, event, Command.CLOSE_CURTAIN)
            true
        }

        binding.leftCurtain2OpenButton.setOnTouchListener { view, event ->
            curtainTouchListener(5, view, event, Command.OPEN_CURTAIN)
            true
        }

        binding.rightCurtain1CloseButton.setOnTouchListener { view, event ->
            curtainTouchListener(6, view, event, Command.CLOSE_CURTAIN)
            true
        }

        binding.rightCurtain1OpenButton.setOnTouchListener { view, event ->
            curtainTouchListener(6, view, event, Command.OPEN_CURTAIN)
            true
        }

        binding.rightCurtain2CloseButton.setOnTouchListener { view, event ->
            curtainTouchListener(7, view, event, Command.CLOSE_CURTAIN)
            true
        }

        binding.rightCurtain2OpenButton.setOnTouchListener { view, event ->
            curtainTouchListener(7, view, event, Command.OPEN_CURTAIN)
            true
        }
    }

    private fun curtainTouchListener(id: Int, view: View, event: MotionEvent, command: Int) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            viewModel.sendCurtainCommand(id, command)
            viewModel.buttonActionDown()
            view.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_pressed, null)
        }
        if (event.action == MotionEvent.ACTION_UP) {
            viewModel.sendCurtainCommand(id, Command.STOP)
            viewModel.buttonActionUp()
            view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
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
        lightBrightness = dialogLayout.findViewById(R.id.lightSeekBar)
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
        rgbBrightness = dialogLayout.findViewById(R.id.rgbSeekBar)

        val colorPicker = dialogLayout.findViewById<ColorPickerView>(R.id.colorPickerView)
        colorPicker.setInitialColor(Color.WHITE)
        colorPicker.subscribe { color, _, _ ->
            val hexColor = String.format("#%06X", 0xFFFFFF and color)
            val intColor = hexColorToInt(hexColor)
            viewModel.sendRGBColor(intColor)
        }

        rgbBrightness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.sendRGBBrightness(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        val redCircle = dialogLayout.findViewById<ImageView>(R.id.redCircle)
        val violetCircle = dialogLayout.findViewById<ImageView>(R.id.violetCircle)
        val yellowCircle = dialogLayout.findViewById<ImageView>(R.id.yellowCircle)
        val blueCircle = dialogLayout.findViewById<ImageView>(R.id.blueCircle)
        val greenCircle = dialogLayout.findViewById<ImageView>(R.id.greenCircle)

        val aquaCircle = dialogLayout.findViewById<ImageView>(R.id.aquaCircle)
        val goldCircle = dialogLayout.findViewById<ImageView>(R.id.goldCircle)
        val pinkCircle = dialogLayout.findViewById<ImageView>(R.id.pinkCircle)
        val darkGreenCircle = dialogLayout.findViewById<ImageView>(R.id.darkGreenCircle)
        val orangeCircle = dialogLayout.findViewById<ImageView>(R.id.orangeColor)

        redCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#FF0000"))
            colorPicker.setInitialColor(Color.parseColor("#FF0000"))
        }
        violetCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#673AB7"))
            colorPicker.setInitialColor(Color.parseColor("#673AB7"))
        }
        yellowCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#FFEB3B"))
            colorPicker.setInitialColor(Color.parseColor("#FFEB3B"))
        }
        blueCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#2196F3"))
            colorPicker.setInitialColor(Color.parseColor("#2196F3"))
        }
        greenCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#00FF0A"))
            colorPicker.setInitialColor(Color.parseColor("#00FF0A"))
        }
        aquaCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#00FFFF"))
            colorPicker.setInitialColor(Color.parseColor("#00FFFF"))
        }
        goldCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#FFD700"))
            colorPicker.setInitialColor(Color.parseColor("#FFD700"))
        }
        pinkCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#FF1493"))
            colorPicker.setInitialColor(Color.parseColor("#FF1493"))
        }
        darkGreenCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#006400"))
            colorPicker.setInitialColor(Color.parseColor("#006400"))
        }
        orangeCircle.setOnClickListener {
            viewModel.sendRGBColor(hexColorToInt("#FF8700"))
            colorPicker.setInitialColor(Color.parseColor("#FF8700"))
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