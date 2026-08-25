package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.memorization.MemorizationError
import abkabk.azbarkon.domain.repository.MemorizationRepository
class StartMemorizationFromPoemUseCase(
    private val memorizationRepository: MemorizationRepository,
) {
    sealed interface StartResult {
        data object AlreadyActive : StartResult
        data object Success : StartResult
        data class Error(val error: MemorizationError) : StartResult
    }

    suspend operator fun invoke(poemId: Int): StartResult {
        if (memorizationRepository.isPoemActive(poemId)) {
            return StartResult.AlreadyActive
        }

        return when (val result = memorizationRepository.addPoem(poemId)) {
            is Result.Success -> StartResult.Success
            is Result.Error -> StartResult.Error(result.error)
        }
    }
}
