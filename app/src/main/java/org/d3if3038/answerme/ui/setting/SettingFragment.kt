package org.d3if3038.answerme.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import org.d3if3038.answerme.R
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.FragmentSettingBinding
import org.d3if3038.answerme.model.Profile

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(requireActivity().dataStore)
    }
    private val viewModel: SettingViewModel by lazy {
        ViewModelProvider(this)[SettingViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        updateProfileUI(
            settingDataStore.getString("username", ""),
            settingDataStore.getString("dicebearLink", ""),
        )
        registerViewModel()

        binding.topBar.topCollapsingToolbarLayout.title = getString(R.string.setting)
        binding.saveProfileBtn.setOnClickListener {
            if (!binding.saveProfileBtn.isEnabled) return@setOnClickListener

            val username = binding.usernameInputText.text.toString()
            if (username.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter The Username...", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.saveProfile(
                Profile(
                    username = username,
                    diceBear = "https://api.dicebear.com/7.x/adventurer/png?backgroundColor=b6e3f4,c0aede,d1d4f9&seed=${username}"
                )
            )
        }

        return binding.root
    }

    private fun  registerViewModel() {
        viewModel.getProfile().observe(viewLifecycleOwner) {
            settingDataStore.putString("username", it.username)
            settingDataStore.putString("dicebearLink", it.diceBear)
            settingDataStore.putBoolean("profileCreated", true)

            binding.saveProfileBtn.isEnabled = false
            binding.usernameInputText.isEnabled = false

            updateProfileUI(it.username, it.diceBear)
        }
        viewModel.getErrorMessage().observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                getString(R.string.failed_message, it),
                Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun updateProfileUI(username: String, dicebearUrl: String) {
        binding.usernameInputText.setText(username)
        if (dicebearUrl.isEmpty()) {
            Glide.with(requireActivity())
                .load(R.drawable.baseline_account_circle_24).into(binding.profileImage)
        } else {
            Glide.with(requireActivity()).load(dicebearUrl).into(binding.profileImage)

        }

        if (settingDataStore.getBoolean("profileCreated", false)) {
            binding.saveProfileBtn.isEnabled = false
            binding.usernameInputText.isEnabled = false
        }
    }
}