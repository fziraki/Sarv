package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.EmptyResult
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.repository.PoetDownloadError
import abkabk.azbarkon.domain.repository.PoetDownloadRepository

class FakePoetDownloadRepository : PoetDownloadRepository {
    var downloadedPoetIds: MutableSet<Int> = mutableSetOf()
    var shouldFail: Boolean = false

    override suspend fun downloadPoet(poetId: Int): EmptyResult<PoetDownloadError> {
        if (shouldFail) return Result.Error(PoetDownloadError.Network)
        downloadedPoetIds += poetId
        return Result.Success(Unit)
    }
}
