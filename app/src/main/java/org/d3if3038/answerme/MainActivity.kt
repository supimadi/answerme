package org.d3if3038.answerme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
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
        setSupportActionBar(binding.topAppBar)

        if (!settingDataStore.getBoolean("is_boarded", false)) {
            startActivity(Intent(this, OnBoardingActivity::class.java))
        }

        navController = findNavController(R.id.mainContainerFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        binding.bottomNavigation.selectedItemId = R.id.menuFeeds
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menuSetting -> {
                    navController.navigate(R.id.settingFragment)
                    binding.topAppBarLayout.title = getString(R.string.setting)
                    true
                }
                R.id.menuFeeds -> {
                    navController.navigate(R.id.feedsFragment)
                    binding.topAppBarLayout.title = getString(R.string.feeds)
                    true
                }
                R.id.menuMyPost -> {
                    navController.navigate(R.id.myPostFragment)
                    binding.topAppBarLayout.title = getString(R.string.my_post)
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}