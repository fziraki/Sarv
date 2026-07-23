package abkabk.azbarkon.core.player

interface AudioPlayer {
    val isPlaying: Boolean
    val currentPosition: Long
    val duration: Long
    val playbackState: AudioPlaybackState
    fun setMediaUrl(url: String)
    fun play()
    fun pause()
    fun seekTo(positionMs: Long)
    fun addListener(listener: AudioPlayerListener)
    fun removeListener(listener: AudioPlayerListener)
    fun release()
}

interface AudioPlayerListener {
    fun onIsPlayingChanged(isPlaying: Boolean) {}
    fun onStateChanged(state: AudioPlaybackState) {}
    fun onError(message: String) {}
}

enum class AudioPlaybackState { IDLE, BUFFERING, READY, ENDED, ERROR }