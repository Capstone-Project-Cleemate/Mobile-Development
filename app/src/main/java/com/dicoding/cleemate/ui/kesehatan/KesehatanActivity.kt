package com.dicoding.cleemate.ui.kesehatan

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.launch
import com.dicoding.cleemate.databinding.ActivityKesehatanBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KesehatanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKesehatanBinding
    private val viewModel = KesehatanViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKesehatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            val selectedConditions = mutableListOf<String>()

            // Cek kondisi checkbox yang dipilih
            if (binding.checkBox1.isChecked) selectedConditions.add("Flu")
            if (binding.checkBox2.isChecked) selectedConditions.add("Demam Berdarah")
            if (binding.checkBox3.isChecked) selectedConditions.add("ISPA")
            if (binding.checkBox4.isChecked) selectedConditions.add("Asma")
            if (binding.checkBox5.isChecked) selectedConditions.add("Alergi")
            if (binding.checkBox6.isChecked) selectedConditions.add("Penyakit Kulit")
            if (binding.checkBox7.isChecked) selectedConditions.add("Tifus")
            if (binding.checkBox8.isChecked) selectedConditions.add("Pneumonia")
            if (binding.checkBox9.isChecked) selectedConditions.add("Diare")

            // Kirim kondisi kesehatan
            CoroutineScope(Dispatchers.IO).launch {
                val result = viewModel.sendHealthConditions(selectedConditions)

                withContext(Dispatchers.Main) {
                    result.onSuccess { response ->
                        // Simpan kondisi kesehatan ke SharedPreferences
                        val sharedPref = getSharedPreferences("HealthPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("health_conditions", selectedConditions.joinToString(", "))
                            apply()
                        }

                        // Set result dengan kondisi kesehatan
                        val resultIntent = Intent()
                        resultIntent.putExtra("HEALTH_CONDITIONS", selectedConditions.joinToString(", "))
                        setResult(RESULT_OK, resultIntent)

                        Toast.makeText(this@KesehatanActivity, "Berhasil menyimpan", Toast.LENGTH_SHORT).show()
                        finish()
                    }.onFailure { exception ->
                        Toast.makeText(this@KesehatanActivity, "Gagal mengirim kondisi: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, KesehatanActivity::class.java)
            context.startActivity(intent)
        }
    }

}