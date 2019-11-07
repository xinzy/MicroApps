package com.xinzy.microapp.relax.media

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import com.xinzy.microapp.lib.util.logV

enum class State {
    /** 初始状态 */
    IDLE,
    /** 播放中 */
    PLAYING,
    /** 暂停 */
    PAUSE,
    /** 停止*/
    STOP,
    /**  */
    PREPARING,
    /**  */
    PREPARED,
}

/** 状态改变 */
interface OnStateChangeListener {
    fun onProgressChanged(current: Int, total: Int)

    fun onStateChanged(status: State)
}

class InternalMediaPlayer : MediaPlayer(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    var state: State = State.IDLE
        private set(value) {
            field = value
            onStateChangeListener?.onStateChanged(field)
        }

    var autoPlay = true
    var onStateChangeListener: OnStateChangeListener? = null
        set(value) {
            field = value
            if (field != null) {
                if (mHandler == null) mHandler = MediaHandler()
                mHandler!!.sendMessage(duration, currentPosition, field)
            } else {
                mHandler?.clear()
            }
        }

    val playing: Boolean
        get() = state == State.PLAYING
    val pausing: Boolean
        get() = state == State.PAUSE

    private var mHandler: MediaHandler? = null

    init {
        isLooping = true
        setAudioStreamType(AudioManager.STREAM_MUSIC)
        setOnPreparedListener(this)
        setOnErrorListener(this)
        setOnCompletionListener(this)
        setOnBufferingUpdateListener(this)
    }

    override fun prepare() {
        super.prepare()
        state = State.PREPARING
    }

    override fun prepareAsync() {
        super.prepareAsync()
        state = State.PREPARING
    }

    override fun start() {
        super.start()
        state = State.PLAYING
    }

    override fun pause() {
        super.pause()
        state = State.PAUSE
    }

    override fun stop() {
        super.stop()
        state = State.STOP
    }

    fun play() {
        if (pausing) {
            start()
        }
    }

    fun close() {
        reset()
        release()
    }

    override fun onPrepared(mp: MediaPlayer) {
        logV("onPrepared")
        state = State.PREPARED
        if (autoPlay) start()
    }

    override fun onCompletion(mp: MediaPlayer) {
        logV("onCompletion")
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        logV("onError what=$what, extra=$extra")
        return true
    }

    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        logV("onBufferingUpdate $percent")
    }
}

private const val MSG_PROGRESS_CHANGE = 100

private class MediaHandler : Handler() {

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MSG_PROGRESS_CHANGE -> {
                val current = msg.arg1
                val duration = msg.arg2
                val listener: OnStateChangeListener? = msg.obj as? OnStateChangeListener
                listener?.onProgressChanged(current, duration)

                if (current >= duration || current + 1000 > duration) {
                } else {
                    sendEmptyMessageDelayed(MSG_PROGRESS_CHANGE, 1000)
                }
            }
        }
    }

    fun sendMessage(total: Int, current: Int, listener: OnStateChangeListener?) {
        val msg = Message.obtain()
        msg.arg1 = current
        msg.arg2 = total
        msg.what = MSG_PROGRESS_CHANGE
        msg.obj = listener

        sendMessageDelayed(msg, 1000)
    }

    fun clear() {
        removeMessages(MSG_PROGRESS_CHANGE)
    }
}