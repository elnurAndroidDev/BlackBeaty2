package com.isayevapps.blackbeaty2.ui
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.preference.PreferenceManager
import com.isayevapps.blackbeaty2.App
import com.isayevapps.blackbeaty2.R
import com.isayevapps.blackbeaty2.databinding.ActivitySeatBinding
import com.isayevapps.blackbeaty2.models.Command
import com.isayevapps.blackbeaty2.viewmodels.States
import com.isayevapps.blackbeaty2.viewmodels.ViewModel

class SeatActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatBinding
    private lateinit var viewModel: ViewModel

    private lateinit var mediaPlayer: MediaPlayer
    private var soundOn = true
    private var vibroOn = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = (application as App).viewModel
        viewModel.states.observe(this) {
            showOrHideStatus(it)
            if (it is States.Connection) {
                viewModel.searchDevice()
            }
        }
        selectPos(viewModel.getSeatPos())
        selectPart(viewModel.getSeatPart())

        mediaPlayer = MediaPlayer.create(this, R.raw.click)

        binding.seatPos1Button.setOnClickListener {
            playClickSound()
            vibrate()
            selectPos(0)
        }
        binding.seatPos2Button.setOnClickListener {
            playClickSound()
            vibrate()
            selectPos(1)
        }
        binding.seatPos3Button.setOnClickListener {
            playClickSound()
            vibrate()
            selectPos(2)
        }
        binding.seatPos4Button.setOnClickListener {
            playClickSound()
            vibrate()
            selectPos(3)
        }

        binding.headButton.setOnClickListener {
            playClickSound()
            vibrate()
            selectPart(0)

        }
        binding.backButton.setOnClickListener {
            playClickSound()
            vibrate()
            selectPart(1)
        }
        binding.legButton.setOnClickListener {
            playClickSound()
            vibrate()
            selectPart(2)
        }
        binding.moveButton.setOnClickListener {
            playClickSound()
            vibrate()
            selectPart(3)
        }

        binding.seatPartUpButton.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                playClickSound()
                vibrate()
                viewModel.sendSeatCommand(Command.UP)
                viewModel.buttonActionDown()
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_pressed, null)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.sendSeatCommand(Command.STOP)
                viewModel.buttonActionUp()
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
            }
            true
        }

        binding.seatPartDownButton.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                playClickSound()
                vibrate()
                viewModel.sendSeatCommand(Command.DOWN)
                viewModel.buttonActionDown()
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_pressed, null)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.sendSeatCommand(Command.STOP)
                viewModel.buttonActionUp()
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
            }
            true
        }

        binding.seatPartLeftButton.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                playClickSound()
                vibrate()
                viewModel.sendSeatCommand(Command.LEFT)
                viewModel.buttonActionDown()
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_pressed, null)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.sendSeatCommand(Command.STOP)
                viewModel.buttonActionUp()
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
            }
            true
        }

        binding.seatPartRightButton.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                playClickSound()
                vibrate()
                viewModel.sendSeatCommand(Command.RIGHT)
                viewModel.buttonActionDown()
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_pressed, null)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.sendSeatCommand(Command.STOP)
                viewModel.buttonActionUp()
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
            }
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

    private fun selectPos(pos: Int) {
        viewModel.setSeatPos(pos)
        val buttonBGDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
        val buttonSelectedBGDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.button_selected_bg, null)
        binding.seatPos1Button.background =
            if (pos == 0) buttonSelectedBGDrawable else buttonBGDrawable
        binding.seatPos2Button.background =
            if (pos == 1) buttonSelectedBGDrawable else buttonBGDrawable
        binding.seatPos3Button.background =
            if (pos == 2) buttonSelectedBGDrawable else buttonBGDrawable
        binding.seatPos4Button.background =
            if (pos == 3) buttonSelectedBGDrawable else buttonBGDrawable
    }

    private fun selectPart(i: Int) {
        viewModel.setSeatPart(i)
        val buttonBGDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
        val buttonSelectedBGDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.button_selected_bg, null)
        binding.headButton.background = if (i == 0) buttonSelectedBGDrawable else buttonBGDrawable
        binding.backButton.background = if (i == 1) buttonSelectedBGDrawable else buttonBGDrawable
        binding.legButton.background = if (i == 2) buttonSelectedBGDrawable else buttonBGDrawable
        binding.moveButton.background = if (i == 3) buttonSelectedBGDrawable else buttonBGDrawable

        if (i == 0) {
            binding.seatPartUpButton.visibility = View.VISIBLE
            binding.seatPartDownButton.visibility = View.VISIBLE
            binding.seatPartLeftButton.visibility = View.GONE
            binding.seatPartRightButton.visibility = View.GONE
        }

        if (i == 1) {
            binding.seatPartUpButton.visibility = View.GONE
            binding.seatPartDownButton.visibility = View.GONE
            binding.seatPartLeftButton.visibility = View.VISIBLE
            binding.seatPartRightButton.visibility = View.VISIBLE
        }
        if (i == 2) {
            binding.seatPartUpButton.visibility = View.VISIBLE
            binding.seatPartDownButton.visibility = View.VISIBLE
            binding.seatPartLeftButton.visibility = View.GONE
            binding.seatPartRightButton.visibility = View.GONE
        }

        if (i == 3) {
            binding.seatPartUpButton.visibility = View.GONE
            binding.seatPartDownButton.visibility = View.GONE
            binding.seatPartLeftButton.visibility = View.VISIBLE
            binding.seatPartRightButton.visibility = View.VISIBLE
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