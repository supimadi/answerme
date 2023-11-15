package org.d3if3038.answerme

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Window
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore

class OnBoardingActivity : AppIntro2() {
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(applicationContext.dataStore)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setIndicatorColor(
            getColor(R.color.teal_700),
            getColor(R.color.teal_200)
        )

        // Set flags for onboarding screen
        isSystemBackButtonLocked = true
        isColorTransitionsEnabled = true

        setImmersiveMode()
        showStatusBar(true)

        // Create parallax effect
        setTransformer(AppIntroPageTransformerType.Parallax(
            titleParallaxFactor = 1.0,
            imageParallaxFactor = -1.0,
            descriptionParallaxFactor = 2.0
        ))

        addSlide(createSlide(
            "Welcome",
            getString(R.string.first_slide_onboarding),
            R.drawable.undraw_joyride
        ))
        addSlide(createSlide(
            "Always Remember A Thing",
            getString(R.string.second_slide_onboarding),
            R.drawable.undraw_cat_epte
        ))
        addSlide(createSlide(
            "Enjoy Your Time",
            getString(R.string.third_slide_onboarding),
            R.drawable.undraw_to_the_moon
        ))
    }

    private fun createSlide(title: String, desc: String, image: Int) : Fragment {
        return AppIntroFragment.createInstance(
            title = title,
            description = desc,
            imageDrawable = image,
            titleColorRes = R.color.teal_700,
            descriptionColorRes = R.color.black,
        )
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        settingDataStore.putBoolean("is_boarded", true)
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        settingDataStore.putBoolean("is_boarded", true)
        super.onDonePressed(currentFragment)
        finish()
    }
}