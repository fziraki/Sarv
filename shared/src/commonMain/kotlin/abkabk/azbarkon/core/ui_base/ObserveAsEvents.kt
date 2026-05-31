package abkabk.azbarkon.core.ui_base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> ObserveAsEvents(
    events: Flow<T>,
    onEvent: (T) -> Unit,
) {
    LaunchedEffect(events) {
        events.collect(onEvent)
    }
}
