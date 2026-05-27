package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.core.network.ApiResult
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.repository.PoetRepository

class GetPoetsUseCase(
    private val repository: PoetRepository,
) {
    suspend operator fun invoke(): ApiResult<List<Poet>> = repository.getPoets()
}
