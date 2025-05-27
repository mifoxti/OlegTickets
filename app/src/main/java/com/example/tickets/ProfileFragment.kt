package com.example.tickets

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val loginButton = view.findViewById<Button>(R.id.button)
        loginButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switch = view.findViewById<SwitchCompat>(R.id.switchL)
        val sharedPrefs = requireContext().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)

        // Считываем сохранённое состояние
        val isDark = sharedPrefs.getBoolean("dark_theme", false)
        switch.isChecked = isDark

        switch.setOnCheckedChangeListener { _, isChecked ->
            // Сохраняем новое значение
            sharedPrefs.edit().putBoolean("dark_theme", isChecked).apply()
            // Перезапускаем активити, чтобы применить тему
            requireActivity().recreate()
        }
    }

}
