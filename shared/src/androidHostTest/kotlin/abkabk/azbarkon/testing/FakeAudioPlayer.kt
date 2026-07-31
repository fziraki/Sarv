package abkabk.azbarkon.testing

import abkabk.azbarkon.core.player.AudioPlaybackState
import abkabk.azbarkon.core.player.AudioPlayer
import abkabk.azbarkon.core.player.AudioPlayerListener

class FakeAudioPlayer : AudioPlayer {
    override var isPlaying: Boolean = false
    override var currentPosition: Long = 0L
    override var duration: Long = 0L
    override var playbackState: AudioPlaybackState = AudioPlaybackState.IDLE
    override fun setMediaUrl(url: String) {}
    override fun play() {}
    override fun pause() {}
    override fun seekTo(positionMs: Long) {}
    override fun addListener(listener: AudioPlayerListener) {}
    override fun removeListener(listener: AudioPlayerListener) {}
    override fun release() {}
}
