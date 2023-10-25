package org.d3if3038.answerme

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Window
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
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

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
            "Selamat Datang",
            "Hai kamu, saatnya memulai obrolan asik dan seru bersama AnswerMe.",
            R.drawable.welcome_art
        ))
        addSlide(createSlide(
            "AnswerMe ?",
            "Kamu dapat mengirim pesan anonym \n" +
                    "pada orang lain tanpa diketahui, kamu juga bisa \n" +
                    "balas pesan orang lain secara anonym, \n" +
                    "ayo coba sekarang!,",
            R.drawable.masrsmellow_art
        ))
        addSlide(createSlide(
            "Woo Hoo!",
            "Kamu hampir sampai, ayo kita mulai!",
            R.drawable.feedback_art
        ))
    }

    private fun createSlide(title: String, desc: String, image: Int) : Fragment {
        return AppIntroFragment.createInstance(
            title = title,
            description = desc,
            imageDrawable = image,
            titleColorRes = R.color.black,
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