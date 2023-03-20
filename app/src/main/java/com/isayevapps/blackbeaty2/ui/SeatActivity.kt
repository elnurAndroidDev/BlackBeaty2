package com.isayevapps.blackbeaty2.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.isayevapps.blackbeaty2.App
import com.isayevapps.blackbeaty2.R
import com.isayevapps.blackbeaty2.databinding.ActivitySeatBinding
import com.isayevapps.blackbeaty2.models.Command
import com.isayevapps.blackbeaty2.viewmodels.States
import com.isayevapps.blackbeaty2.viewmodels.ViewModel

class SeatActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatBinding
    private lateinit var viewModel: ViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = (application as App).viewModel
        viewModel.states.observe(this) {
            showOrHideStatus(it)
            if (it is States.Connection) {
                Log.d("MyTag", "seat connection...")
                viewModel.searchDevice()
            }
        }
        selectPos(viewModel.getSeatPos())
        selectPart(viewModel.getSeatPart())

        binding.seatPos1Button.setOnClickListener {
            selectPos(0)
        }
        binding.seatPos2Button.setOnClickListener {
            selectPos(1)
        }
        binding.seatPos3Button.setOnClickListener {
            selectPos(2)
        }
        binding.seatPos4Button.setOnClickListener {
            selectPos(3)
        }

        binding.headButton.setOnClickListener {
            selectPart(0)

        }
        binding.backButton.setOnClickListener {
            selectPart(1)
        }
        binding.legButton.setOnClickListener {
            selectPart(2)
        }
        binding.moveButton.setOnClickListener {
            selectPart(3)
        }

        binding.seatPartUpButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendSeatCommand(Command.UP)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.seatPartDownButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendSeatCommand(Command.DOWN)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.seatPartLeftButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendSeatCommand(Command.LEFT)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.seatPartRightButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendSeatCommand(Command.RIGHT)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
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