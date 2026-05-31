package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Badge
import abkabk.azbarkon.domain.model.GameLevel
import abkabk.azbarkon.domain.model.UserInfo

class GetUserInfoUseCase {
    suspend operator fun invoke(): Result<UserInfo, DataError> =
        Result.Success(
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
