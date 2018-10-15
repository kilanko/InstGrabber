package ng.lekki.instsaveforinstagram

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.breuhteam.apprate.AppRate
import kotlinx.android.synthetic.main.activity_main.*
import ng.lekki.instsaveforinstagram.Templar.Graham
import android.R.id.edit
import android.content.SharedPreferences
import android.preference.PreferenceManager



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.app_name)
        val colorWhite = ContextCompat.getColor(applicationContext, android.R.color.white)
        toolbar.setTitleTextColor(colorWhite)


        ThreadRipper()


        AppRate.app_launched(this@MainActivity, packageName,0,4)


        val startKit = Intent(this@MainActivity,Graham::class.java)
        ContextCompat.startForegroundService(this@MainActivity,startKit)


        icon.setOnClickListener {

            phoneGram("com.instagram.android")

        }


        btn_open.setOnClickListener {

            phoneGram("com.instagram.android")

        }



    }


    private fun phoneGram(packageN: String) {
        val apppackage = packageN
        try {
            val i = packageManager.getLaunchIntentForPackage(apppackage)
            startActivity(i)
            finish()
        } catch (e: Exception) {
           startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageN)))
        }

    }

    //menu options

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tools,menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){

            R.id.saves ->{

                startActivity(Intent(this@MainActivity,Downloads::class.java))

            }


            R.id.heart ->{

                phoneGram("com.instagram.android")


            }
        }

        return super.onOptionsItemSelected(item)
    }




    fun ThreadRipper(){

        val t = Thread(Runnable {
            //  Initialize SharedPreferences
            val getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(baseContext)

            //  Create a new boolean and preference and set it to true
            val isFirstStart = getPrefs.getBoolean("firstStart", true)

            //  If the activity has never started before...
            if (isFirstStart) {

                //  Launch app intro
                val i = Intent(this@MainActivity, AppIntros::class.java)

                runOnUiThread { startActivity(i) }

                //  Make a new preferences editor
                val e = getPrefs.edit()

                //  Edit preference to make it false because we don't want this to run again
                e.putBoolean("firstStart", false)

                //  Apply changes
                e.apply()
            }
        })

        // Start the thread
        t.start()
    }

}
