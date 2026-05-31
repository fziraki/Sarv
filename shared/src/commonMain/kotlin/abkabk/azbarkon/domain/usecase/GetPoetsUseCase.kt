package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.repository.PoetRepository

class GetPoetsUseCase(
    private val repository: PoetRepository,
) {
    suspend operator fun invoke(): Result<List<Poet>, DataError.Local> = repository.getPoets()
}
