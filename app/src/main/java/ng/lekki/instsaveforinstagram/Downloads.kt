package ng.lekki.instsaveforinstagram

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener
import kotlinx.android.synthetic.main.downloads.*
import kotlinx.android.synthetic.main.save_item.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.util.*

class Downloads : AppCompatActivity(), AutoPermissionsListener {
    var adapter: AlphaAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.downloads)

        toolbarx.setTitleTextColor(ContextCompat.getColor(applicationContext,android.R.color.white))
        toolbarx.setTitle(R.string.menu_save)

        checkx()
    }



    fun checkx(){

        if (checkPermission())
        {
            loadFolder()

        } else {


            AutoPermissions.loadActivityPermissions(this@Downloads, 1)

        }
    }



    fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this@Downloads, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }



    fun loadFolder() {

        val dataList = ArrayList<String>()
        var imageList = ArrayList<String>()
        val titleList = ArrayList<String>()
        val dateList = ArrayList<Long>()


        launch(UI) {


            val result = async(CommonPool) {

                createFolder()
                val path =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath  + "/gramsave/"

                // code to retrieve from media library
                val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.SIZE, MediaStore.Video.Thumbnails.DATA)
                val queryUri = MediaStore.Files.getContentUri("external")

                val cursor =  contentResolver.query(queryUri, projection, MediaStore.Files.FileColumns.DATA + " LIKE ? AND " + MediaStore.Files.FileColumns.DATA + " NOT LIKE ?", arrayOf(path + "%", path + "%/%"), MediaStore.Files.FileColumns.DATE_ADDED + " desc")

                var url = ""


                if (cursor != null) {

                    if (cursor.moveToFirst()) {

                        val Column_data = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                        val Column_name = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                        val Column_mime = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
                        val Column_id = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                        val Column_time = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                        val Column_type = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
                        val Column_size = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)


                        do {

                            val mData = cursor.getString(Column_data)
                            val mName = cursor.getString(Column_name)
                            val mMime = cursor.getString(Column_mime)
                            val mId = cursor.getString(Column_id)
                            val mTime = cursor.getString(Column_time)
                            val mType = cursor.getString(Column_type)
                            val mDate = Date(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)) * 1000)


                            if (mMime != null && mMime.contains("video")) {

                                val uri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + "/" + mId)
                                url = uri.toString()


                            }

                            if (mMime!= null && mMime.contains("audio")) {

                                val uri = Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/" + mId)
                                url = uri.toString()


                            }

                            if (mMime!= null && mMime.contains("image")) {

                                url = mData

                            }

                            dataList.add(mData)
                            imageList.add(url)
                            titleList.add(mName)
                            val milliSeconds = mDate.time
                            dateList.add(milliSeconds)

                        } while (cursor.moveToNext())


                    } else {

                    }
                }

                cursor.close()



            }.await()

            if (titleList.size != 0){
                jappa.visibility = View.GONE

                adapter = AlphaAdapter(itemLayoutRes = R.layout.save_item,
                        itemCount = dataList.size,
                        binder = {

                            val loot =  imageList[it.adapterPosition]
                            val point = dataList[it.adapterPosition]
                            val imat  = "file://"+ imageList[it.adapterPosition]



                            val nicky = titleList[it.adapterPosition]
                            it.itemView.username.text = nicky
                            it.itemView.topic.text = "♫ " + titleList[it.adapterPosition].replace(nicky,"").replace("-","")
                            it.itemView.timestamp.setReferenceTime(dateList[it.adapterPosition])


                            var ImageID = ""




                            if (nicky.contains("mp4")){
                                ImageID = imageList[it.adapterPosition]
                                it.itemView.playcons.visibility = View.VISIBLE

                                it.itemView.setOnClickListener {


                                    MediaScannerConnection.scanFile(this@Downloads, arrayOf(point), null) { path, uri ->

                                        val firetent = Intent(Intent.ACTION_SEND)
                                        firetent.putExtra(Intent.EXTRA_STREAM,uri)
                                        firetent.type = "video/*"
                                        firetent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        startActivity(firetent)
                                    }

                                }


                            }else{

                                ImageID = "file://"+ imageList[it.adapterPosition]
                                it.itemView.playcons.visibility = View.GONE

                                it.itemView.setOnClickListener {
                                    MediaScannerConnection.scanFile(this@Downloads, arrayOf(point), null) { path, uri ->
                                        val gent = Intent(Intent.ACTION_VIEW)
                                        gent.setDataAndType(uri, "image/*")
                                        gent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        startActivity(gent)
                                    }

                                }

                            }


                            GlideApp.with(this@Downloads)
                                    .asBitmap()
                                    .load(ImageID)
                                    .into(object : SimpleTarget<Bitmap>(){
                                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                                            it.itemView.albumart.setImageBitmap(resource)

                                        }


                                    })




                        })


                RVz.adapter = adapter
                RVz.layoutManager = GridLayoutManager(this@Downloads,3)
                adapter!!.notifyDataSetChanged()

            }else{


            }

        }


    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AutoPermissions.parsePermissions(this@Downloads, requestCode, permissions, this)
    }


    override fun onGranted(requestCode: Int, permissions: Array<String>) {

    }

    override fun onDenied(requestCode: Int, permissions: Array<String>) {

    }



    fun createFolder(){

        val dex = File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "gramsave")
        if (!dex.exists())
            dex.mkdirs()

    }
}
