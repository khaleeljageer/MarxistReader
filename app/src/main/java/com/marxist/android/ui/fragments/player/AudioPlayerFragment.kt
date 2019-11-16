package com.marxist.android.ui.fragments.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSessionManager
import android.os.Bundle
import android.os.Handler
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.marxist.android.R
import com.marxist.android.database.AppDatabase
import com.marxist.android.database.entities.LocalFeeds
import com.marxist.android.model.ShowSnackBar
import com.marxist.android.utils.AppConstants
import com.marxist.android.utils.PrintLog
import com.marxist.android.utils.RxBus
import com.marxist.android.utils.download.DownloadUtil
import com.marxist.android.utils.download.PreferencesHelper
import kotlinx.android.synthetic.main.audio_player_control_fragment.*
import java.io.File
import java.io.IOException
import java.util.*

class AudioPlayerFragment : Fragment(), MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
    MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
    AudioManager.OnAudioFocusChangeListener {

    private var isPrepared: Boolean = false
    private var bufferedPosition: Int = 0
    private var localFeeds: LocalFeeds? = null
    private var resumePosition: Int = 0
    private lateinit var mContext: Context
    private var mediaPlayer: MediaPlayer? = null
    private var formatBuilder: StringBuilder? = null
    private var formatter: Formatter? = null
    private var dragging: Boolean = false
    private var seekDispatcher: SeekDispatcher? = null
    private var handler: Handler? = null

    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private var ongoingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    companion object {
        const val PROGRESS_BAR_MAX = 1000
        const val TIME_UNSET = Long.MIN_VALUE + 1
        const val ACTION_PLAY = "com.marxist.android.ui.fragments.player.ACTION_PLAY"
        const val ACTION_PAUSE = "com.marxist.android.ui.fragments.player.ACTION_PAUSE"
        const val ACTION_FORWARD = "com.marxist.android.ui.fragments.player.ACTION_FORWARD"
        const val ACTION_REWIND = "com.marxist.android.ui.fragments.player.ACTION_REWIND"
        const val ACTION_STOP = "com.marxist.android.ui.fragments.player.ACTION_STOP"

        fun newInstance(localFeeds: LocalFeeds, type: Int): AudioPlayerFragment {
            val bundle = Bundle()
            bundle.putSerializable("KEY_LOCAL_FEEDS", localFeeds)
            bundle.putInt("KEY_AUDIO_TYPE", type)
            val fragment = AudioPlayerFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callStateListener()
        registerBecomingNoisyReceiver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.audio_player_control_fragment, container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler()
        seekDispatcher = DEFAULT_SEEK_DISPATCHER
        formatBuilder = StringBuilder()
        formatter = Formatter(formatBuilder, Locale.getDefault())
        sbPlayer.max = PROGRESS_BAR_MAX

        btnPlayPause.tag = "PLAY"
        pbPrepare.visibility = View.INVISIBLE
        btnPlayPause.addAnimatorListener(animatorListener)
        btnPlayPause.setOnClickListener {
            if (btnPlayPause.tag == "PLAY") {
                btnPlayPause.tag = "PAUSE"
                btnPlayPause.setMinAndMaxFrame(0, 33)
                if (isPrepared)
                    playMedia()
            } else {
                btnPlayPause.tag = "PLAY"
                btnPlayPause.setMinAndMaxFrame(33, 66)
                if (isPrepared)
                    pauseMedia()
            }
            btnPlayPause.playAnimation()
            btnPlayPause.isActivated = btnPlayPause.isAnimating
            btnPlayPause.postInvalidate()

            if (mediaPlayer == null) {
                pbPrepare.visibility = View.VISIBLE
                btnPlayPause.visibility = View.GONE
                initMediaPlayer()
            }
        }

        btnForward.setOnClickListener {
            btnForward.playAnimation()
            forward()
        }

        btnRewind.setOnClickListener {
            btnRewind.playAnimation()
            rewind()
        }

        btnRemove.setOnClickListener {
            removeDownloads()
        }

        btnDownload.setOnClickListener {
            downloadItem()
        }

        sbPlayer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val position = positionValue(progress)
                    if (txtPosition != null) {
                        txtPosition.text = stringForTime(position)
                    }
                    if (mediaPlayer != null && !dragging) {
                        seekTo(position)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                dragging = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                dragging = false
                if (mediaPlayer != null) {
                    seekTo(positionValue(seekBar.progress))
                }
            }
        })
    }

    private fun removeDownloads() {
        btnRemove.visibility = View.INVISIBLE
        val filePath = File(localFeeds!!.downloadPath)
        AppDatabase.getAppDatabase(mContext).localFeedsDao()
            .resetAudioStatus(false, "", localFeeds!!.title, localFeeds!!.pubDate)
        filePath.delete()
        localFeeds!!.downloadPath = ""
        localFeeds!!.isDownloaded = false

        RxBus.publish(ShowSnackBar(getString(R.string.audio_deleted)))
    }

    private val animatorListener = AnimatorListenerAdapter(
        onStart = { btnPlayPause.isActivated = true },
        onEnd = {
            btnPlayPause.isActivated = false
        },
        onCancel = {
            btnPlayPause.isActivated = false
        },
        onRepeat = {
        }
    )

    private fun downloadItem() {
        DownloadUtil.queueForDownload(
            mContext,
            localFeeds!!.title,
            localFeeds!!.audioUrl,
            localFeeds!!.pubDate,
            AppConstants.AUDIO
        )

        RxBus.publish(ShowSnackBar(getString(R.string.download_started)))
        btnDownload.visibility = View.INVISIBLE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        localFeeds = (arguments!!.getSerializable("KEY_LOCAL_FEEDS") as LocalFeeds?)!!
        if (mediaSessionManager == null) {
            try {
                initMediaSession()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        if (localFeeds!!.isDownloaded) {
            btnDownload.visibility = View.INVISIBLE
            btnRemove.visibility = View.VISIBLE
        } else {
            btnDownload.visibility = View.VISIBLE
            btnRemove.visibility = View.INVISIBLE
        }
    }

    private fun initMediaPlayer() {
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer()

        mediaPlayer!!.setOnCompletionListener(this)
        mediaPlayer!!.setOnErrorListener(this)
        mediaPlayer!!.setOnPreparedListener(this)
        mediaPlayer!!.setOnBufferingUpdateListener(this)
        mediaPlayer!!.setOnSeekCompleteListener(this)
        mediaPlayer!!.setOnInfoListener(this)
        mediaPlayer!!.reset()


        mediaPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
        try {
            if (localFeeds!!.isDownloaded) {
                mediaPlayer!!.setDataSource("file://${localFeeds!!.downloadPath}")
            } else {
                mediaPlayer!!.setDataSource(localFeeds!!.audioUrl)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaPlayer!!.prepareAsync()
    }

    @Throws(RemoteException::class)
    private fun initMediaSession() {
        if (mediaSessionManager != null) return

        mediaSessionManager =
            mContext.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager?
        mediaSession = MediaSessionCompat(mContext, "MRAudioPlayer")
        transportControls = mediaSession!!.controller.transportControls
        mediaSession!!.isActive = true
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession!!.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()

                resumeMedia()
            }

            override fun onPause() {
                super.onPause()

                pauseMedia()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer!!.release()
        }
        if (phoneStateListener != null) {
            telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }

        mContext.unregisterReceiver(becomingNoisyReceiver)
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            pauseMedia()
        }
    }

    private fun playMedia() {
        if (mediaPlayer == null) return
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            handler!!.post(updateProgressAction)
        }
    }

    private fun forward() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition + 10000)
            mediaPlayer!!.start()
        }
    }

    private fun rewind() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition - 10000)
            mediaPlayer!!.start()
        }
    }

    private fun stopMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            handler!!.removeCallbacks(updateProgressAction)
        }
    }

    private fun pauseMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            handler!!.removeCallbacks(updateProgressAction)
            resumePosition = mediaPlayer!!.currentPosition
        }
    }

    private fun resumeMedia() {
        if (mediaPlayer == null) return
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.seekTo(resumePosition)
            mediaPlayer!!.start()
        }
    }

    private fun registerBecomingNoisyReceiver() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        mContext.registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    private fun callStateListener() {
        telephonyManager = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING -> if (mediaPlayer != null) {
                        pauseMedia()
                        ongoingCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE -> if (mediaPlayer != null) {
                        if (ongoingCall) {
                            ongoingCall = false
                            resumeMedia()
                        }
                    }
                }
            }
        }
        telephonyManager!!.listen(
            phoneStateListener,
            PhoneStateListener.LISTEN_CALL_STATE
        )
    }

    override fun onCompletion(p0: MediaPlayer?) {
        stopMedia()
        btnPlayPause.tag = "PLAY"
        btnPlayPause.setMinAndMaxFrame(33, 66)
        btnPlayPause.isActivated = btnPlayPause.isAnimating
        btnPlayPause.postInvalidate()
    }

    override fun onPrepared(p0: MediaPlayer?) {
        isPrepared = true
        pbPrepare.visibility = View.INVISIBLE
        btnPlayPause.visibility = View.VISIBLE
        handler!!.post(updateProgressAction)
        playMedia()
    }


    override fun onError(p0: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra"
            )
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR SERVER DIED $extra"
            )
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR UNKNOWN $extra"
            )
        }
        return false
    }

    override fun onSeekComplete(p0: MediaPlayer?) {
        PrintLog.debug(
            "Khaleel", "onSeekComplete"
        )
    }

    override fun onInfo(p0: MediaPlayer?, p1: Int, p2: Int): Boolean = false

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        bufferedPosition = percent
    }

    override fun onAudioFocusChange(focusState: Int) {
        when (focusState) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mediaPlayer == null)
                    initMediaPlayer()
                else if (!mediaPlayer!!.isPlaying) mediaPlayer!!.start()
                mediaPlayer!!.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> if (mediaPlayer!!.isPlaying) mediaPlayer!!.setVolume(
                0.1f,
                0.1f
            )
        }
    }

    private val updateProgressAction = Runnable { updateProgress() }
    private fun updateProgress() {
        val duration = (if (mediaPlayer == null) 0 else mediaPlayer!!.duration).toLong()
        val position = (if (mediaPlayer == null) 0 else mediaPlayer!!.currentPosition).toLong()
        if (txtDuration != null) {
            txtDuration.text = stringForTime(duration)
        }
        if (txtPosition != null && !dragging) {
            txtPosition.text = stringForTime(position)
        }

        if (sbPlayer != null) {
            if (!dragging) {
                sbPlayer.progress = progressBarValue(position)
            }
            val bufferedPosition =
                (if (mediaPlayer == null) 0 else bufferedPosition).toLong()
            sbPlayer.secondaryProgress = progressBarValue(bufferedPosition)
        }
        handler!!.removeCallbacks(updateProgressAction)

        if (mediaPlayer!!.isPlaying) {
            handler!!.postDelayed(updateProgressAction, 1000)
        }
    }

    private fun stringForTime(time: Long): String {
        var timeMs = time
        if (timeMs == TIME_UNSET) {
            timeMs = 0
        }
        val totalSeconds = (timeMs + 500) / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        formatBuilder!!.setLength(0)
        return if (hours > 0)
            formatter!!.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        else
            formatter!!.format("%02d:%02d", minutes, seconds).toString()
    }

    private fun progressBarValue(position: Long): Int {
        val duration = if (mediaPlayer == null) TIME_UNSET else mediaPlayer!!.duration.toLong()
        return if (duration == TIME_UNSET || duration == 0L)
            0
        else
            (position * PROGRESS_BAR_MAX / duration).toInt()
    }

    private fun seekTo(positionMs: Long) {
        val dispatched = seekDispatcher!!.dispatchSeek(mediaPlayer!!, positionMs)
        if (!dispatched) {
            updateProgress()
        }
    }

    private fun positionValue(progress: Int): Long {
        val duration = if (mediaPlayer == null) TIME_UNSET else mediaPlayer!!.duration.toLong()
        return if (duration == TIME_UNSET) 0 else duration * progress / PROGRESS_BAR_MAX
    }

    /**
     * Dispatches seek operations to the player.
     */
    interface SeekDispatcher {

        /**
         * @param player      The player to seek.
         * @param positionMs  The seek position in the specified window, or [TIME_UNSET] to seek
         * to the window's default position.
         * @return True if the seek was dispatched. False otherwise.
         */
        fun dispatchSeek(player: MediaPlayer, positionMs: Long): Boolean

    }

    /**
     * Default [SeekDispatcher] that dispatches seeks to the player without modification.
     */
    val DEFAULT_SEEK_DISPATCHER: SeekDispatcher = object : SeekDispatcher {

        override fun dispatchSeek(
            player: MediaPlayer,
            positionMs: Long
        ): Boolean {
            player.seekTo(positionMs.toInt())
            return true
        }
    }
}