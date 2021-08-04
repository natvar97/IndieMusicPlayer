package com.indialone.indiemusicplayer

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.recyclerview.widget.RecyclerView
import com.indialone.indiemusicplayer.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var items: Array<String?>? = null

    //    private lateinit var myAdapter: ArrayAdapter<String>
    private lateinit var mCustomAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        runtimePermission()

    }

    fun runtimePermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    displaySongs()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }

    fun findSong(file: File): ArrayList<File> {
        val arrayList = ArrayList<File>()
        val listFiles = file.listFiles()

        if (listFiles != null) {
            for (singleFile in listFiles) {
                if (singleFile.isDirectory && !singleFile.isHidden) {
                    arrayList.addAll(findSong(singleFile))
                } else {
                    if (singleFile.name.endsWith(".mp3") || singleFile.name.endsWith(".wav")) {
                        arrayList.add(singleFile)
                    }
                }
            }
        }
        return arrayList
    }

    fun displaySongs() {
        val mySongs = findSong(Environment.getExternalStorageDirectory())

        val size = mySongs.size - 1
        items = arrayOfNulls(mySongs.size)
        for (i in 0..size) {
            items!![i] = mySongs[i].name.toString().replace(".mp3", "").replace(".wav", "")
        }

//        myAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        mCustomAdapter = CustomAdapter(this,items)
        mBinding.musicList.adapter = mCustomAdapter
        mBinding.musicList.setOnItemClickListener { parent, view, position, id ->
            val songName = mBinding.musicList.getItemAtPosition(position) as String?
            val intent = Intent(this, MusicPlayerActivity::class.java)
            intent.putExtra("songs", mySongs)
            intent.putExtra("songName", songName)
            intent.putExtra("position", position)
            startActivity(intent)
        }

    }

}