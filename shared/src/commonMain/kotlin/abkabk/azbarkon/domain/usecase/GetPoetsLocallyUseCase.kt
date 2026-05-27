package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.repository.PoetRepository

class GetPoetsLocallyUseCase(
    private val repository: PoetRepository,
) {
    suspend operator fun invoke(): List<Poet> = repository.getPoetsLocally()
}
