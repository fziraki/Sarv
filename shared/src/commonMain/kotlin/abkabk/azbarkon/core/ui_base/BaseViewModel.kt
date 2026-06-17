package abkabk.azbarkon.core.ui_base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<ACTION, STATE, EVENT>(
    initialState: STATE,
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _events = Channel<EVENT>()
    val events = _events.receiveAsFlow()

    protected fun setState(reducer: STATE.() -> STATE) {
        _state.update(reducer)
    }

    protected suspend fun sendEvent(event: EVENT) {
        _events.send(event)
    }

    abstract fun onAction(action: ACTION)
}
