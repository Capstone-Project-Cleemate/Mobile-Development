package com.dicoding.cleemate.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.cleemate.databinding.FragmentNotificationsBinding
import com.dicoding.cleemate.network.akun.ApiConfig
import com.dicoding.cleemate.network.akun.ApiService

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create ApiService instance
        val apiService = ApiConfig.getApiService()

        // Create ViewModel with ApiService
        viewModel = ViewModelProvider(this, NotificationsViewModelFactory(apiService)).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observe result message
        viewModel.resultMessage.observe(viewLifecycleOwner) { message ->
            binding.tvResultMessage.text = message
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Optionally handle loading state, e.g., show/hide a progress bar
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Example: Fetch weather data (replace with actual latitude and longitude)
        viewModel.fetchWeatherData(-1.606593, 103.523279)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Factory class to create ViewModel with ApiService
class NotificationsViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            return NotificationsViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}