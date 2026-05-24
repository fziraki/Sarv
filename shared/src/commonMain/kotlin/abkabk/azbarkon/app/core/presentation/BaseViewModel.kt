package abkabk.azbarkon.app.core.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<EVENT, STATE, EFFECT>(
    initialState: STATE
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effect = Channel<EFFECT>()
    val effect = _effect.receiveAsFlow()

    protected fun setState(reducer: STATE.() -> STATE) {
        _state.update(reducer)
    }

    protected suspend fun sendEffect(effect: EFFECT) {
        _effect.send(effect)
    }

    abstract fun onEvent(event: EVENT)
}