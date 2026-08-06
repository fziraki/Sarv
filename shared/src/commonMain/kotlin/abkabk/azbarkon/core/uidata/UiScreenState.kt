package abkabk.azbarkon.core.uidata

sealed interface UiScreenState {
    data object Idle : UiScreenState

    data object Loading : UiScreenState

    data class Error(
        val message: UiText,
        val retryable: Boolean = true,
    ) : UiScreenState

    data object Success : UiScreenState
}
