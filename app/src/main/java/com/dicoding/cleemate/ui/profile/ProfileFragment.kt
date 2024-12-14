package com.dicoding.cleemate.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.cleemate.R
import com.dicoding.cleemate.ui.ViewModelFactory
import com.dicoding.cleemate.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel menggunakan ViewModelFactory
        val factory = ViewModelFactory.getInstance(requireContext())
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        // Inisialisasi view
        tvName = view.findViewById(R.id.tv_name)
        tvEmail = view.findViewById(R.id.tv_email)
        btnLogout = view.findViewById(R.id.btn_logout)

        // Observasi state profile
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.profileState.collect { state ->
                when (state) {
                    is ProfileState.Loading -> {
                        // Tampilkan loading jika diperlukan
                    }
                    is ProfileState.Success -> {
                        tvName.text = state.name
                        tvEmail.text = state.email
                    }
                    is ProfileState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    is ProfileState.Logout -> {
                        // Navigasi ke halaman welcome
                        startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
        }

        // Setup logout button
        btnLogout.setOnClickListener {
            profileViewModel.logout()
        }
    }
}