package ng.lekki.instsaveforinstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.downloader.*
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener
import kotlinx.android.synthetic.main.bruno.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class Whiskey : AppCompatActivity() , AutoPermissionsListener {
    var gramBox: ArrayList<String>? = null
    var counter = 0
    var gLink = ""
    var fbAds: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.whiskey)


        val pr = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build()
        PRDownloader.initialize(getApplicationContext(), pr)


        val intents = intent
        val shorthand = intents.getStringExtra(Intent.EXTRA_TEXT)
        val realURL = shorthand.substring(0,shorthand.indexOf("?")).replace("?","")
        gLink = realURL

        if (checkPermission()) {

            gramPump(gLink)



        }else{

            AutoPermissions.loadActivityPermissions(this@Whiskey, 1)


        }


        close.setOnClickListener {

            finishAndRemoveTask()
        }


        play_btn.setOnClickListener {

            startActivity(Intent(this@Whiskey,Downloads::class.java))

        }


        fbAds = InterstitialAd(this@Whiskey,getString(R.string.fb_ads))



        fbAds!!.setAdListener(object : InterstitialAdListener {
            override fun onLoggingImpression(p0: Ad?) {


            }

            override fun onAdLoaded(p0: Ad?) {

                fbAds!!.show();

            }

            override fun onError(p0: Ad?, p1: AdError?) {


            }

            override fun onInterstitialDismissed(p0: Ad?) {


            }

            override fun onAdClicked(p0: Ad?) {


            }

            override fun onInterstitialDisplayed(p0: Ad?) {


            }


        })



        // Load ads into Interstitial Ads
        fbAds!!.loadAd()
    }



    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AutoPermissions.parsePermissions(this@Whiskey, requestCode, permissions, this)
    }



    override fun onDenied(requestCode: Int, permissions: Array<String>) {


    }

    override fun onGranted(requestCode: Int, permissions: Array<String>) {

        gramPump(gLink)

    }









    fun gramPump(itemId:String) {
        gramBox = ArrayList<String>()
        Log.d("bully",itemId)

        var catch = ""
        launch(UI) {
            val result = async(CommonPool) {

                try {

                    val opin = "?__a=1"
                    val gramUrl = itemId + opin
                    Log.d("kettle",gramUrl)
                    val responding = parseIt(gramUrl)
                    val jsons = JSONObject(responding)
                    val iru = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("__typename")

                    if (iru.contains("GraphSidecar")) {

                        val mediaBox = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getJSONObject("edge_sidecar_to_children").getJSONArray("edges")

                        for (i in 0..mediaBox.length() - 1) {

                            val jsonobj = mediaBox.getJSONObject(i)
                            val genre = jsonobj.getJSONObject("node").getString("__typename")
                            if (genre.contains("GraphVideo")) {

                                val videoURL = jsonobj.getJSONObject("node").getString("video_url")
                                val viewURL = jsonobj.getJSONObject("node").getString("display_url")

                                gramBox!!.add(videoURL)
                            } else {

                                val photoURL = jsonobj.getJSONObject("node").getString("display_url")
                                gramBox!!.add(photoURL)

                            }
                        }

                    }


                    if (iru.contains("GraphImage")) {

                        val imageLink = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url")
                        Log.d("cup",imageLink)
                        gramBox!!.add(imageLink)


                    }


                    if (iru.contains("GraphVideo")) {


                        val videoLink = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("video_url")
                        val photoLink = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url")
                        Log.d("cup",videoLink)

                        gramBox!!.add(videoLink)


                    }



                } catch (e: Exception) {


                    catch = e.message!!.toString()
                }


            }.await()

            runOnUiThread {


                keep()

            }
        }
    }















    // downloader methods


    fun keep(){

        var extension = ""
        val urld = gramBox!![counter]

        if(urld.contains(".jpg")){
            extension = "jpg"
        }

        if(urld.contains(".png")){
            extension = "png"
        }


        if(urld.contains(".gif")){
            extension = "gif"
        }


        if (urld.contains(".mp4")){

            extension = "mp4"
        }
        val sizes = gramBox!!.size
        label.text = "$counter /  $sizes"

        val timeStamp = System.currentTimeMillis()
        titled.text = "♫ $timeStamp"
        val name = "insta_$timeStamp.$extension"
        val dirPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "gramsave")
        if (!dirPath.exists())
            dirPath.mkdirs()

        val filed = File(dirPath, name)
        val filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/gramsave/$name"
        val look = arrayOf(filepath)




        val downloadId = PRDownloader.download(urld, dirPath.absolutePath, name)
                .build()
                .setOnProgressListener(object : OnProgressListener {
                    override fun onProgress(progress: Progress) {
                        val progressPercent = progress.currentBytes * 100 / progress.totalBytes
                        val total = progressPercent.toInt()
                        progress_status.text = "$total%"
                        progressBar.isIndeterminate = false
                        progressBar.progress = total

                    }
                })
                .setOnStartOrResumeListener(object: OnStartOrResumeListener {
                    override fun onStartOrResume() {

                        showControls()
                    }

                })
                .start(object : OnDownloadListener {
                    override fun onError(error: Error?) {


                    }


                    override fun onDownloadComplete() {

                        counter += 1
                        label.text = "$counter /  $sizes"

                        MediaScannerConnection.scanFile(applicationContext, arrayOf(filed.absolutePath), null) { path, uri ->

                            if (counter != gramBox!!.size){

                                runOnUiThread {


                                    keep()
                                }

                            }else{


                                runOnUiThread {
                                    counter = 0
                                    play_btn.visibility = View.VISIBLE
                                    progress_status.text = getString(R.string.fini)
                                    progressBar.visibility =View.GONE


                                }


                            }

                        }


                    }

                })

    }




    //show controls
    fun showControls(){
        covrIcon.visibility = View.VISIBLE
        progress_status.visibility  = View.VISIBLE
        titled.visibility = View.VISIBLE
        progression.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        progress_status.visibility = View.VISIBLE
        close.visibility = View.VISIBLE

    }





    fun parseIt(link:String):String{

        val client = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build()
        val saverequest = Request.Builder()
                .url(link)
                .build()
        val response = client.newCall(saverequest).execute()


        val json = JSONObject(response.body()!!.string())


        return json.toString()
    }






}
