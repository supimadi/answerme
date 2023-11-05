package org.d3if3038.answerme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(applicationContext.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (!settingDataStore.getBoolean("is_boarded", false)) {
            startActivity(Intent(this, OnBoardingActivity::class.java))
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainContainerFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.selectedItemId = R.id.feedPages
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }
}