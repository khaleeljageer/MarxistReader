package com.marxist.android.ui.fragments.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSessionManager
import android.os.Bundle
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.marxist.android.R
import com.marxist.android.utils.PrintLog
import kotlinx.android.synthetic.main.audio_player_control_fragment.*
import java.io.IOException

class AudioPlayerFragment : Fragment(), MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
    MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
    AudioManager.OnAudioFocusChangeListener {

    private var resumePosition: Int = 0
    private lateinit var mContext: Context
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private var ongoingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    companion object {
        const val ACTION_PLAY = "com.marxist.android.ui.fragments.player.ACTION_PLAY"
        const val ACTION_PAUSE = "com.marxist.android.ui.fragments.player.ACTION_PAUSE"
        const val ACTION_FORWARD = "com.marxist.android.ui.fragments.player.ACTION_FORWARD"
        const val ACTION_REWIND = "com.marxist.android.ui.fragments.player.ACTION_REWIND"
        const val ACTION_STOP = "com.marxist.android.ui.fragments.player.ACTION_STOP"
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
        btnPlayPause.setMinFrame(33)
        btnPlayPause.setOnClickListener {
            btnPlayPause.playAnimation()
            playMedia()
        }

        btnForward.setOnClickListener {
            btnForward.playAnimation()
            forward()
        }

        btnRewind.setOnClickListener {
            btnRewind.playAnimation()
            rewind()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mediaSessionManager == null) {
            try {
                initMediaSession()
                initMediaPlayer()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
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


        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer!!.setDataSource("http://archive.org/download/2019JulyMarxist/last%20years%20of%20karl%20marx.mp3")
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
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
        }
    }

    private fun forward() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition + 10000)
            mediaPlayer!!.start()
        }
    }

    private fun rewind() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition - 10000)
            mediaPlayer!!.start()
        }
    }

    private fun stopMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
    }

    private fun pauseMedia() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            resumePosition = mediaPlayer!!.currentPosition
        }
    }

    private fun resumeMedia() {
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
    }

    override fun onPrepared(p0: MediaPlayer?) {
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
        PrintLog.debug(
            "Khaleel", "Percent $percent"
        )
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
}