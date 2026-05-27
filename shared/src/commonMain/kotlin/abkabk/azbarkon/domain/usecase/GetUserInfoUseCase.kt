package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.core.network.ApiResult
import abkabk.azbarkon.domain.model.Badge
import abkabk.azbarkon.domain.model.GameLevel
import abkabk.azbarkon.domain.model.UserInfo
import abkabk.azbarkon.domain.repository.PoetRepository

class GetUserInfoUseCase(
    private val repository: PoetRepository,
) {
    suspend operator fun invoke(): ApiResult<UserInfo> =
        ApiResult.Success(
            data =
                UserInfo(
                    completedLevel = GameLevel(id = 3, name = "همنشین غزل", 900),
                    inProgressLevel = GameLevel(id = 4, name = "حافظ ابیات", 900),
                    currentScore = 700,
                    streakNumber = 18,
                    poetsNumber = 12,
                    poemsNumber = 240,
                    badges = listOf(Badge(id = 1, "اولین غزل")),
                ),
        )
}
