package org.d3if3038.answerme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import org.d3if3038.answerme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var navContoller: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navContoller = findNavController(R.id.mainContainerFragment)

        binding.bottomNavigation.selectedItemId = R.id.menuFeeds
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menuSetting -> {
                    navContoller.navigate(R.id.settingFragment)
                    true
                }
                R.id.menuFeeds -> {
                    navContoller.navigate(R.id.feedsFragment)
                    true
                }
                R.id.menuMyPost -> {
                    navContoller.navigate(R.id.myPostFragment)
                    true
                }

                else -> false
            }
        }


    }
}