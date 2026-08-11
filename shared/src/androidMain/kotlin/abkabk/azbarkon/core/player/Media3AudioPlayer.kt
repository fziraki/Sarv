package abkabk.azbarkon.core.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class Media3AudioPlayer(
    private val exoPlayer: ExoPlayer,
) : AudioPlayer {

    private val listeners = mutableSetOf<AudioPlayerListener>()

    init {
        // ponytail: permanent focus gain - pauses other apps' audio for good;
        // user resumes their own music manually
        exoPlayer.setAudioAttributes(AudioAttributes.DEFAULT, true)
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                listeners.forEach { it.onIsPlayingChanged(isPlaying) }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val mapped = when (playbackState) {
                    Player.STATE_IDLE -> AudioPlaybackState.IDLE
                    Player.STATE_BUFFERING -> AudioPlaybackState.BUFFERING
                    Player.STATE_READY -> AudioPlaybackState.READY
                    Player.STATE_ENDED -> AudioPlaybackState.ENDED
                    else -> AudioPlaybackState.IDLE
                }
                if (mapped == AudioPlaybackState.ENDED) {
                    exoPlayer.pause()
                }
                listeners.forEach { it.onStateChanged(mapped) }
            }

            override fun onPlayerError(error: PlaybackException) {
                listeners.forEach { it.onError(error.message ?: "Playback error") }
            }
        })
    }

    override val isPlaying get() = exoPlayer.isPlaying
    override val currentPosition get() = exoPlayer.currentPosition
    override val duration get() = exoPlayer.duration.coerceAtLeast(0L)
    override val playbackState: AudioPlaybackState
        get() = when (exoPlayer.playbackState) {
            Player.STATE_BUFFERING -> AudioPlaybackState.BUFFERING
            Player.STATE_READY -> AudioPlaybackState.READY
            Player.STATE_ENDED -> AudioPlaybackState.ENDED
            else -> AudioPlaybackState.IDLE
        }

    override fun setMediaUrl(url: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
    }

    override fun play() = exoPlayer.play()
    override fun pause() = exoPlayer.pause()
    override fun seekTo(positionMs: Long) = exoPlayer.seekTo(positionMs)
    override fun addListener(listener: AudioPlayerListener) { listeners += listener }
    override fun removeListener(listener: AudioPlayerListener) { listeners -= listener }
    override fun release() = exoPlayer.release()
}
