package com.isayevapps.blackbeaty2.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.isayevapps.blackbeaty2.App
import com.isayevapps.blackbeaty2.R
import com.isayevapps.blackbeaty2.databinding.ActivityLightBinding
import com.isayevapps.blackbeaty2.models.Command
import com.isayevapps.blackbeaty2.viewmodels.States
import com.isayevapps.blackbeaty2.viewmodels.ViewModel
import top.defaults.colorpicker.ColorPickerView

class LightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLightBinding
    private lateinit var viewModel: ViewModel

    private lateinit var lightAlertDialog: AlertDialog
    private lateinit var rgbAlertDialog: AlertDialog
    private lateinit var setNewColorDialog: AlertDialog

    private lateinit var lightBrightness: SeekBar
    private lateinit var rgbBrightness: SeekBar
    private lateinit var brightnessIco: ImageView
    private lateinit var effectIco: ImageView
    private lateinit var newColorPicker: ColorPickerView
    private lateinit var mainColorPicker: ColorPickerView
    private lateinit var effectSpinner: Spinner
    private var colorCircleToChange: ImageView? = null
    private var newColor = ""
    private var colorKey = ""
    private var led_id = 0

    private lateinit var circle1: ImageView
    private lateinit var circle2: ImageView
    private lateinit var circle3: ImageView
    private lateinit var circle4: ImageView
    private lateinit var circle5: ImageView
    private lateinit var circle6: ImageView
    private lateinit var circle7: ImageView
    private lateinit var circle8: ImageView
    private lateinit var circle9: ImageView
    private lateinit var circle10: ImageView
    private lateinit var colorPickerCover: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = (application as App).viewModel
        /*viewModel.init(this, savedInstanceState == null)
        viewModel.states.observe(this) {
            showOrHideStatus(it)
            if (it is States.Connection) {
                viewModel.searchDevice()
            }
        }*/

        viewModel.led1OnOff.observe(this) {
            if (it == 0) {
                binding.led1OffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.led1OffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gold))
            }
        }

        viewModel.led2OnOff.observe(this) {
            if (it == 0) {
                binding.led2OffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.led2OffButton.imageTintList =
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

        viewModel.lightBrightness.observe(this) {
            if (it == 0) {
                binding.lightOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.lightOffButton.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gold))
            }
        }

        binding.led1OffButton.setOnClickListener {
            viewModel.onOffLed1()
        }

        binding.led2OffButton.setOnClickListener {
            viewModel.onOffLed2()
        }

        binding.lightOffButton.setOnClickListener {
            viewModel.onOffLight()
        }

        binding.starSkyOffButton.setOnClickListener {
            viewModel.onOffStarSky()
        }

        binding.led1TextView.setOnClickListener {
            led_id = Command.RGB_UP_ID
            effectSpinner.visibility = View.VISIBLE
            rgbBrightness.visibility = View.VISIBLE
            brightnessIco.visibility = View.VISIBLE
            effectIco.visibility = View.VISIBLE
            mainColorPicker.setEnabledBrightness(false)

            val effect = getEffectFromMemory(led_id)
            effectSpinner.setSelection(effect)
            enableColors(effect == 0)
            rgbAlertDialog.setTitle("Верхняя подсветка")
            rgbAlertDialog.show()
        }

        binding.led2TextView.setOnClickListener {
            led_id = Command.RGB_DOWN_ID
            effectSpinner.visibility = View.VISIBLE
            rgbBrightness.visibility = View.VISIBLE
            brightnessIco.visibility = View.VISIBLE
            effectIco.visibility = View.VISIBLE
            mainColorPicker.setEnabledBrightness(false)
            val effect = getEffectFromMemory(led_id)
            effectSpinner.setSelection(effect)
            enableColors(effect == 0)
            rgbAlertDialog.setTitle("Нижняя подсветка")
            rgbAlertDialog.show()
        }

        binding.lightTextView.setOnClickListener {
            lightBrightness.progress = viewModel.lightBrightness.value ?: 0
            lightAlertDialog.show()
        }

        binding.starSkyTextView.setOnClickListener {
            effectSpinner.visibility = View.GONE
            rgbBrightness.visibility = View.GONE
            brightnessIco.visibility = View.GONE
            effectIco.visibility = View.GONE
            mainColorPicker.setEnabledBrightness(true)
            enableColors(true)
            rgbAlertDialog.setTitle("Звёздное небо")
            rgbAlertDialog.show()
        }

        prepareLightDialog()
        prepareRGBDialog()
        prepareSetNewColorDialog()
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
        lightBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.sendLightBrightness(p0?.progress ?: 50)
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

        circle1 = dialogLayout.findViewById(R.id.redCircle)
        circle2 = dialogLayout.findViewById(R.id.violetCircle)
        circle3 = dialogLayout.findViewById(R.id.yellowCircle)
        circle4 = dialogLayout.findViewById(R.id.blueCircle)
        circle5 = dialogLayout.findViewById(R.id.greenCircle)
        circle6 = dialogLayout.findViewById(R.id.aquaCircle)
        circle7 = dialogLayout.findViewById(R.id.darkBlueCircle)
        circle8 = dialogLayout.findViewById(R.id.pinkCircle)
        circle9 = dialogLayout.findViewById(R.id.darkGreenCircle)
        circle10 = dialogLayout.findViewById(R.id.orangeColor)

        colorPickerCover = dialogLayout.findViewById(R.id.colorPickerCover)
        rgbBrightness = dialogLayout.findViewById(R.id.rgbSeekBar)
        brightnessIco = dialogLayout.findViewById(R.id.bright_ico)
        effectIco = dialogLayout.findViewById(R.id.effect_ico)

        mainColorPicker = dialogLayout.findViewById<ColorPickerView>(R.id.colorPickerView)
        mainColorPicker.setInitialColor(Color.WHITE)
        mainColorPicker.subscribe { color, _, _ ->
            val hexColor = String.format("#%06X", 0xFFFFFF and color)
            val intColor = viewModel.hexColorToInt(hexColor)
            viewModel.sendColor(intColor)
        }

        effectSpinner = dialogLayout.findViewById(R.id.effectsSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.effects,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.dropdown_spinner_item)
            effectSpinner.adapter = adapter
        }

        effectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                val effect = parent?.getItemAtPosition(pos).toString()
                if (effect == "Цвет") {
                    enableColors(true)
                    saveEffectToMemory(led_id, 0)
                } else {
                    enableColors(false)
                }
                if (effect == "Радуга") {
                    viewModel.sendEffect(led_id, 1)
                    saveEffectToMemory(led_id, 1)
                }
                if (effect == "Конфетти") {
                    viewModel.sendEffect(led_id, 2)
                    saveEffectToMemory(led_id, 2)
                }
                if (effect == "Бегущие огни") {
                    viewModel.sendEffect(led_id, 3)
                    saveEffectToMemory(led_id, 3)
                }
                if (effect == "Циклон") {
                    viewModel.sendEffect(led_id, 4)
                    saveEffectToMemory(led_id, 4)
                }
                if (effect == "Фокус") {
                    viewModel.sendEffect(led_id, 5)
                    saveEffectToMemory(led_id, 5)
                }
                if (effect == "Радуга с мерцанием") {
                    viewModel.sendEffect(led_id, 6)
                    saveEffectToMemory(led_id, 6)
                }
                if (effect == "Все эффекты") {
                    viewModel.sendEffect(led_id, 7)
                    saveEffectToMemory(led_id, 7)
                }
                if (effect == "Светомузыка") {
                    viewModel.sendEffect(led_id, 8)
                    saveEffectToMemory(led_id, 8)
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

        val circles = arrayOf(
            circle1,
            circle2,
            circle3,
            circle4,
            circle5,
            circle6,
            circle7,
            circle8,
            circle9,
            circle10
        )

        circles.forEachIndexed { id, circle ->
            circle.setOnLongClickListener {
                showNewColorDialog(circle, "color${id + 1}")
                true
            }
        }

        for (circle in circles) {
            circle.setOnClickListener {
                val hexColor = it.getTag(COLOR_KEY).toString()
                mainColorPicker.setInitialColor(Color.parseColor(hexColor))
            }
        }
    }

    private fun setColorToCircle(circle: ImageView, colorKey: String, defaultColor: String) {
        val color = getColorFromMemory(colorKey, defaultColor)
        circle.background.setTint(Color.parseColor(color))
        circle.setTag(COLOR_KEY, color)
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

    private fun showOrHideStatus(state: States) {
        if (state is States.WaitForConnection) {
            binding.connectionStatus.visibility = View.VISIBLE
            binding.connectionStatus.text = "Ожидание подключения..."
        }
        if (state is States.Connection) {
            binding.connectionStatus.visibility = View.VISIBLE
            binding.connectionStatus.text = "Поиск устройства..."
        }
        if (state is States.Connected) {
            binding.connectionStatus.visibility = View.GONE
        }
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

    private fun getEffectFromMemory(key: Int): Int {
        val sharedPref =
            this.getSharedPreferences("RGB_EFFECTS", Context.MODE_PRIVATE)
        val c = sharedPref.getInt("Effect $key", 0)
        return c ?: 0
    }

    private fun saveEffectToMemory(key: Int, effect: Int) {
        val sharedPref =
            this.getSharedPreferences(
                "RGB_EFFECTS",
                Context.MODE_PRIVATE
            )
        with(sharedPref.edit()) {
            putInt("Effect $key", effect)
            apply()
        }
    }

    private fun enableColors(flag: Boolean) {
        colorPickerCover.visibility = if (flag) View.GONE else View.VISIBLE
        circle1.isEnabled = flag
        circle2.isEnabled = flag
        circle3.isEnabled = flag
        circle4.isEnabled = flag
        circle5.isEnabled = flag
        circle6.isEnabled = flag
        circle7.isEnabled = flag
        circle8.isEnabled = flag
        circle9.isEnabled = flag
        circle10.isEnabled = flag
    }

    companion object {
        const val COLOR_KEY = R.color.white
    }
}