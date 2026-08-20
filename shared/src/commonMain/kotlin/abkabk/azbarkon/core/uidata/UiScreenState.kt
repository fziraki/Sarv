package abkabk.azbarkon.core.uidata

private var errorKeyCounter = 0L
private fun nextErrorKey(): Long = errorKeyCounter++

sealed interface UiScreenState {
    data object Idle : UiScreenState

    data object Loading : UiScreenState

    data class Error(
        val message: UiText,
        val retryable: Boolean = false,
        val isSuccess: Boolean = false,
        val key: Long? = nextErrorKey(),
    ) : UiScreenState

    data object Success : UiScreenState
}
