package com.zref.experiment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.zref.experiment.databinding.ActivityMainBinding
import java.io.File.separator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textWatcher = CurrencyTextWatcher(
            "Rp ",
            ',',
            '.',
        )
        textWatcher.applyTo(binding.edit1)
        binding.buttonConvert.setOnClickListener {
            Log.i("AOEU", "anu1 ${textWatcher.value}")
            textWatcher.isShowZero = !textWatcher.isShowZero
        }
    }
}