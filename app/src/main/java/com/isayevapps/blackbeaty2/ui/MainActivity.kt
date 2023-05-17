package com.isayevapps.blackbeaty2.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.preference.PreferenceManager
import com.isayevapps.blackbeaty2.App
import com.isayevapps.blackbeaty2.R
import com.isayevapps.blackbeaty2.databinding.ActivityMainBinding
import com.isayevapps.blackbeaty2.models.Command
import com.isayevapps.blackbeaty2.viewmodels.States
import com.isayevapps.blackbeaty2.viewmodels.ViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private lateinit var mediaPlayer: MediaPlayer
    private var soundOn = true
    private var vibroOn = true

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
                viewModel.searchDevice()
            }
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.click)

        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

        binding.seatButton.setOnClickListener {
            startActivity(Intent(this, SeatActivity::class.java))
        }

        binding.lightButton.setOnClickListener {
            startActivity(Intent(this, LightActivity::class.java))
        }

        binding.downDrawerCloseButton.setOnClickListener {
            playClickSound()
            vibrate()
            viewModel.sendBarCommand(Command.CLOSE_BAR)
        }

        binding.downDrawerOpenButton.setOnClickListener {
            playClickSound()
            vibrate()
            viewModel.sendBarCommand(Command.OPEN_BAR)
        }

        binding.doorButton.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)

            with(builder)
            {
                setTitle("Дверь")
                setMessage("Вы действительно хотите открыть/закрыть дверь?")
                setPositiveButton("Да") { dialogInterface, _ ->
                    viewModel.sendDoorCommand()
                    dialogInterface.dismiss()
                }
                setNegativeButton("Нет") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                show()
            }
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

    override fun onResume() {
        super.onResume()
        setFon()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        soundOn = sharedPref.getBoolean("click_sound", true)
        vibroOn = sharedPref.getBoolean("click_vibro", true)
    }

    private fun vibrate() {
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            this.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (vibroOn)
            vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun playClickSound() {
        if (soundOn)
            mediaPlayer.start()
    }

    private fun setFon() {
        val sharedPref = this.getSharedPreferences("APP_SHARED_PREF", Context.MODE_PRIVATE)
        val fon = sharedPref.getString("fon", "fon1") ?: "fon1"
        when (fon) {
            "fon1" -> binding.fonContainer.setImageResource(R.drawable.fon1)
            "fon2" -> binding.fonContainer.setImageResource(R.drawable.fon2)
            "fon3" -> binding.fonContainer.setImageResource(R.drawable.fon3)
            "fon4" -> binding.fonContainer.setImageResource(R.drawable.fon4)
            "fon5" -> binding.fonContainer.setImageResource(R.drawable.fon5)
            "fon6" -> binding.fonContainer.setImageResource(R.drawable.fon6)
            else -> binding.fonContainer.setImageURI(fon.toUri())
        }
    }

    private fun curtainTouchListener(id: Int, view: View, event: MotionEvent, command: Int) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            viewModel.sendCurtainCommand(id, command)
            viewModel.buttonActionDown()
            playClickSound()
            vibrate()
            view.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_pressed, null)
        }
        if (event.action == MotionEvent.ACTION_UP) {
            viewModel.sendCurtainCommand(id, Command.STOP)
            viewModel.buttonActionUp()
            view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
        }
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
}