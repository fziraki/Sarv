package abkabk.azbarkon.domain.usecase

import com.sarv.db.VerseQueries

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
