package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.EmptyResult
import abkabk.azbarkon.core.domain.result.Error

sealed interface PoetDownloadError : Error {
    data object Network : PoetDownloadError
    data object MergeFailed : PoetDownloadError
}

interface PoetDownloadRepository {
    suspend fun downloadPoet(poetId: Int): EmptyResult<PoetDownloadError>
}
