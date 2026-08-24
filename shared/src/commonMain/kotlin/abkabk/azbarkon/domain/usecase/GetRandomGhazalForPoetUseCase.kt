package abkabk.azbarkon.domain.usecase

import com.azbarkon.db.VerseQueries

class GetRandomGhazalForPoetUseCase(
    private val verseQueries: VerseQueries,
) {
    operator fun invoke(poetId: Int): Int? {
        return verseQueries
            .selectRandomGhazalPoemIdByPoet(poetId.toLong())
            .executeAsOneOrNull()
            ?.toInt()
    }
}
