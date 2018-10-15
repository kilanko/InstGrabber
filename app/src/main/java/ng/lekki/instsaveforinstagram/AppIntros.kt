package ng.lekki.instsaveforinstagram

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import ng.lekki.instsaveforinstagram.Templar.Graham
import org.jetbrains.annotations.Nullable


class AppIntros : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startKit = Intent(this@AppIntros, Graham::class.java)
        ContextCompat.startForegroundService(this@AppIntros,startKit)


        val backGrounds = ContextCompat.getColor(applicationContext,R.color.colorPrimaryDark)

        val sliderPage1 = SliderPage()
        sliderPage1.title = getString(R.string.guide_a)
        sliderPage1.description = getString(R.string.guide_name_a)
        sliderPage1.imageDrawable = R.drawable.ic_a0
        sliderPage1.bgColor = backGrounds



        val sliderPage2 = SliderPage()
        sliderPage2.title = getString(R.string.guide_b)
        sliderPage2.description = getString(R.string.guide_name_b)
        sliderPage2.imageDrawable = R.drawable.ic_a1
        sliderPage2.bgColor = backGrounds



        val sliderPage3 = SliderPage()
        sliderPage3.title = getString(R.string.guide_c)
        sliderPage3.description = getString(R.string.guide_name_c)
        sliderPage3.imageDrawable = R.drawable.ic_a2
        sliderPage3.bgColor = backGrounds




        val sliderPage4 = SliderPage()
        sliderPage4.title = getString(R.string.guide_d)
        sliderPage4.description = getString(R.string.guide_name_d)
        sliderPage4.imageDrawable = R.drawable.ic_a3
        sliderPage4.bgColor = backGrounds


        addSlide(AppIntroFragment.newInstance(sliderPage1))
        addSlide(AppIntroFragment.newInstance(sliderPage2))
        addSlide(AppIntroFragment.newInstance(sliderPage3))
        addSlide(AppIntroFragment.newInstance(sliderPage4))



        showSkipButton(false)
        isProgressButtonEnabled = true

    }


    override fun onSkipPressed(currentFragment: Fragment) {
        super.onSkipPressed(currentFragment)


    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)

        startActivity(Intent(this@AppIntros,MainActivity::class.java))

    }


}
