package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.UserInfo

interface UserRepository {
    suspend fun getUserInfo(): Result<UserInfo, DataError>
}
