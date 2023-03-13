package com.isayevapps.blackbeaty2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.isayevapps.blackbeaty2.databinding.ActivitySeatBinding

class SeatActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seatPos1Button.setOnClickListener {
            selectPos(1)
        }
        binding.seatPos2Button.setOnClickListener {
            selectPos(2)
        }
        binding.seatPos3Button.setOnClickListener {
            selectPos(3)
        }
        binding.seatPos4Button.setOnClickListener {
            selectPos(4)
        }

        binding.headButton.setOnClickListener {
            selectPart(1)
            binding.seatPartUpButton.visibility = View.VISIBLE
            binding.seatPartDownButton.visibility = View.VISIBLE
            binding.seatPartLeftButton.visibility = View.GONE
            binding.seatPartRightButton.visibility = View.GONE
        }
        binding.backButton.setOnClickListener {
            selectPart(2)
            binding.seatPartUpButton.visibility = View.GONE
            binding.seatPartDownButton.visibility = View.GONE
            binding.seatPartLeftButton.visibility = View.VISIBLE
            binding.seatPartRightButton.visibility = View.VISIBLE
        }
        binding.legButton.setOnClickListener {
            selectPart(3)
            binding.seatPartUpButton.visibility = View.VISIBLE
            binding.seatPartDownButton.visibility = View.VISIBLE
            binding.seatPartLeftButton.visibility = View.GONE
            binding.seatPartRightButton.visibility = View.GONE
        }
        binding.moveButton.setOnClickListener {
            selectPart(4)
            binding.seatPartUpButton.visibility = View.GONE
            binding.seatPartDownButton.visibility = View.GONE
            binding.seatPartLeftButton.visibility = View.VISIBLE
            binding.seatPartRightButton.visibility = View.VISIBLE
        }
    }

    private fun selectPos(i: Int) {
        val buttonBGDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
        val buttonSelectedBGDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.button_selected_bg, null)
        binding.seatPos1Button.background =
            if (i == 1) buttonSelectedBGDrawable else buttonBGDrawable
        binding.seatPos2Button.background =
            if (i == 2) buttonSelectedBGDrawable else buttonBGDrawable
        binding.seatPos3Button.background =
            if (i == 3) buttonSelectedBGDrawable else buttonBGDrawable
        binding.seatPos4Button.background =
            if (i == 4) buttonSelectedBGDrawable else buttonBGDrawable
    }

    private fun selectPart(i: Int) {
        val buttonBGDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
        val buttonSelectedBGDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.button_selected_bg, null)
        binding.headButton.background = if (i == 1) buttonSelectedBGDrawable else buttonBGDrawable
        binding.backButton.background = if (i == 2) buttonSelectedBGDrawable else buttonBGDrawable
        binding.legButton.background = if (i == 3) buttonSelectedBGDrawable else buttonBGDrawable
        binding.moveButton.background = if (i == 4) buttonSelectedBGDrawable else buttonBGDrawable
    }
}