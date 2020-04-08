package com.mahesch.trymyui.activity

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.mahesch.trymyui.R
import com.mahesch.trymyui.retrofitclient.ApiService
import kotlinx.android.synthetic.main.video_player_activity.*

class VideoPlayerActivity : AppCompatActivity() {

    private var currentWindow : Int? = 0
    private var playbackPosition : Long? = 0L
    private var playWhenReady : Boolean? =  true

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_player_activity)

        initializePlayer()
    }

    private fun initializePlayer(){

        if(null == simpleExoPlayer){
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this@VideoPlayerActivity), DefaultTrackSelector(), DefaultLoadControl())

            video_view.player = simpleExoPlayer
            simpleExoPlayer?.playWhenReady = this!!.playWhenReady!!
            simpleExoPlayer?.seekToDefaultPosition()

            val mediaSource = buildMediaSource(Uri.parse(ApiService.BASE_URL + ApiService.VIDEO_URL))
            simpleExoPlayer?.prepare(mediaSource, true, false)
        }

    }

    private fun buildMediaSource(uri: Uri): MediaSource? {
        val userAgent = "exoplayer-codelab"
        return if (uri.lastPathSegment.contains("mp4")) {
            ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else null
    }

    fun onTimelineChanged(
        timeline: Timeline?,
        manifest: Any?,
        reason: Int) {
    }

    fun onTracksChanged(
        trackGroups: TrackGroupArray?,
        trackSelections: TrackSelectionArray?
    ) {
    }

    fun onRepeatModeChanged(repeatMode: Int) {}

    fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

    fun onLoadingChanged(isLoading: Boolean) {}

    fun onPlayerError(error: ExoPlaybackException?) {
        Toast.makeText(
            this@VideoPlayerActivity,
            "" + getString(R.string.went_wrong),
            Toast.LENGTH_LONG
        ).show()
    }

    fun onPositionDiscontinuity(reason: Int) {}

    fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

    fun onSeekProcessed() {}


    override fun onStart() {
        super.onStart()

        //    initializePlayer();
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()

        //    releasePlayer();
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        if (simpleExoPlayer != null) {
            playbackPosition = simpleExoPlayer?.getCurrentPosition()
            currentWindow = simpleExoPlayer?.getCurrentWindowIndex()
            playWhenReady = simpleExoPlayer?.playWhenReady
            simpleExoPlayer?.release()

        }
    }


    override fun onBackPressed() {
        //super.onBackPressed();
        startActivity(Intent(this@VideoPlayerActivity, TabActivity::class.java))
        finish()
    }
}
