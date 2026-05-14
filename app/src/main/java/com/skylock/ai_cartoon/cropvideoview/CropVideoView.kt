package com.skylock.ai_cartoon.cropvideoview



import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.SurfaceTexture
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout
import java.io.IOException


class CropVideoView : FrameLayout, TextureView.SurfaceTextureListener {

    companion object {
        private const val TAG = "mobi.zeezoo.photoenhancer.feature.widget.cropvideoview.CropVideoView"
    }

    private var LOG_ON: Boolean = true
    private lateinit var mBackground: FrameLayout
    var mIsDataSourceSet: Boolean = false
    var mIsPlayCalled: Boolean = false
    var mIsVideoPrepared: Boolean = false
    var mIsViewAvailable: Boolean = false
    var mListener: MediaPlayerListener? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mScaleType: ScaleType = ScaleType.NONE
    private var mState: State = State.UNINITIALIZED
    private lateinit var mTextureView: TextureView
    private var mVideoHeight: Float = 0f
    private var mVideoWidth: Float = 0f

    var vScaleType: Int = 0x7f0406c5

    var vScaleStyle: IntArray = intArrayOf(vScaleType)
    enum class State {
        UNINITIALIZED, PLAY, STOP, PAUSE, END
    }

    constructor(context: Context) : super(context) {
        if (!isInEditMode) initView()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        if (!isInEditMode) initView()
    }

    @SuppressLint("ResourceType")
    constructor(context: Context, attributeSet: AttributeSet?, i: Int) : super(context, attributeSet, i) {
        if (!isInEditMode) initView()
        attributeSet?.let {
            val obtainStyledAttributes = context.obtainStyledAttributes(it, vScaleStyle, 0, 0)
            val i2 = obtainStyledAttributes.getInt(0x7f0406c5, ScaleType.NONE.ordinal)
            obtainStyledAttributes.recycle()
            mScaleType = ScaleType.values()[i2]
            mScaleType = ScaleType.CENTER_CROP
        }
    }

    private fun initView() {
        initPlayer()
        mTextureView = TextureView(context)
        mBackground = FrameLayout(context)
        removeAllViews()
        addView(mBackground)
        addView(mTextureView)
        setScaleType(ScaleType.CENTER_CROP)
        mTextureView.surfaceTextureListener = this
    }

    private fun initPlayer() {
        if (mMediaPlayer == null) {
            println("MediaPlayer: init")
            mMediaPlayer = MediaPlayer()
        } else {
            println("MediaPlayer: null")
            mMediaPlayer?.reset()
        }
        mIsVideoPrepared = false
        mIsPlayCalled = false
        mState = State.UNINITIALIZED
    }

    fun setLogOn(bool: Boolean) {
        LOG_ON = bool
    }

    fun setScaleType(scaleType: ScaleType) {
        mScaleType = scaleType
    }

    fun isPlaying(): Boolean = mMediaPlayer?.isPlaying ?: false

    fun setRawData(i: Int) {
        initPlayer()
        mIsDataSourceSet = true
        try {
            setDataSource(resources.openRawResourceFd(i))
        } catch (e: Exception) {
            mListener?.onError(e)
            log(e.message)
        }
    }

    fun setAssetData(str: String) {
        initPlayer()
        try {
            setDataSource(context.assets.openFd(str))
        } catch (e: Exception) {
            mListener?.onError(e)
            log(e.message)
        }
    }

    fun setDataSource(str: String) {
        initPlayer()
        try {
            mMediaPlayer?.setDataSource(str)
            mIsDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            mListener?.onError(e)
            log(e.message)
        }
    }

    fun setDataSource(context: Context, uri: Uri) {
        initPlayer()
        try {
            mMediaPlayer?.setDataSource(context, uri)
            mIsDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            mListener?.onError(e)
            log(e.message)
        }
    }

    fun setDataSource(assetFileDescriptor: AssetFileDescriptor) {
        initPlayer()
        try {
            mMediaPlayer?.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            mIsDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            mListener?.onError(e)
            log(e.message)
        }
    }

    private fun prepare() {
        try {
            mMediaPlayer?.setOnVideoSizeChangedListener { _, i, i2 ->
                mVideoWidth = i.toFloat()
                mVideoHeight = i2.toFloat()
                scaleVideoSize(i, i2)
            }
            mMediaPlayer?.setOnCompletionListener {
                mState = State.END
                log("Video has ended.")
                mListener?.onVideoEnd()
            }
            mMediaPlayer?.prepareAsync()
            mMediaPlayer?.setOnPreparedListener {
                mIsVideoPrepared = true
                if (mIsPlayCalled && mIsViewAvailable) {
                    log("Player is prepared and play() was called.")
                    play()
                }
                mListener?.onVideoPrepared()
            }
            mMediaPlayer?.setOnErrorListener { _, _, _ -> false }
        } catch (e: Exception) {
            mListener?.onError(e)
            log(e.message)
        }
    }

    fun play(i: Int = 0) {
        println("mIsDataSourceSet: $mIsDataSourceSet")
        if (!mIsDataSourceSet || mMediaPlayer == null) {
            log("play() was called but data source was not set.")
            return
        }
        mIsPlayCalled = true
        if (!mIsVideoPrepared) {
            log("play() was called but video is not prepared yet, waiting.")
            return
        }
        if (!mIsViewAvailable) {
            log("play() was called but view is not available yet, waiting.")
            return
        }
        if (mState == State.PLAY) {
            log("play() was called but video is already playing.")
            return
        }
        if (mState == State.PAUSE) {
            log("play() was called but video is paused, resuming.")
            mState = State.PLAY
            mMediaPlayer?.start()
        } else {
            mState = State.PLAY
            mMediaPlayer?.seekTo(i)
            mMediaPlayer?.start()
        }
    }

    fun getVideoHeight(): Float = mVideoHeight
    fun getVideoWidth(): Float = mVideoWidth

    fun pause() {
        if (mState == State.PAUSE || mState == State.STOP || mState == State.END) {
            log("pause() was called but video already paused/stopped/ended.")
            return
        }
        mState = State.PAUSE
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer?.pause()
        }
    }

    fun stop() {
        if (mState == State.STOP || mState == State.END) {
            log("stop() was called but video already stopped or ended.")
            return
        }
        mState = State.STOP
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer?.pause()
            mMediaPlayer?.seekTo(0)
        }
    }

    fun setLooping(z: Boolean) {
        mMediaPlayer?.isLooping = z
    }

    fun seekTo(i: Int) {
        mMediaPlayer?.seekTo(i)
    }

    fun getDuration(): Int = mMediaPlayer?.duration ?: 0

    fun getFullDuration(timeUnit: TimeUnit): Int {
        val duration = mMediaPlayer?.duration ?: 0
        return when (timeUnit) {
            TimeUnit.SECONDS -> duration / 1000
            TimeUnit.MINUTES -> (duration / 1000) / 60
            TimeUnit.HOURS -> ((duration / 1000) / 60) / 60
            else -> duration
        }
    }

    fun getCurrentPosition(): Int = mMediaPlayer?.currentPosition ?: 0

    fun setVolume(f: Float, f2: Float) {
        mMediaPlayer?.setVolume(f, f2)
    }

    fun isLooping(): Boolean = mMediaPlayer?.isLooping ?: false

    private fun log(str: String?) {
        val message = str ?: "error null"
        if (LOG_ON) Log.d(TAG, message)
    }

    fun reset() {
        mMediaPlayer?.reset()
    }

    fun release() {
        mMediaPlayer?.let {
            it.reset()
            it.release()
            mMediaPlayer = null
        }
    }

    private fun scaleVideoSize(i: Int, i2: Int) {
        if (i == 0 || i2 == 0) return
        val scaleMatrix = ScaleManager(Size(width, height), Size(i, i2)).getScaleMatrix(mScaleType)
        if (scaleMatrix != null) {
            mTextureView.setTransform(scaleMatrix)
        }
    }

    fun setListener(mediaPlayerListener: MediaPlayerListener) {
        mListener = mediaPlayerListener
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, i: Int, i2: Int) {
        val surface = Surface(surfaceTexture)
        mMediaPlayer?.let {
            it.setSurface(surface)
            mIsViewAvailable = true
            if (!mIsDataSourceSet) {
                log("Surface is available but data source was lost. Resetting media player...")
                resetPlayer()
            }
            if (mIsPlayCalled && mIsVideoPrepared) {
                log("View is available, restarting playback.")
                play()
            }
        }
    }

    fun resetPlayer() {
        mMediaPlayer?.let {
            it.reset()
            it.release()
        }
        mMediaPlayer = MediaPlayer()
        mIsVideoPrepared = false
        mIsPlayCalled = false
        mState = State.UNINITIALIZED
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, i: Int, i2: Int) {}
    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        mIsViewAvailable = false
        return false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isPlaying()) {
            pause()
        }
        log("view detached from window")
    }

    private fun addBackground() {
        mBackground.visibility = VISIBLE
    }

    override fun setBackground(drawable: Drawable?) {
        addBackground()
        mBackground.background = drawable
    }

    override fun setBackgroundColor(i: Int) {
        addBackground()
        mBackground.setBackgroundColor(i)
    }

    override fun setBackgroundResource(i: Int) {
        addBackground()
        mBackground.setBackgroundResource(i)
    }

    override fun setBackgroundTintList(colorStateList: ColorStateList?) {
        addBackground()
        mBackground.backgroundTintList = colorStateList
    }

    override fun setBackgroundTintMode(mode: PorterDuff.Mode?) {
        addBackground()
        mBackground.backgroundTintMode = mode
    }

    fun removeBackground() {
        mBackground.background = null
        mBackground.visibility = INVISIBLE
    }

    fun getMediaPlayer(): MediaPlayer? = mMediaPlayer
}