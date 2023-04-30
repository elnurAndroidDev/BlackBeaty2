package com.isayevapps.blackbeaty2.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.tabs.TabLayout
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
    private lateinit var setNewColorDialog: AlertDialog
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private lateinit var lightBrightness: SeekBar
    private lateinit var newColorPicker: ColorPickerView
    private lateinit var spinner: Spinner
    private lateinit var tabLayout: TabLayout
    private var colorCircleToChange: ImageView? = null
    private var newColor = ""
    private var colorKey = ""
    private var colorFor = ForRGB

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = (application as App).viewModel
        /*viewModel.init(this, savedInstanceState == null)
        viewModel.states.observe(this) {
            showOrHideStatus(it)
            if (it is States.Connection) {
                viewModel.searchDevice()
            }
        }*/

        viewModel.rgbOnOff.observe(this) {
            if (it == 0) {
                binding.rgbOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.rgbOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gold))
            }
        }

        viewModel.starSkyOnOff.observe(this) {
            if (it == 0) {
                binding.starSkyOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.starSkyOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gold))
            }
        }

        viewModel.ledBrightness.observe(this) {
            if (it == 0) {
                binding.lightOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.lightOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gold))
            }
        }

        prepareLightDialog()
        prepareRGBDialog()
        prepareSetNewColorDialog()

        binding.seatButton.setOnClickListener {
            startActivity(Intent(this, SeatActivity::class.java))
        }

        binding.lightTextView.setOnClickListener {
            lightBrightness.progress = viewModel.ledBrightness.value ?: 0
            lightAlertDialog.show()
        }

        binding.starSkyTextView.setOnClickListener {
            colorFor = ForStarSky
            rgbAlertDialog.show()
        }

        binding.starSkyOffButton.setOnClickListener {
            viewModel.onOffStarSky()
        }

        binding.rgbTextView.setOnClickListener {
            colorFor = ForRGB
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

        val circle1 = dialogLayout.findViewById<ImageView>(R.id.redCircle)
        val circle2 = dialogLayout.findViewById<ImageView>(R.id.violetCircle)
        val circle3 = dialogLayout.findViewById<ImageView>(R.id.yellowCircle)
        val circle4 = dialogLayout.findViewById<ImageView>(R.id.blueCircle)
        val circle5 = dialogLayout.findViewById<ImageView>(R.id.greenCircle)
        val circle6 = dialogLayout.findViewById<ImageView>(R.id.aquaCircle)
        val circle7 = dialogLayout.findViewById<ImageView>(R.id.darkBlueCircle)
        val circle8 = dialogLayout.findViewById<ImageView>(R.id.pinkCircle)
        val circle9 = dialogLayout.findViewById<ImageView>(R.id.darkGreenCircle)
        val circle10 = dialogLayout.findViewById<ImageView>(R.id.orangeColor)

        val mainColorPicker = dialogLayout.findViewById<ColorPickerView>(R.id.colorPickerView)
        mainColorPicker.setInitialColor(Color.WHITE)
        mainColorPicker.subscribe { color, _, _ ->
            val hexColor = String.format("#%06X", 0xFFFFFF and color)
            val intColor = viewModel.hexColorToInt(hexColor)
            if (colorFor == ForRGB)
                viewModel.sendRGBColor(intColor)
            else
                viewModel.sendStarSkyColor(intColor)
        }

        val colorPickerCover = dialogLayout.findViewById<View>(R.id.colorPickerCover)

        spinner = dialogLayout.findViewById<Spinner>(R.id.effectsSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.effects,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                val effect = parent?.getItemAtPosition(pos).toString()
                if (effect == "Цвет") {
                    colorPickerCover.visibility = View.GONE
                    circle1.isEnabled = true
                    circle2.isEnabled = true
                    circle3.isEnabled = true
                    circle4.isEnabled = true
                    circle5.isEnabled = true
                    circle6.isEnabled = true
                    circle7.isEnabled = true
                    circle8.isEnabled = true
                    circle9.isEnabled = true
                    circle10.isEnabled = true
                } else {
                    colorPickerCover.visibility = View.VISIBLE
                    circle1.isEnabled = false
                    circle2.isEnabled = false
                    circle3.isEnabled = false
                    circle4.isEnabled = false
                    circle5.isEnabled = false
                    circle6.isEnabled = false
                    circle7.isEnabled = false
                    circle8.isEnabled = false
                    circle9.isEnabled = false
                    circle10.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        setColorToCircle(circle1, "color1", "#FF0000")
        setColorToCircle(circle2, "color2", "#673AB7")
        setColorToCircle(circle3, "color3", "#FFEB3B")
        setColorToCircle(circle4, "color4", "#2196F3")
        setColorToCircle(circle5, "color5", "#00FF0A")
        setColorToCircle(circle6, "color6", "#00FFFF")
        setColorToCircle(circle7, "color7", "#003FDD")
        setColorToCircle(circle8, "color8", "#FF1493")
        setColorToCircle(circle9, "color9", "#006400")
        setColorToCircle(circle10, "color10", "#FF8700")


        circle1.setOnLongClickListener {
            showNewColorDialog(circle1, "color1")
            true
        }

        circle2.setOnLongClickListener {
            showNewColorDialog(circle2, "color2")
            true
        }

        circle3.setOnLongClickListener {
            showNewColorDialog(circle3, "color3")
            true
        }

        circle4.setOnLongClickListener {
            showNewColorDialog(circle4, "color4")
            true
        }

        circle5.setOnLongClickListener {
            showNewColorDialog(circle5, "color5")
            true
        }

        circle6.setOnLongClickListener {
            showNewColorDialog(circle6, "color6")
            true
        }

        circle7.setOnLongClickListener {
            showNewColorDialog(circle7, "color7")
            true
        }

        circle8.setOnLongClickListener {
            showNewColorDialog(circle8, "color8")
            true
        }

        circle9.setOnLongClickListener {
            showNewColorDialog(circle9, "color9")
            true
        }

        circle10.setOnLongClickListener {
            showNewColorDialog(circle10, "color10")
            true
        }


        circle1.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
        circle2.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
        circle3.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
        circle4.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
        circle5.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
        circle6.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
        circle7.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
        circle8.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
        circle9.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
        circle10.setOnClickListener {
            val hexColor = it.getTag(COLOR_KEY).toString()
            mainColorPicker.setInitialColor(Color.parseColor(hexColor))
        }
    }

    private fun setColorToCircle(circle: ImageView, colorKey: String, defaultColor: String) {
        val color = getColorFromMemory(colorKey, defaultColor)
        circle.background.setTint(Color.parseColor(color))
        circle.setTag(COLOR_KEY, color)
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

    private fun prepareSetNewColorDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        val inflater = layoutInflater
        builder.setTitle("Установить новый цвет")
        val dialogLayout = inflater.inflate(R.layout.set_new_color_dialog, null)
        builder.setView(dialogLayout)
        builder.setNegativeButton("Отмена") { dialogInterface, _ -> dialogInterface.dismiss() }
        builder.setPositiveButton("OK") { dialogInterface, _ ->
            colorCircleToChange?.background?.setTint(Color.parseColor(newColor))
            colorCircleToChange?.setTag(COLOR_KEY, newColor)
            saveColorToMemory(colorKey, newColor)
            dialogInterface.dismiss()
        }

        newColorPicker = dialogLayout.findViewById(R.id.colorPickerView2)
        newColorPicker.subscribe { color, _, _ ->
            val hexColor = String.format("#%06X", 0xFFFFFF and color)
            newColor = hexColor
        }

        setNewColorDialog = builder.create()
    }

    private fun showNewColorDialog(circle: ImageView, _colorKey: String) {
        colorCircleToChange = circle
        colorKey = _colorKey
        val hexColor = circle.getTag(COLOR_KEY).toString()
        newColorPicker.setInitialColor(Color.parseColor(hexColor))
        setNewColorDialog.show()
    }

    private fun getColorFromMemory(colorKey: String, defaultColor: String): String {
        val sharedPref =
            this.getSharedPreferences("RGB_COLORS", Context.MODE_PRIVATE)
        val c = sharedPref.getString(colorKey, defaultColor)
        return c ?: defaultColor
    }

    private fun saveColorToMemory(colorKey: String, hexColor: String) {
        val sharedPref =
            this.getSharedPreferences(
                "RGB_COLORS",
                Context.MODE_PRIVATE
            )
        with(sharedPref.edit()) {
            putString(colorKey, hexColor)
            apply()
        }
    }

    companion object {
        const val COLOR_KEY = R.color.white
        const val ForRGB = 0
        const val ForStarSky = 1
    }
}