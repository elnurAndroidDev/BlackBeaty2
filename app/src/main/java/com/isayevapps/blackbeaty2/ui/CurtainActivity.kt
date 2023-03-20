package com.isayevapps.blackbeaty2.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.isayevapps.blackbeaty2.App
import com.isayevapps.blackbeaty2.databinding.ActivityCurtainBinding
import com.isayevapps.blackbeaty2.models.Command
import com.isayevapps.blackbeaty2.viewmodels.States

class CurtainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCurtainBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurtainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = (application as App).viewModel
        viewModel.states.observe(this) {
            showOrHideStatus(it)
            if (it is States.Connection) {
                Log.d("MyTag", "curtain connection...")
                viewModel.searchDevice()
            }
        }

        binding.leftCurtain1CloseButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendCurtainCommand(4, Command.CLOSE)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.leftCurtain1OpenButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendCurtainCommand(4, Command.OPEN)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.leftCurtain2CloseButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendCurtainCommand(5, Command.CLOSE)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.leftCurtain2OpenButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendCurtainCommand(5, Command.OPEN)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.rightCurtain1CloseButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendCurtainCommand(6, Command.CLOSE)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.rightCurtain1OpenButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendCurtainCommand(6, Command.OPEN)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.rightCurtain2CloseButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendCurtainCommand(7, Command.CLOSE)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
        }

        binding.rightCurtain2OpenButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.sendCurtainCommand(7, Command.OPEN)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.stopSendingLongCommand()
            }
            true
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