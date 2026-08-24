package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.repository.PoemRepository
import abkabk.azbarkon.domain.repository.SavedPoemRepository
import abkabk.azbarkon.features.mypoems.PoetGroupUi
import abkabk.azbarkon.features.mypoems.toPoetGroups

class GetMyPoemsUseCase(
    private val poemRepository: PoemRepository,
    private val savedPoemRepository: SavedPoemRepository,
) {
    data class MyPoemsResult(
        val likedGroups: List<PoetGroupUi>,
        val bookmarkedGroups: List<PoetGroupUi>,
    )

    suspend operator fun invoke(): Result<MyPoemsResult, DataError> {
        val likedIds = savedPoemRepository.getLikedIds()
        val bookmarkedIds = savedPoemRepository.getBookmarkedIds()

        val likedResult = poemRepository.getPoemsByIds(likedIds)
        val bookmarkedResult = poemRepository.getPoemsByIds(bookmarkedIds)

        return when {
            likedResult is Result.Error -> likedResult
            bookmarkedResult is Result.Error -> bookmarkedResult
            else -> {
                val likedPoems = (likedResult as Result.Success).data
                val bookmarkedPoems = (bookmarkedResult as Result.Success).data
                Result.Success(
                    MyPoemsResult(
                        likedGroups = likedPoems.toPoetGroups(),
                        bookmarkedGroups = bookmarkedPoems.toPoetGroups(),
                    )
                )
            }
        }
    }
}
