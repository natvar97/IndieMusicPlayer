package com.indialone.indiemusicplayer

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import com.indialone.indiemusicplayer.databinding.ActivityMusicPlayerBinding
import java.io.File

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMusicPlayerBinding
    private var songName = ""
    private var songPosition: Int = 0
    private var mySongs = ArrayList<File>()
    private var mMediaPlayer: MediaPlayer? = null
    private var mSongUri: Uri? = null
    private var seekBarThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.title = "Now Playing"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (mMediaPlayer != null) {
            mMediaPlayer?.stop()
            mMediaPlayer?.release()
        }

        if (intent != null) {
            val bundle = intent.extras
//            val songNameTemp = bundle!!.getString("songName")
            mySongs = bundle!!.getSerializable("songs") as ArrayList<File>
            songPosition = bundle.getInt("position", 0)
        }

        mSongUri = Uri.parse(mySongs.get(songPosition).toString())
        songName = mySongs[songPosition].name
        mBinding.tvSongName.apply {
            isSelected = true
            text = songName
        }

        mMediaPlayer = MediaPlayer.create(this, mSongUri)
        mMediaPlayer!!.start()

        val audioSessionId = mMediaPlayer!!.audioSessionId
        if (audioSessionId != -1) {
            mBinding.visualizer.setAudioSessionId(audioSessionId)
        }

        seekBarThread = object : Thread() {
            override fun run() {
                super.run()
                val totalDuration = mMediaPlayer!!.duration
                var currentDuration = 0

                while (currentDuration < totalDuration) {
                    try {
                        sleep(500)
                        currentDuration = mMediaPlayer!!.currentPosition
                        mBinding.seekbarProgress.progress = currentDuration
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

            }
        }

        mBinding.seekbarProgress.max = mMediaPlayer!!.duration
        seekBarThread!!.start()
        mBinding.seekbarProgress.progressDrawable.setColorFilter(
            resources.getColor(R.color.white),
            PorterDuff.Mode.MULTIPLY
        )
        mBinding.seekbarProgress.thumb.setColorFilter(
            resources.getColor(R.color.white),
            PorterDuff.Mode.SRC_IN
        )

        mBinding.seekbarProgress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mMediaPlayer!!.seekTo(seekBar!!.progress)
            }

        })

        val endTime = createTime(mMediaPlayer!!.duration)
        mBinding.tvSongEnd.text = endTime

        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                val currentTime = createTime(mMediaPlayer!!.currentPosition)
                mBinding.tvSongStart.text = currentTime
                Handler(Looper.getMainLooper()).postDelayed(this, 1000)
            }
        }, 1000)

        mBinding.btnPlay.setOnClickListener {
            if (mMediaPlayer!!.isPlaying) {
                mBinding.btnPlay.setImageResource(R.drawable.icon_play)
                mMediaPlayer!!.pause()
            } else {
                mBinding.btnPlay.setImageResource(R.drawable.icon_pause)
                mMediaPlayer!!.start()
            }
        }

        mBinding.btnPlayNext.setOnClickListener {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            songPosition = ((songPosition + 1) % mySongs.size)
            mSongUri = Uri.parse(mySongs[songPosition].toString())
            mMediaPlayer = MediaPlayer.create(this, mSongUri)
            songName = mySongs[songPosition].name
            mBinding.tvSongName.text = songName
            mMediaPlayer!!.start()
            mBinding.btnPlay.setImageResource(R.drawable.icon_pause)
            startAnimation(mBinding.ivMusic)
            val audioSessionId = mMediaPlayer!!.audioSessionId
            if (audioSessionId != -1) {
                mBinding.visualizer.setAudioSessionId(audioSessionId)
            }
        }

        mBinding.btnPlayPrevious.setOnClickListener {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            songPosition = if ((songPosition - 1 < 0)) (mySongs.size - 1) else (songPosition - 1)
            mSongUri = Uri.parse(mySongs[songPosition].toString())
            mMediaPlayer = MediaPlayer.create(this, mSongUri)
            songName = mySongs[songPosition].name
            mBinding.tvSongName.text = songName
            mMediaPlayer!!.start()
            startAnimation(mBinding.ivMusic)
            val audioSessionId = mMediaPlayer!!.audioSessionId
            if (audioSessionId != -1) {
                mBinding.visualizer.setAudioSessionId(audioSessionId)
            }
        }

        mMediaPlayer!!.setOnCompletionListener {
            mBinding.btnPlayNext.performClick()
        }

        mBinding.btnPlayForward.setOnClickListener {
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.seekTo(mMediaPlayer!!.currentPosition + 10000)
            }
        }

        mBinding.btnPlayBackward.setOnClickListener {
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.seekTo(mMediaPlayer!!.currentPosition - 1000)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBinding.visualizer != null) {
            mBinding.visualizer.release()
        }
    }

    fun startAnimation(view: View) {
        val animator: ObjectAnimator =
            ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        animator.duration = 1000
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animator)
        animatorSet.start()
    }

    fun createTime(duration: Int): String {
        var time = ""
        val minute = duration / 1000 / 60
        val second = duration / 1000 % 60

        time += "$minute:"
        if (second < 10) {
            time += "0"
        }
        time += second
        return time
    }

}