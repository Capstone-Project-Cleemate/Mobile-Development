package com.dicoding.cleemate.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieAnimationView
import com.dicoding.cleemate.R
import com.dicoding.cleemate.databinding.FragmentHomeBinding
import com.dicoding.cleemate.model.HealthRequest
import com.dicoding.cleemate.model.WeatherResponse
import com.dicoding.cleemate.network.akun.ApiConfig
import com.dicoding.cleemate.ui.kesehatan.KesehatanActivity
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private val healthActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val healthConditions = result.data?.getStringExtra("HEALTH_CONDITIONS")
            healthConditions?.let {
                binding.tvHealth.text = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set tanggal dan waktu
        val currentDateTime = SimpleDateFormat("EEEE d MMMM HH:mm", Locale("id", "ID")).format(Date())
        val dateParts = currentDateTime.split(" ")
        binding.tvDate.text = "${dateParts[0]}, ${dateParts[1]} ${dateParts[2]}"
        binding.tvTime.text = dateParts[3]

        // Observer loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observer data cuaca
        viewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
            updateWeatherUI(weatherResponse)
        }

        // Dapatkan lokasi pengguna
        requestLocationAndFetchWeather()

        // Ambil kondisi kesehatan dari SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("HealthPrefs", Context.MODE_PRIVATE)
        val healthConditions = sharedPref.getString("health_conditions", "Tidak ada masalah kesehatan saat ini.")
        binding.tvHealth.text = healthConditions

        // Tambahkan listener untuk edit kondisi kesehatan
        binding.tvHealthHeader.setOnClickListener {
            healthActivityResultLauncher.launch(Intent(requireContext(), KesehatanActivity::class.java))
        }

        binding.tvRekomenHeader.setOnClickListener {
            fetchRekomendasi()
        }
    }

    private fun requestLocationAndFetchWeather() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.fetchWeatherData(it.latitude, it.longitude)
                }
            }
        } else {
            // Request permission
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun updateWeatherUI(weatherResponse: WeatherResponse) {
        val currentWeather = weatherResponse.cuaca.cuaca[0][0]

        // Update lokasi
        binding.tvLokasi.text = "${weatherResponse.cuaca.lokasi.desa}, ${weatherResponse.cuaca.lokasi.kecamatan}"

        // Update suhu dan cuaca
        binding.tvSuhu.text = "${currentWeather.t}°C"
        binding.tvDeskripsiCuaca.text = currentWeather.weather_desc

        binding.tvKelembaban.text = "Kelembapan: ${currentWeather.hu}%"

        // Update animasi cuaca
        updateWeatherAnimation(binding.iconTemp, currentWeather.weather_desc)

    }

    private fun updateWeatherAnimation(lottieView: LottieAnimationView, weatherDesc: String) {
        val animationRes = when {
            weatherDesc.contains("Hujan Ringan", ignoreCase = true) -> R.raw.light_rain
            weatherDesc.contains("Hujan Lokal", ignoreCase = true) -> R.raw.light_rain
            weatherDesc.contains("Hujan Sedang", ignoreCase = true) -> R.raw.moderate_rain
            weatherDesc.contains("Hujan Lebat", ignoreCase = true) -> R.raw.moderate_rain
            weatherDesc.contains("Hujan Petir", ignoreCase = true) -> R.raw.heavy_intentsity
            weatherDesc.contains("Cerah", ignoreCase = true) -> R.raw.clear_sky
            weatherDesc.contains("Berawan", ignoreCase = true) -> R.raw.scattered_clouds
            weatherDesc.contains("Berawan Tebal", ignoreCase = true) -> R.raw.overcast_clouds
            weatherDesc.contains("Cerah Berawan", ignoreCase = true) -> R.raw.few_clouds
            weatherDesc.contains("Kabut", ignoreCase = true) -> R.raw.broken_clouds
            weatherDesc.contains("Udara Kabur", ignoreCase = true) -> R.raw.broken_clouds
            weatherDesc.contains("Asap", ignoreCase = true) -> R.raw.broken_clouds
            else -> R.raw.unknown
        }
        lottieView.setAnimation(animationRes)
        lottieView.playAnimation()
    }

    private fun fetchRekomendasi() {
        val sharedPref = requireActivity().getSharedPreferences("HealthPrefs", Context.MODE_PRIVATE)

        // Ambil kondisi kesehatan yang sebelumnya dipilih
        val healthConditions = sharedPref.getString("health_conditions", null)

        if (!healthConditions.isNullOrBlank()) {
            // Pisahkan kondisi kesehatan menjadi list
            val conditionList = healthConditions.split(", ")

            // Gunakan CoroutineScope untuk proses API
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Kirim request ke API dengan kondisi kesehatan
                    val apiService = ApiConfig.getApiService()
                    val request = HealthRequest(conditionList)
                    val response = apiService.sendHealthConditions(request)

                    // Tampilkan rekomendasi di UI Thread
                    withContext(Dispatchers.Main) {
                        // Gabungkan rekomendasi menjadi satu string
                        val rekomendasiText = response.rekomendasi.joinToString("\n\n") { item ->
                            "Penyakit: ${item.penyakit}\nRekomendasi: ${item.rekomendasi}"
                        }

                        // Update TextView rekomendasi
                        binding.tvRekomendasi.text = rekomendasiText

                        // Optional: Tampilkan toast sebagai konfirmasi
                        Toast.makeText(requireContext(), "Rekomendasi diperbarui", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Tangani error
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Gagal memuat rekomendasi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // Jika tidak ada kondisi kesehatan yang dipilih
            Toast.makeText(requireContext(), "Silakan pilih kondisi kesehatan terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationAndFetchWeather()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}