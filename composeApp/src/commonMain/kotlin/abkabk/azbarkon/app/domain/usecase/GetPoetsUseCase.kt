package abkabk.azbarkon.app.domain.usecase

import abkabk.azbarkon.app.core.network.ApiResult
import abkabk.azbarkon.app.domain.model.Poet
import abkabk.azbarkon.app.domain.repository.PoetRepository

class GetPoetsUseCase(
    private val repository: PoetRepository
) {

    suspend operator fun invoke(): ApiResult<List<Poet>> {
        return repository.getPoets()
    }
}